package com.example.firebase_project

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*

class itemkids : AppCompatActivity() {

    private var imgsubdish: ImageView? = null
    private  var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_itemkids)

        storageReference = FirebaseStorage.getInstance().reference

        val fabAddButton: FloatingActionButton = findViewById(R.id.idFABAdd)
        fabAddButton.setOnClickListener {
            val dialog = Dialog(this@itemkids)
            dialog.setContentView(R.layout.dailog_box_foodimage)
            dialog.window?.setBackgroundDrawableResource(R.drawable.edit_text_background_border)

            imgsubdish = dialog.findViewById(R.id.imgdlgdish)

            val cardviewDailog: CardView = dialog.findViewById(R.id.cardviewDailog)
            cardviewDailog.setOnClickListener {
                val options = arrayOf<CharSequence>("Take Photo", "Choose photo from Gallery", "Cancel")
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
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                0 -> {
                    val selectedImage = data?.extras?.get("data") as? Bitmap
                    selectedImage?.let {
                        imgsubdish?.setImageBitmap(it)
                        uploadImageToFirebase(it)
                    }
                }
                1 -> {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let { uri ->
                        imgsubdish?.setImageURI(uri)
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        uploadImageToFirebase(bitmap)
                    }
                }
            }
        }
    }


    private fun uploadImageToFirebase(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        // Create a unique filename for the image
        val filename = UUID.randomUUID().toString()

        // Create a reference to the Firebase storage location
        val imageRef = storageReference!!.child("images/$filename.jpg")

        // Upload the image data to Firebase Storage
        val uploadTask = imageRef.putBytes(imageData)
        uploadTask.addOnSuccessListener {
            // Image upload successful
            // You can save the image URL to the Firebase Realtime Database if needed
        }.addOnFailureListener {
            // Image upload failed
        }
    }
}
