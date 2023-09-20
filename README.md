# seattleSdkProject
### seattleSdk 主要功能
集成webviewApi展示广告相关内容</br>
** 
💡本SDK为Android SDK，仅支持安卓应用的调用
** 

### 集成
api project(":seattlesdk")</br>
** 支持的SDK最低版本为21 **
### 使用方法
使用本SDK时，需要在当前Activity实现 **AnalyticsDelegate**，示例如下





    class MainActivity : AppCompatActivity(), AnalyticsDelegate by AnalyticsDelegateImpl() {

        private lateinit var appBarConfiguration: AppBarConfiguration
        private lateinit var binding: ActivityMainBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_main, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_settings -> true
                else -> super.onOptionsItemSelected(item)
            }
        }

        override fun onSupportNavigateUp(): Boolean {
            val navController = findNavController(R.id.FirstFragment)
            return navController.navigateUp(appBarConfiguration)
                    || super.onSupportNavigateUp()
        }
    }

### 参数说明
  Context 上下文对象 </br>
  packageName 当前应用包名</br>
  token 使用我们提供的接口请求获得token </br>
  callback 用于处理初始化返回数据的对象   </br>  
  callback 示例
  
     private val callback = object : (String) -> Unit {
         override fun invoke(str: String) {
             println("Received callback message: $str")
         }
     }
     
### 方法说明
   💡本SDK向外暴露两个方法</br>
       1. 初始化 initSdk("应用包名",token,callback)</br>
       2. 指定显示位置 showContent(view)</br>
### token获取
     通过appkey 和 appsecrect 调用指定接口来获取token</br>
     appkey 和 appsecrect会发放给使用本SDK调用者
### SDK调用
       1. 声明SDK 示例如下：
          private lateinit var mySdk: SeattleSdk
          mySdk=SeattleSdk(context)
       2. 调用获取token接口拿到token
       3. 初始化SDK如下：
          mySdk.initSdk("你的应用包名",token,callback)
        4. 初始化成功后，指定SDK显示的位置，如下：
           private lateinit var container: ViewGroup
           container=findViewById(R.id.xxxx)
           mySDK.showContent(container)