package com.example.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.lang.Exception
import java.net.URI

class ShowSearchStoreResult : AppCompatActivity() {

    companion object{
        const val getUserInfoId = 17000001
        var result =  JSONObject()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_search_store_result)
    }

    override fun onResume() {
        super.onResume()

        //データがおかしいと即死するので注意、いい書き方募集中
        result = JSONObject(intent.getStringExtra("result"))
        val store_list = ShowSearchStoreResult.result.getJSONArray("restaurants")
        var name = mutableListOf<String>()

        if (store_list.length() != 0) {
            for (index in 0 until store_list.length()) {
                val store: JSONObject = store_list.getJSONObject(index)
                name.add(store.getString("restaurant_name"))
            }
        }

        val listView = findViewById<ListView>(R.id.listView)

        //ArrayAdapter
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name)

        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val store: JSONObject = store_list.getJSONObject(position)
            val intent = Intent(this@ShowSearchStoreResult, ShowSerachStoreResult::class.java)

            intent.putExtra(
                "restaurantId",
                store_list.getJSONObject(position).getInt("restaurant_id")
            )
            intent.putExtra(
                "restaurantName",
                store_list.getJSONObject(position).getInt("restaurant_name")
            )
            intent.putExtra(
                "EmailAddress",
                store_list.getJSONObject(position).getString("email_address")
            )
            intent.putExtra("Address", store_list.getJSONObject(position).getString("address"))
            intent.putExtra("timeOpen", store_list.getJSONObject(position).getString("time_open"))
            intent.putExtra("timeClose", store_list.getJSONObject(position).getString("time_close"))
            intent.putExtra("features", store_list.getJSONObject(position).getString("features"))
            intent.putExtra(
                "holidays",
                store_list.getJSONObject(position).getJSONArray("holidays").toString()
            )
            intent.putExtra("token", User.globalToken)
            startActivity(intent)
        }
    }
}