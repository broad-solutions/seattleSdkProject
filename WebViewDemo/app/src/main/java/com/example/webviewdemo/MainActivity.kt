package com.example.webviewdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.seattlesdk.AnalyticsDelegate
import com.example.seattlesdk.AnalyticsDelegateImpl
import com.example.seattlesdk.SeattleSdk
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : AppCompatActivity(), AnalyticsDelegate by AnalyticsDelegateImpl() {
    private lateinit var mySdk: SeattleSdk
    private lateinit var webviewContainer:ViewGroup
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mySdk=SeattleSdk(this)
        webviewContainer=findViewById(R.id.webviewContainer)
        val client = OkHttpClient()

        val url = "http://demoapi.pubadding.com/access_token"

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                println(responseData)
            } else {
                println("Request was not successful: ${response.code}")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}