package com.example.myapplication


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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



class CollectionActivityFinal: AppCompatActivity(), SensorEventListener, NavigationView.OnNavigationItemSelectedListener{
    private var gyros : ArrayList<Triple<Float, Float, Float>> = ArrayList()
    private var accs : ArrayList<Triple<Float, Float, Float>> = ArrayList()
    private var timestamps : ArrayList<Long> = ArrayList()
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var sensorManager: SensorManager
    private var accSensor: Sensor? = null
    private var gyroSensor: Sensor? = null

    private lateinit var databaseRefined : DatabaseReference
    private lateinit var auth : FirebaseAuth

    private var activity: Int = -1
    private var type: Int = -1

    var start = false

    private var name : String? = null
    private var altura : Int? = null
    private var peso : Float? = null


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection_activity_final)
        supportActionBar?.hide()

        drawerLayout = findViewById(R.id.drawer_layoutFinal)
        navView= findViewById(R.id.nav_viewFinal)

        navView.setNavigationItemSelectedListener(this)
        val imgbtn : ImageButton = findViewById(R.id.imageButtonFinal)
        imgbtn.setOnClickListener {
            drawerLayout.openDrawer(navView)
            return@setOnClickListener
        }

        val btnstop : Button = findViewById(R.id.btnStop)

        val typeOfRoam : Boolean = intent.getBooleanExtra("typeRoam",false)
        activity = intent.getIntExtra("activity", -1)
        type = intent.getIntExtra("type", -1)

        if(!typeOfRoam){
            btnstop.visibility = View.VISIBLE
        }

        databaseRefined =
            Firebase.database("https://data-575fe-default-rtdb.europe-west1.firebasedatabase.app/").reference

        auth = Firebase.auth


        if (auth.currentUser != null) {
            val uidRef = databaseRefined.child("users").child(auth.currentUser!!.uid)
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = dataSnapshot.getValue(User::class.java)
                    name = user!!.name.toString()
                    altura = user.height!!
                    peso = user.weight!!

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, databaseError.message)
                }
            }
            uidRef.addListenerForSingleValueEvent(valueEventListener)

        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        sensorManager.registerListener(this, accSensor,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, gyroSensor,SensorManager.SENSOR_DELAY_NORMAL)

        val context: Context = applicationContext
        val powerManager: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "motionDetection:keepAwake")



        val btnstart : Button = findViewById(R.id.btnStart)
        val texStart : TextView = findViewById(R.id.textCollection)
        texStart.visibility = View.VISIBLE
        val texcollecting : TextView = findViewById(R.id.textCollecting)
        texcollecting.visibility = View.INVISIBLE

        val handler = Handler()
        val runnable = object : Runnable {
            var counter = 0
            @SuppressLint("SetTextI18n")
            override fun run() {
                val dots = when (counter % 3) {
                    0 -> "."
                    1 -> ".."
                    else -> "..."
                }
                texcollecting.text = "Collecting$dots"
                counter++
                handler.postDelayed(this, 500)
            }
        }


        btnstart.setOnClickListener {
            btnstart.visibility = View.GONE
            handler.post(runnable)
            texStart.visibility = View.INVISIBLE
            texcollecting.visibility = View.VISIBLE
            wakeLock.acquire(10*60*1000L /*10 minutes*/)

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
                        sendDataToFirebase(name!!, peso!!, altura!!)
                    }

                },24000)
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        timer.cancel()
                        timer.purge()
                        wakeLock.release()
                        runOnUiThread { btnstart.visibility = View.VISIBLE
                            texStart.visibility = View.VISIBLE
                            texcollecting.visibility = View.INVISIBLE
                            handler.removeCallbacks(runnable)}
                    }
                },24000)

            }else{
                Toast.makeText(this, "Collecting of data until you stop !!", Toast.LENGTH_SHORT).show()
                start = true

                btnstop.setOnClickListener {
                    start = false
                    texStart.visibility = View.VISIBLE
                    texcollecting.visibility = View.INVISIBLE
                    handler.removeCallbacks(runnable)
                    thread{
                        sendDataToFirebase(name!!, peso!!, altura!!)
                    }
                    timer.cancel()
                    timer.purge()
                    wakeLock.release()

                    btnstart.visibility = View.VISIBLE
                }

            }
        }

    }

    fun sendDataToFirebase(name : String, peso : Float, altura : Int){
        val size = listOf(accs.size, gyros.size).min()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item1 -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.nav_item2 -> {
                val intent = Intent(this, MainActivityCollection::class.java)
                startActivity(intent)
                return true
            }

            R.id.nav_item3 -> {
                // Handle item 3 click
                val intent = Intent(this, ChangeInfoActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.nav_item4 -> {
                // Handle item 3 click
                val intent = Intent(this, LeaderboardActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return false
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }



}