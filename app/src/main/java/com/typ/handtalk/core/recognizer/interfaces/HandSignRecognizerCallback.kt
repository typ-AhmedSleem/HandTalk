package com.typ.handtalk.core.recognizer.interfaces

import com.typ.handtalk.core.recognizer.RecognizerError
import com.typ.handtalk.core.resolvers.models.FrameResult
import com.typ.handtalk.utils.Height
import com.typ.handtalk.utils.Width

interface HandSignRecognizerCallback {

    /**
     * Called when the recognizer is ready to be used.
     */
    fun onRecognizerReady()

    /**
     * Called when a hand is recognized by the recognizer or both hand.
     */
    fun onRecognizeHands(frameResult: FrameResult, inputShape: Pair<Height, Width>)

    /**
     * Invoked when the recognizer detected another sign that is
     * when compared to the previous sign, they're not equal.
     */
    fun onHandSignChanged(oldResult: FrameResult, newResult: FrameResult)

    /**
     * Invoked when the recognizer detected sign that is same
     * as the previous sign.
     *
     * MotionEstimation algorithm will be fed with this frame to
     * calculate the distance travelled by each hand
     * and at which direction they are moving.
     */
    fun onSameSignRecognized(result: FrameResult)

    /**
     * Called when the recognizer encounters an error.
     */
    fun onRecognizerError(error: RecognizerError)

    /**
     * Called when the hands are no longer visible in the processed frame.
     */
    fun onHandsDisappear()

}