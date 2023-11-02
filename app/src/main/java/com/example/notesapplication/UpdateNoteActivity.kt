package com.example.notesapplication

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.notesapplication.databinding.ActivityUpdateBinding
import java.io.ByteArrayOutputStream

class UpdateNoteActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUpdateBinding
    private lateinit var db: NotesDatabaseHelper
    private var noteId: Int=-1
    private var PickImageRequest=1
    private val CaptureImageRequest = 2  // Request code for capturing an image from the camera

    private var CameraRequest=101


    private  var updatedImagePath= Uri.parse("${R.drawable.baseline_image_24}") // Initialize imagePath as nullable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db= NotesDatabaseHelper(this)

        noteId=intent.getIntExtra("noteId",-1)
        if(noteId==-1){
            finish()
            return
        }else{
            val note = db.getNoteById(noteId)
            binding.updateTitleEditText.setText(note.title)
            binding.updateContentEditText.setText(note.content)



            binding.updatedImageView.setOnClickListener {
                showImageSourceDialog()
            }


            binding.updateSaveBtn.setOnClickListener {
                val newTitle= binding.updateTitleEditText.text.toString()
                val newContent= binding.updateContentEditText.text.toString()


                val updateNote=Note(noteId, newTitle, newContent, note.imagePath)
                db.updateNote(updateNote)
                finish()
                Toast.makeText(this,"Changes Saved",Toast.LENGTH_SHORT).show()

            }

        }


    }

    private fun showImageSourceDialog() {
        val item= arrayOf("Pick an Image from Your Gallery", "Snap a Fresh Photo Using Your Camera")
        val alertDialog=AlertDialog.Builder(this)
            .setTitle("Select Image Source")
            .setItems(item){
                dialog,which->
                when(which){
                    0 -> choseImage()
                    1->{
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

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent,CaptureImageRequest)
    }


    private fun requestCameraPermission() {
        return ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),CameraRequest )
    }

    private fun hasCameraPermission(): Boolean {

        return ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
    }

    private fun choseImage() {
        val intent = Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,PickImageRequest)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==PickImageRequest&&resultCode== Activity.RESULT_OK&& data!=null&&data.data!=null){
            updatedImagePath= data.data!!
            binding.updatedImageView.setImageURI(updatedImagePath)

            binding.updateSaveBtn.setOnClickListener {
                val newTitle= binding.updateTitleEditText.text.toString()
                val newContent= binding.updateContentEditText.text.toString()


                val updateNote=Note(noteId, newTitle, newContent,updatedImagePath?.toString())
                db.updateNote(updateNote)
                finish()
                Toast.makeText(this,"Changes Saved",Toast.LENGTH_SHORT).show()

            }
            }else if (requestCode==CaptureImageRequest&&resultCode== Activity.RESULT_OK&& data!=null){
            val imageBitmap = data.extras?.get("data") as Bitmap?

            if (imageBitmap != null) {
                updatedImagePath= getImageUriFromBitmap(imageBitmap)
                binding.updatedImageView.setImageURI(updatedImagePath)

                binding.updateSaveBtn.setOnClickListener {
                    val newTitle= binding.updateTitleEditText.text.toString()
                    val newContent= binding.updateContentEditText.text.toString()


                    val updateNote=Note(noteId, newTitle, newContent,updatedImagePath?.toString())
                    db.updateNote(updateNote)
                    finish()
                    Toast.makeText(this,"Changes Saved",Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap?): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }


}