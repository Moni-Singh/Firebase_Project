package com.example.firebase_project

import android.annotation.SuppressLint
import android.app.Person
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.firebase_project.dataclass.Receipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.*

class add_item : AppCompatActivity() {

    private lateinit var edtTime :EditText
    private var btnsaveItem:ImageButton? = null
    private var edtIngredients:EditText? = null
    private var edtInstructions:EditText? = null
    private var edtcategory:EditText? =  null
    private var edtServing:Spinner? =  null

    private lateinit var uid: String
    private lateinit var storageRef: StorageReference
    private lateinit var itemRef: DatabaseReference


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)



        storageRef = FirebaseStorage.getInstance().reference
        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""


        val database = FirebaseDatabase.getInstance()
        itemRef = database.getReference("Recipe")






        val person = resources.getStringArray(R.array.Person)

        edtServing = findViewById(R.id.edtServing)
         if(edtServing != null){
             val adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item, person)
             edtServing!!.adapter = adapter


             edtServing!!.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
                 override fun onItemSelected(
                     parent: AdapterView<*>?,
                     view: View?,
                     position: Int,
                     id: Long
                 ) {
                   Toast.makeText(applicationContext,"selected",Toast.LENGTH_SHORT).show()

                 }

                 override fun onNothingSelected(parent: AdapterView<*>?) {
                     TODO("Not yet implemented")
                 }

             }
         }
        edtcategory = findViewById(R.id.edtcategory)
        edtInstructions = findViewById(R.id.edtInstructions)
        edtIngredients = findViewById(R.id.edtIngredients)
        btnsaveItem = findViewById(R.id.btnsaveItem)
        btnsaveItem!!.setOnClickListener {
            val ingredients: String = edtIngredients!!.text.toString()
            val instruction: String = edtInstructions!!.text.toString()
            val category: String = edtcategory!!.text.toString()
            val person: String = edtServing!!.selectedItem.toString()
            val time: String = edtTime!!.text.toString()

            // Create a unique key for the recipe
            val recipeKey = itemRef.push().key

            // Create a Recipe object with the data
            val recipe = Receipe(category, ingredients, instruction, time, person,uid)

            // Insert the recipe into the database
            if (recipeKey != null) {
                itemRef.child(recipeKey).setValue(recipe)
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, "Recipe added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    println("$recipe")
                    // Finish the activity after adding the recipe
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "Failed to add recipe", Toast.LENGTH_SHORT).show()
                    }
            }
        }


        edtTime = findViewById(R.id.edtTime)
        edtTime.setOnClickListener {
            showTimePickerDialog()
        }


    }

    private fun showTimePickerDialog() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                val selectedTime = formatTime(selectedHour, selectedMinute)
                edtTime.setText(selectedTime)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val simpleDateFormat = SimpleDateFormat("mm", Locale.getDefault())
        return simpleDateFormat.format(calendar.time)

    }
}