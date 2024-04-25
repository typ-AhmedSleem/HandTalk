package com.typ.handtalk.core.a2s.playables

import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class A2SignPlayableImageSequence(
    private val sequence: Array<A2SignPlayableImage>
) {

    suspend fun playOn(iv: ImageView) {
        sequence.forEach { img ->
            withContext(Dispatchers.Main) {
                img.loadInto(iv)
            }
            delay(DELAY_BETWEEN_FRAMES)
        }
    }

    companion object {
        private const val DELAY_BETWEEN_FRAMES = 500L
    }

}