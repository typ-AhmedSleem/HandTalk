package com.typ.handtalk.impl

import com.typ.handtalk.domain.models.FrameResult
import com.typ.handtalk.domain.models.HandTalkEvent
import com.typ.handtalk.domain.models.RecognizerError
import com.typ.handtalk.impl.launcher.launchHandTalkRecognizer
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

actual class HandTalk actual constructor(
    private val recognizer: HandSignRecognizer
) {
    actual val frameState: StateFlow<FrameResult?> = recognizer.frameState
    actual val handtalkEvents: SharedFlow<HandTalkEvent> = recognizer.handtalkEvents
    actual val errors: SharedFlow<RecognizerError> = recognizer.errors

    actual fun start() {
        recognizer.setup()
        launchHandTalkRecognizer(recognizer)
    }

    actual fun stop() {
        // Emit StopRecognizer event so the activity can reactively finish itself
        recognizer.emitEvent(HandTalkEvent.StopRecognizer)
        recognizer.release()
    }
}
