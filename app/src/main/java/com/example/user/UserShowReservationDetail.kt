package com.example.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class UserShowReservationDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_show_reservation_detail)
    }

    override fun onResume() {
        super.onResume()
        val reservationId = intent.getIntExtra("reservation_id", -1)
        val restaurantId = intent.getIntExtra("restaurant_id", -1)
        val restaurantName = intent.getStringExtra("restaurant_name")
        val timeStart = intent.getIntExtra("time_start", -1)
        val timeEnd = intent.getIntExtra("time_end", -1)
        val strTimeStart = intent.getStringExtra("format_time_start")
        val strTimeEnd = intent.getStringExtra("format_time_end")
        val numPeople = intent.getIntExtra("num_people", -1)

        val textRestaurantName: TextView = findViewById(R.id.textBoxRestaurantName)
        val textTimeStart: TextView = findViewById(R.id.textBoxTimeStart)
        val textTimeEnd: TextView = findViewById(R.id.textBoxTimeEnd)
        val textNumPeople: TextView = findViewById(R.id.textBoxNumPeople)
        val buttonToDelete: Button = findViewById(R.id.buttonDeleteReservation)

        textRestaurantName.text = restaurantName
        textTimeStart.text = strTimeStart
        textTimeEnd.text = strTimeEnd
        textNumPeople.text = numPeople.toString()

        buttonToDelete.setOnClickListener {
            val intent = Intent(this@UserShowReservationDetail, ConfirmDeleteReservation::class.java)
            intent.putExtra("reservationId", reservationId)
            startActivity(intent)
        }

    }

}