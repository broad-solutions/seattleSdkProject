package com.example.seattlesdk

internal enum class EventList {
    OpenStarted,//开始加载
    OpenSuccess,//加载成功
    WebViewClicked,//点击事件
    UserStayTimeInMillisecond,//webview存在时长
    WebsiteSwitched,//url切换
    SessionTimeInMillisecond,//website存在时长
    TotalWebsitesInSession,//Session里展示过的网站总数
    WebsiteLoadError,//website错误事件，对应错误列表详见飞书
    pageLoadFinished,//页面加载完成时长
}