package com.example.bookmytrip

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar


class BookTripActivity : AppCompatActivity() {

    private lateinit var destinationInput: EditText
    private lateinit var dateInput: EditText
    private lateinit var bookTripButton: Button
    private lateinit var progressBar: ProgressBar
    private var selectedDate: String = ""
    private lateinit var firestore: FirebaseFirestore


    private val channelId = "trip_notification_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_trip)

        FirebaseApp.initializeApp(this)
        firestore = FirebaseFirestore.getInstance()


        destinationInput = findViewById(R.id.destinationInput)
        dateInput = findViewById(R.id.dateInput)
        bookTripButton = findViewById(R.id.bookTripButton)
        progressBar = findViewById(R.id.progressBar)
        MobileAds.initialize(
            this
        ) { initializationStatus: InitializationStatus? -> }
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf("6E3458AD2FAE84BC523B561DADA9D3C1"))
            .build()
        MobileAds.setRequestConfiguration(configuration);
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        dateInput.setOnClickListener {
            showDatePicker()
        }

        createNotificationChannel()
        val viewBookedTripsButton = findViewById<Button>(R.id.viewBookedTripsButton)
        viewBookedTripsButton.setOnClickListener {
            startActivity(Intent(this, BookedTripsActivity::class.java))
        }


        bookTripButton.setOnClickListener {
            val destination = destinationInput.text.toString()
            val date = selectedDate

            if (destination.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                val tripDetails = hashMapOf(
                    "destination" to destination,
                    "date" to date)
                firestore.collection("trips")
                    .add(tripDetails)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Trip saved to Firestore!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to save trip: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                val sharedPref = getSharedPreferences("BookedTrips", MODE_PRIVATE)
                val editor = sharedPref.edit()

                val gson = Gson()
                val trip = Trip(destination, date)

                val existingTripsJson = sharedPref.getString("trip_list", null)
                val tripList: MutableList<Trip> = if (existingTripsJson != null) {
                    val type = object : TypeToken<MutableList<Trip>>() {}.type
                    gson.fromJson(existingTripsJson, type)
                } else {
                    mutableListOf()
                }

                tripList.add(trip)
                val updatedJson = gson.toJson(tripList)
                editor.putString("trip_list", updatedJson)
                editor.apply()
                progressBar.visibility = ProgressBar.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    progressBar.visibility = ProgressBar.GONE
                    showDialog(destination, date)
                    showNotification(destination, date)
                    vibratePhone()
                }, 2000)
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"
            dateInput.setText(selectedDate)
        }, year, month, day)

        datePicker.show()
    }


    private fun showDialog(destination: String, date: String) {
        AlertDialog.Builder(this)
            .setTitle("Trip Booked!")
            .setMessage("Your trip to $destination on $date is confirmed.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showNotification(destination: String, date: String) {
        val channelId = "trip_notification_channel"
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Check for POST_NOTIFICATIONS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

            // Request the permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
            return // Exit here; show notification once permission is granted
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("Trip Booked Successfully!")
            .setContentText("Trip to $destination on $date is confirmed.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(notificationSound)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(1, notificationBuilder.build())
        }
    }


    private fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(0, 200, 100, 300)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createWaveform(pattern, -1)
            )
        } else {
            vibrator.vibrate(pattern, -1)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "com.example.bookmytrip.Trip Notifications"
            val descriptionText = "Channel for booking trip notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = descriptionText
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
