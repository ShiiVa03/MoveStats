package com.example.myapplication

import CustomAdapter
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LeaderboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val activities = listOf("Walking", "Running", "UpStairs", "DownStairs", "Idle")
    private val colours = listOf(
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.MAGENTA
    )

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navView: NavigationView

    @SuppressLint("SetTextI18n")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.leaderboard_activities)
        supportActionBar?.hide()

        drawerLayout = findViewById(R.id.drawer_layoutLead)
        navView= findViewById(R.id.nav_viewLEad)

        navView.setNavigationItemSelectedListener(this)
        val imgbtn : ImageButton = findViewById(R.id.imageButtonLead)
        imgbtn.setOnClickListener {
            drawerLayout.openDrawer(navView)
            return@setOnClickListener
        }


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