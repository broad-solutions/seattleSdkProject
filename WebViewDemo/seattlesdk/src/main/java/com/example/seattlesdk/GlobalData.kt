package com.example.seattlesdk

import android.annotation.SuppressLint
import android.util.SparseArray

@SuppressLint("UseSparseArrays")
internal object GlobalData {
    private var token:String=""
    private var packageName:String=""
    private var status:Boolean=true//webview的显示状态
    val errorCountMap = SparseArray<Int>()//统计错误数量
    val noRepartList: MutableList<String> = mutableListOf()//访问过的urlList
    fun setList(url:String){
        if(!noRepartList.contains(url)){
            noRepartList.add(url)
        }
    }
    fun getSize(): Int {
        return noRepartList.size
    }
    fun updateToken(newToken:String){
        token=newToken
    }

    fun getToken(): String {
        return token
    }
    fun setPackageName(pkName:String){
        packageName=pkName
    }
    fun getPackageName():String{
        return packageName
    }
    fun setStatus(boolean: Boolean): Boolean {
        status=boolean
        return status
    }
    fun getStatus():Boolean{
        return status
    }
    fun getErrorArray(): SparseArray<Int> {
        return errorCountMap
    }
}