package com.typ.handtalk.core.recognizer

sealed class RecognizerError(
    val code: Int,
    override val message: String?,
    val reason: String? = null
) : Exception(message) {

    class GPUError(reason: String? = null) : RecognizerError(201, "Gesture recognizer failed to initialize.", reason)
    class OtherError(reason: String? = null) : RecognizerError(202, "Gesture recognizer failed to initialize.", reason)
    class UnknownError(reason: String? = null) : RecognizerError(203, "An unknown error has occurred.", reason)

    override fun toString(): String {
        return "RecognizerError(code=$code, message=$message, reason=$reason)"
    }

}
