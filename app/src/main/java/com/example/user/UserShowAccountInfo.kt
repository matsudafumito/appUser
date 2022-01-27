package com.example.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception
import java.net.URI
import java.util.*
import kotlin.concurrent.schedule

class UserShowAccountInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_show_account_info)
    }

    override fun onResume() {
        super.onResume()

        val errorDisplay: TextView = findViewById(R.id.errorDisplay)
        val buttonChangeAccountInfo: Button = findViewById(R.id.buttonChangeAccountInfo)

        val token = User.globalToken
        val userName = User.globalUserName
        val userId = User.globalUserId
        val birthday = intent.getStringExtra("birthday")
        val gender = intent.getStringExtra("gender")
        val email = intent.getStringExtra("email")
        val address = intent.getStringExtra("address")

        val txtUserName: TextView = findViewById(R.id.textBoxUserName)
        val txtBirthday: TextView = findViewById(R.id.textBoxUserBirthday)
        val txtGender: TextView = findViewById(R.id.textBoxUserGender)
        val txtEmail: TextView = findViewById(R.id.textBoxUserEmail)
        val txtAddress: TextView = findViewById(R.id.textBoxUserAddress)

        txtUserName.text = userName
        txtBirthday.text = birthday
        txtGender.text = gender
        txtEmail.text = email
        txtAddress.text = address

        buttonChangeAccountInfo.setOnClickListener {
            val intent = Intent(this@UserShowAccountInfo, UserChangeAccountInfo::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("userName", userName)
            intent.putExtra("birthday", birthday)
            intent.putExtra("gender", gender)
            intent.putExtra("emailAddr", email)
            intent.putExtra("address", address)
            intent.putExtra("token", token)
            startActivity(intent)
        }
    }

}
