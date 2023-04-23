package com.example.myapplication

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileNotFoundException
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class Stats {
    val weeklyStats: ArrayList<ArrayList<Int>> = ArrayList()



    companion object {
        const val API_IP = "https://movestats.onrender.com/"
        const val TIME_PER_SAMPLE = 20
        const val filename = "data.json"
        val activities = listOf("Walking", "Running", "Up-Stairs", "Down-Stairs", "Idle")

        fun getJsonDataFromFile(context: Context, filename: String): String {
            return try {
                val inputStream = context.openFileInput(filename)
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                inputStream.close()
                jsonString
            } catch(e: FileNotFoundException) {
                "{}"
            }
        }

        fun setJsonDataToFile(context: Context, filename: String, jsonString: String) {
            val fileContents = jsonString.toByteArray()
            val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
            outputStream.write(fileContents)
            outputStream.close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun load(context: Context) {
        val jsonFileString = getJsonDataFromFile(context, filename)

        val type: Type = object : TypeToken<Map<String, List<Int>>>() {}.type
        val stats = Gson().fromJson<Map<String, ArrayList<Int>>>(jsonFileString, type)


        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val now = LocalDateTime.now()

        weeklyStats.clear()

        for (i in 0..6) {
            val time = now.minus(i.toLong(), ChronoUnit.DAYS)
            val s = time.format(formatter)

            stats[s]?.let { weeklyStats.add(it) } ?: weeklyStats.add(List(activities.size) { 0 }.toCollection(ArrayList()))
        }
    }


    class MyWorker(context: Context, params: WorkerParameters) : Worker(context, params),
        SensorEventListener {
        private lateinit var apiService: APIService
        private var todayDate: String = ""
        private lateinit var dailyStats: ArrayList<Int>
        private lateinit var allStats: HashMap<String, ArrayList<Int>>

        private lateinit var sensorManager: SensorManager
        private lateinit var accSensor: Sensor
        private lateinit var gyroSensor: Sensor
        private var accs: MutableList<Triple<Float, Float, Float>> = mutableListOf()
        private var gyros: MutableList<Triple<Float, Float, Float>>  = mutableListOf()


        @RequiresApi(Build.VERSION_CODES.O)
        private fun updateDate() : Boolean{
            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
            val s = LocalDateTime.now().format(formatter)

            if (todayDate != s) {
                todayDate = s
                dailyStats = List(activities.size) { 0 }.toCollection(ArrayList())
                return true
            }

            return false
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun load() {
            val jsonFileString = getJsonDataFromFile(applicationContext, filename)

            val type: Type = object : TypeToken<HashMap<String, List<Int>>>() {}.type
            allStats = Gson().fromJson(jsonFileString, type)

            updateDate() // Get Today's date

            allStats[todayDate]?.let { dailyStats = it }
        }

        private fun save() {
            allStats[todayDate] = dailyStats
            val jsonString = Gson().toJson(allStats)
            setJsonDataToFile(applicationContext, filename, jsonString)
        }

        @OptIn(DelicateCoroutinesApi::class)
        @JvmSuppressWildcards
        private fun sendData(collected: List<List<CollectedStats>>) {

            val call = apiService.predict(collected)
            val localTodayDate = todayDate // Bring locally to compare with change

            call.enqueue(object : Callback<List<Int>> {
                override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                    // handle the response
                    Log.d("TAG", response.body().toString())
                    response.body()?.let { dailyStats =
                        dailyStats.mapIndexed { i, e-> e + TIME_PER_SAMPLE * it.count {it == i} } as ArrayList<Int>
                    }
                    save()
                }

                override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                    // handle the failure
                    Log.d("TAG", "FAILURE: $t")


                    if (localTodayDate == todayDate) {
                        val callback_obj = this

                        GlobalScope.launch {
                            delay(300_000)
                            call.clone().enqueue(callback_obj) // retry the request
                        }
                    }
                }
            })
        }

        private fun collect(): List<CollectedStats> {
            val size = listOf(accs.size, gyros.size).min()
            val collected = ArrayList<CollectedStats>()
            for (i in 0 until size)
                collected.add(CollectedStats(accs[i].toList(), gyros[i].toList()))
            accs.clear()
            gyros.clear()
            return collected
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun doWork(): Result {
            // Perform your long-running task here
            // This method runs on a background thread

            load()

            val client = OkHttpClient.Builder().build()

            val retrofit = Retrofit.Builder()
                .baseUrl(API_IP)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            apiService = retrofit.create(APIService::class.java)

            sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

            sensorManager.registerListener(this, accSensor,SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager.registerListener(this, gyroSensor,SensorManager.SENSOR_DELAY_NORMAL)


            while (true) {
                val samples : ArrayList<List<CollectedStats>> = ArrayList()

                // Each iteration (sample) takes 20sec so a total of 300sec (5min) to complete
                for (i in 0 until 1) {
                    Thread.sleep((TIME_PER_SAMPLE * 1_000).toLong())
                    samples.add(collect())
                    Log.d("TAG", "Iteration $i")
                }

                sendData(samples)
                updateDate()
            }
        }

        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                if (event.sensor.type == Sensor.TYPE_GYROSCOPE){
                    gyros.add(Triple(event.values[0], event.values[1], event.values[2]))
                }else if(event.sensor.type == Sensor.TYPE_ACCELEROMETER){
                    accs.add(Triple(event.values[0], event.values[1], event.values[2]))
                }
            }

        }

        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        }
    }

}