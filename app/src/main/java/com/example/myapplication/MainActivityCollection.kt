package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class MainActivityCollection : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.initial)
        supportActionBar?.hide()

        val btnFree = findViewById<Button>(R.id.freeRoam)
        val btn20 = findViewById<Button>(R.id.button20sec)

        btnFree.setOnClickListener {
            val intent = Intent(this, CollectionActivity::class.java)
            intent.putExtra("type", false)
            startActivity(intent)
        }
        btn20.setOnClickListener {
            val intent = Intent(this, CollectionActivity::class.java)
            intent.putExtra("type", true)
            startActivity(intent)
        }

    }
}