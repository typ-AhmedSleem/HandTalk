package com.typ.handtalk.core.resolvers

import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.typ.handtalk.core.models.hands.LeftHand
import com.typ.handtalk.core.models.hands.RightHand
import com.typ.handtalk.core.models.signs.HandSign
import com.typ.handtalk.core.resolvers.models.FrameResult

object FrameResultResolver {

    private const val TAG = "FrameResultResolver"
    const val RIGHT_HAND_INDEX = 1
    const val LEFT_HAND_INDEX = 0

    @JvmStatic
    fun resolve(rawResult: GestureRecognizerResult): FrameResult? {
        // * Obtain hands and its gestures
        val (rightHand, leftHand) = identifyHands(rawResult)
        // * Return null if no hands were identified
        if (rightHand == null && leftHand == null) return null
        // * Return new FrameResult instance
        return FrameResult(leftHand, rightHand)
    }

    private fun identifyHands(raw: GestureRecognizerResult): Pair<RightHand?, LeftHand?> {
        var rightHand: RightHand? = null
        var leftHand: LeftHand? = null

        for (i in 0..<raw.handedness().size) {
            val hand = raw.handedness()[i].first()
            val gesture = raw.gestures()[i].first()

            if (hand.index() == RIGHT_HAND_INDEX) {
                // * Found RIGHT hand
                rightHand = RightHand(
                    sign = HandSign(
                        gesture.categoryName(),
                        gesture.score()
                    ),
                    landmarks = raw.landmarks()[i]
                )
                // Pass the iteration to avoid unnecessary left hand checking
                continue
            }
            if (hand.index() == LEFT_HAND_INDEX) {
                // * Found LEFT hand
                leftHand = LeftHand(
                    sign = HandSign(
                        gesture.categoryName(),
                        gesture.score()
                    ),
                    landmarks = raw.landmarks()[i]
                )
            }
        }
        // No hands are detected at all
        return rightHand to leftHand
    }

}
