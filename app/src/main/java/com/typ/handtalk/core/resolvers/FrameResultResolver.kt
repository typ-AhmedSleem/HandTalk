package com.typ.handtalk.core.resolvers

import android.util.Log
import com.google.mediapipe.tasks.components.containers.Category
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.typ.handtalk.core.models.HandSign
import com.typ.handtalk.core.resolvers.models.FrameResult

object FrameResultResolver {

    private const val TAG = "FrameResultResolver"
    private const val RIGHT_HAND_INDEX = 1
    private const val LEFT_HAND_INDEX = 0

    @JvmStatic
    fun resolve(raw: GestureRecognizerResult): FrameResult {
        // * Combine hand results with gesture results
        val combined = raw.handedness().zip(raw.gestures())
        // * Obtain hands and its gestures
        val rightHandSign = obtainRightHandSign(combined)
        val leftHandSign = obtainLeftHandSign(combined)
        // Region: Start Log
//        Log.i(
//            TAG, """FrameResultResolver::resolve
//            rightHandSign => $rightHandSign
//            leftHandSign => $leftHandSign
//        """.trimIndent()
//        )
        // Region: End Log
        // * Return new FrameResult instance
        return FrameResult(leftHandSign, rightHandSign)
    }

    private fun obtainLeftHandSign(combined: List<Pair<List<Category>, List<Category>>>): HandSign? {
        for ((rHand, rGesture) in combined) {
            val hand = rHand.first()
            val gesture = rGesture.first()
            if (hand.index() != LEFT_HAND_INDEX) {
                // * Not the LEFT hand. Pass this iteration
                continue
            }
            // This is the LEFT hand we are looking for
            return HandSign(
                gesture.categoryName(),
                gesture.score()
            )
        }
        // LEFT hand wasn't found
        return null
    }

    private fun obtainRightHandSign(combined: List<Pair<List<Category>, List<Category>>>): HandSign? {
        for ((rHand, rGesture) in combined) {
            val hand = rHand.first()
            val gesture = rGesture.first()
            if (hand.index() != RIGHT_HAND_INDEX) {
                // * Not the RIGHT hand. Pass this iteration
                continue
            }
            // This is the RIGHT hand we are looking for
            return HandSign(
                gesture.categoryName(),
                gesture.score()
            )
        }
        // RIGHT hand wasn't found
        return null
    }

}
