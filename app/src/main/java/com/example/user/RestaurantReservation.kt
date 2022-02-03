package com.example.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.lang.Exception
import java.net.URI

class RestaurantReservation : AppCompatActivity() {
    //サーバとの通信用の呪文？
    private val uri = WsClient.serverRemote
    private var client = SearchStoreWsClient(this, uri)

    companion object{
        const val getUserInfoId = 17000000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_reservation)
    }

    override fun onResume() {
        super.onResume()

        val info_restaurant_id = intent.getIntExtra("restaurantId", 0)

        val seat_id = findViewById<EditText>(R.id.restaurant_reservation_edittext_seat_id)
        val num_people = findViewById<EditText>(R.id.restaurant_reservation_edittext_num_people)
        val time_start = findViewById<EditText>(R.id.restaurant_reservation_edittext_time_start)
        val time_end = findViewById<EditText>(R.id.restaurant_reservation_edittext_time_end)
        val button_reservation = findViewById<Button>(R.id.restaurant_reservation_button_reservation)

        button_reservation.setOnClickListener {
            //searchTargetを検索するためのリクエストメッセージ定義
            val token = User.globalToken

            val reservation_data = JSONObject()
            reservation_data.put("restaurant_id", info_restaurant_id.toString())
            reservation_data.put("seat_id", seat_id.toString())
            reservation_data.put("time_start", seat_id.toString())
            reservation_data.put("time_end", seat_id.toString())
            reservation_data.put("num_people", seat_id.toString())

            val getInfoParams = JSONObject()
            getInfoParams.put("type", "new")
            getInfoParams.put("token", token)
            getInfoParams.put("reservationData", reservation_data)
            val getInfoRequest = client.createJsonrpcReq("updateInfo/reservation",
                getUserInfoId, getInfoParams)

            //リクエストメッセージの送信
            try {
                if (client.isClosed) {
                    client.reconnect()
                }
                client.send(getInfoRequest.toString())
            } catch (ex: Exception) {
                Log.i(javaClass.simpleName, "send failed")
                Log.i(javaClass.simpleName, "$ex")
//                errorDisplay.text = "インターネットに接続されていません"
//                errorDisplay.visibility = View.VISIBLE
            }
        }
    }
}

class RestaurantReservationWsClient(private val activity: Activity, uri: URI) : WsClient(uri){

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

        if(resId == SearchStore.getUserInfoId){
            if(status == "success"){
                activity.finish()

            }else if(status == "error"){
                activity.runOnUiThread{
                    errorDisplay.text = result.getString("reason")
                    errorDisplay.visibility = View.VISIBLE
                }
            }
        }
    }
}