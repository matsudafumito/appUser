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
import java.lang.Exception
import java.net.URI
import java.util.*
import kotlin.concurrent.schedule

class UserShowAccountInfo : AppCompatActivity() {

    companion object{
        const val getUserInfoId: Int = 4
    }

    private val uri = WsClient.serverRemote
    private var client = GetUserInfoWsClient(this, uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_show_account_info)
        client.connect()
    }

    override fun onResume() {
        super.onResume()

        val errorDisplay: TextView = findViewById(R.id.errorDisplay)
        val buttonChangeAccountInfo: Button = findViewById(R.id.buttonChangeAccountInfo)

        val token = User.globalToken
        val userName = User.globalUserName

        val getInfoParams = JSONObject()
        getInfoParams.put("searchBy", "user_name")
        getInfoParams.put("user_name", userName)
        getInfoParams.put("token", token)
        val getInfoRequest = client.createJsonrpcReq("getInfo/user/basic", getUserInfoId, getInfoParams)

        //attempt to send until connection established
        Timer().schedule(50, 200) {
            Log.i(javaClass.simpleName, "set req ${getInfoRequest.toString()}")
            try {
                if (client.isClosed) {
                    client.reconnect()
                }
                client.send(getInfoRequest.toString())
                errorDisplay.text = "情報取得中..."
                errorDisplay.visibility = View.VISIBLE
            } catch (ex: Exception) {
                Log.i(javaClass.simpleName, "send failed")
                Log.i(javaClass.simpleName, "$ex")
            }
            // if msg arrived
            if(client.isReceived){
                errorDisplay.visibility = View.INVISIBLE
                this.cancel()
            }
        }

        buttonChangeAccountInfo.setOnClickListener {
            if(client.isReceived){
                val intent = Intent(this@UserShowAccountInfo, UserChangeAccountInfo::class.java)
                intent.putExtra("userId", client.userId)
                intent.putExtra("userName", client.userName)
                intent.putExtra("birthday", client.birthday)
                intent.putExtra("gender", client.gender)
                intent.putExtra("emailAddr", client.emailAddr)
                intent.putExtra("address", client.address)
                intent.putExtra("token", token)
                client.close(WsClient.NORMAL_CLOSURE)
                startActivity(intent)
            }else{
                return@setOnClickListener
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        client = GetUserInfoWsClient(this, uri)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        client.close(WsClient.NORMAL_CLOSURE)
    }

}

class GetUserInfoWsClient(private val activity: Activity, uri: URI) : WsClient(uri){
    var userId: Int = -1
    var userName = ""
    var birthday = ""
    var gender = ""
    var emailAddr = ""
    var address = ""
    var isReceived = false

    private val errorDisplay: TextView by lazy { activity.findViewById(R.id.errorDisplay) }
    private val txtUserName: TextView by lazy { activity.findViewById(R.id.textBoxUserName) }
    private val txtBirthDay: TextView by lazy { activity.findViewById(R.id.textBoxUserBirthday) }
    private val txtGender: TextView by lazy { activity.findViewById(R.id.textBoxUserGender) }
    private val txtEmail: TextView by lazy { activity.findViewById(R.id.textBoxUserEmail) }
    private val txtAddress: TextView by lazy { activity.findViewById(R.id.textBoxUserAddress) }

    override fun onMessage(message: String?) {
        super.onMessage(message)
        Log.i(javaClass.simpleName, "msg arrived")
        Log.i(javaClass.simpleName, "$message")

        val wholeMsg = JSONObject("$message")
        val resId: Int = wholeMsg.getInt("id")
        val result: JSONObject = wholeMsg.getJSONObject("result")
        val status: String = result.getString("status")

        if(resId == UserShowAccountInfo.getUserInfoId){
            this.isReceived = true
            if(status == "success"){
                this.userId = result.getInt("user_id")
                this.userName = result.getString("user_name")
                this.birthday = result.getString("birthday")
                this.gender = result.getString("gender")
                this.emailAddr = result.getString("email_addr")
                this.address = result.getString("address")

                activity.runOnUiThread{
                    txtUserName.text = this.userName
                    txtBirthDay.text = this.birthday
                    txtGender.text = this.gender
                    txtEmail.text = this.emailAddr
                    txtAddress.text = this.address
                }
            }else if(status == "error"){
                activity.runOnUiThread{
                    errorDisplay.text = "アカウント情報を取得できません"
                    errorDisplay.visibility = View.INVISIBLE
                }
            }
        }
    }
}