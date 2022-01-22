package com.example.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

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
        val timeStart = intent.getStringExtra("time_start")
        val timeEnd = intent.getStringExtra("time_end")
        val numPeople = intent.getIntExtra("num_people", -1)

        Log.i(javaClass.simpleName, "reservationId: $reservationId")
        Log.i(javaClass.simpleName, "restaurantId: $restaurantId")
        Log.i(javaClass.simpleName, "restaurantName: $restaurantName")
        Log.i(javaClass.simpleName, "timeStart: $timeStart")
        Log.i(javaClass.simpleName, "timeEnd: $timeEnd")
        Log.i(javaClass.simpleName, "numPeople: $numPeople")
    }

}