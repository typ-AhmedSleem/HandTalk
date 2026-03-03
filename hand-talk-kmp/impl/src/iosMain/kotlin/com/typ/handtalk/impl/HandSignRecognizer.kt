package com.typ.handtalk.impl

import com.typ.handtalk.domain.models.FrameResult
import com.typ.handtalk.domain.models.HandTalkEvent
import com.typ.handtalk.domain.models.RecognizerError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

actual class HandSignRecognizer {
    private val _frameState = MutableStateFlow<FrameResult?>(null)
    actual val frameState: StateFlow<FrameResult?> = _frameState.asStateFlow()

    private val _handtalkEvents = MutableSharedFlow<HandTalkEvent>()
    actual val handtalkEvents: SharedFlow<HandTalkEvent> = _handtalkEvents.asSharedFlow()

    private val _errors = MutableSharedFlow<RecognizerError>()
    actual val errors: SharedFlow<RecognizerError> = _errors.asSharedFlow()

    actual fun setup() {
        // iOS implementation stub
    }

    actual fun release() {
        // iOS implementation stub
    }
}
