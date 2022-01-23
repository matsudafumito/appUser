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
import java.net.URI

class ConfirmDeleteReservation : AppCompatActivity() {

    companion object{
        const val deleteReservationReqId = 9
    }

    private val uri = WsClient.serverRemote
    private val client = DeleteReservationWsClient(this, uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_delete_reservation)
    }

    override fun onResume() {
        super.onResume()
        client.connect()
        val token = User.globalToken
        val reservationId = intent.getIntExtra("reservationId", -1)

        val errorDisplay: TextView = findViewById(R.id.errorDisplay)
        val deleteButton: Button = findViewById(R.id.buttonDeleteReservation)

        deleteButton.setOnClickListener {
            val params = JSONObject()
            params.put("token", token)
            params.put("type", "delete")
            params.put("reservation_id", reservationId)
            val request = client.createJsonrpcReq("updateInfo/reservation", deleteReservationReqId, params)
            try {
                if(client.isClosed){
                    client.reconnect()
                }
                client.send(request.toString())
            }catch (ex:Exception){
                Log.i(javaClass.simpleName, "send failed")
                Log.i(javaClass.simpleName, "$ex")
                errorDisplay.text = "インターネットに接続されていません"
                errorDisplay.visibility = View.VISIBLE
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        client.reconnect()
    }
}


class DeleteReservationWsClient(private val activity: Activity, uri: URI) : WsClient(uri){

    private val errorDisplay: TextView by lazy {
        activity.findViewById(R.id.errorDisplay)
    }

    override fun onMessage(message: String?) {
        super.onMessage(message)
        Log.i(javaClass.simpleName, "msg arrived")
        Log.i(javaClass.simpleName, "$message")

        val wholeMsg = JSONObject("$message")
        val resId: Int = wholeMsg.getInt("id")
        val result: JSONObject = wholeMsg.getJSONObject("result")
        val status: String = result.getString("status")

        if(resId == ConfirmDeleteReservation.deleteReservationReqId){
            if(status == "success"){
                val intent = Intent(activity, ShowResult::class.java)
                intent.putExtra("message", "予約を削除しました")
                intent.putExtra("transitionBtnMessage", "ホームへ")
                intent.putExtra("isBeforeLogin", false)
                this.close(NORMAL_CLOSURE)
                activity.startActivity(intent)

            }else if(status == "error"){
                activity.runOnUiThread {
                    errorDisplay.text = result.getString("reason")
                    errorDisplay.visibility = View.VISIBLE
                }
            }
        }
    }
}