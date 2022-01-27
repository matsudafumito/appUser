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
import java.net.URI

class UserConfirmEditEvaluation : AppCompatActivity() {

    companion object{
        const val deleteEvaluationReqId = 12
        const val editEvaluationReqId = 13
    }

    val uri = WsClient.serverRemote
    val client = EditDeleteEvaluationWsClient(this, uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_confirm_edit_evaluation)
    }

    override fun onResume() {
        super.onResume()
        client.connect()
        val type = intent.getStringExtra("type")
        val evaluationId = intent.getIntExtra("evaluation_id", -1)
        val restaurantName = intent.getStringExtra("restaurant_name")
        val evaluationGrade = intent.getIntExtra("evaluation_grade", -1)
        val evaluationComment = intent.getStringExtra("evaluation_comment")

        val txtRestaurantName: TextView = findViewById(R.id.textBoxRestaurantName)
        val txtEvaluationGrade: TextView = findViewById(R.id.textBoxEvaluationGrade)
        val txtEvaluationComment: TextView = findViewById(R.id.textBoxComment)

        val txtGuide: TextView = findViewById(R.id.guideText)
        val buttonConfirm: Button = findViewById(R.id.buttonConfirm)

        if(type == "edit"){
            txtGuide.text = "以下の内容で食べログを更新します"
        }else if(type == "delete"){
            txtGuide.text = "以下の食べログを削除します"
        }
        txtGuide.visibility = View.VISIBLE

        txtRestaurantName.text = restaurantName
        txtEvaluationGrade.text = evaluationGrade.toString()
        txtEvaluationComment.text = evaluationComment

        buttonConfirm.setOnClickListener {
            if(type == "edit"){
                client.sendEditEvaluationReq(evaluationId, User.globalToken, evaluationGrade, evaluationComment.toString())
            }else if(type == "delete"){
                client.sendDeleteEvaluationReq(evaluationId, User.globalToken)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        client.close(WsClient.NORMAL_CLOSURE)
    }
}


class EditDeleteEvaluationWsClient(private val activity: Activity, uri: URI) : WsClient(uri){

    private val errorDisplay: TextView by lazy {
        activity.findViewById(R.id.errorDisplay)
    }

    fun sendEditEvaluationReq(evaluationId: Int, token: String,
                              evaluationGrade: Int, evaluationComment: String){

        val evaluationData = JSONObject()
        evaluationData.put("evaluation_id", evaluationId)
        evaluationData.put("evaluation_grade", evaluationGrade)
        evaluationData.put("evaluation_comment", evaluationComment)
        val params = JSONObject()
        params.put("type", "change")
        params.put("token", token)
        params.put("evaluationData", evaluationData)
        val request = this.createJsonrpcReq("updateInfo/evaluation", UserConfirmEditEvaluation.editEvaluationReqId, params)
        try {
            if(this.isClosed){
                this.reconnect()
            }
            this.send(request.toString())
        }catch (ex:Exception){
            Log.i(javaClass.simpleName, "send failed")
            Log.i(javaClass.simpleName, "$ex")
            errorDisplay.text = "インターネットに接続されていません"
            errorDisplay.visibility = View.VISIBLE
        }
    }

    fun sendDeleteEvaluationReq(evaluationId: Int, token: String){
        val params = JSONObject()
        params.put("type", "delete")
        params.put("token", token)
        params.put("evaluation_id", evaluationId)
        val request = this.createJsonrpcReq("updateInfo/evaluation", UserConfirmEditEvaluation.deleteEvaluationReqId, params)
        try {
            if(this.isClosed){
                this.reconnect()
            }
            this.send(request.toString())
        }catch (ex:Exception){
            Log.i(javaClass.simpleName, "send failed")
            Log.i(javaClass.simpleName, "$ex")
            errorDisplay.text = "インターネットに接続されていません"
            errorDisplay.visibility = View.VISIBLE
        }
    }

    override fun onMessage(message: String?) {
        super.onMessage(message)

        Log.i(javaClass.simpleName, "msg arrived")
        Log.i(javaClass.simpleName, "$message")

        val wholeMsg = JSONObject("$message")
        val resId: Int = wholeMsg.getInt("id")
        val result: JSONObject = wholeMsg.getJSONObject("result")
        val status: String = result.getString("status")

        if(resId == UserConfirmEditEvaluation.editEvaluationReqId){
            if(status == "success"){
                val intent = Intent(activity, ShowResult::class.java)
                intent.putExtra("message", "食べログを編集しました")
                intent.putExtra("transitionBtnMessage", "ホームへ")
                intent.putExtra("isBeforeLogin", false)
                this.close(NORMAL_CLOSURE)
                activity.startActivity(intent)

            }else if(status == "error"){
                activity.runOnUiThread {
                    errorDisplay.text = "食べログを編集できませんでした"
                    errorDisplay.visibility = View.VISIBLE
                }
            }

        }else if(resId == UserConfirmEditEvaluation.deleteEvaluationReqId){
            if(status == "success"){
                val intent = Intent(activity, ShowResult::class.java)
                intent.putExtra("message", "食べログを削除しました")
                intent.putExtra("transitionBtnMessage", "ホームへ")
                intent.putExtra("isBeforeLogin", false)
                this.close(NORMAL_CLOSURE)
                activity.startActivity(intent)

            }else if(status == "error"){
                activity.runOnUiThread {
                    errorDisplay.text = "食べログを編集できませんでした"
                    errorDisplay.visibility = View.VISIBLE
                }

            }
        }
    }
}