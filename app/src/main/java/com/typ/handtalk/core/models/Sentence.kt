package com.typ.handtalk.core.models

data class Sentence(
    val words: Array<Word>
) {

    val allSigns: List<String>
        get() {
            val signs = mutableListOf<String>()
            for (word in words) {
                word.signs.signs.forEach {
                    signs.add(it.rhsLabel.toString())
                }
            }
            return signs
        }

    val arabic: String
        get() = words.joinToString(" ") { it.arabicText }

    val firstWord: Word?
        get() = words.firstOrNull()

    val lastWord: Word?
        get() = words.lastOrNull()

    val length: Int = words.size

    fun startsSameAs(other: Sentence): Boolean {
        return firstWord == other.firstWord
    }

    fun sameOrMoreLengthThan(other: Sentence?): Boolean {
        return length >= (other?.length ?: 5)
    }

    override fun toString(): String {
        return "sen($arabic)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Sentence) return false

        if (!words.contentEquals(other.words)) return false

        return true
    }

    override fun hashCode(): Int {
        return words.contentHashCode()
    }

    fun sameAs(another: Sentence?): Boolean {
        if (another == null) return false
        if (length != another.length) return false
        return words.zip(another.words).all { (a, b) -> a == b }
    }

    fun clone() = Sentence(words.copyOf())


}