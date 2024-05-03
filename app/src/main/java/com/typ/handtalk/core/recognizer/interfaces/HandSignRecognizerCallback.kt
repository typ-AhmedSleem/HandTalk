package com.typ.handtalk.core.recognizer.interfaces

import com.typ.handtalk.core.recognizer.RecognizerError
import com.typ.handtalk.core.recognizer.ResultBundle
import com.typ.handtalk.core.resolvers.models.FrameResult

typealias Width = Int
typealias Height = Int

interface HandSignRecognizerCallback {

    fun onRecognizerResult(resultBundle: ResultBundle)

    /**
     * Called when the recognizer is ready to be used.
     */
    fun onRecognizerReady()

    /**
     * Called when a hand is recognized by the recognizer or both hand.
     */
    fun onRecognizeHands(frameResult: FrameResult, inputShape: Pair<Height, Width>)

    /**
     * Called when the recognizer encounters an error.
     */
    fun onRecognizerError(error: RecognizerError)

    fun onHandsDisappear()

}