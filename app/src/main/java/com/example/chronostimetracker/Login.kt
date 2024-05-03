package com.example.chronostimetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
class Login : AppCompatActivity() {
    private lateinit var register: Button
    private lateinit var login: Button
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        register = findViewById(R.id.btnRegister)
        login = findViewById(R.id.btnLogin)
        username = findViewById(R.id.edtUsername)
        password = findViewById(R.id.edtPassword)
        auth = Firebase.auth
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        login.setOnClickListener() {
            val loginEmail = username.text.toString()
            val loginPassword = password.text.toString()
            userLogin(loginEmail, loginPassword)
        }
        register.setOnClickListener() {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)

        }
    }
    private fun userLogin(loginEmail: String, loginPassword: String) {
        auth.signInWithEmailAndPassword(loginEmail, loginPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext,"Login Successful", Toast.LENGTH_LONG).show()
                    val intent = Intent( this@Login,ListOfEntries::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Login failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

    }
}