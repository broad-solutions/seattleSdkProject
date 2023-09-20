package com.example.seattlesdk

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Message
import android.util.Log
import androidx.core.util.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


interface AnalyticsDelegate {
    fun registerAnalytics(lifecycle: LifecycleOwner)
    fun notifyEvent(msg: Message)
}

class AnalyticsDelegateImpl : AnalyticsDelegate, DefaultLifecycleObserver {


    override fun registerAnalytics(lifecycle: LifecycleOwner) {
        when (lifecycle) {
            is FragmentActivity -> {
                lifecycle.lifecycle.addObserver(this)
            }
            is Fragment -> {
                lifecycle.lifecycle.addObserver(this)
            }
        }
    }

    override fun notifyEvent(msg: Message) {

    }

    override fun onStart(owner: LifecycleOwner) {

        traceEvent("started")

    }


    override fun onStop(owner: LifecycleOwner) {
        traceEvent("stopped")

    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        if (owner is Activity) {
            owner.lifecycle.removeObserver(this)
        }
    }

    @SuppressLint("LongLogTag")
    var startTime = 0L
    private fun traceEvent(event: String) {
        if(event=="started"){
            startTime = System.currentTimeMillis()
        }else{
            val endTime = System.currentTimeMillis() // 记录销毁WebView的时间
            val duration = endTime - startTime // 计算WebView存在的时长
            Log.i("WebView存在时长", "$duration 毫秒")
            val webViewStayTime=DataUtils.postData(duration,EventList.UserStayTimeInMillisecond,"Insight")//上传Webview存在时长
            val webViewUrlSize=DataUtils.postData(GlobalData.getSize(),EventList.TotalWebsitesInSession,"Insight")//webview里显示过的网站总数（不重复的）
            DataUtils.uploadData(webViewStayTime)
            DataUtils.uploadData(webViewUrlSize)
            //上报错误
            GlobalData.errorCountMap.forEach { key, value -> // 处理不为0的值
                if (value != 0) {
                    // 在这里可以使用 key 和 value 进行进一步的处理
                    println("Error code $key 出现了 $value 次.")
                    val data=mapOf("code" to key*-1,"times" to value)//取反，key为错误码value为出现次数
                    val errorData=DataUtils.postData(data,EventList.WebsiteLoadError,"Error")
                    DataUtils.uploadData(errorData)
                }
            }
            GlobalData.setStatus(false)
        }
    }
}