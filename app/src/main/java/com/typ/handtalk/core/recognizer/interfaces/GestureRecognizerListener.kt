package com.typ.handtalk.core.recognizer

interface GestureRecognizerListener {
    fun onRecognizerResult(resultBundle: ResultBundle)

    fun onRecognizerError(error: RecognizerError)
}