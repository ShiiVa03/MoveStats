package com.example.myapplication

import CustomAdapterCollection
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CollectionActivityType : AppCompatActivity() {

    private var type = -1

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection_activity_type)
        supportActionBar?.hide()

        val activity = intent.getIntExtra("activity", -1)
        val typeRoam = intent.getBooleanExtra("typeRoam", false)
        val data = ArrayList<ItemsViewModel>()
        val activities = listOf("Inside left pocket", "Inside right pocket", "In your hand", "On a desk/flat surface")
        activities.forEach { e ->
            data.add(
                ItemsViewModel(
                    R.drawable.ic_launcher_foreground,
                    0f,
                    e,
                    Color.WHITE
                )
            )
        }
        val adapter = CustomAdapterCollection(data)
        adapter.setOnItemClickListener(object : CustomAdapterCollection.OnItemClickListener{
            override fun onItemClick(position: Int) {
                type = position
            }
        })
        val recyclerview = findViewById<RecyclerView>(R.id.recyCollectionType)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)

        val btnNext = findViewById<Button>(R.id.btnNextType)

        btnNext.setOnClickListener {
            if (type == -1) {
                Toast.makeText(this, "Select type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, CollectionActivityFinal::class.java)
            intent.putExtra("activity", activity)
            intent.putExtra("typeRoam", typeRoam)
            intent.putExtra("type",type)
            this.startActivity(intent)
        }
    }


}