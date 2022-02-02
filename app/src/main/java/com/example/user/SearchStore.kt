package com.example.user

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_search_store.*
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import org.w3c.dom.Text
import java.lang.Exception
import java.net.URI

class SearchStore : AppCompatActivity() {
    //サーバとの通信用の呪文？
    private val uri = WsClient.serverRemote
    private var client = WsClient(uri)

    companion object{
        const val getUserInfoId = 17000000
    }

    //Activityの初期化処理
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_store)
    }

    //Activityの開始時(表示時)の処理
    override fun onResume() {
        super.onResume()

        val errorDisplay: TextView = findViewById(R.id.errorDisplay2)
        val buttonSearchStore = findViewById<Button>(R.id.buttonSearchStore)
        val textSearchStore = findViewById<EditText>(R.id.textSearchStore)

        buttonSearchStore.setOnClickListener{
            val searchTarget: String = textSearchStore.getText().toString()

            //searchTargetを検索するためのリクエストメッセージ定義
            val token = User.globalToken

            val getInfoParams = JSONObject()
            getInfoParams.put("keyword", searchTarget)
            getInfoParams.put("token", token)
            val getInfoRequest = client.createJsonrpcReq("getInfo/restaurants",
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
                errorDisplay.text = "インターネットに接続されていません"
                errorDisplay.visibility = View.VISIBLE
            }
        }
    }
}

class SearchStoreWsClient(private val activity: Activity, uri: URI) : WsClient(uri){

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
                val intent = Intent(activity, ShowSearchStoreResult :: class.java)
                //データベースの検索結果を遷移先に送る
                intent.putExtra("result", result.toString())

                //店舗検索結果(検索にマッチした店舗のリスト)表示画面へ遷移
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