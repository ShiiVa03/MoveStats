package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.thread


data class Record (val timestamp: Long,
                  val name: String,
                  val height: Int,
                  val weight: Float,
                  val lado: Int,
                  val activity: Int,
                  val gyros: List<Float>,
                  val acc: List<Float>)



class CollectionActivity: AppCompatActivity(), SensorEventListener {
    private var gyros : ArrayList<Triple<Float, Float, Float>> = ArrayList()
    private var accs : ArrayList<Triple<Float, Float, Float>> = ArrayList()
    private var timestamps : ArrayList<Long> = ArrayList()

    private lateinit var sensorManager: SensorManager
    private var accSensor: Sensor? = null
    private var gyroSensor: Sensor? = null

    private lateinit var databaseRefined : DatabaseReference

    var activity: Int = -1
    var type: Int = -1

    var start = false

    private var gyrosPlot : List<Triple<Float, Float, Float>> = ArrayList()
    private var accsPlot : List<Triple<Float, Float, Float>> = ArrayList()




    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection_activity)

        val spinner: Spinner = findViewById(R.id.spinner)
        val spinner2: Spinner = findViewById(R.id.spinner2)


        val list = arrayOf("Andar","Correr","Subir Escada","Descer Escada","Estar quieto")
        val list2 = arrayOf("Bolso Esquerdo","Bolso Direito","Na mão","Na mesa")

        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item,list)
        val aa2 = ArrayAdapter(this,android.R.layout.simple_spinner_item,list2)
        aa.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        aa2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        sensorManager.registerListener(this, accSensor,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gyroSensor,SensorManager.SENSOR_DELAY_NORMAL)

        databaseRefined = Firebase.database("https://data-575fe-default-rtdb.europe-west1.firebasedatabase.app/").reference


        spinner.adapter = aa
        spinner2.adapter = aa2
        spinner2.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                type = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                activity = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val btnstart = findViewById<Button>(R.id.button2)
        val btnoff = findViewById<Button>(R.id.button3)


        val context: Context = applicationContext
        val powerManager: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "motionDetection:keepAwake")


        val typeOfRoam : Boolean = intent.getBooleanExtra("type",false)

        btnstart.setOnClickListener {
            btnstart.visibility = View.GONE
            wakeLock.acquire(10*60*1000L /*10 minutes*/)

            val nameEdit = findViewById<EditText>(R.id.editTextTextPersonName)
            val name = nameEdit.text.toString()

            if(TextUtils.isEmpty(name)) {
                nameEdit.error = "Please Enter Your Name"
                return@setOnClickListener
            }
            val alturaEdit = findViewById<EditText>(R.id.editTextTextPersonName2)

            if(TextUtils.isEmpty(alturaEdit.text.toString())) {
                alturaEdit.error = "Please Enter your Height"
                return@setOnClickListener
            }
            val altura = alturaEdit.text.toString().toInt()

            val pesoEdit = findViewById<EditText>(R.id.editTextTextPersonName4)
            if(TextUtils.isEmpty(pesoEdit.text.toString())) {
                pesoEdit.error = "Please Enter your Weight"
                return@setOnClickListener
            }

            val peso = pesoEdit.text.toString().toFloat()


            if (type == -1  && activity == -1){
                Toast.makeText(this, "Select activity and type please", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val timer = Timer()

            if (typeOfRoam){
                Toast.makeText(this, "Collecting 20 seconds of data !!", Toast.LENGTH_SHORT).show()
                timer.schedule(object : TimerTask(){
                    override fun run() {
                        start = true
                    }
                },4000)

                timer.schedule(object : TimerTask(){
                    override fun run() {
                        start = false
                       sendDataToFirebase(name, peso, altura)
                    }

                },24000)
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        timer.cancel()
                        timer.purge()
                        wakeLock.release()
                        Handler(Looper.getMainLooper()).post {
                            val intent = Intent(context, GraphDisplayActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
                            val arrya = ArrayList(gyrosPlot)
                            Log.d("LIST AHHAHAHAHA  ",arrya.toString())
                            intent.putExtra("gyrosPlot", ArrayList(gyrosPlot))
                            intent.putExtra("accsPlot", ArrayList(accsPlot))
                            context.startActivity(intent)
                        }
                        runOnUiThread { btnstart.visibility = View.VISIBLE }


                    }
                },24000)

            }else{
                Toast.makeText(this, "Collecting of data until you stop !!", Toast.LENGTH_SHORT).show()
                start = true

                btnoff.setOnClickListener {
                    start = false
                    thread{
                        sendDataToFirebase(name, peso, altura)
                    }
                    timer.cancel()
                    timer.purge()
                    wakeLock.release()

                    val intent = Intent(this, GraphDisplayActivity::class.java)
                    intent.putExtra("gyrosPlot", ArrayList(gyrosPlot))
                    intent.putExtra("accsPlot", ArrayList(accsPlot))
                    startActivity(intent)
                    btnstart.visibility = View.VISIBLE
                }

            }
    }

    }

    fun sendDataToFirebase(name : String, peso : Float, altura : Int){
        val size = listOf(accs.size, gyros.size).min()
        gyrosPlot = gyros.slice(0 until size)
        Log.d("LIST HEHEHEHHEHE ",gyrosPlot.toString())
        accsPlot = accs.slice(0 until size)
        Log.d("LIST HEHEHEHHEHE ",accsPlot.toString())
        for (i in 0 until size) {
            val accsSent =
                listOf(accs[i].first, accs[i].second, accs[i].third)
            val gyrosSent =
                listOf(gyros[i].first, gyros[i].second, gyros[i].third)
            val timestamp : Long = timestamps[i]

            val rec = Record(timestamp, name, altura, peso, type,
                activity, gyrosSent, accsSent)

            val newref = databaseRefined.child("Data").push()
            newref.setValue(rec)
        }
        accs.clear()
        gyros.clear()
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE){
            if(this.start) {
                gyros.add(Triple(event.values[0],event.values[1],event.values[2]))
                timestamps.add(System.currentTimeMillis())
            }
        }else if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            if(this.start) {
                accs.add(Triple(event.values[0],event.values[1],event.values[2]))
                timestamps.add(System.currentTimeMillis())
            }
        }

    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    override fun onResume() {
        super.onResume()
        accSensor?.also { acc ->
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_FASTEST)
        }
        gyroSensor?.also { acc ->
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }


}