package com.example.firebase_project

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginPage : AppCompatActivity() {


    lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnlogin: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)



        email = findViewById(R.id.loginEmail)
        password = findViewById(R.id.loginPassword)

        val sharedPreferences = getSharedPreferences("shared_Preference", MODE_PRIVATE)
        val islogin = sharedPreferences.getBoolean("islogin", false)

        if (islogin) {
            val i = Intent(this, Dashboard::class.java)
            startActivity(i)
            finish()
        }


        btnlogin = findViewById(R.id.btnLogin)
        btnlogin.setOnClickListener {
            loginuser()
        }
        auth = FirebaseAuth.getInstance()
        val btnregistpage: Button = findViewById(R.id.rgisetruserbtn)
        btnregistpage.setOnClickListener {
            val i = Intent(this@LoginPage, RegisterPage::class.java)
            startActivity(i)
        }
    }

    private fun loginuser() {


        val email = email.text.toString()
        val password = password.text.toString()


        fun isPasswordValid(password: String): Boolean {
            val passwordRegex =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
            return password.matches(passwordRegex.toRegex())
        }
        if (email.isBlank()) {
            Toast.makeText(this, "email should not be blank", Toast.LENGTH_SHORT).show()
            return
        }
        if (isPasswordValid(password)) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
        } else {
            // Password is invalid, show error message
            Toast.makeText(
                this,
                "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and be at least 8 characters long.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }


        val progressDialog = ProgressDialog(this@LoginPage)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Please wait")
        progressDialog.show()



        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
               //Shared preference
                val sharedPreferences = getSharedPreferences("shared_Preference", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString(email, email)
                editor.putString(password, password)
                editor.apply()

                editor.putBoolean("islogin", true)
                editor.apply()

                Toast.makeText(this, "Sucessfully login", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                startActivity(Intent(applicationContext, Dashboard::class.java))
                finish()
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()

            }

        }
    }
    }
