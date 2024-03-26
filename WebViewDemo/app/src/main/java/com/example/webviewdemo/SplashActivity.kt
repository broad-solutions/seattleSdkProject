package com.example.webviewdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.seattlesdk.IMASDK
import com.google.ads.interactivemedia.v3.api.AdEvent

class SplashActivity : AppCompatActivity(){
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val message = intent?.getStringExtra("message")
                // 处理接收到的消息
                if (message != null) {
                    if (message == "PAUSED") {
                        val mainIntent = Intent(context, MainActivity::class.java)
                        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context?.startActivity(mainIntent)
                        finish()
                        // 发送广播通知当前 Activity 结束
                        val finishIntent = Intent("com.example.FINISH_SPLASH")
                        LocalBroadcastManager.getInstance(context!!).sendBroadcast(finishIntent)
                    }
                }
            }
        }

        val filter = IntentFilter("com.example.ACTION_AD_EVENT")
        this.registerReceiver(broadcastReceiver, filter, RECEIVER_NOT_EXPORTED)

        IMASDK().SdkStart(this,"",R.id.SplashAdContaniner,"Splash")


    }

    override fun onDestroy() {
        super.onDestroy()
        this.finish()
    }
}