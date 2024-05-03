package com.typ.handtalk.core.a2s

import com.typ.handtalk.core.a2s.playables.A2SignPlayable
import com.typ.handtalk.core.a2s.playables.A2SignPlayableImage
import com.typ.handtalk.core.a2s.playables.A2SignPlayableVideo

object A2SPlayableRepository {

    private val IMAGES_REPO = mapOf(
        "اهلا" to A2SignPlayableImage("hi.jpg"),
        "انا" to A2SignPlayableImage("my.jpg"),
        "اسمي" to A2SignPlayableImage("name.jpg"),
    )

    private val VIDEOS_REPO = mapOf(
        "احمد" to A2SignPlayableVideo("ahmed.mp4"),
    )

    @JvmStatic
    fun getPlayableForWord(word: String): A2SignPlayable? {
        return when (val finalWord = word.lowercase()) {
            in IMAGES_REPO -> {
                IMAGES_REPO[finalWord]
            }

            in VIDEOS_REPO -> {
                VIDEOS_REPO[finalWord]
            }

            else -> {
                null
            }
        }
    }
}
