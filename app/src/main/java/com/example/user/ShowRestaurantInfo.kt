package com.example.user

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ShowRestaurantInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_search_store_result)
    }

    override fun onResume() {
        super.onResume()

        val result = JSONObject(intent.getStringExtra("result"))

        val restaurant_name = findViewById<TextView>(R.id.show_restaurant_info_name)
        val restaurant_address = findViewById<TextView>(R.id.show_restaurant_info_address)
        val restaurant_email_address = findViewById<TextView>(R.id.show_restaurant_info_email_address)
        val restaurant_time_open = findViewById<TextView>(R.id.show_restaurant_info_time_open)
        val restaurant_time_close = findViewById<TextView>(R.id.show_restaurant_info_time_close)

        restaurant_name.text = result.getString("restaurant_name")
        restaurant_address.text = result.getString("address")
        restaurant_email_address.text = result.getString("email_addr")
        restaurant_time_open.text = "始業: ${result.getString("time_open")}"
        restaurant_time_close.text = "終業: ${result.getString("time_close")}"
    }
}