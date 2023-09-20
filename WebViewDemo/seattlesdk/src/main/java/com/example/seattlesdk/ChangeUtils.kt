package com.example.seattlesdk

import android.os.Build
import android.webkit.WebView
import androidx.annotation.RequiresApi
import com.example.seattlesdk.DataUtils.uploadData
import kotlinx.coroutines.*

internal object ChangeUtils {
    private var isUserInteracted: Boolean = false
    private var countdownJob: Job? = null
    private var currentUrl=""
    private var switchTime:Int=0
    private var urlList:List<Any>?=null
    private var previousLoadTime: Long = 0//上一个页面加载完成时间
    @RequiresApi(Build.VERSION_CODES.O)
    fun changeUrl(webView: WebView, websites:List<Any>, url:String,urlSwitchTime:Int){
        if(!GlobalData.getStatus()){
            return
        }
        GlobalData.setList(url)
        webView.loadUrl(url)
        currentUrl=url
        switchTime=urlSwitchTime
        urlList=websites
        onWebViewScrolled(webView)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onUserInteracted(webView: WebView) {//重置计时器
        if(!GlobalData.getStatus()){
            return
        }
        isUserInteracted = true
        countdownJob?.cancel()
        countdownJob = CoroutineScope(Dispatchers.Main).launch {
            if(!GlobalData.getStatus()){
                return@launch
            }
            delay(20000)
            onWebViewScrolled(webView)
            println("操作停止20后秒无操作")
            isUserInteracted = false
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onWebViewScrolled(webView: WebView) {
        if(!GlobalData.getStatus()){
            return
        }
        //记录加载完成时间
        previousLoadTime = System.currentTimeMillis()
        // 取消之前的倒计时任务
        countdownJob?.cancel()
        // 创建新的倒计时任务
        countdownJob = CoroutineScope(Dispatchers.Main).launch {
            if (!isUserInteracted) {// 如果用户未操作
                if (switchTime > 0) {// 切换时间大于0
                    if(!GlobalData.getStatus()){
                        return@launch
                    }
                    print("用户未操作，准备在 $switchTime 毫秒后开始切换")
                    delay(switchTime.toLong())// 延时切换时间后切换
                    println("开始切换")
                    switchToNextUrl(webView, currentUrl)
                }
            } else {// 如果用户已操作
                if(!GlobalData.getStatus()){
                    return@launch
                }
                delay(20000)// 过了20秒没新的操作
                println("20秒未操作，$isUserInteracted")
                if (switchTime > 0) {
                    print("用户已操作，准备在 $switchTime 毫秒后开始切换")
                    delay(switchTime.toLong())// 延时
                    println("开始延时切换")
                    switchToNextUrl(webView, currentUrl)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun switchToNextUrl(webView: WebView, url: String) {
        if(!GlobalData.getStatus()){
            return
        }
        val currentIndex = urlList!!.indexOf(url)
        val nextIndex = (currentIndex + 1) % urlList!!.size
        val nextUrl = urlList!![nextIndex]
        println("切换到$nextUrl,当前url是$currentUrl")
        //记录切换时的时间
        val currentTime=System.currentTimeMillis()
        //计算停留时间
        val stayTime=currentTime- previousLoadTime
        println("在$currentUrl 的停留时间是================================${stayTime}")
        //回传数据
        val data=mapOf("fromWeb" to currentUrl, "toWeb" to nextUrl)
        val params = DataUtils.postData(data,EventList.WebsiteSwitched,"Insight")
        val sessionStayTime=stayTime
        val sessionParams=DataUtils.postData(sessionStayTime,EventList.SessionTimeInMillisecond,"Insight")
        uploadData(params)
        uploadData(sessionParams)
        changeUrl(webView, urlList!!, nextUrl as String, switchTime)
    }
}