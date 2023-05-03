package com.example.myapplication


import android.content.ContentValues.TAG

import android.os.Bundle
import android.text.TextUtils

import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID

class ChangeInfoActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var databaseRefined : DatabaseReference

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.changeinfo_activity)
        supportActionBar?.hide()

        auth = Firebase.auth

        databaseRefined =
            Firebase.database("https://data-575fe-default-rtdb.europe-west1.firebasedatabase.app/").reference

        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)
        val fieldPeso = findViewById<EditText>(R.id.changeWeight)
        val fieldAltura = findViewById<EditText>(R.id.changeHeight)

        val updates = mutableMapOf<String, Any>()


        buttonSubmit.setOnClickListener {
            if (!TextUtils.isEmpty(fieldPeso.text.toString())) {
                updates["weight"] = fieldPeso.text.toString().toFloat()

            }
            if (!TextUtils.isEmpty(fieldAltura.text.toString())) {
                updates["height"] = fieldAltura.text.toString().toInt()
            }


            if (updates.isNotEmpty()) {
                if (auth.currentUser != null) {
                    databaseRefined.child("users").child(auth.currentUser!!.uid)
                        .updateChildren(updates)
                        .addOnSuccessListener {
                            Log.d(TAG, "Document updated successfully!")
                            Toast.makeText(this, "Update Successful",Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating document", e)
                        }
                }

            }
        }
    }
}