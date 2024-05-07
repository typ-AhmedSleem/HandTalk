package com.typ.handtalk.ui.a2s.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.VideoView
import com.typ.handtalk.R
import com.typ.handtalk.core.a2s.playables.A2SignPlayable
import com.typ.handtalk.core.a2s.playables.A2SignPlayableImage
import com.typ.handtalk.core.a2s.playables.A2SignPlayableVideo
import com.typ.handtalk.databinding.LayoutA2sPlayablePlayerViewBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This view class is a composition of two views: VideoView and ImageView.
 * The VideoView will be used to display A2SignPlayableVideo.
 * The ImageView will be used to display A2SignPlayableImage.
 * It also provides some functions to interact with with the view like:
 *      - displayVideo(A2SignPlayableVideo).
 *      - displayImage(A2SignPlayableImage).
 *      - displayImageSequence(A2SignPlayableImageSequence). [PLANNED FOR LATER]
 */
class A2SPlayablePlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val binding = LayoutA2sPlayablePlayerViewBinding.bind(
        inflate(
            context,
            R.layout.layout_a2s_playable_player_view,
            this
        )
    )

//    private var currentlyActiveJob: Job? = null

    var playing: Boolean = false
        private set

    init {
        // Show sample imageview if inEditMode
        if (isInEditMode) binding.ivPlayable.visibility = VISIBLE
        else reset() // Reset the player
    }

    fun reset() {
        binding.ivPlayable.visibility = INVISIBLE
        binding.videoPlayable.visibility = INVISIBLE

        binding.ivPlayable.setImageDrawable(null)
        binding.videoPlayable.setVideoURI(null)

//        currentlyActiveJob?.cancel()
        playing = false
    }

    suspend fun display(playable: A2SignPlayable?, callback: () -> A2SignPlayable?) {
        if (playable == null) return
        playing = true
        when (playable) {
            is A2SignPlayableImage -> {
                playable.loadInto(binding.ivPlayable)
            }

            is A2SignPlayableVideo -> {
                displayVideo(playable, callback)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun displayVideo(video: A2SignPlayableVideo, callback: () -> A2SignPlayable?) {
        if (!ensureVideoInCache(video)) return
        // Display the video from its file path and hide the ImageView
        withContext(Dispatchers.Main) {
            binding.ivPlayable.visibility = INVISIBLE
            binding.videoPlayable.apply {
                visibility = VISIBLE
                setOnCompletionListener {
                    val next = callback()
                    if (next != null && next is A2SignPlayableVideo) {
//                        startPlayback(this, next)
                        if (next.cacheVideo(context)) {
                            startPlayback(this, next)
                        }
                    }
                }
                startPlayback(this, video)
            }
        }
    }

    private suspend fun ensureVideoInCache(video: A2SignPlayableVideo): Boolean {
        // Return the function if the video doesn't exist in Assets
        if (!video.existsInAssets(context.assets)) return false
        Log.i(TAG, "displayVideo: Video at path '${video.getVideoPath(context.cacheDir)}' exists in assets folder.")
        // Save video file from Assets to cache if not exist in cache
        if (!video.existsInCache(context.cacheDir)) {
            Log.i(TAG, "displayVideo: Video doesn't exist in cache folder. Caching it...")
            val cached = video.cacheVideoOnIO(context)
            if (!cached) {
                Log.i(TAG, "displayVideo: Video can't be cached.")
                return false
            }
            Log.i(TAG, "displayVideo: Video has been cached.")
        }
        Log.i(TAG, "displayVideo: Video at path '${video.filePath}' exists in cache folder.")
        return true
    }

    private fun startPlayback(player: VideoView, video: A2SignPlayableVideo) {
        Log.i(TAG, "startPlayback: Starting video playback for: '${video.filename}'")
        player.setVideoPath(video.getVideoPath(context.cacheDir))
        player.start()
    }

    companion object {
        const val TAG = "A2SPlayablePlayerView"
    }

}