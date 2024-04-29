package com.typ.handtalk.core.recognizer.interfaces

import com.typ.handtalk.core.resolvers.models.FrameResult

interface HandRecognizerInternalCallback {

    /**
     * Invoked when the right hand is not detected
     * or either detected but its sign is not recognized
     * (aka: 'None' label).
     */
    fun onHandDisappeared()

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

}