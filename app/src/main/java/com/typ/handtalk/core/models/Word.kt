package com.typ.handtalk.core.models

import com.typ.handtalk.core.algorithms.sequencer.GestureSequence

class Word(
    val id: Int,
    val arabicText: String,
    val signs: GestureSequence
)