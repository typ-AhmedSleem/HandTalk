package com.typ.handtalk.core.resolvers

import android.util.Log
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.typ.handtalk.core.models.Hand
import com.typ.handtalk.core.models.HandSign

object RecognizerResultResolver {

    private const val TAG = "RecognizerResultResolver"

    @JvmStatic
    fun resolveResults(raw: GestureRecognizerResult): Array<Hand> {
//        /*! Create a suitable result class and don't use hand this way */
//        TODO("To be implemented")

        val hands = mutableListOf<Hand>()
        raw.handedness().zip(raw.gestures()).forEachIndexed { _, (rawHands, rawGestures) ->
            val rawHand = rawHands.first()
            val rawGesture = rawGestures.first()
            hands += Hand(
                idx = rawHand.index(),
                sign = HandSign(rawGesture.categoryName(), rawGesture.score()),
                landmarks = emptyList()
            )
        }

        if (hands.isNotEmpty()) {
            Log.d(TAG, "Resolved raw results and returned $hands hand.")
        }
        return hands.toTypedArray()
    }

}
