package com.example.bookmytrip

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val parisCard = findViewById<LinearLayout>(R.id.card_paris)
        val malaysiaCard=findViewById<LinearLayout>(R.id.card_malaysia)
        val singaporeCard=findViewById<LinearLayout>(R.id.card_singapore)
        val navView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        // You can initialize click listeners or navigation here
        parisCard.setOnClickListener {
            val intent = Intent(this, DestinationDetailActivity::class.java)
            intent.putExtra("destination_name", "Paris, France")
            intent.putExtra("destination_description", "The city of lights and love!")
            intent.putExtra("destination_image", R.drawable.destination1) // Make sure this image exists in drawable
            startActivity(intent)
        }
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_bookings -> {
                    val intent = Intent(this, BookTripActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_explore -> {
                    val intent = Intent(this,ExploreActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {
                    val dbHelper = UserDatabaseHelper(this)
                    val user = dbHelper.getLastUser()

                    val intent = if (user != null) {
                        Intent(this, WelcomeActivity::class.java)
                    } else {
                        Intent(this, ActivityProfile::class.java)
                    }
                    startActivity(intent)
                    true
                }
                // Add other tabs if needed
                else -> false
            }
        }

    }
}
