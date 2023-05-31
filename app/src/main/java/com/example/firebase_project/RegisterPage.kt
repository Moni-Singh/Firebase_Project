package com.example.firebase_project

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.firebase_project.dataclass.Register
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class RegisterPage : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var username: EditText
    private lateinit var etPass: EditText
    private lateinit var etConfirmpass: EditText
    private lateinit var emailreg: EditText
    private lateinit var btnReg: Button
    private lateinit var rgisetrbtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)



        auth = Firebase.auth
        username = findViewById(R.id.regusername)
        emailreg = findViewById(R.id.regEmail)
        etPass = findViewById(R.id.loginPassword)
        etConfirmpass = findViewById(R.id.confirmPassword)
        btnReg = findViewById(R.id.btnRegister) as Button
        rgisetrbtn = findViewById(R.id.rgisetrbtn) as Button
        rgisetrbtn.setOnClickListener {
            startActivity(Intent(applicationContext,LoginPage::class.java))
        }
        FirebaseApp.initializeApp(this)



        btnReg.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser() {
        val name = username.text.toString()
        val email = emailreg.text.toString()
        val password = etPass.text.toString()
        val confirmPassword = etConfirmpass.text.toString()




        if (email.isBlank()) {
            Toast.makeText(this, "please enter your email", Toast.LENGTH_SHORT).show()
            return
        }
        if (name.isBlank()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 8) {
            Toast.makeText(this, "Minimum password should be 8 character", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "password should be same", Toast.LENGTH_SHORT).show()
            return
        }
        var progressDialog = ProgressDialog(this@RegisterPage)
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Please wait")
        progressDialog.show()


        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener (this){

            if(it.isSuccessful){

                val user = Register(name,email,image="", bioinfo = "", gender = "", birthday = "", education = "")

                val userId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""


                val userData = HashMap<String, Any>()
                userData["email"] = email
                userData["name"] = name
                userData["uid"] = userId
                userData["image"] = ""
                userData["bioinfo"] = ""
                userData["gender"] = ""
                userData["birthday"] = ""
                userData["education"] = ""


                val database: FirebaseDatabase = FirebaseDatabase.getInstance()
                val usersRef: DatabaseReference = database.getReference("Users")

                usersRef.child(userId).setValue(userData).addOnCompleteListener { dataTask ->
                    if (dataTask.isSuccessful){
                        println("uid = $userId")
                        println("user = $user")
                        progressDialog.dismiss()
                        startActivity(Intent(applicationContext, LoginPage::class.java))
                    }else{
                        Toast.makeText(applicationContext,"Failed",Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                Toast.makeText(applicationContext,"Unsuccessfull",Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        }
    }
}
