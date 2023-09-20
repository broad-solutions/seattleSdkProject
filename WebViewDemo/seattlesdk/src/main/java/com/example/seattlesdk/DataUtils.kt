package com.example.seattlesdk

import android.util.Log
import com.example.seattlesdk.model.UploadDTO
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

internal object DataUtils {
    fun postData(data: Any, eventList: EventList,eventType:String): Map<String, Any> {
        val events = listOf(
            mapOf(
                "data" to data,
                "ext" to "",
                "name" to eventList.name,
                "type" to eventType
            )
        )

        return mapOf(
            "events" to events,
            "packageName" to GlobalData.getPackageName()
        )
    }

    fun uploadData(params: Map<String, Any>) {
        val client = HttpClient()
        println("开始上传，上传事件是${params}")
        client.post("/v1/event/upload", params, GlobalData.getToken(), object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 处理请求失败的情况
                Log.e("失败","请求失败：${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                // 处理请求成功的情况 对返回的数据进行处理
                val responseBody = response.body?.string()
                val result= Gson().fromJson(responseBody, UploadDTO::class.java)
                if(result.code==0){
                    Log.i("成功","=================================数据回传成功=================================")
                }else{
                    Log.e("失败","=================================数据回传失败${result.msg}==========================")
                }
            }
        })
    }
}