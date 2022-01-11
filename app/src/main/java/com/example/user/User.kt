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
import kotlin.math.log

class User : AppCompatActivity() {

    companion object {
        const val logoutReqId: Int = 3
    }

    private val uri = WsClient.serverRemote
    private var client = LogoutWsClient(this, uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
    }

    override fun onResume() {
        super.onResume()
        client.connect()

        val token = intent.getStringExtra("token")
        val tokenExpiry = intent.getStringExtra("expire")

        Log.i(javaClass.simpleName, "token recved $token")
        Log.i(javaClass.simpleName, "token expiry $tokenExpiry")

        val buttonToHome: Button = findViewById(R.id.buttonHome)
        val buttonToSearchRestaurant: Button = findViewById(R.id.buttonSearchRestaurant)
        val buttonToSetting: Button = findViewById(R.id.buttonSetting)
        val buttonLogout: Button = findViewById(R.id.buttonLogout)

        buttonToHome.setOnClickListener {
            //doNothing
        }

        buttonToSearchRestaurant.setOnClickListener {
            TODO("not yet implemented")
        }

        buttonToSetting.setOnClickListener {
            TODO("not yet implemented")
        }

        buttonLogout.setOnClickListener {
            val logoutRequest = JSONObject()
            val logoutParams = JSONObject()

            logoutParams.put("token", token)

            logoutRequest.put("jsonrpc", "2.0")
            logoutRequest.put("id", logoutReqId)
            logoutRequest.put("method", "logout")
            logoutRequest.put("params", logoutParams)

            client.send(logoutRequest.toString())
        }

    }

    override fun onRestart() {
        super.onRestart()
        client = LogoutWsClient(this, uri)
    }

}

class LogoutWsClient(private val activity: Activity, uri: URI) : WsClient(uri){

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
        }

    }
}