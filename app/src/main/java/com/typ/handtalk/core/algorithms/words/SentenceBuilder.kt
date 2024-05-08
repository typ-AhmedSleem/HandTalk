package com.typ.handtalk.core.algorithms.words

import com.typ.handtalk.core.models.Sentence
import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.repository.Sentences

class SentenceBuilder {

    private var words = mutableListOf<Word>()

    val expectedSentence: Sentence?
        get() {
            if (words.isEmpty()) return null
            // Get the from repo the sentence with same first word
            return Sentences.getSentenceByFirstWord(words.first())
        }

    val currentSentence: Sentence
        get() = Sentence(words.toTypedArray())

    fun appendWord(word: Word): Boolean {
        expectedSentence?.let {
            if (currentSentence.length >= it.length) return true
        }
        words.add(word)
        return expectedSentence?.let {
            return currentSentence.length >= it.length
        } ?: false
    }

    fun reset() {
        words = mutableListOf()
    }

}