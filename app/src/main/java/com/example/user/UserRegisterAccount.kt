package com.example.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject
import java.net.URI

class UserRegisterAccount : AppCompatActivity() {

    companion object{
        const val registerReqId: Int = 2
    }

    private val uri = WsClient.serverRemote
    private var client = RegisterWsClient(this, uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_register_account)
    }

    override fun onResume() {
        super.onResume()
        client.connect()

        //edit text, name and password
        val eTxtUserName: EditText = findViewById(R.id.textBoxUserName)
        val eTxtPassword: EditText = findViewById(R.id.textBoxPassword)
        val errorDisplay: TextView = findViewById(R.id.errorDisplay)

        //when register button pushed
        val buttonRegister: Button = findViewById(R.id.buttonRegister)
        buttonRegister.setOnClickListener {
            val registerRequest = JSONObject()
            val registerParams = JSONObject()
            val userName: String = eTxtUserName.text.toString()
            val password: String = eTxtPassword.text.toString()

            //check params are inputted
            if(userName.isEmpty() || password.isEmpty()){
                when {
                    userName.isEmpty() -> {
                        errorDisplay.text = "ユーザネームが入力されていません"
                    }
                    password.isEmpty() -> {
                        errorDisplay.text = "パスワードが入力されていません"
                    }
                }
                errorDisplay.visibility = View.VISIBLE
                return@setOnClickListener
            }

            registerParams.put("user_name", userName)
            registerParams.put("password", password)

            registerRequest.put("jsonrpc", "2.0")
            registerRequest.put("id", registerReqId)
            registerRequest.put("method", "register/user")

            registerRequest.put("params", registerParams)

            Log.i(javaClass.simpleName, "send register req")
            Log.i(javaClass.simpleName, registerRequest.toString())
            client.send(registerRequest.toString())
        }
    }

    override fun onBackPressed(){
        super.onBackPressed()
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        client = RegisterWsClient(this, uri)
    }
}

class RegisterWsClient(private val activity: Activity, uri: URI) : WsClient(uri){
    private val errorDisplay : TextView by lazy {
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

        //if message is about register/user
        if(resId == UserRegisterAccount.registerReqId){
            //if success, transition to ShowResult page
            if(status == "success"){
                val intent = Intent(activity, ShowResult::class.java)
                val message = "アカウント登録が完了しました"
                val transitionBtnMessage = "ログインページへ"
                val isBeforeLogin = true

                intent.putExtra("message", message)
                intent.putExtra("transitionBtnMessage", transitionBtnMessage)
                intent.putExtra("isBeforeLogin", isBeforeLogin)
                activity.runOnUiThread{
                    activity.startActivity(intent)
                }

                //when error occurred with registration
            }else if(status == "error"){
                val reason: String = result.getString("reason")
                activity.runOnUiThread{
                    if(reason == "user_name has already taken by other user"){
                        errorDisplay.text = "ユーザネームが重複しています"
                    }else{
                        errorDisplay.text = reason
                    }
                    errorDisplay.visibility = View.VISIBLE
                    Log.i(javaClass.simpleName, "registration failed with reason $reason")
                }
            }
        }

    }
}