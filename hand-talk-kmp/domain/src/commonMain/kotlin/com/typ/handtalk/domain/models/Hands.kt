package com.typ.handtalk.domain.models

sealed class Hand(
    val idx: Int,
    val sign: HandSign?,
    val landmarks: List<Landmark>
) {
    fun hasSameSignAs(other: HandSign?): Boolean {
        return sign == other
    }

    override fun toString(): String {
        return "${this::class.simpleName}(sign=${sign?.label})"
    }
}

class LeftHand(sign: HandSign?, landmarks: List<Landmark>) : Hand(0, sign, landmarks)
class RightHand(sign: HandSign?, landmarks: List<Landmark>) : Hand(1, sign, landmarks)
