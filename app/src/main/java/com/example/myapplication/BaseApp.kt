package com.example.myapplication

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

class BaseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // code to be executed before main activity

        //val sharedPreferences = applicationContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        //sharedPreferences.edit().clear().apply()
        //sharedPreferences.edit().clear().apply(); val workScheduled = true
        //val workScheduled = sharedPreferences.getBoolean("work_scheduled", false)

        //if (!workScheduled) {
        Log.d("TAGCAT", "HEHE")
        val myWorkRequest = OneTimeWorkRequest.Builder(Stats.MyWorker::class.java)
            .build()

        WorkManager.getInstance(applicationContext)
            .beginUniqueWork("my_work", ExistingWorkPolicy.REPLACE, myWorkRequest)
            .enqueue()

            //WorkManager.getInstance(applicationContext).cancelAllWork()

            // Set the flag to true so the work is only scheduled once
            //sharedPreferences.edit().putBoolean("work_scheduled", true).apply()
        //}
    }
}