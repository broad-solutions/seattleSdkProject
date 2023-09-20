package com.example.seattlesdk

import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.WebView
import androidx.core.util.forEach

internal class SDKManager(private val webView: WebView)  {
    init {
        var startTime = System.currentTimeMillis()
        webView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(p0: View) {
                Log.d("SDKManager", "WebView is attached to window")
                GlobalData.setStatus(true)
                startTime = System.currentTimeMillis()
            }

            override fun onViewDetachedFromWindow(p0: View) {
                Log.d("SDKManager", "WebView is detached from window")

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
                webView.destroy()
            }
        })
    }
}