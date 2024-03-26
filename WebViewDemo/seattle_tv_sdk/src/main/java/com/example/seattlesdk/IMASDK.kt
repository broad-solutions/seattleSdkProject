package com.example.seattlesdk

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.ima.ImaAdsLoader
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView
import com.example.seattle_tv_sdk.R
import com.google.ads.interactivemedia.v3.api.AdEvent

class IMASDK{
    private var player: ExoPlayer? = null
    private var adsLoader: ImaAdsLoader? = null
    private var adTagUrl:String?=null
    private var playerView:PlayerView?=null
    private var activity:AppCompatActivity?=null

    fun sendBroadcastMessage(context: Context, message: String) {
        val intent = Intent("com.example.ACTION_AD_EVENT")
        intent.putExtra("message", message)
        context.sendBroadcast(intent)
    }

    @OptIn(UnstableApi::class)
    fun SdkStart(activity: AppCompatActivity, videoUrl: String,containerId: Int,type:String) {
        this.activity=activity
        // 获取 BlankFragment 中的 PlayerView
        if(type=="Section"){
            adTagUrl=("https://pubads.g.doubleclick.net/gampad/ads?iu=/21775744923/external/"
                    + "vmap_ad_samples&sz=640x480&cust_params=sample_ar%3Dpremidpostpod&ciu_szs=300x250&"
                    + "gdfp_req=1&ad_rule=1&output=vmap&unviewed_position_start=1&env=vp&impl=s&cmsid=496&"
                    + "vid=short_onecue&correlator=")
        }else if(type=="Banner"){
            adTagUrl="https://googleads.g.doubleclick.net/pagead/ads?client=ca-video-pub-4881399016139609&slotname=vast-test-2&ad_type=video_text_image&description_url=http%3A%2F%2Fxgamesworld.com&max_ad_duration=30000&videoad_start_delay=-1&vpmute=1&vpa=auto"
        }else if(type=="Splash"){
            adTagUrl="https://sdk-ios.ad.smaato.net/oapi/v6/ad?pub=1100042525&adspace=131616976&format=interstitial&dimension=full_320x480&secure=0&lang=en&coppa=0&extensions=omid&iosadid=29D12863-C46D-4A27-9F86-E3783DDC2284&iosadtracking=1&connection=WiFi&devicemodel=x86_64&bundle=com.smaato.sdk-demo-ios&client=sdkios_21.1.1&fcid=10281DE5-4E8F-4354-805D-6C7DEC743124&height=896&vastver=4&linearity=1&privacyIcon=1&width=414&videotype=interstitial&response=XML&mraidver=3"
        }
        val fragment = BlankFragment()
        activity.supportFragmentManager.beginTransaction()
            .add(containerId, fragment)
            .commit()
        // 等待 fragment 被添加到 activity 中
        activity.supportFragmentManager.executePendingTransactions()
        Handler(Looper.getMainLooper()).postDelayed({
            playerView = fragment.view?.findViewById(R.id.videoView_fragment)
            if (playerView != null) {
                // 找到了 PlayerView
                adsLoader = ImaAdsLoader.Builder(activity)
                    .setAdEventListener(buildAdEventListener(type))
                    .build()

                val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(activity)
                val mediaSourceFactory: MediaSource.Factory = DefaultMediaSourceFactory(dataSourceFactory)
                    .setLocalAdInsertionComponents(
                        { adsLoader },
                        playerView!!
                    )


                player = ExoPlayer.Builder(activity).setMediaSourceFactory(mediaSourceFactory).build()
                playerView!!.setPlayer(player)
                adsLoader?.setPlayer(player)

                val contentUri = Uri.parse(videoUrl)
                val adTagUri = Uri.parse(adTagUrl)
                val mediaItem = MediaItem.Builder()
                    .setUri(contentUri)
                    .setAdsConfiguration(MediaItem.AdsConfiguration.Builder(adTagUri).build())
                    .build()

                player?.setMediaItem(mediaItem)
                player?.prepare()
                player?.playWhenReady = true
            } else {
                Log.e("playerView", "PlayerView not found in BlankFragment layout")
            }
        }, 100) // 1000毫秒 = 1秒

    }
    private fun buildAdEventListener(type:String): AdEvent.AdEventListener {
        return AdEvent.AdEventListener { event: AdEvent ->
            val eventType = event.type
            if (eventType == AdEvent.AdEventType.AD_PROGRESS) {
                return@AdEventListener
            }
            if(type=="Splash"&&eventType.toString()=="PAUSED"){
                activity?.let { sendBroadcastMessage(it, eventType.toString()) }
            }
        }
    }

    fun releasePlayer() {
        adsLoader!!.setPlayer(null)
        playerView!!.setPlayer(null)
        player!!.release()
        player = null
    }
    interface AdPlaybackCompletionListener  {
        fun onAdPlaybackComplete()
    }
}