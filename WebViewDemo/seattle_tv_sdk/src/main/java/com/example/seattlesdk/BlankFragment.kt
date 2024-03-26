package com.example.seattlesdk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.SimpleExoPlayer
import androidx.media3.ui.PlayerView
import com.example.seattle_tv_sdk.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment : Fragment() {
    private var playerView: PlayerView? = null
    @UnstableApi
    private var player: SimpleExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerView =  view.findViewById(R.id.videoView_fragment)
//        Log.e("playerView",view.toString())
        // 创建 SimpleExoPlayer 实例
        player = SimpleExoPlayer.Builder(requireContext()).build()

        // 准备播放器
        player?.prepare()
        // 将 PlayerView 与播放器关联
        playerView?.player = player

    }
    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
    @OptIn(UnstableApi::class)
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            if (playerView != null) {
                playerView!!.onResume()
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            if (playerView != null) {
                playerView!!.onResume()
            }
        }
    }

    @OptIn(UnstableApi::class)
    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            if (playerView != null) {
                playerView!!.onPause()
            }
            releasePlayer()
        }
    }

    @OptIn(UnstableApi::class)
    override fun onStop() {
        super.onStop()
//        if (Util.SDK_INT > 23) {
//            if (playerView != null) {
//                playerView!!.onPause()
//            }
//            releasePlayer()
//        }
        // 判断 Fragment 是否可见
        if (isVisible) {
            println("Fragment is visible")
        } else {
            println("Fragment is not visible")
        }

        // 判断 Fragment 是否已经添加到 Activity 中
        if (isAdded) {
            println("Fragment is added to Activity")
        } else {
            println("Fragment is not added to Activity")
        }

        // 判断 Fragment 是否被销毁
        if (isDetached || isRemoving) {
            println("Fragment is destroyed")
        } else {
            println("Fragment is not destroyed")
        }
    }


    @OptIn(UnstableApi::class)
    private fun releasePlayer() {
        playerView!!.setPlayer(null)
        player!!.release()
        player = null
    }
}