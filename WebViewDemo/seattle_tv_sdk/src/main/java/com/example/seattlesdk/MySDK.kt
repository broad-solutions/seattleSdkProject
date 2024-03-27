package com.example.seattlesdk

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.example.seattle_tv_sdk.R
import com.example.seattlesdk.GlobalData.errorCountMap
import com.example.seattlesdk.model.AppConfigDTO
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException


class SeattleSdk(private val context: FragmentActivity) {
    //调用SDK需要传Context，packageName,token
    private var webView: WebView? = null
    private lateinit var progressBar: ProgressBar
    private var currentUrl: String = ""
    private val client = HttpClient()
    private var urlSwitchTime = 0
    private var websites: List<String> = listOf("")
    private val handler = Handler(Looper.getMainLooper())
    private var previousLoadTime: Long = 0//上一个url停留时间
    private var oldUrl: String = ""
    private var containerGp: ViewGroup? = null

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.M)
    fun showContent(container: ViewGroup) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.webview, container, false) as ConstraintLayout
        containerGp = container
        webView = view.findViewById(R.id.webContent)
        webView!!.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView!!.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        progressBar = view.findViewById(R.id.progressBar)
        // 获取布局文件中的WebView控件
        webView = view.findViewById(R.id.webContent)
        //注册SDK，用来监控SDK的状态
        // 设置WebView的属性和加载URL等操作
        webView?.settings?.javaScriptEnabled = true
        CookieManager.getInstance()
            .setAcceptThirdPartyCookies(webView, true)//Google要求webview允许第三方cookie
        // Register the WebView.
        webView?.let { MobileAds.registerWebView(it) }
        // 创建一个WebViewClient并设置给WebView
        val webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                // 页面开始加载时的逻辑处理
                progressBar.visibility = View.VISIBLE
                //上传开始加载事件
                val data = mapOf("OpenStarted" to url)
                val startParams = DataUtils.postData(data, EventList.OpenStarted, "Insight")
                DataUtils.uploadData(startParams)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onPageFinished(view: WebView?, url: String?) {
                // 页面加载完成时的逻辑处理
                progressBar.visibility = View.GONE
                //记录url
                oldUrl = view!!.url.toString()
                // 记录加载完成的时间
                previousLoadTime = System.currentTimeMillis()
                val data = mapOf("OpenSuccess" to url)
                val successParams = DataUtils.postData(data, EventList.OpenSuccess, "Insight")
                DataUtils.uploadData(successParams)
                //页面加载完成后开始处理url切换逻辑
                onWebViewLoaded()
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                // 加载页面出错时的逻辑处理
                val errorCode: Int = error?.errorCode?.times(-1) ?: 0
                Log.e("errorCode", errorCode.toString())
                val count = errorCountMap.get(errorCode, 0)
                errorCountMap.put(errorCode, count + 1)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {

                return false
            }

            override fun doUpdateVisitedHistory(
                view: WebView?,
                url: String?,
                isReload: Boolean
            ) {//访问新的url
                super.doUpdateVisitedHistory(view, url, isReload)
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
            }
        }
        webView?.webViewClient = webViewClient
        SDKManager(webView!!)
        // 设置滚动监听器和触摸监听器
        ScrollUtils.setupScrollListener(webView!!, websites, currentUrl)
        // 创建一个WebChromeClient并设置给WebView来监听webview加载进度
        val webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                // 页面加载进度变化时的逻辑处理
                progressBar.progress = newProgress
            }
        }
        webView?.webChromeClient = webChromeClient
        // 将布局文件添加到指定的容器内
        container.addView(view)
    }

    @SuppressLint("InvalidWakeLockTag", "LongLogTag")
    fun initSdk(packageName: String, token: String, callback: (String) -> Unit) {
        Log.e("token==========================",token)
        if (context is AnalyticsDelegate) {
            context.registerAnalytics(context)
            GlobalData.updateToken(token)//设置token
            GlobalData.setPackageName(packageName)//设置packageName
            // 发起GET请求
            val params = mapOf("packageName" to packageName)
            client.get("/v1/app/config", token, params, object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // 处理请求失败的逻辑
                    handler.post {
                        callback.invoke("请求失败,${e.message.toString()}")
                    }
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call, response: Response) {
                    // 处理请求成功的逻辑
                    val responseData = response.body!!.string()
                    val config = Gson().fromJson(responseData, AppConfigDTO::class.java)
                    if (config.error != null) {
                        kotlin.runCatching {
                            if (config.error != null) {
                                handler.post {
//                                Toast.makeText(context,config.error,Toast.LENGTH_LONG).show()
                                    if (config.data.webviewApiEnabled != 0) {
                                        MobileAds.initialize(context) {}
                                    }
                                    callback.invoke("请求失败,${config.error}")
                                }
                            }
                        }
                    } else {
                        urlSwitchTime = config.data.urlSwitchTime
                        websites = config.data.websites
                        currentUrl = websites[0]

                        handler.post {
                            ChangeUtils.changeUrl(
                                webView!!,
                                websites,
                                currentUrl,
                                urlSwitchTime
                            )
                        }//urlSwitchTime
                        callback.invoke("SDK初始成功")
                    }
                }

            })
        } else {callback("请先实现 AnalyticsDelegate")}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onWebViewLoaded() {
        progressBar.visibility = View.GONE
        // 重置计时
        ChangeUtils.onWebViewScrolled(webView!!)

    }
}