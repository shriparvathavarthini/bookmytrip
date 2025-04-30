package com.example.bookmytrip

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Button
import com.example.bookmytrip.R
import com.example.bookmytrip.Trip
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import android.content.Intent
import android.content.ActivityNotFoundException
import android.widget.Button

class BookedTripsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booked_trips)

        val tripDetailsTextView = findViewById<TextView>(R.id.tripDetailsText)

        val sharedPref = getSharedPreferences("BookedTrips", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPref.getString("trip_list", null)

        if (json != null) {
            val type = object : TypeToken<List<Trip>>() {}.type
            val tripList: List<Trip> = gson.fromJson(json, type)
            val displayText = tripList.joinToString("\n\n") { trip ->
                "Destination: ${trip.destination}\nDate: ${trip.date}"
            }
            tripDetailsTextView.text = displayText
        } else {
            tripDetailsTextView.text = "No booked trips yet!"
        }
        val shareButton = findViewById<Button>(R.id.shareWhatsAppButton)

        shareButton.setOnClickListener {
            val tripDetails = tripDetailsTextView.text.toString()
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Hey! I've booked a trip:\n\n$tripDetails")
                type = "text/plain"
                `package` = "com.whatsapp" // force WhatsApp only
            }

            try {
                startActivity(sendIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
