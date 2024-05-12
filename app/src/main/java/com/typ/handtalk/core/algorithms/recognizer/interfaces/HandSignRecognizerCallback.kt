package com.typ.handtalk.core.algorithms.recognizer.interfaces

import com.typ.handtalk.core.algorithms.recognizer.RecognizerError
import com.typ.handtalk.core.models.ImageShape
import com.typ.handtalk.core.resolvers.models.FrameResult

interface HandSignRecognizerCallback {

    /**
     * Called when the recognizer is ready to be used.
     */
    fun onRecognizerReady()

    /**
     * Called when a hand is appeared in the processed frame.
     */
    fun onHandAppeared(frame: FrameResult, inputShape: ImageShape)

    /**
     * Called when a hand is recognized by the recognizer or both hand.
     */
    fun onRecognizeHands(frameResult: FrameResult, inputShape: ImageShape)

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
    fun onSameSignRecognized(result: FrameResult, inputShape: ImageShape)

    /**
     * Called when the recognizer encounters an error.
     */
    fun onRecognizerError(error: RecognizerError)

    /**
     * Called when the hands are no longer visible in the processed frame.
     */
    fun onHandsDisappear()

}