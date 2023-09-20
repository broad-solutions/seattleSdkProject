package com.example.webviewdemo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.seattlesdk.AnalyticsDelegate
import com.example.seattlesdk.AnalyticsDelegateImpl
import com.example.seattlesdk.SeattleSdk
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class second : AppCompatActivity(), AnalyticsDelegate by AnalyticsDelegateImpl(){
    private lateinit var mySDK: SeattleSdk
    private lateinit var webViewContainer: ViewGroup
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val btn=findViewById<TextView>(R.id.textView4)
        btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        mySDK =SeattleSdk(this)
        // 获取指定的容器,容器是APP的容器，主要是确定webView在屏幕上的显示位置
        webViewContainer = findViewById(R.id.webContent)
        //测试token获取
        MyAsyncTask().execute("http://demoapi.pubadding.com/access_token")
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
    @SuppressLint("StaticFieldLeak")
    inner class MyAsyncTask : AsyncTask<String, Void, String>() {

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String): String? {
            val url = params[0]
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            return response.body?.string()
        }
        private val callback = object : (String) -> Unit {
            override fun invoke(str: String) {
                println("Received callback message: $str")
            }
        }
        @RequiresApi(Build.VERSION_CODES.M)
        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                val jsonArray = JSONObject(result)
                jsonArray.getInt("code")
                jsonArray.getString("msg")
                val data = jsonArray.getString("data")
                mySDK.initSdk("com.example.webviewdemo",data,callback)
                //Log.e("初始化", mySDK.initSdk("com.example.webviewdemo",data,callback).toString())
                mySDK.showContent(webViewContainer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}