package com.example.seattlesdk

// ScrollUtils.kt

import android.annotation.SuppressLint
import android.os.Build
import android.view.MotionEvent
import android.webkit.WebView
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*

internal object ScrollUtils {
    private var isUserScrolling = false

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.M)
    fun setupScrollListener(webView: WebView, websites:List<Any>, url:String) {
        webView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (isUserScrolling) {
                // 滚动事件由用户操作触发
                println("用户触发")
                ChangeUtils.onWebViewScrolled(webView)
                ChangeUtils.onUserInteracted(webView)
            } else {
                // 滚动事件由代码控制触发
                println("非用户触发")
            }
        }

        webView.setOnTouchListener { view, event ->
            when (event.actionMasked) {
                // 触摸开始（按下）
                MotionEvent.ACTION_DOWN,
                    // 触摸结束（抬起）
                MotionEvent.ACTION_UP -> {
                    // 判断是否是滚动操作
                    if (webView.canScrollVertically(1) || webView.canScrollVertically(-1)) {
                        println("用户操作的滚动")
                        ChangeUtils.onWebViewScrolled(webView)
                        ChangeUtils.onUserInteracted(webView)
                        val xAndy=mapOf("x" to event.x,"y" to event.y)
                        val clickData=DataUtils.postData(xAndy,EventList.WebViewClicked,"Insight")
                        DataUtils.uploadData(clickData)//上传点击事件
                        isUserScrolling=true
                    } else {
                        println("非用户操作的滚动")
                        isUserScrolling=false
                    }
                }
            }
            // 消费该事件，阻止WebView再次处理该事件
            false
        }

    }

    fun scrollToPosition(webView: WebView, x: Int, y: Int, delay: Long) {
        isUserScrolling = false
        webView.postDelayed({
            webView.scrollTo(x, y)
        }, delay)
    }
}
