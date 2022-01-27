package com.example.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class UserShowEvaluationDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_show_evaluation_detail)
    }

    override fun onResume() {
        super.onResume()
        val evaluationId = intent.getIntExtra("evaluation_id", -1)
        val restaurantId = intent.getIntExtra("restaurant_id", -1)
        val restaurantName = intent.getStringExtra("restaurant_name")
        val evaluationGrade = intent.getIntExtra("evaluation_grade", -1)
        val evaluationComment = intent.getStringExtra("evaluation_comment")

        val txtRestaurantName: TextView = findViewById(R.id.textBoxRestaurantName)
        val txtEvaluationGrade: TextView = findViewById(R.id.textBoxEvaluationGrade)
        val txtEvaluationComment: TextView = findViewById(R.id.textBoxComment)

        txtRestaurantName.text = restaurantName
        txtEvaluationGrade.text = evaluationGrade.toString()
        txtEvaluationComment.text = evaluationComment

        val buttonToEdit: Button = findViewById(R.id.buttonEditEvaluation)

        buttonToEdit.setOnClickListener {
            val intent = Intent(this@UserShowEvaluationDetail, UserEditEvaluation::class.java)
            intent.putExtra("evaluation_id", evaluationId)
            intent.putExtra("restaurant_name", restaurantName)
            intent.putExtra("evaluation_grade", evaluationGrade)
            intent.putExtra("evaluation_comment", evaluationComment)
            startActivity(intent)
        }
    }
}