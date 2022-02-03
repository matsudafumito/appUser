package com.example.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ReviewAnnouncement : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_announcement)

        val homeBtn: Button = findViewById(R.id.buttonHome)
        homeBtn.setOnClickListener {
            val intent = Intent(this,User::class.java)
            startActivity(intent)
        }
    }
}