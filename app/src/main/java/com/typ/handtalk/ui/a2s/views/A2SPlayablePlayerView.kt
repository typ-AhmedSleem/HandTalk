package com.typ.handtalk.ui.a2s.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.typ.handtalk.R
import com.typ.handtalk.core.a2s.playables.A2SignPlayable
import com.typ.handtalk.core.a2s.playables.A2SignPlayableImage
import com.typ.handtalk.core.a2s.playables.A2SignPlayableVideo
import com.typ.handtalk.databinding.LayoutA2sPlayablePlayerViewBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    init {
        // Initially, hide both of the views
        binding.ivPlayable.visibility = INVISIBLE
        binding.videoPlayable.visibility = INVISIBLE
        // Show the imageview if inEditMode
        if (isInEditMode) binding.ivPlayable.visibility = VISIBLE
        else {
            // Reset both of the views
            binding.ivPlayable.setImageDrawable(null)
            binding.videoPlayable.setVideoURI(null)
        }
    }

    fun display(playable: A2SignPlayable) {
        when (playable) {
            is A2SignPlayableImage -> {
                playable.loadInto(binding.ivPlayable)
            }

            is A2SignPlayableVideo -> {
                displayVideo(playable)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun displayVideo(video: A2SignPlayableVideo) {
        GlobalScope.launch {
            // Return the function if the video doesn't exist in Assets
            if (!video.existsInAssets(context.assets)) return@launch
            // Save video file from Assets to cache if not exist in cache
            if (!video.existsInCache(context.cacheDir)) {
                video.copyToCache(context)
            }
            // Display the video from its file path and hide the ImageView
            withContext(Dispatchers.Main) {
                binding.videoPlayable.setVideoPath(video.getVideoPath(context.cacheDir))
                binding.ivPlayable.visibility = INVISIBLE
                binding.videoPlayable.visibility = VISIBLE
            }
        }
    }


}