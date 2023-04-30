package com.example.myapplication

import CustomAdapter
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LeaderboardActivity : AppCompatActivity() {

    private val activities = listOf("Walking", "Running", "UpStairs", "DownStairs", "Idle")
    private val colours = listOf(
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.MAGENTA
    )

    @SuppressLint("SetTextI18n")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.leaderboard_activities)
        supportActionBar?.hide()

        val textView = findViewById<TextView>(R.id.textviewleaderboard1)
        textView.text = "LEADERBOARDS"

        val data = initData()
        setupCards(data)
    }

    private fun initData(): ArrayList<ItemsViewModel> {
        val data = ArrayList<ItemsViewModel>()

        activities.forEachIndexed() { i, act ->
            data.add(
                ItemsViewModel(
                    R.drawable.ic_launcher_foreground,
                    0f,
                    act,
                    colours[i]
                )
            )
        }

        return data
    }

    private fun setupCards(data: ArrayList<ItemsViewModel>) {
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerviewleaderboard1)

        val adapterListener = CustomAdapter.OnClickListener { item ->
            val i = Intent(this, LeaderboardActivityIndv::class.java)
            i.putExtra("Activity", item.text)
            i.putExtra("Colour", item.colour)
            this.startActivity(i)
        }

        val adapter = CustomAdapter(data, adapterListener)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)
    }
}