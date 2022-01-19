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
import org.w3c.dom.Text
import java.lang.Exception
import java.net.URI

class UserChangeAccountInfo : AppCompatActivity() {
    companion object{
        const val changeUserInfoId: Int = 5
        var token: String = ""
        var userName: String = ""
    }

    private val uri = WsClient.serverRemote
    private var client = ChangeUserInfoWsClient(this, uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_change_account_info)
    }

    override fun onResume() {
        super.onResume()
        client.connect()
        val currentUserId = intent.getIntExtra("userId", 0)
        val currentUserName = intent.getStringExtra("userName")
        val currentBirthday = intent.getStringExtra("birthday")
        val currentGender = intent.getStringExtra("gender")
        val currentEmail = intent.getStringExtra("emailAddr")
        val currentAddress = intent.getStringExtra("address")
        token = intent.getStringExtra("token")!!

        val etxtUserName: EditText = findViewById(R.id.textBoxUserName)
        val etxtUserBirthday: EditText = findViewById(R.id.textBoxUserBirthday)
        val etxtUserGender: EditText = findViewById(R.id.textBoxUserGender)
        val etxtUserEmail: EditText = findViewById(R.id.textBoxUserEmail)
        val etxtUserAddress: EditText = findViewById(R.id.textBoxUserAddress)
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)
        val errorDisplay: TextView = findViewById(R.id.errorDisplay)

        etxtUserName.setText(currentUserName)
        etxtUserBirthday.setText(currentBirthday)
        etxtUserGender.setText(currentGender)
        etxtUserEmail.setText(currentEmail)
        etxtUserAddress.setText(currentAddress)

        buttonSubmit.setOnClickListener {

            val params = JSONObject()
            params.put("user_name", etxtUserName.text.toString())
            params.put("birthday", etxtUserBirthday.text.toString())
            params.put("gender", etxtUserGender.text.toString())
            params.put("email_addr", etxtUserEmail.text.toString())
            params.put("address", etxtUserAddress.text.toString())
            params.put("token", token)

            userName = etxtUserName.text.toString()

            val request = client.createJsonrpcReq("updateInfo/user/basic", changeUserInfoId, params)

            try {
                if (client.isClosed) {
                    client.reconnect()
                }
                client.send(request.toString())
            } catch (ex: Exception) {
                Log.i(javaClass.simpleName, "send failed")
                Log.i(javaClass.simpleName, "$ex")
                errorDisplay.text = "インターネットに接続されていません"
                errorDisplay.visibility = View.VISIBLE
            }
        }
    }
}

class ChangeUserInfoWsClient(private val activity: Activity, uri: URI) : WsClient(uri){

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

        if(resId == UserChangeAccountInfo.changeUserInfoId){
            if(status == "success"){
                val intent = Intent(activity, ShowResult::class.java)
                intent.putExtra("token", UserChangeAccountInfo.token)
                intent.putExtra("userName", UserChangeAccountInfo.userName)
                intent.putExtra("message", "アカウント情報を変更しました")
                intent.putExtra("transitionBtnMessage", "ホームへ")
                intent.putExtra("isBeforeLogin", false)
                activity.startActivity(intent)

            }else if(status == "error"){
                activity.runOnUiThread{
                    errorDisplay.text = result.getString("reason")
                    errorDisplay.visibility = View.VISIBLE
                }
            }
        }

    }
}