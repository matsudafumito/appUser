package com.example.user

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.TextView
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI

class UserShowCurrentEvaluations : AppCompatActivity() {

    companion object{
        const val getUserEvaluationsId = 10
        const val getRestaurantInfoId = 11
    }

    private val uri = WsClient.serverRemote
    private var client = EvaluationWsClient(this, uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_show_current_evaluations)
    }

    override fun onResume() {
        super.onResume()
        val token = User.globalToken
        val user_id = User.globalUserId
        Log.i(javaClass.simpleName, "token: $token")
        Log.i(javaClass.simpleName, "user_id: $user_id")
        client = EvaluationWsClient(this, uri)
        client.connect()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        client.close(WsClient.NORMAL_CLOSURE)
    }

    override fun onRestart() {
        super.onRestart()
        client.reconnect()
    }

}

class EvaluationWsClient(private val activity: Activity, uri: URI) : WsClient(uri){
    private val user_id = User.globalUserId
    private val token = User.globalToken

    val evaluationList = mutableListOf<Evaluation>()
    val evaluationIdsList = mutableListOf<Int>()
    val evaluatedRestaurantIdsList = mutableListOf<Int>()
    val evaluatedGradeList = mutableListOf<Int>()
    val evaluatedCommentList = mutableListOf<String>()

    var isAllDone = false
    var idx = 0

    private val errorDisplay: TextView by lazy {
        activity.findViewById(R.id.errorDisplay)
    }

    fun sendReqGetUserEvaluations(){
        val params = JSONObject()
        params.put("user_id", user_id)
        params.put("token", token)
        val request = createJsonrpcReq("getInfo/user/evaluations", UserShowCurrentEvaluations.getUserEvaluationsId, params)

        try {
            if(this.isClosed){
                this.reconnect()
            }
            this.send(request.toString())
        }catch (ex: Exception){
            Log.i(javaClass.simpleName, "send failed with reason $ex")
            activity.runOnUiThread {
                errorDisplay.text = "食べログ情報を取得できません"
                errorDisplay.visibility = View.VISIBLE
            }
        }

    }

    /**
     * this method will send single request(getInfo/restaurant/basic)
     * request params is specified in argument
     */
    private fun sendReqGetRestaurantInfo(restaurant_id: Int){
        val params = JSONObject()
        params.put("searchBy", "restaurant_id")
        params.put("restaurant_id", restaurant_id)
        params.put("token", this.token)
        val request = this.createJsonrpcReq("getInfo/restaurant/basic", UserShowCurrentEvaluations.getRestaurantInfoId, params)

        try {
            if(this.isClosed){
                this.reconnect()
            }
            this.send(request.toString())
        }catch (ex:Exception){
            Log.i(javaClass.simpleName, "send failed with reason $ex")
            activity.runOnUiThread {
                errorDisplay.text = "食べログ情報を取得できません"
                errorDisplay.visibility = View.VISIBLE
            }
        }
    }

    /**
     * this method will send multiple request(getInfo/restaurant/basic)
     */
    fun sendReqGetRestaurantInfos(restaurantIds: List<Int>){
        restaurantIds.forEach{
            sendReqGetRestaurantInfo(it)
        }
    }

    override fun onOpen(handshakedata: ServerHandshake?) {
        super.onOpen(handshakedata)
        this.sendReqGetUserEvaluations()
    }

    override fun onMessage(message: String?) {
        super.onMessage(message)
        Log.i(javaClass.simpleName, "msg arrived")
        Log.i(javaClass.simpleName, "$message")

        val wholeMsg = JSONObject("$message")
        val resId: Int = wholeMsg.getInt("id")
        val result: JSONObject = wholeMsg.getJSONObject("result")
        val status: String = result.getString("status")

        if(resId == UserShowCurrentEvaluations.getUserEvaluationsId){
            if(status == "success"){
                val evaluations = result.getJSONArray("evaluations")
                for (i in 0 until evaluations.length()){
                    val evaluation: JSONObject = evaluations.getJSONObject(i)
                    this.evaluationIdsList.add(evaluation.getInt("evaluation_id"))
                    this.evaluatedRestaurantIdsList.add(evaluation.getInt("restaurant_id"))
                    this.evaluatedGradeList.add(evaluation.getInt("evaluation_grade"))
                    this.evaluatedCommentList.add(evaluation.getString("evaluation_comment"))
                }
                this.sendReqGetRestaurantInfos(this.evaluatedRestaurantIdsList)

            }else if(status == "error"){
                Log.i(javaClass.simpleName, "error on getting user evaluations")
                activity.runOnUiThread {
                    errorDisplay.text = "食べログ情報を取得できませんでした"
                    errorDisplay.visibility = View.VISIBLE
                }
            }

        }else if(resId == UserShowCurrentEvaluations.getRestaurantInfoId){
            if(status == "success"){

                val restaurantName = result.getString("restaurant_name")
                val evaluation = Evaluation(this.evaluationIdsList[idx],
                                            this.evaluatedRestaurantIdsList[idx],
                                            restaurantName, this.evaluatedGradeList[idx],
                                            this.evaluatedCommentList[idx])

                this.evaluationList.add(evaluation)
                idx++
                if(idx == this.evaluatedRestaurantIdsList.size){
                    activity.runOnUiThread {
                        var listView: ListView = activity.findViewById(R.id.evaluationListView)
                        val adapter = EvaluationListAdapter(activity, this.evaluationList)
                        listView.adapter = adapter
                        this.close(NORMAL_CLOSURE)
                    }
                    idx = 0
                }
            }else if(status == "error") {
                Log.i(javaClass.simpleName, "error on getting evaluation info")
                activity.runOnUiThread {
                    errorDisplay.text = "食べログ情報を取得できませんでした"
                    errorDisplay.visibility = View.VISIBLE
                }
            }
        }
    }
}