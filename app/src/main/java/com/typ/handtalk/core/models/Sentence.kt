package com.typ.handtalk.core.models

data class Sentence(
    val words: Array<Word>
) {

    val arabic: String
        get() = words.joinToString(" ", postfix = ".") { it.arabicText }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Sentence) return false

        if (!words.contentEquals(other.words)) return false

        return true
    }

    override fun hashCode(): Int {
        return words.contentHashCode()
    }

}