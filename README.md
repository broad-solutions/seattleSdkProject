# [seattleSdkProject](https://github.com/broad-solutions/seattleSdkProject#seattlesdkproject) 说明文档

## seattleSdk主要功能
	 本sdk核心功能为：提供统一安全的H5广告页面链接的配置、
  支持配置多个链接、
  支持链接的动态切换和间隔设置、
  支持webviewapi动态开关以满足谷歌的要求。
  为合作伙伴提供统一一致透明方便的SDK。

  注意：本sdk为Android sdk,仅支持Android应用调用。

## 集成
	在您的应用中的build.gradle内引入本sdk
	引入方式：api project(":seattlesdk")
 ![1698307215365](https://github.com/broad-solutions/seattleSdkProject/assets/29178778/e6626b07-b958-48d8-8a81-ed281fc7a457)

## 使用方法
	使用本SDK时，需要在当前Activity实现 **AnalyticsDelegate**，代码示例如下：
```
class MainActivity : AppCompatActivity(), AnalyticsDelegate by AnalyticsDelegateImpl() {
		//your code
		```
		```
}
```
关键点为 **AnalyticsDelegate by AnalyticsDelegateImpl()**
![无标题](https://github.com/broad-solutions/seattleSdkProject/assets/29178778/e01a5efe-6521-4851-b44b-88c845f60971)



## 参数说明
	调用本sdk需要向sdk传递 4 个参数，分别是：
		1. Context 上下文对象
		2. packageName 当前应用包名
		3. token 使用接口请求获得的token
		4. callback 用于接收sdk反馈给您的消息
		
	 token 获取方法：
		我们将提供获取token的数据接口，接口为GET请求，调用后返回token，具体如何调用该接口，根据您的实际情况决定。
  
		token返回数据结构如下:
  
		{
		  code:0,
		  message:"Success"
		  data:"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsic2VhdHRsZS1iYWNrZW5kLW9hdXRoIiwic2VhdHRsZS1iYWNrZW5kLWFwaSJdLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNjk4MzE2MTIwLCJhdXRob3JpdGllcyI6WyJBU"
             }
       
       其中 data 即为您需要的token (注意：示例中的数据均为虚假数据，请勿直接使用)
  ## sdk api说明
	  本sdk向外曝露两个方法：
	  1. 初始化： initSdk("应用包名",token,callback)
	  2. 指定sdk中的webview显示位置： showContent(view)
## 使用sdk
	   1. 声明sdk
	   2. 调用token接口获取token
	   3. 初始化sdk
	   4. 在初始化成功后显示sdk中的webview
	   示例：
	        private lateinit var mySdk: SeattleSdk
	        mySdk=SeattleSdk(context)
	        mySdk.initSdk("yourappName",token,callback)
	        private lateinit var container: ViewGroup
	        container=findViewById(R.id.xxxx)
            mySDK.showContent(container)
        
![无标题](https://github.com/broad-solutions/seattleSdkProject/assets/29178778/a5d79702-c923-428d-814e-18aa09c70e31)
![无标题2](https://github.com/broad-solutions/seattleSdkProject/assets/29178778/c1d57d6c-b589-4ce0-9c72-70f46c176e55)

