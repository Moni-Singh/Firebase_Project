package com.example.firebase_project

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase_project.dataclass.Register
import com.example.newproject.DataClass.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.*

class Profile : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var button: Button

    private var imageUrl: String = ""

    private lateinit var nameEditText: EditText
    private lateinit var bioinfoEditText: EditText
    private lateinit var birthdayEditText: EditText
    private lateinit var genderEditText: EditText
    private lateinit var educationEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var btnEditProfile: Button

    private lateinit var saveButton: Button
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var uid: String
    private var isEditMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("Users")
        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        nameEditText = findViewById(R.id.edtname)
        bioinfoEditText = findViewById(R.id.edtbioinfo)
        birthdayEditText = findViewById(R.id.edtbirthday)
        imageView = findViewById(R.id.imageView)
        button = findViewById(R.id.buttonLoadPicture)
        genderEditText = findViewById(R.id.edtgender)
        emailEditText = findViewById(R.id.edtEmail)
        btnEditProfile = findViewById(R.id.edtEditProfile)
        educationEditText = findViewById(R.id.edteducation)
        saveButton = findViewById(R.id.btnSaveProfile)

        // Load user data from the database
        loadUserData()
        //Edit Profile
        btnEditProfile.setOnClickListener {
            enableEditText()
            isEditMode = true
        }
        //For calender
        birthdayEditText.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->

                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    birthdayEditText.setText(dat)
                },

                year,
                month,
                day
            )

            datePickerDialog.show()
        }


        //Popup for gender
        genderEditText.setOnClickListener {

            val options = arrayOf("Male", "Female") // Options for selection
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Gender")
            builder.setItems(options) { _, which ->
                val selectedGender = options[which]
                genderEditText.setText(selectedGender)
            }
            builder.setNegativeButton("Cancel", null)
            val dialog = builder.create()
            dialog.show()


        }
        // Button click listener for picking an image
        button.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose photo from Gallery", "Cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Choose an option")
            builder.setItems(options) { dialog, item ->
                when (item) {
                    0 -> {
                        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(takePicture, 0)
                    }
                    1 -> {
                        val pickPhoto =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pickPhoto, 1)
                    }
                    2 -> dialog.dismiss()
                }
            }
            builder.show()
        }


        // Save button click listener
        saveButton.setOnClickListener {

            val name: String = nameEditText.text.toString()
            val email: String = emailEditText.text.toString()
            val bioinfo: String = bioinfoEditText.text.toString()
            val birthday: String = birthdayEditText.text.toString()
            val gender: String = genderEditText.text.toString()
            val education: String = educationEditText.text.toString()
            val imageUrl: String = this.imageUrl

            if (isEditMode) {
                if (validateInput()) {
                    isEditMode = false
                    disableEditText()

                    val user = UserProfile(imageUrl, name, email, bioinfo, gender, birthday, education)

                    // Update user data in the database
                    val userRef = usersRef.child(uid)
                    userRef.setValue(user).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                applicationContext,
                                "Data updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Failed to update data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(applicationContext, "Data saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(): Boolean {
        val name = nameEditText.text.toString().trim()
        val bioinfo = bioinfoEditText.text.toString().trim()
        val birthday = birthdayEditText.text.toString().trim()
        val gender = genderEditText.text.toString().trim()
        val education = educationEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()

        if (name.isEmpty()) {
            nameEditText.error = "Please enter a name"
            return false
        }

        if (bioinfo.isEmpty()) {
            bioinfoEditText.error = "Please enter bioinfo"
            return false
        }
        if (birthday.isEmpty()) {
            birthdayEditText.error = "Please enter birthdate"
            return false
        }
        if (gender.isEmpty()) {
            genderEditText.error = "Please enter gender"
            return false
        }
        if (education.isEmpty()) {
            educationEditText.error = "Please enter education"
            return false
        }
        if (email.isEmpty()) {
            emailEditText.error = "Please enter email"
            return false
        }

        // Add more validation rules for other fields if needed

        return true
    }

    fun enableEditText() {
        nameEditText.isEnabled = true
        bioinfoEditText.isEnabled = true
        birthdayEditText.isEnabled = true
        genderEditText.isEnabled = true
        educationEditText.isEnabled = true
        emailEditText.isEnabled = true
        button.isEnabled = true
        imageView.isEnabled = true
    }

    fun disableEditText() {
        nameEditText.isEnabled = false
        bioinfoEditText.isEnabled = false
        birthdayEditText.isEnabled = false
        genderEditText.isEnabled = false
        educationEditText.isEnabled = false
        emailEditText.isEnabled = false

        button.isEnabled = false
        imageView.isEnabled = false
    }

    private fun loadUserData() {
        disableEditText()
        val userRef = usersRef.child(uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(UserProfile::class.java)
                    if (user != null) {
                        // Populate views with the retrieved data
                        nameEditText.setText(user.name)
                        emailEditText.setText(user.email)
                        bioinfoEditText.setText(user.bioinfo)
                        birthdayEditText.setText(user.birthday)
                        genderEditText.setText(user.gender)
                        educationEditText.setText(user.education)
                        imageUrl = user.image
                        if (imageUrl.isNotEmpty()) {
                            Picasso.get().load(imageUrl).into(imageView)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> {
                    val imageUri = data?.data
                    if (imageUri != null) {
                        // Display the selected image using Picasso library
                        Picasso.get().load(imageUri).into(imageView)

                        // Upload the image to Firebase Storage and get the download URL
                        val imageRef = storageRef.child("images/$uid")
                        imageRef.putFile(imageUri)
                            .addOnSuccessListener { taskSnapshot ->
                                // Get the download URL of the uploaded image
                                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                                    val imageUrl = uri.toString()

                                    // Store the image URL in the database
                                    val userRef = usersRef.child(uid)
                                    userRef.child("image").setValue(imageUrl)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Image uploaded successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Failed to upload image",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    applicationContext,
                                    "Failed to upload image: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
        }
    }

}
