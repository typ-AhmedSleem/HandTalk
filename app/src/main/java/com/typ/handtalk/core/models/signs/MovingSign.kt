package com.typ.handtalk.core.models.signs

import com.typ.handtalk.core.enums.MovingDirection

open class MovingSign(
    label: String,
    score: Float = 0f,
    val distance: Int,
    val direction: MovingDirection,
    val duration: Float = 0f
) : HandSign(label, score)