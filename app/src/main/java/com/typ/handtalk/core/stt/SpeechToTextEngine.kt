package com.typ.handtalk.core.stt


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

class SpeechToTextEngine(
    context: Context,
    val callback: (String) -> Unit
) : RecognitionListener {

    // Speech recognizer
    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
        this.setRecognitionListener(this@SpeechToTextEngine)
    }

    // Speech recognizer intent
    private val recognizerIntent: Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
    }

    /**
     * Calls SpeechRecognition instance to start listening for
     * speech spoken by user and receive results on callback
     */
    fun listenToUser() {
        // Start listening
        recognizer.startListening(this.recognizerIntent)
    }

    /**
     * CALLBACK: called when recognizer is ready
     * to listen for speech
     */
    override fun onReadyForSpeech(params: Bundle?) {
    }

    /**
     * CALLBACK: called when user starts speaking
     * and recognizer starts detecting that
     */
    override fun onBeginningOfSpeech() {
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }

    /**
     * CALLBACK: called when user stops speaking
     * and recognizer detects that
     */
    override fun onEndOfSpeech() {
    }

    /**
     * CALLBACK: called when an error happened
     * while or after listening to user speech
     */
    override fun onError(code: Int) {
        val error = when (code) {
            SpeechRecognizer.ERROR_NO_MATCH -> SpeechRecognitionError.SRNoMatchError()
            else -> {
                SpeechRecognitionError.SRNotSupportedError()
            }
        }
        Log.i(TAG, "onError: $error")
    }

    /**
     * CALLBACK: called after recognizer detects end of speech
     * and converts speech into text
     */
    override fun onResults(results: Bundle?) {
        val speech = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        callback(speech?.firstOrNull() ?: "")
    }

    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

    companion object {
        const val TAG = "ChatBot"
    }

}
