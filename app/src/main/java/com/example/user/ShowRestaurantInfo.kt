package com.example.user

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ShowRestaurantInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_restaurant_info)
    }

    override fun onResume() {
        super.onResume()

        val info_restaurant_id = intent.getIntExtra("restaurantId", 0)

        val info_restaurant_name = intent.getStringExtra("restaurantName")
        val info_restaurant_address = intent.getStringExtra("Address")
        val info_restaurant_email_address = intent.getStringExtra("EmailAddress")
        val info_restaurant_time_open = intent.getStringExtra("timeOpen")
        val info_restaurant_time_close = intent.getStringExtra("timeClose")
        val info_restaurant_features = intent.getStringExtra("features")
        val info_restaurant_holidays = intent.getStringExtra("holidays")

        val restaurant_name = findViewById<TextView>(R.id.show_restaurant_info_name)
        val restaurant_address = findViewById<TextView>(R.id.show_restaurant_info_address)
        val restaurant_email_address = findViewById<TextView>(R.id.show_restaurant_info_email_address)
        val restaurant_time_open = findViewById<TextView>(R.id.show_restaurant_info_time_open)
        val restaurant_time_close = findViewById<TextView>(R.id.show_restaurant_info_time_close)
        val restaurant_features = findViewById<TextView>(R.id.show_restaurant_info_features)
        val restaurant_holidays = findViewById<TextView>(R.id.show_restaurant_info_holidays)

        restaurant_name.text = info_restaurant_name
        restaurant_address.text = info_restaurant_address
        restaurant_email_address.text = info_restaurant_email_address
        restaurant_time_open.text = "始業: " + info_restaurant_time_open
        restaurant_time_close.text = "終業: " + info_restaurant_time_close
        restaurant_features.text = info_restaurant_features
        restaurant_holidays.text = info_restaurant_holidays

        val restaurant_reservation = findViewById<Button>(R.id.show_restaurant_info_button_reservation)
        restaurant_reservation.setOnClickListener {
            val intent = Intent(this, RestaurantReservation::class.java)
            intent.putExtra("restaurant_id",info_restaurant_id)
            startActivity(intent)
        }
    }
}