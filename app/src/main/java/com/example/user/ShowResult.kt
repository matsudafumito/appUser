package com.example.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ShowResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result)
    }

    override fun onResume() {
        super.onResume()

        val message = intent.getStringExtra("message")
        val transitionBtnMessage = intent.getStringExtra("transitionBtnMessage")
        val isBeforeLogin = intent.getBooleanExtra("isBeforeLogin", true)

        val txtMessage: TextView = findViewById(R.id.message)
        val transitionBtn: Button = findViewById(R.id.transitionButton)

        txtMessage.text = message
        transitionBtn.text = transitionBtnMessage

        transitionBtn.setOnClickListener {
            intent = if(isBeforeLogin){
                Intent(this@ShowResult, UserLogin::class.java)
            }else{
                Intent(this@ShowResult, User::class.java)
            }
            startActivity(intent)
        }

    }
}