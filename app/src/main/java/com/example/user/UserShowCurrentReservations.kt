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

class UserShowCurrentReservations : AppCompatActivity() {

    companion object {
        const val getUserReservationsId = 8
        const val getRestaurantInfoId = 9
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_show_current_reservations)
    }

    override fun onResume() {
        super.onResume()
        val token = User.globalToken
        val userId = User.globalUserId
        val userName = User.globalUserName
        Log.i(javaClass.simpleName, "token: $token")
        Log.i(javaClass.simpleName, "user_id: $userId")
        val uri = WsClient.serverRemote
        var client = ReservationWsClient(this, uri, userId, token)
        client.connect()
    }
}

class ReservationWsClient(private val activity: Activity,
                          uri: URI, user_id: Int, token: String) : WsClient(uri) {

    private val user_id = user_id
    private val token = token
    val ReservationList = mutableListOf<Reservation>()
    val reservedRestaurantIds = mutableListOf<Int>()
    val reservationTimeStartList = mutableListOf<Int>()
    val reservationTimeEndList = mutableListOf<Int>()
    val reservationIdsList = mutableListOf<Int>()
    val reservationNumPeopleList = mutableListOf<Int>()

    var isAllDone = false
    var idx = 0

    private val errorDisplay: TextView by lazy {
        activity.findViewById(R.id.errorDisplay)
    }

    fun sendReqGetUserReservations(){
        Log.i(javaClass.simpleName, "send req get user reservations")
        val params = JSONObject()
        params.put("user_id", this.user_id)
        params.put("token", this.token)
        val request = createJsonrpcReq("getInfo/user/reservations", UserShowCurrentReservations.getUserReservationsId, params)

        try {
            if(this.isClosed){
                this.reconnect()
            }
            this.send(request.toString())
        }catch (ex:Exception){
            Log.i(javaClass.simpleName, "send failed with reason $ex")
            activity.runOnUiThread {
                errorDisplay.text = "予約情報を取得できません"
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
        val request = this.createJsonrpcReq("getInfo/restaurant/basic", UserShowCurrentReservations.getRestaurantInfoId, params)

        try {
            if(this.isClosed){
                this.reconnect()
            }
            this.send(request.toString())
        }catch (ex:Exception){
            Log.i(javaClass.simpleName, "send failed with reason $ex")
            activity.runOnUiThread {
                errorDisplay.text = "予約情報を取得できません"
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
        this.sendReqGetUserReservations()
    }

    override fun onMessage(message: String?) {
        super.onMessage(message)
        Log.i(javaClass.simpleName, "msg arrived")
        Log.i(javaClass.simpleName, "$message")

        val wholeMsg = JSONObject("$message")
        val resId: Int = wholeMsg.getInt("id")
        val result: JSONObject = wholeMsg.getJSONObject("result")
        val status: String = result.getString("status")


        if(resId == UserShowCurrentReservations.getUserReservationsId){
            if(status == "success"){
                val reservations = result.getJSONArray("reservations")
                for (i in 0 until reservations.length()){
                    val reservation: JSONObject = reservations.getJSONObject(i)
                    this.reservedRestaurantIds.add(reservation.getInt("restaurant_id"))
                    this.reservationTimeStartList.add(reservation.getInt("time_start"))
                    this.reservationTimeEndList.add(reservation.getInt("time_end"))
                    this.reservationIdsList.add(reservation.getInt("reservation_id"))
                    this.reservationNumPeopleList.add(reservation.getInt("num_people"))
                }
                this.sendReqGetRestaurantInfos(this.reservedRestaurantIds)

            }else if(status == "error"){
                Log.i(javaClass.simpleName, "error on getting user reservations")
                activity.runOnUiThread {
                    errorDisplay.text = "予約情報を取得できませんでした"
                    errorDisplay.visibility = View.VISIBLE
                }
            }

        }else if(resId == UserShowCurrentReservations.getRestaurantInfoId){
            if(status == "success"){
                //the order of request is guaranteed by websocket protocol

                val restaurantId = result.getInt("restaurant_id")
                val restaurantName = result.getString("restaurant_name")
                val reservation = Reservation(this.reservedRestaurantIds[idx], restaurantName,
                    this.reservationTimeStartList[idx], this.reservationTimeEndList[idx], this.reservationIdsList[idx],
                    this.reservationNumPeopleList[idx])

                this.ReservationList.add(reservation)

                Log.i(javaClass.simpleName, "rsrv: $idx / ${this.reservedRestaurantIds.size-1}")
                Log.i(javaClass.simpleName, "reservation_id: ${this.ReservationList[idx].reservationId}")
                Log.i(javaClass.simpleName, "restaurant_id: ${this.ReservationList[idx].restaurantId}")
                Log.i(javaClass.simpleName, "time_start: ${this.ReservationList[idx].reservationTime_start}")
                Log.i(javaClass.simpleName, "time_end: ${this.ReservationList[idx].reservationTime_end}")
                Log.i(javaClass.simpleName, "num_people: ${this.ReservationList[idx].numPeople}")

                idx++
                //if index reached last, display reservations
                if(idx == this.reservedRestaurantIds.size){
                    activity.runOnUiThread {
                        var listView: ListView = activity.findViewById(R.id.reservationListView)
                        val adapter = ReservationListAdapter(activity, this.ReservationList)
                        listView.adapter = adapter
                        //if all information arrived, close connection here
                        this.close(NORMAL_CLOSURE)
                    }
                    idx = 0
                }

            }else if(status == "error"){
                Log.i(javaClass.simpleName, "error on getting restaurant info")
                activity.runOnUiThread {
                    errorDisplay.text = "店舗情報を取得できませんでした"
                    errorDisplay.visibility = View.VISIBLE
                }
            }

        }

    }

}