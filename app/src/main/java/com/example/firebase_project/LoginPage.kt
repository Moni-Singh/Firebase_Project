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

    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnlogin: Button
    private lateinit var btnotp1: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        email = findViewById(R.id.loginEmail)
        password = findViewById(R.id.loginPassword)

        sharedPreferences = getSharedPreferences("shared_Preference", MODE_PRIVATE)
        val isLogin = sharedPreferences.getBoolean("isLogin", false)

        if (isLogin) {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }

        btnotp1 = findViewById(R.id.btnotp1)
        btnotp1.setOnClickListener {
            val intent = Intent(this@LoginPage, PhoneActivity::class.java)
            startActivity(intent)
        }

        btnlogin = findViewById(R.id.btnLogin)
        btnlogin.setOnClickListener {
            loginUser()
        }

        auth = FirebaseAuth.getInstance()

        val btnregistpage: Button = findViewById(R.id.rgisetruserbtn)
        btnregistpage.setOnClickListener {
            val intent = Intent(this@LoginPage, RegisterPage::class.java)
            startActivity(intent)
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$"
        return password.matches(passwordRegex.toRegex())
    }

    private fun loginUser() {
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()

        if (emailStr.isBlank()) {
            Toast.makeText(this, "Email should not be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isPasswordValid(passwordStr)) {
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

        auth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val editor = sharedPreferences.edit()
                editor.putString("email", emailStr)
                editor.putString("password", passwordStr)
                editor.putBoolean("isLogin", true)
                editor.apply()

                Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()

                val intent = Intent(applicationContext, Dashboard::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }
    }
}
