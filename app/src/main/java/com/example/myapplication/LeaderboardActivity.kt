package com.example.myapplication

import CustomAdapter
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
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

    private lateinit var idle : ArrayList<User>
    private lateinit var walking : ArrayList<User>
    private lateinit var running : ArrayList<User>
    private lateinit var upstairs : ArrayList<User>
    private lateinit var downstairs : ArrayList<User>
    private lateinit var databaseRefined : DatabaseReference

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.leaderboard)
        supportActionBar?.hide()

        databaseRefined = Firebase.database("https://data-575fe-default-rtdb.europe-west1.firebasedatabase.app/").reference


        fetchUsers()
    }

    fun fetchUsers() {
        val users = databaseRefined.child("users")

        idle = ArrayList()
        users.orderByChild("IdleTime").get().addOnSuccessListener {
            for (ds in it.children) {
                val user = ds.getValue(User::class.java)
                idle.add(user!!)
            }

            val data = initData(idle)
            setupCards(data)

        }.addOnFailureListener{
            Log.e("firebase", "error getting data", it)
        }
    }

    private fun initData(list : ArrayList<User>): ArrayList<ItemsViewModel> {
        val data = ArrayList<ItemsViewModel>()

        list.reverse()
        list.forEachIndexed { i, user ->
            data.add(
                ItemsViewModel(
                    R.drawable.ic_launcher_foreground,
                    0f,
                    "Rank " + (i+1).toString() + ": " + user.name!! + " >>> " + user.IdleTime + " seg",
                    Color.WHITE
                )
            )
        }

        return data
    }

    private fun setupCards(data: ArrayList<ItemsViewModel>) {
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerleaderboard)
        val adapterListener = CustomAdapter.OnClickListener {}
        val adapter = CustomAdapter(data, adapterListener)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)
    }
}