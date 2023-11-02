package com.example.notesapplication
import android.Manifest

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.notesapplication.databinding.ActivityAddNoteBinding
import java.io.ByteArrayOutputStream

class AddNoteActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var db: NotesDatabaseHelper

    private val PickImageRequest =1
    private val CaptureImageRequest = 2  // Request code for capturing an image from the camera

    private  var imagePath= Uri.parse("android.resource://com.example.notesapplication/${R.drawable.baseline_image_24}") // Initialize imagePath as nullable

    // Define permission request codes
    private val CAMERA_PERMISSION_REQUEST = 101
    private val STORAGE_PERMISSION_REQUEST = 102


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db= NotesDatabaseHelper(this)


        binding.btnSave.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()

            if (title.isNotEmpty() || content.isNotEmpty() || imagePath!=null) {
                    val note = Note(0, title, content, imagePath?.toString())
                    db.insertNote(note)
                    finish()
                    Toast.makeText(this, "Note Saved Successfully $imagePath", Toast.LENGTH_SHORT).show()


            } else {
                Toast.makeText(this, "Please Provide Information In At Least One Text Field or Select An Image", Toast.LENGTH_SHORT).show()
            }



        }

        binding.imageView.setOnClickListener {
            showImageSourceDialog()
        }
    }

    private fun showImageSourceDialog() {
        val item= arrayOf("Pick an Image from Your Gallery", "Snap a Fresh Photo Using Your Camera")
        val dialog=AlertDialog.Builder(this)
            .setTitle("Select Image Source")
            .setItems(item){dialog,which ->
                when (which) {
                    0 -> {
                        // Check and request storage permission
                        if (hasStoragePermission()) {
                            chooseImage()
                        } else {
                            requestStoragePermission()
                        }
                    }
                    1 -> {
                        if (hasCameraPermission()) {
                            captureImage()
                        } else {
                            requestCameraPermission()
                        }
                    }
                }
                dialog.dismiss()
            }
            .show()
    }
    // Check if camera permission is granted
    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    // Request camera permission
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
    }

    // Check if storage permission is granted
    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    // Request storage permission
    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST)
    }

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,CaptureImageRequest)
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,PickImageRequest)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==PickImageRequest&&resultCode== Activity.RESULT_OK&& data!=null&&data.data!=null){
            imagePath = data.data!!
            binding.imageView.setImageURI(imagePath)


        }else if (requestCode==CaptureImageRequest&&resultCode==Activity.RESULT_OK&&data!=null){
            val imageBitmap = data.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                // Convert the Bitmap to a Uri
                imagePath = getImageUriFromBitmap(imageBitmap)
                binding.imageView.setImageURI(imagePath)
            }
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }



}



