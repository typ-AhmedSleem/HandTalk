package com.typ.handtalk.core.a2s

import com.typ.handtalk.core.a2s.playables.A2SignPlayable
import com.typ.handtalk.core.a2s.playables.A2SignPlayableImage
import com.typ.handtalk.core.a2s.playables.A2SignPlayableVideo

object A2SPlayableRepository {

    private val IMAGES_REPO = mapOf(
        "hi" to A2SignPlayableImage("hi"),
        "my" to A2SignPlayableImage("my"),
        "name" to A2SignPlayableImage("name"),
        "is" to A2SignPlayableImage("is"),
    )

    private val VIDEOS_REPO = mapOf(
        "hi" to A2SignPlayableVideo("hi"),
        "my" to A2SignPlayableVideo("my"),
        "name" to A2SignPlayableVideo("name"),
        "is" to A2SignPlayableVideo("is"),
        "ahmed" to A2SignPlayableVideo("ahmed"),
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
