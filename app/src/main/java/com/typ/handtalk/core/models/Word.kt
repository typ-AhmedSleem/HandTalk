package com.typ.handtalk.core.models

import com.typ.handtalk.core.models.signs.HandSign

class Word(
    val id: Int,
    val arabicText: String,
    val signs: Array<HandSign>
)