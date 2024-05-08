package com.typ.handtalk.core.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import kotlin.random.Random

/**
 * Class that utilizes TTS (TextToSpeech) apis
 * to speak out sentences we obtain from
 * Sign language to Text translation
 */
class TextSpeaker(context: Context) {

    private var status: Status = Status.NOT_READY
    private val engine = TextToSpeech(context) { ttsStatus ->
        status = when (ttsStatus) {
            TextToSpeech.SUCCESS -> {
                initializeEngine()
                Status.READY
            }

            else -> Status.FAILED
        }
    }

    /**
     * Setup tts instance with desired language and voice.
     *
     * !NOTE: This engine only speaks US english now but it
     * !should speak Arabic as primary language.
     */
    private fun initializeEngine() {
        // Set language
        if (engine.isLanguageAvailable(ARABIC) == TextToSpeech.LANG_AVAILABLE) {
            val result = engine.setLanguage(ARABIC)
            when (result) {
                TextToSpeech.LANG_MISSING_DATA -> {
                    Log.w(TAG, "Arabic tts data or voice is missing.")
                    return
                }

                TextToSpeech.LANG_NOT_SUPPORTED -> {
                    Log.w(TAG, "Arabic tts is not supported.")
                    return
                }
            }
            engine.language = Locale("ar")
            Log.d(TAG, "Language is set to: ${ARABIC.language}.")
        } else Log.w(TAG, "Language isn't available.")
        // Set voice
        val voices = engine.voices
        if (voices.isEmpty()) {
            Log.w(TAG, "No voices available for this language.")
        } else {
            voices.filter { it.locale == ARABIC }.randomOrNull(Random(System.currentTimeMillis()))?.let {
                engine.voice = it
                Log.d(TAG, "Voice is set to: $it.")
            }
        }
    }

    fun speak(sentence: String) {
        if (status == Status.READY) {
            // Speak out sentence
            engine.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            // Can't speak text because of error or failed to init
            Log.w(TAG, "Can't speak due to error or failed to initialize engine. EngineStatus= $status")
        }
    }

    enum class Status {
        NOT_READY,
        READY,
        FAILED
    }

    companion object {
        const val TAG = "TextSpeaker"
        val ARABIC = Locale("ar")
    }

}