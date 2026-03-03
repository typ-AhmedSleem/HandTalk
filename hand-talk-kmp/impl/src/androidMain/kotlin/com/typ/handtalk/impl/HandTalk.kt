package com.typ.handtalk.impl

import com.typ.handtalk.domain.models.FrameResult
import com.typ.handtalk.domain.models.HandTalkEvent
import com.typ.handtalk.domain.models.RecognizerError
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

actual class HandTalk(
    val recognizer: HandSignRecognizer
) {
    val recognizerInitialized: Boolean get() = recognizer.isInitialized

    fun setupGestureRecognizer() = recognizer.setup()
    actual val frameState: StateFlow<FrameResult?> = recognizer.frameState
    actual val handtalkEvents: SharedFlow<HandTalkEvent> = recognizer.handtalkEvents
    actual val errors: SharedFlow<RecognizerError> = recognizer.errors

    actual fun start() {
        recognizer.setup()
    }

    actual fun stop() {
        recognizer.release()
    }
}
