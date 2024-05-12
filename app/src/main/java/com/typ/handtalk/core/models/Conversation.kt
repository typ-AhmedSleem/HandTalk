package com.typ.handtalk.core.models

data class Conversation(
    val id: Int,
    val sentences: Array<Sentence>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Conversation) return false

        if (id != other.id) return false
        if (!sentences.contentEquals(other.sentences)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + sentences.contentHashCode()
        return result
    }

}