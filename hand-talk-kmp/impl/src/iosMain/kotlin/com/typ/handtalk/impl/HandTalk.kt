package com.typ.handtalk.impl

import com.typ.handtalk.domain.models.FrameResult
import com.typ.handtalk.domain.models.HandTalkEvent
import com.typ.handtalk.domain.models.RecognizerError
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

actual class HandTalk actual constructor(recognizer: HandSignRecognizer) {
    actual val frameState: StateFlow<FrameResult?> = recognizer.frameState
    actual val handtalkEvents: SharedFlow<HandTalkEvent> = recognizer.handtalkEvents
    actual val errors: SharedFlow<RecognizerError> = recognizer.errors

    actual fun start() {
        // iOS implementation stub
    }

    actual fun stop() {
        // iOS implementation stub
    }
}
