package com.typ.handtalk.core.models

open class HandSign(
    val label: String,
    val score: Float = 0f
) {

    override fun toString(): String {
        return "HandSign('$label' -> $score)"
    }
}
