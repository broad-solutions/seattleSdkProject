// Copyright 2022 Google LLC
package com.example.seattlesdk

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.VideoView
import com.google.ads.interactivemedia.v3.api.AdPodInfo
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory
import com.google.ads.interactivemedia.v3.api.player.AdMediaInfo
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer.VideoAdPlayerCallback
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate
import java.util.Timer
import java.util.TimerTask

/** Example implementation of IMA's VideoAdPlayer interface.  */

class VideoAdPlayerAdapter(private val videoPlayer: VideoView, audioManager: AudioManager) :
    VideoAdPlayer {
    private val audioManager: AudioManager
    private val videoAdPlayerCallbacks: MutableList<VideoAdPlayerCallback> = ArrayList()
    private var timer: Timer? = null
    private var adDuration = 0

    // The saved ad position, used to resumed ad playback following an ad click-through.
    private var savedAdPosition = 0
    private var loadedAdMediaInfo: AdMediaInfo? = null

    init {
        videoPlayer.setOnCompletionListener { notifyImaOnContentCompleted() }
        this.audioManager = audioManager
        Log.e("VideoAdPlayerAdapter","VideoAdPlayerAdapter")
    }

    override fun addCallback(videoAdPlayerCallback: VideoAdPlayerCallback) {
        videoAdPlayerCallbacks.add(videoAdPlayerCallback)
    }

    override fun loadAd(adMediaInfo: AdMediaInfo, adPodInfo: AdPodInfo) {
        // This simple ad loading logic works because preloading is disabled. To support
        // preloading ads your app must maintain state for the currently playing ad
        // while handling upcoming ad downloading and buffering at the same time.
        // See the IMA Android preloading guide for more info:
        // https://developers.google.com/interactive-media-ads/docs/sdks/android/client-side/preload
        loadedAdMediaInfo = adMediaInfo

        Log.e("loadAd","loadAd")
    }
    override fun pauseAd(adMediaInfo: AdMediaInfo) {
        Log.i(LOGTAG, "pauseAd")
        savedAdPosition = videoPlayer.getCurrentPosition()
        stopAdTracking()
    }

    override fun playAd(adMediaInfo: AdMediaInfo) {
        Log.e("playAd", adMediaInfo.url)
        videoPlayer.setVideoURI(Uri.parse(adMediaInfo.url))
        videoPlayer.setOnPreparedListener { mediaPlayer: MediaPlayer ->
            adDuration = mediaPlayer.duration
            if (savedAdPosition > 0) {
                mediaPlayer.seekTo(savedAdPosition)
            }
            mediaPlayer.start()
            startAdTracking()
        }
        videoPlayer.setOnErrorListener { _: MediaPlayer?, errorType: Int, _: Int ->
            notifyImaSdkAboutAdError(
                errorType
            )
        }
        videoPlayer.setOnCompletionListener {
            savedAdPosition = 0
            notifyImaSdkAboutAdEnded()
        }
    }

    override fun release() {
        // any clean up that needs to be done.
    }

    override fun removeCallback(videoAdPlayerCallback: VideoAdPlayerCallback) {
        videoAdPlayerCallbacks.remove(videoAdPlayerCallback)
    }

    override fun stopAd(adMediaInfo: AdMediaInfo) {
        Log.i(LOGTAG, "stopAd")
        stopAdTracking()
    }

    /** Returns current volume as a percent of max volume.  */
    override fun getVolume(): Int {
        return (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
    }

    private fun startAdTracking() {
        Log.i(LOGTAG, "startAdTracking")
        if (timer != null) {
            return
        }
        timer = Timer()
        val updateTimerTask: TimerTask = object : TimerTask() {
            override fun run() {
                val progressUpdate = getAdProgress()
                notifyImaSdkAboutAdProgress(progressUpdate)
            }
        }
        timer!!.schedule(updateTimerTask, POLLING_TIME_MS, INITIAL_DELAY_MS)
    }

    private fun notifyImaSdkAboutAdEnded() {
        Log.i(LOGTAG, "notifyImaSdkAboutAdEnded")
        savedAdPosition = 0
        for (callback in videoAdPlayerCallbacks) {
            callback.onEnded(loadedAdMediaInfo!!)
        }
    }

    private fun notifyImaSdkAboutAdProgress(adProgress: VideoProgressUpdate) {
        for (callback in videoAdPlayerCallbacks) {
            callback.onAdProgress(loadedAdMediaInfo!!, adProgress)
        }
    }

    /**
     * @param errorType Media player's error type as defined at
     * https://cs.android.com/android/platform/superproject/+/master:frameworks/base/media/java/android/media/MediaPlayer.java;l=4335
     * @return True to stop the current ad playback.
     */
    private fun notifyImaSdkAboutAdError(errorType: Int): Boolean {
        Log.i(LOGTAG, "notifyImaSdkAboutAdError")
        when (errorType) {
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> Log.e(
                LOGTAG,
                "notifyImaSdkAboutAdError: MEDIA_ERROR_UNSUPPORTED"
            )

            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> Log.e(
                LOGTAG,
                "notifyImaSdkAboutAdError: MEDIA_ERROR_TIMED_OUT"
            )

            else -> {}
        }
        for (callback in videoAdPlayerCallbacks) {
            callback.onError(loadedAdMediaInfo!!)
        }
        return true
    }

    fun notifyImaOnContentCompleted() {
        Log.i(LOGTAG, "notifyImaOnContentCompleted")
        for (callback in videoAdPlayerCallbacks) {
            callback.onContentComplete()
        }
    }

    private fun stopAdTracking() {
        Log.i(LOGTAG, "stopAdTracking")
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    override fun getAdProgress(): VideoProgressUpdate {
        val adPosition = videoPlayer.getCurrentPosition().toLong()
        return VideoProgressUpdate(adPosition, adDuration.toLong())
    }

    companion object {

        private const val LOGTAG = "IMABasicSample"
        private const val POLLING_TIME_MS: Long = 250
        private const val INITIAL_DELAY_MS: Long = 250
    }
}
