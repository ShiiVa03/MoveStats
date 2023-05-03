package com.example.myapplication

import android.content.ContentValues
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
import java.util.UUID

class RegisterHW : AppCompatActivity()  {
    private lateinit var auth : FirebaseAuth
    private lateinit var databaseRefined : DatabaseReference

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.heightweight)
        supportActionBar?.hide()

        val password = intent.getStringExtra("pass")!!
        val email = intent.getStringExtra("email")!!
        val name = intent.getStringExtra("name")


        auth = Firebase.auth

        databaseRefined = Firebase.database("https://data-575fe-default-rtdb.europe-west1.firebasedatabase.app/").reference

        val buttonRegister = findViewById<Button>(R.id.buttonHW)
        val fieldPeso = findViewById<EditText>(R.id.editTextWeightR)
        val fieldAltura = findViewById<EditText>(R.id.editTextHeightR)

        buttonRegister.setOnClickListener {
            val pesoT = fieldPeso.text.toString()
            if (TextUtils.isEmpty(pesoT)) {
                fieldPeso.error = "Required."
                return@setOnClickListener
            }
            val peso = pesoT.toFloat()

            val alturaT = fieldAltura.text.toString()
            if (TextUtils.isEmpty(alturaT)) {
                fieldAltura.error = "Required."
                return@setOnClickListener
            }
            val altura = alturaT.toInt()
            val uniqueID : String = UUID.randomUUID().toString()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(ContentValues.TAG, "createUserWithEmail:success")
                        val intent = Intent(this, MainActivity::class.java)
                        if(auth.currentUser != null) {
                            val user = User(uniqueID, name, email, altura, peso)
                            databaseRefined.child("users").child(auth.currentUser!!.uid)
                                .setValue(user)
                        }
                        startActivity(intent)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }

    }
}