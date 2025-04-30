package com.example.bookmytrip

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class WelcomeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)

        val welcomeImage = findViewById<CircleImageView>(R.id.profileImageView)
        val welcomeText = findViewById<TextView>(R.id.welcomeMessage)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        val dbHelper = UserDatabaseHelper(this)
        val user = dbHelper.getLastUser()

        if (user != null) {
            welcomeText.text = "Welcome, ${user.username}!"

            if (!user.profileImagePath.isNullOrEmpty()) {
                welcomeImage.setImageURI(Uri.parse(user.profileImagePath))
            } else {
                welcomeImage.setImageResource(R.drawable.default_profile)
            }

            // Fallback to internal image if saved
            val file = File(filesDir, "profile_image.png")
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                welcomeImage.setImageBitmap(bitmap)
            }
        } else {
            Toast.makeText(this, "No user found!", Toast.LENGTH_SHORT).show()
        }

        logoutButton.setOnClickListener {
            dbHelper.deleteAllUsers()

            val file = File(filesDir, "profile_image.png")
            if (file.exists()) {
                file.delete()
            }

            Toast.makeText(this, "Logged out!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ActivityProfile::class.java)
            startActivity(intent)
            finish()
        }
    }
}