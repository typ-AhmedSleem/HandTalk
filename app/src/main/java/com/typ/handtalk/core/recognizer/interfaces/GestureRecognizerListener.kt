package com.typ.handtalk.core.recognizer.interfaces

import com.typ.handtalk.core.recognizer.RecognizerError
import com.typ.handtalk.core.recognizer.ResultBundle

interface GestureRecognizerListener {
    fun onRecognizerResult(resultBundle: ResultBundle)

    fun onRecognizerError(error: RecognizerError)
}