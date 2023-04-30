package com.example.myapplication

import CustomAdapter
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LeaderboardActivityIndv : AppCompatActivity() {

    private lateinit var ranks : ArrayList<User>
    private lateinit var databaseRefined : DatabaseReference

    @SuppressLint("SetTextI18n")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.leaderboard)
        supportActionBar?.hide()

        databaseRefined = Firebase.database("https://data-575fe-default-rtdb.europe-west1.firebasedatabase.app/").reference

        val extras = intent.extras
        val textView = findViewById<TextView>(R.id.textviewleaderboard2)
        textView.text = extras!!.getString("Activity") + " Leaderboard"
        textView.setTextColor(extras.getInt("Colour"))

        fetchUsers(extras.getString("Activity")!!)
    }


    fun fetchUsers(act : String) {
        val users = databaseRefined.child("users")
        var path = ""

        when (act) {
            "Walking" -> path = "WalkingTime"
            "Running" -> path = "RunningTime"
            "UpStairs" -> path = "UpStairsTime"
            "DownStairs" -> path = "DownStairsTime"
            "Idle" -> path = "IdleTime"
        }

        ranks = ArrayList()
        users.orderByChild(path).get().addOnSuccessListener {
            for (ds in it.children) {
                val user = ds.getValue(User::class.java)
                ranks.add(user!!)
            }

            val data = initData(ranks, act)
            setupCards(data)

        }.addOnFailureListener{
            Log.e("firebase", "error getting data", it)
        }
    }

    private fun initData(list : ArrayList<User>, act : String): ArrayList<ItemsViewModel> {
        val data = ArrayList<ItemsViewModel>()
        var time = 0

        list.reverse()
        list.forEachIndexed { i, user ->
            when (act) {
                "Walking" -> time = user.WalkingTime!!
                "Running" -> time = user.RunningTime!!
                "UpStairs" -> time = user.UpStairsTime!!
                "DownStairs" -> time = user.DownStairsTime!!
                "Idle" -> time = user.IdleTime!!
            }

            data.add(
                ItemsViewModel(
                    R.drawable.ic_launcher_foreground,
                    0f,
                    "Rank " + (i+1).toString() + ": " + user.name!! + " >>> " + time + " seg",
                    Color.WHITE
                )
            )
        }

        return data
    }

    private fun setupCards(data: ArrayList<ItemsViewModel>) {
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerviewleaderboard2)
        val adapterListener = CustomAdapter.OnClickListener {}
        val adapter = CustomAdapter(data, adapterListener)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)
    }
}