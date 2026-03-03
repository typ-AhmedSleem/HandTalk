package com.typ.handtalk.impl

import com.typ.handtalk.domain.models.FrameResult
import com.typ.handtalk.domain.models.HandTalkEvent
import com.typ.handtalk.domain.models.RecognizerError
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

expect class HandTalk {
    val frameState: StateFlow<FrameResult?>
    val handtalkEvents: SharedFlow<HandTalkEvent>
    val errors: SharedFlow<RecognizerError>

    fun start()
    fun stop()
}
