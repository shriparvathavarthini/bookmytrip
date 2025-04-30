package com.example.bookmytrip

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.hdodenhof.circleimageview.CircleImageView
import android.Manifest
import android.net.Uri
import java.io.File


class ActivityProfile : AppCompatActivity() {
    private lateinit var profileImage: ImageView
    private lateinit var editProfilePic: ImageButton
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var locationInput: EditText
    private lateinit var applyButton: Button
    private lateinit var imageUri: Uri
    private val CAMERA_PERMISSION_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private lateinit var dbHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        dbHelper = UserDatabaseHelper(this)

        // Initialize views
        profileImage = findViewById(R.id.profileImage)
        editProfilePic = findViewById(R.id.editProfilePic)
        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        locationInput = findViewById(R.id.locationInput)
        applyButton = findViewById(R.id.applyButton)

        // Camera click
        editProfilePic.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        // Apply button logic
        applyButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val location = locationInput.text.toString()

            Toast.makeText(this, "Saved: $username, $location", Toast.LENGTH_SHORT).show()
            // You can now store this data in Firebase, SQLite, SharedPreferences etc.
        }
        loadProfileImage()
        applyButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val location = locationInput.text.toString()

            if (username.isNotBlank() && password.isNotBlank()) {
                val dbHelper = UserDatabaseHelper(this)
                val profilePath = if (::imageUri.isInitialized) imageUri.toString() else null

                val result = dbHelper.insertUser(username, password, location, profilePath)

                if (result != -1L) {
                    Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val filename = "profile_image.png"
        val fileOutputStream = openFileOutput(filename, MODE_PRIVATE)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
        return filename
    }

    private fun loadProfileImage() {
        val file = getFileStreamPath("profile_image.png")
        if (file.exists()) {
            val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
            profileImage.setImageBitmap(bitmap)
        }
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            profileImage.setImageBitmap(imageBitmap)
            // Save the image
            saveImageToInternalStorage(imageBitmap)
        }
    }

}