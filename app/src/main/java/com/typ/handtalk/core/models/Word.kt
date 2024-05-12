package com.typ.handtalk.core.models

import com.typ.handtalk.core.algorithms.sequencer.GestureSequence

data class Word(
    val id: Int,
    val arabicText: String,
    val signs: GestureSequence
) {

    override fun toString(): String {
        return "(${id}:${arabicText})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Word) return false

        // Check exclusively for words of ids 8, 9 as they are the same
        if (id == 8 && other.id == 9) return true
        if (id == 9 && other.id == 8) return true

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}