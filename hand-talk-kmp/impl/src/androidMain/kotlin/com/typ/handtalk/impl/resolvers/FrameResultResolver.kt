package com.typ.handtalk.impl.resolvers

import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.typ.handtalk.domain.models.FrameResult
import com.typ.handtalk.domain.models.HandSign
import com.typ.handtalk.domain.models.Landmark
import com.typ.handtalk.domain.models.LeftHand
import com.typ.handtalk.domain.models.RightHand

object FrameResultResolver {
    const val RIGHT_HAND_INDEX = 1
    const val LEFT_HAND_INDEX = 0

    fun resolve(rawResult: GestureRecognizerResult): FrameResult {
        var rightHand: RightHand? = null
        var leftHand: LeftHand? = null

        for (i in 0 until rawResult.handedness().size) {
            val hand = rawResult.handedness()[i].first()
            val gesture = rawResult.gestures()[i].first()
            val landmarks = rawResult.landmarks()[i].map { Landmark(it.x(), it.y(), it.z()) }

            val handSign = HandSign(gesture.categoryName(), gesture.score())

            if (hand.index() == RIGHT_HAND_INDEX) {
                rightHand = RightHand(handSign, landmarks)
            } else if (hand.index() == LEFT_HAND_INDEX) {
                leftHand = LeftHand(handSign, landmarks)
            }
        }

        return FrameResult(
            leftHand = leftHand,
            rightHand = rightHand,
            timestamp = rawResult.timestampMs()
        )
    }
}
