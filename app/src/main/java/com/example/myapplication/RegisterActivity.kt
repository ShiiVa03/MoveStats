package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
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

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var databaseRefined : DatabaseReference

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        supportActionBar?.hide()

        auth = Firebase.auth

        val fieldName = findViewById<EditText>(R.id.editTextName)
        val fieldEmail = findViewById<EditText>(R.id.editTextUsername)
        val fieldPassword = findViewById<EditText>(R.id.editTextTextPassword)


        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        databaseRefined = Firebase.database("https://data-575fe-default-rtdb.europe-west1.firebasedatabase.app/").reference

        buttonRegister.setOnClickListener {

            val name = fieldName.text.toString()
            if (TextUtils.isEmpty(name)) {
                fieldName.error = "Required."
                return@setOnClickListener
            }
            val email = fieldEmail.text.toString()
            if (TextUtils.isEmpty(email)) {
                fieldEmail.error = "Required."
                return@setOnClickListener
            }

            val password = fieldPassword.text.toString()
            if (TextUtils.isEmpty(password)) {
                fieldPassword.error = "Required."
                return@setOnClickListener
            }
            if(password.length < 6){
                fieldPassword.error = "Password should be at least 6 characters"
                return@setOnClickListener
            }

            val intent = Intent(this, RegisterHW::class.java)
            intent.putExtra("name", name)
            intent.putExtra("email", email)
            intent.putExtra("pass", password)
            startActivity(intent)

        }
    }
}