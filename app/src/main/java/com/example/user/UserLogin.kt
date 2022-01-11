package com.example.user

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject
import java.net.URI

class UserLogin : AppCompatActivity() {

    companion object{
        const val loginReqId: Int = 1
    }

    private val uri = WsClient.serverRemote
    private var client = LoginWsClient(this, uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_login)
    }

    override fun onResume() {
        super.onResume()
        client.connect()

        //edit text, name and password
        val eTxtUserName: EditText = findViewById(R.id.textBoxUserName)
        val eTxtPassword: EditText = findViewById(R.id.textBoxPassword)

        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val buttonCreateAcc: Button = findViewById(R.id.buttonCreateAccount)

        buttonLogin.setOnClickListener {
            val loginRequest = JSONObject()
            val loginParams = JSONObject()
            val userName: String = eTxtUserName.text.toString()
            val password: String = eTxtPassword.text.toString()
            val role = "user"

            loginParams.put("user_name", userName)
            loginParams.put("password", password)
            loginParams.put("role", role)

            loginRequest.put("jsonrpc", "2.0")
            loginRequest.put("id", loginReqId)
            loginRequest.put("method", "login")
            loginRequest.put("params", loginParams)
            Log.i(javaClass.simpleName, "send login req")
            Log.i(javaClass.simpleName, loginRequest.toString())
            client.send(loginRequest.toString())
        }

        buttonCreateAcc.setOnClickListener {
            val intent = Intent(this@UserLogin, UserRegisterAccount::class.java)
            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        client = LoginWsClient(this, uri)
    }

}

class LoginWsClient(private val activity: Activity, uri: URI) : WsClient(uri){

    private val errorDisplay : TextView by lazy{
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

        //if message is about login
        if(resId == UserLogin.loginReqId){
            if(status == "success"){
                val token: String = result.getString("token")
                val expire: String = result.getString("expire")
                Log.i(javaClass.simpleName, "login success")
                Log.i(javaClass.simpleName, "token: $token")
                Log.i(javaClass.simpleName, "expires in $expire")

                this.close(NORMAL_CLOSURE)
                activity.runOnUiThread{
                    val intent = Intent(activity, User::class.java)
                    intent.putExtra("token", token)
                    intent.putExtra("expire", expire)
                    activity.startActivity(intent)
                }

            }else if(status == "error"){
                activity.runOnUiThread{
                    val reason: String = result.getString("reason")
                    errorDisplay.visibility = View.VISIBLE
                    Log.i(javaClass.simpleName, "login failed with reason $reason")
                }
            }
        }
    }
}
