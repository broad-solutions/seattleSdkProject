package com.example.webviewdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.seattlesdk.IMASDK
import com.example.webviewdemo.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity(){

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentList: List<Fragment>
    private var currentFragmentIndex = 0

    private val videoUrls = listOf(
        "https://example.com/video1.mp4",
        "https://example.com/video2.mp4",
        "https://example.com/video3.mp4"
    )
    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        GlobalScope.launch(Dispatchers.IO) {
            val client = OkHttpClient.Builder()
                .hostnameVerifier { _, _ -> true } // 禁用主机名验证
                .build()

            val request = Request.Builder()
                .url("http://clientapi.pubadding.com/oauth/token?client_id=S766494354&client_secret=17fadc2e6b084965ab5e690bf4bd8f6c&grant_type=client_credentials")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json=response.body?.string()
                    val gson = Gson()
                    val accessTokenResponse = gson.fromJson(json, Datautils::class.java)
                    val token=accessTokenResponse.access_token
                    print("token:${token}")
                } else {
                    println("请求失败: ${response.message}")
                }
            }
        }


        val videoUrl="https://storage.googleapis.com/gvabox/media/samples/stock.mp4"

        IMASDK().SdkStart(this,videoUrl,R.id.videoContentMain,"Banner")



        binding.textView8.setOnClickListener {
            val intent = Intent(this, thrid::class.java)
            startActivity(intent)
//            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun switchFragment() {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragmentContainer, fragmentList[currentFragmentIndex])
//            .commit()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    override fun onDestroy() {
        super.onDestroy()
        IMASDK().releasePlayer()
    }
}