package com.example.user

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView

class Evaluation(val evaluationId: Int, val restaurantId: Int, val restaurantName: String,
                 val evaluationGrade: Int, val evaluationComment: String)

class EvaluationListAdapter(val context: Context, val evaluationList: MutableList<Evaluation>) : BaseAdapter(){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_user_evaluation, null)
        val restaurantName = view.findViewById<TextView>(R.id.restaurant_name)
        val evaluationGrade = view.findViewById<TextView>(R.id.grade)
        val buttonToDetail = view.findViewById<Button>(R.id.buttonToDetail)

        val evaluation = evaluationList[position]

        restaurantName.text = evaluation.restaurantName
        evaluationGrade.text = evaluation.evaluationGrade.toString()

        buttonToDetail.setOnClickListener {
            val intent = Intent(context, UserShowEvaluationDetail::class.java)
            intent.putExtra("evaluation_id", evaluation.evaluationId)
            intent.putExtra("restaurant_id", evaluation.restaurantId)
            intent.putExtra("restaurant_name", evaluation.restaurantName)
            intent.putExtra("evaluation_grade", evaluation.evaluationGrade)
            intent.putExtra("evaluation_comment", evaluation.evaluationComment)
            context.startActivity(intent)
        }

        return view
    }

    override fun getItem(position: Int): Any{
        return evaluationList[position]
    }

    override fun getItemId(position: Int): Long{
        return 0
    }

    override fun getCount(): Int{
        return evaluationList.size
    }

}