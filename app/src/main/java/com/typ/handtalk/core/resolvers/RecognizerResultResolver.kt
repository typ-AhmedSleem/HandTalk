package com.typ.handtalk.core.resolvers

import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.typ.handtalk.core.models.Hand

object RecognizerResultResolver {

    @JvmStatic
    fun resolveResults(rawResult: GestureRecognizerResult): Array<Hand> {
        /*! Create a suitable result class and don't use hand this way */
        TODO("To be implemented")
    }

}
