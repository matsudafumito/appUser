package com.example.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class UserShowCurrentEvaluations : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_show_current_evaluations)
    }

    override fun onResume() {
        super.onResume()
        val token = intent.getStringExtra("token")
        val user_id = intent.getIntExtra("user_id", -1)
        Log.i(javaClass.simpleName, "token: $token")
        Log.i(javaClass.simpleName, "user_id: $user_id")
    }
}