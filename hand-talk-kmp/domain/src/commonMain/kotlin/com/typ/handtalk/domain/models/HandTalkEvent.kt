package com.typ.handtalk.domain.models

sealed class HandTalkEvent {
    data class SignRecognized(val lhs: HandSign?, val rhs: HandSign?) : HandTalkEvent()
    data class SignChanged(val old: HandSign, val new: HandSign) : HandTalkEvent()
    object HandsDisappeared : HandTalkEvent()
    
    /** Internal event emitted by [HandTalk.stop] to signal the recognizer activity to finish. */
    object StopRecognizer : HandTalkEvent()
}
