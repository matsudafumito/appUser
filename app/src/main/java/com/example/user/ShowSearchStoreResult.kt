package com.example.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.lang.Exception
import java.net.URI

class ShowSearchStoreResult : AppCompatActivity() {
    //サーバとの通信用の呪文？
    private val uri = WsClient.serverRemote
    private var client = ShowSearchStoreWsClient(this, uri)

    companion object{
        const val getUserInfoId = 17000001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_search_store_result)
    }

    override fun onResume() {
        super.onResume()

        //データがおかしいと即死するので注意、いい書き方募集中
        val result = JSONObject(intent.getStringExtra("result"))
        val store_list = result.getJSONArray("restaurants")

        //受け取った検索結果を元に検索結果画面を生成
        for (i in 1..store_list.length()) {
            //店舗ごとのレイアウトの読み込み
            val storelist_layout = layoutInflater.inflate(R.layout.activity_show_search_store_result_sub, null)

            //店舗名表示
            val store_name = storelist_layout.findViewById<TextView>(R.id.show_search_store_result_name)
            store_name.text = store_list.getJSONObject(i).getString("restaurant_name")

            //ボタンの設定
            val more_detail = storelist_layout.findViewById<Button>(R.id.show_search_store_result_more_detail)
            more_detail.setOnClickListener{
                val searchTarget: String = store_name.toString()

                //searchTargetを検索するためのリクエストメッセージ定義
                val token = User.globalToken

                val getInfoParams = JSONObject()
                getInfoParams.put("searchBy", "restaurant_name")
                getInfoParams.put("restaurant_name", searchTarget)
                getInfoParams.put("token", token)
                val getInfoRequest = client.createJsonrpcReq("getInfo/restaurant/basic",
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
                    /*
                     * errorDisplay.text = "インターネットに接続されていません"
                     * errorDisplay.visibility = View.VISIBLE
                     */
                }
            }
        }
    }
}

class ShowSearchStoreWsClient(private val activity: Activity, uri: URI) : WsClient(uri){

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

        if(resId == ShowSearchStoreResult.getUserInfoId){
            if(status == "success"){
                val intent = Intent(activity, ShowSearchStoreResult :: class.java)
                //データベースの検索結果を遷移先に送る
                intent.putExtra("result", result.toString())

                //店舗基本情報表示へ遷移
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