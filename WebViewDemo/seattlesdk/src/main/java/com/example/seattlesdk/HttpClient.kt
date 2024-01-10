package com.example.seattlesdk

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

internal class HttpClient {

    private val client = OkHttpClient()
    private val baseUrl="https://sdkapi.pubadding.com"
    // GET请求
    fun get(url: String, token: String, params: Map<String, Any>, callback: Callback) {
        val queryString = StringBuilder()
        for ((key, value) in params) {
            if (queryString.isNotEmpty()) {
                queryString.append("&")
            }
            queryString.append(key).append("=").append(value)
        }
        val requestUrl = if (queryString.isNotEmpty()) {
            "$url?$queryString"
        } else {
            url
        }
        val headers = Headers.Builder()
            .add("Authorization", "Bearer $token")
            .build()
        val request = Request.Builder()
            .url(baseUrl+requestUrl)
            .headers(headers)
            .build()
        client.newCall(request).enqueue(callback)
    }

    // POST请求
    fun post(url: String, params: Map<String, Any>, token: String, callback: Callback) {
        val jsonObject = JSONObject(params)
        val jsonBody = jsonObject.toString()
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = jsonBody.toRequestBody(mediaType)
        println("提交的数据是--------------------------------------------->$jsonObject")
        val request = Request.Builder()
            .url(baseUrl+url)
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        client.newCall(request).enqueue(callback)
    }
}