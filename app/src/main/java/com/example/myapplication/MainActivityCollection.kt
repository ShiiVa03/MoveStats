package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class MainActivityCollection : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navView: NavigationView
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.initial)
        supportActionBar?.hide()


        drawerLayout = findViewById(R.id.drawer_layout)
        navView= findViewById(R.id.nav_view)

        navView.setNavigationItemSelectedListener(this)
        val imgbtn : ImageButton = findViewById(R.id.imageButton)
        imgbtn.setOnClickListener {
            drawerLayout.openDrawer(navView)
            return@setOnClickListener
        }

        val btnFree = findViewById<Button>(R.id.buttonCollectNonStop)
        val btn20 = findViewById<Button>(R.id.button20sec)

        btnFree.setOnClickListener {
            val intent = Intent(this, CollectionActivity::class.java)
            intent.putExtra("typeRoam", false)
            startActivity(intent)
        }
        btn20.setOnClickListener {
            val intent = Intent(this, CollectionActivity::class.java)
            intent.putExtra("typeRoam", true)
            startActivity(intent)
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