package com.example.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject
import java.net.URI

class UserEditEvaluation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit_evaluation)
    }

    override fun onResume() {
        super.onResume()

        val evaluationId = intent.getIntExtra("evaluation_id", -1)
        val restaurantName = intent.getStringExtra("restaurant_name")
        val evaluationGrade = intent.getIntExtra("evaluation_grade", -1)
        val evaluationComment = intent.getStringExtra("evaluation_comment")

        val txtRestaurantName: TextView = findViewById(R.id.textBoxRestaurantName)
        val eTxtEvaluationGrade: EditText = findViewById(R.id.textBoxEvaluationGrade)
        val eTxtEvaluationComment: EditText = findViewById(R.id.textBoxComment)

        txtRestaurantName.text = restaurantName
        eTxtEvaluationGrade.setText(evaluationGrade.toString())
        eTxtEvaluationComment.setText(evaluationComment)

        val buttonEdit: Button = findViewById(R.id.buttonEdit)
        val buttonDelete: Button = findViewById(R.id.buttonDelete)

        buttonEdit.setOnClickListener {
            val intent = Intent(this@UserEditEvaluation, UserConfirmEditEvaluation::class.java)
            intent.putExtra("evaluation_id", evaluationId)
            intent.putExtra("restaurant_name", restaurantName)
            val grade: String = eTxtEvaluationGrade.text.toString()
            intent.putExtra("evaluation_grade", grade.toInt())
            intent.putExtra("evaluation_comment", eTxtEvaluationComment.text.toString())
            intent.putExtra("type", "edit")
            startActivity(intent)
        }

        buttonDelete.setOnClickListener {
            val intent = Intent(this@UserEditEvaluation, UserConfirmEditEvaluation::class.java)
            intent.putExtra("evaluation_id", evaluationId)
            intent.putExtra("restaurant_name", restaurantName)
            intent.putExtra("evaluation_grade", evaluationGrade)
            intent.putExtra("evaluation_comment", evaluationComment)
            intent.putExtra("type", "delete")
            startActivity(intent)
        }

    }
}

