package com.example.firebase_project

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase_project.Adapter.ChildAdapter
import com.example.firebase_project.dataclass.ChildItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class itemkids : AppCompatActivity() {

    private var imgsubdish: ImageView? = null
    private var submitButton: Button? = null
    private var cancelButton: Button? = null
    private var edttittle: EditText? = null
    private var searchviewitem: SearchView? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var usersRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private val childList = ArrayList<ChildItem>()
    private lateinit var adapter: ChildAdapter
    private lateinit var uid: String
    private lateinit var progressDialog: ProgressDialog


    private var selectedImageUri: Uri? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itemkids)

        storageRef = FirebaseStorage.getInstance().reference
        uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("Item")

//for progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Sending data...")
        progressDialog.setCancelable(false)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        fetchChildItemsFromFirebase(uid)

        searchviewitem = findViewById(R.id.searchviewitem)
        searchviewitem!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText)
                return true
            }
        })

        val fabAddButton: FloatingActionButton = findViewById(R.id.idFABAdd)
        fabAddButton.setOnClickListener {
            val dialog = Dialog(this@itemkids)
            dialog.setContentView(R.layout.dailog_box_foodimage)
            dialog.window?.setBackgroundDrawableResource(R.drawable.edit_text_background_border)

            imgsubdish = dialog.findViewById(R.id.imgdlgdish)
            edttittle = dialog.findViewById(R.id.edttittle)
            val cardviewDailog: CardView = dialog.findViewById(R.id.cardviewDailog)
            submitButton = dialog.findViewById(R.id.submitButton)
            cancelButton = dialog.findViewById(R.id.cancelButton)

            cardviewDailog.setOnClickListener {
                val options =
                    arrayOf<CharSequence>("Take Photo", "Choose photo from Gallery", "Cancel")
                val builder = AlertDialog.Builder(this@itemkids)
                builder.setTitle("Choose an option")
                builder.setItems(options) { dialog, item ->
                    when (item) {
                        0 -> {
                            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(takePicture, 0)
                        }
                        1 -> {
                            val pickPhoto = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            )
                            startActivityForResult(pickPhoto, 1)
                        }
                        2 -> dialog.dismiss()
                    }
                }
                builder.show()
            }

            cancelButton!!.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Do you want to Cancel?")
                builder.setTitle("Alert!")
                builder.setCancelable(false)
                builder.setPositiveButton("Yes") { dialog, which -> finish() }
                builder.setNegativeButton("No") { dialog, which -> dialog.cancel() }
                val alertDialog = builder.create()
                alertDialog.show()
            }

            submitButton!!.setOnClickListener {
                val title = edttittle!!.text.toString()

                if (title.isNotEmpty() && selectedImageUri != null) {
                    progressDialog.show() // Show the loader

                    val childRef = usersRef.child("").push()

                    // Upload image to Firebase Storage
                    val imageRef = storageRef.child("images/${childRef.key}.png")
                    val uploadTask = imageRef.putFile(selectedImageUri!!)

                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        imageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val imageUrl = task.result.toString()

                            val childItem = ChildItem(uid, title, imageUrl)
                            childRef.setValue(childItem)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Data saved to Firebase",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    progressDialog.dismiss() // Hide the loader on success
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Failed to save data to Firebase",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    progressDialog.dismiss() // Hide the loader on failure
                                }
                        } else {
                            Toast.makeText(
                                this,
                                "Failed to upload image to Firebase Storage",
                                Toast.LENGTH_SHORT
                            ).show()
                            progressDialog.dismiss() // Hide the loader on failure
                        }
                    }
                } else {
                    Toast.makeText(this, "Please fill in both title and image", Toast.LENGTH_SHORT)
                        .show()
                }
            }

//            submitButton!!.setOnClickListener {
//                val title = edttittle!!.text.toString()
//
//                if (title.isNotEmpty() && selectedImageUri != null) {
//                    val childRef = usersRef.child("").push()
//
//                    // Upload image to Firebase Storage
//                    val imageRef = storageRef.child("images/${childRef.key}.png")
//                    val uploadTask = imageRef.putFile(selectedImageUri!!)
//
//                    uploadTask.continueWithTask { task ->
//                        if (!task.isSuccessful) {
//                            task.exception?.let {
//                                throw it
//                            }
//                        }
//                        imageRef.downloadUrl
//                    }.addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            val imageUrl = task.result.toString()
//
//                            val childItem = ChildItem(uid, title, imageUrl)
//                            childRef.setValue(childItem)
//                                .addOnSuccessListener {
//                                    Toast.makeText(
//                                        this,
//                                        "Data saved to Firebase",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                                .addOnFailureListener {
//                                    Toast.makeText(
//                                        this,
//                                        "Failed to save data to Firebase",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            dialog.dismiss()
//                        } else {
//                            Toast.makeText(
//                                this,
//                                "Failed to upload image to Firebase Storage",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                } else {
//                    Toast.makeText(this, "Please fill in both title and image", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }


            dialog.show()
        }

        recyclerView = findViewById(R.id.rv_sub_category)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = ChildAdapter(childList)
        recyclerView.adapter = adapter
    }

    private fun performSearch(query: String?) {
        val filteredList = ArrayList<ChildItem>()

        if (!query.isNullOrBlank()) {
            for (childItem in childList) {
                if (childItem.title.contains(query, ignoreCase = true)) {
                    filteredList.add(childItem)
                }
            }
        } else {
            filteredList.addAll(childList)
        }

        adapter.updateList(filteredList)
    }

    private fun fetchChildItemsFromFirebase(userUid: String) {
        val query = usersRef.orderByChild("uid").equalTo(userUid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                childList.clear()
                for (childSnapshot in dataSnapshot.children) {
                    val childItem = childSnapshot.getValue(ChildItem::class.java)
                    childItem?.let {
                        childList.add(childItem)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error if fetching data fails
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> { // Image captured from camera
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imgsubdish?.setImageBitmap(imageBitmap)
                    selectedImageUri = getImageUri(imageBitmap)
                }
                1 -> { // Image selected from gallery
                    val imageUri = data?.data
                    imgsubdish?.setImageURI(imageUri)
                    selectedImageUri = imageUri
                }
            }
        }
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }
}
