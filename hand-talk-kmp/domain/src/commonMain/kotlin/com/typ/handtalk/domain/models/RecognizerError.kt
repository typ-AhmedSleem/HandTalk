package com.typ.handtalk.domain.models

sealed class RecognizerError(val message: String?) {
    class GPUError(message: String?) : RecognizerError(message)
    class ModelLoadError(message: String?) : RecognizerError(message)
    class OtherError(message: String?) : RecognizerError(message)
    class UnknownError(message: String?) : RecognizerError(message)
}
