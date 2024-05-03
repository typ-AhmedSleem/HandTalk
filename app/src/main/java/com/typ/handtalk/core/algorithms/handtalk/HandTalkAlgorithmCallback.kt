package com.typ.handtalk.core.algorithms.handtalk

import com.typ.handtalk.core.errors.HandTalkError
import com.typ.handtalk.core.resolvers.models.FrameResult
import com.typ.handtalk.utils.Height
import com.typ.handtalk.utils.Width

interface HandTalkAlgorithmCallback {

    /**
     * Called when a new gesture is identified.
     */
    fun onIdentifyGesture(frameResult: FrameResult)

    /**
     * Called when a new word is identified.
     */
    fun onIdentifyNewWord(word: String)

    /**
     * Called when the algorithm has identified a the full sentence.
     */
    fun onTranslateFullSentence(sentence: String)

    /**
     * Called when the recognizer reports that primary hand has disappeared.
     */
    fun onHandsDisappear()

    /**
     * Called when recognizer results landmarks for both hands.
     */
    fun onReadyToDrawLandmarks(frameResult: FrameResult, inputShape: Pair<Width, Height>)

    fun onErrorOccurred(error: HandTalkError)

}
