package com.typ.handtalk.core.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

/**
 * Class that utilizes TTS (TextToSpeech) apis
 * to speak out sentences we obtain from
 * Sign language to Text translation
 */
class TextSpeaker {

    private var status: Status = Status.NOT_READY
    private lateinit var engine: TextToSpeech

    private val engineInitialized: Boolean
        get() = ::engine.isInitialized

    /**
     * Setup tts instance with desired language and voice.
     *
     * !NOTE: This engine only speaks US english now but it
     * !should speak Arabic as primary language.
     */
    fun initializeEngine(context: Context, onInitializedCallback: (() -> Unit)?) {
        // Create tts instance
        if (!engineInitialized) {
            engine = TextToSpeech(context) { ttsStatus ->
                when (ttsStatus) {
                    TextToSpeech.SUCCESS -> {
                        setupEngine()
                        onInitializedCallback?.invoke()
                    }

                    else -> Status.FAILED
                }
            }
        }
    }

    private fun setupEngine() {
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
            Log.d(TAG, "Language is set to: ${ARABIC.language}.")
        } else Log.w(TAG, "Language isn't available.")
        // Set voice
        val voices = engine.voices
        if (voices.isEmpty()) {
            Log.w(TAG, "No voices available for this language.")
        } else {
            status = Status.READY
            Log.d(TAG, "Voice is set to: ${engine.voice}")
//            voices.filter { it.locale == ARABIC }.randomOrNull(Random(System.currentTimeMillis()))?.let {
//                engine.voice = it
//                status = Status.READY
//                Log.d(TAG, "Voice is set to: $it.")
//            }
        }
    }

    fun speak(sentence: String?) {
        if (sentence.isNullOrEmpty()) return
        if (status == Status.READY) {
            // Speak out sentence
            Log.d(TAG, "Speaking out: $sentence")
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