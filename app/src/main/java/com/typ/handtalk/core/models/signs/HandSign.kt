package com.typ.handtalk.core.models.signs

open class HandSign(
    val label: String,
    val score: Float = 0f
) {

    override fun toString(): String {
        return "HandSign('$label' @ %.2f)".format(score)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HandSign) return false

        if (label != other.label) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = label.hashCode()
        result = 31 * result + score.hashCode()
        return result
    }
}
