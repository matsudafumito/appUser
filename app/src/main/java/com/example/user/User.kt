package com.example.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import org.json.JSONObject
import java.net.URI

class User : AppCompatActivity() {

    companion object {
        const val logoutReqId: Int = 3
        const val getUserInfoId: Int = 7
    }

    private val uri = WsClient.serverRemote
    private var client = UserTopWsClient(this, uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
    }

    fun sendReqGetUserInfoByName(token: String, clientName: String, client: UserTopWsClient){
        val params = JSONObject()
        params.put("searchBy", "user_name")
        params.put("user_name", clientName)
        params.put("token", token)
        val request = client.createJsonrpcReq("getInfo/user/basic", getUserInfoId, params)
        client.send(request.toString())
    }

    override fun onResume() {
        super.onResume()
        client.connect()

        val token = intent.getStringExtra("token")
        val tokenExpiry = intent.getStringExtra("expire")
        val userName = intent.getStringExtra("userName")

        Log.i(javaClass.simpleName, "token recved $token")
        Log.i(javaClass.simpleName, "token expiry $tokenExpiry")
        Log.i(javaClass.simpleName, "userName: $userName")

        val buttonToHome: Button = findViewById(R.id.buttonHome)
        val buttonToSearchRestaurant: Button = findViewById(R.id.buttonSearchRestaurant)
        val buttonToSetting: Button = findViewById(R.id.buttonSetting)
        val buttonLogout: Button = findViewById(R.id.buttonLogout)
        val buttonToCurrentReservations: Button = findViewById(R.id.buttonShowCurrentReservations)
        val buttonToCurrentEvaluations: Button = findViewById(R.id.buttonShowCurrentEvaluations)


        buttonToCurrentReservations.setOnClickListener {

        }

        buttonToCurrentEvaluations.setOnClickListener {

        }

        buttonToHome.setOnClickListener {
            //doNothing
        }

        buttonToSearchRestaurant.setOnClickListener {
            TODO("not yet implemented")
        }

        buttonToSetting.setOnClickListener {
            val intent = Intent(this@User, UserShowAccountInfo::class.java)
            intent.putExtra("userName", userName)
            intent.putExtra("token", token)
            startActivity(intent)
            client.close(WsClient.NORMAL_CLOSURE)
        }

        buttonLogout.setOnClickListener {
            val logoutParams = JSONObject()
            logoutParams.put("token", token)
            val logoutRequest = client.createJsonrpcReq("logout", logoutReqId, logoutParams)

            try{
                if(client.isClosed) {
                    client.reconnect()
                }
                client.send(logoutRequest.toString())
            } catch (ex: Exception){
                Log.i(javaClass.simpleName, "send failed $ex")
                val intent = Intent(this@User, ShowResult::class.java)
                val message = "ログアウトしました"
                val transitionBtnMessage = "ログインページへ"
                val isBeforeLogin = true
                Log.i(javaClass.simpleName, "logout with no request")
                intent.putExtra("message", message)
                intent.putExtra("transitionBtnMessage", transitionBtnMessage)
                intent.putExtra("isBeforeLogin", isBeforeLogin)
                startActivity(intent)
                finish()
            }
        }

    }

    override fun onRestart() {
        super.onRestart()
        client = UserTopWsClient(this, uri)
    }

}

class UserTopWsClient(private val activity: Activity, uri: URI) : WsClient(uri){

    var user_id: Int = -1
    var user_name: String = ""
    var birthday: String = ""
    var gender: String = ""
    var email_addr: String = ""
    var address: String = ""

    fun isUserInfoArrived(): Boolean{
        if(this.user_id == -1){
            return false
        }
        return true
    }

    override fun onMessage(message: String?) {
        super.onMessage(message)
        Log.i(javaClass.simpleName, "msg arrived")
        Log.i(javaClass.simpleName, "$message")

        val wholeMsg = JSONObject("$message")
        val resId: Int = wholeMsg.getInt("id")
        val result: JSONObject = wholeMsg.getJSONObject("result")
        val status: String = result.getString("status")

        //if message is about logout
        if(resId == User.logoutReqId){

            val intent = Intent(activity, ShowResult::class.java)
            var message = ""
            val transitionBtnMessage = "ログインページへ"
            val isBeforeLogin = true

            //if logout successes
            if(status == "success"){
                message = "ログアウトに成功しました"

            }else if(status == "error"){
                message = "ログアウトに失敗しました"
            }

            intent.putExtra("message", message)
            intent.putExtra("transitionBtnMessage", transitionBtnMessage)
            intent.putExtra("isBeforeLogin", isBeforeLogin)

            activity.startActivity(intent)
            activity.finish()
            this.close(NORMAL_CLOSURE)

        //if msg is about getInfo/user/basic
        }else if(resId == User.getUserInfoId){
            if(status == "success"){
                this.user_id = result.getInt("user_id")
                this.user_name = result.getString("user_name")
                this.birthday = result.getString("birthday")
                this.gender = result.getString("gender")
                this.email_addr = result.getString("email_addr")
                this.address = result.getString("address")
            }else if(status == "error"){
                Log.i(javaClass.simpleName, "getInfo failed")
            }
        }
    }
}