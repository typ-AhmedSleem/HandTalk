package com.typ.handtalk.core.a2s

import com.typ.handtalk.core.a2s.playables.A2SignPlayable

data class A2STranslationHistoryRecord(
    val sentence: String,
    val signs: Array<A2SignPlayable>
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is A2STranslationHistoryRecord) return false

        if (sentence.lowercase().trim() != other.sentence.lowercase().trim()) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sentence.hashCode()
        result = 31 * result + signs.contentHashCode()
        return result
    }
}
