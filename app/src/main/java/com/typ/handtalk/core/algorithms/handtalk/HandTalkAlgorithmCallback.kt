package com.typ.handtalk.core.algorithms.handtalk

import com.typ.handtalk.core.errors.HandTalkError
import com.typ.handtalk.core.models.ImageShape
import com.typ.handtalk.core.models.Sentence
import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.resolvers.models.FrameResult

interface HandTalkAlgorithmCallback {

    /**
     * Called when a new gesture is identified.
     */
    fun onIdentifyGesture(frameResult: FrameResult)

    /**
     * Called when a new word is identified that flagged as SINGLE_WORD from builder.
     */
    fun onIdentifyNewWord(word: Word)

    /**
     * Called when a new word is identified that flagged as WORD_ACCEPTED from builder.
     */
    fun onIdentifySentenceWord(word: Word)


    /**
     * Called when the algorithm has identified a the full sentence.
     */
    fun onTranslateFullSentence(sentence: Sentence)

    /**
     * Called when the algorithm has identified a partial sentence.
     * @param currentSentence The current sentence.
     * @param expectedSentence The expected sentence.
     */
    fun onSuggestSentence(currentSentence: Sentence, expectedSentence: Sentence)

    /**
     * Called when the recognizer reports that primary hand has disappeared.
     */
    fun onHandsDisappear()

    /**
     * Called when recognizer results landmarks for both hands.
     */
    fun onReadyToDrawLandmarks(frameResult: FrameResult, inputShape: ImageShape)

    fun onErrorOccurred(error: HandTalkError)

}
