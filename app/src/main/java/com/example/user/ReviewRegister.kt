package com.example.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class ReviewRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_register)

        val txtName: EditText = findViewById(R.id.textBoxUserName)
        val txtTime: EditText = findViewById(R.id.reserve)

        val homeBtn: Button = findViewById(R.id.buttonRegister)
        homeBtn.setOnClickListener {
            val intent = Intent(this,User::class.java)
            startActivity(intent)
        }

    }
}