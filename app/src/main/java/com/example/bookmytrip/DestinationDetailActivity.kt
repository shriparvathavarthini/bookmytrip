package com.example.bookmytrip

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DestinationDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destination_detail)

        val name = intent.getStringExtra("destination_name")
        val description = intent.getStringExtra("destination_description")
        val imageResId = intent.getIntExtra("destination_image", R.drawable.destination1)

        val titleText = findViewById<TextView>(R.id.destinationTitle)
        val descText = findViewById<TextView>(R.id.destinationDescription)
        val videoView = findViewById<VideoView>(R.id.destinationVideo)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.destination_video) // replace with your video name

        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener { it.isLooping = true }
        videoView.start()

        titleText.text = name
        descText.text = description

    }
}
