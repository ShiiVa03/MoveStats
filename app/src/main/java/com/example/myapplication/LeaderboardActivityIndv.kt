package com.example.myapplication


import android.annotation.SuppressLint
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
        val act = intent.getStringExtra("Activity")!!
        val text : TextView = findViewById(R.id.textviewleaderboard2)
        text.text = "$act Leaderboard"
        fetchUsers(act)
    }


    private fun fetchUsers(act : String) {
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

    private fun initData(list : ArrayList<User>, act : String): ArrayList<ItemViewModel> {
        val data = ArrayList<ItemViewModel>()
        var time = 0

        if(list.isNotEmpty()) {
            list.reverse()
            list.forEach { user ->
                when (act) {
                    "Walking" -> time = user.WalkingTime!!
                    "Running" -> time = user.RunningTime!!
                    "UpStairs" -> time = user.UpStairsTime!!
                    "DownStairs" -> time = user.DownStairsTime!!
                    "Idle" -> time = user.IdleTime!!
                }

                data.add(
                    ItemViewModel(
                        user.name!!.toString(),
                        time.toString()
                    )
                )
            }
        }

        return data
    }

    private fun setupCards(data: ArrayList<ItemViewModel>) {
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerviewleaderboard2)
        val adapter = CustomAdapterLeaderboard(data)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)
    }
}