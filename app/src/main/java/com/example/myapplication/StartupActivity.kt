package com.example.myapplication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class StartupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.startupactivity)
        supportActionBar?.hide()


        auth = Firebase.auth

        val fieldEmail = findViewById<EditText>(R.id.editTextUsername)
        val fieldPassword = findViewById<EditText>(R.id.editTextTextPassword)
        val signup = findViewById<TextView>(R.id.newUser)


        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
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
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInWithEmail:success")
                        Toast.makeText(
                            this,
                            "Authentication success.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            this,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }
        }

        signup.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}