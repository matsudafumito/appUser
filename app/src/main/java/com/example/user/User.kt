package com.example.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class User : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
    }

    override fun onResume() {
        super.onResume()

        val buttonToHome: Button = findViewById(R.id.buttonHome)
        val buttonToSearchRestaurant: Button = findViewById(R.id.buttonSearchRestaurant)
        val buttonToSetting: Button = findViewById(R.id.buttonSetting)

        buttonToHome.setOnClickListener {
            //doNothing
        }

        buttonToSearchRestaurant.setOnClickListener {
            TODO("not yet implemented")
        }

        buttonToSetting.setOnClickListener {
            TODO("not yet implemented")
        }

    }

}