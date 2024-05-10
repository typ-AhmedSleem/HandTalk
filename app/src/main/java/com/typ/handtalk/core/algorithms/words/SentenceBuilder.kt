package com.typ.handtalk.core.algorithms.words

import android.util.Log
import com.typ.handtalk.core.models.Sentence
import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.repository.Sentences

class SentenceBuilder {

    private var words = mutableListOf<Word>()

    val expectedSentence: Sentence?
        get() {
            if (words.isEmpty()) return null
            val sentence = Sentences.getSentenceByFirstWord(words.first())
            Log.d("SentenceBuilder", "expectedSentence: $sentence")
            // Get the from repo the sentence with same first word
            return sentence
        }

    val currentSentence: Sentence
        get() {
            val sentence = Sentence(words.toTypedArray())
            Log.d("SentenceBuilder", "currentSentence: $sentence")
            return sentence
        }

    val isSentenceCompleted: Boolean
        get() = currentSentence.sameAs(expectedSentence)

    /**
     * Append a word to the current sentence.
     * @return true if the word is appended, false otherwise
     */
    fun appendWord(word: Word): Boolean {
        // Check if the last word is same as the the given word
        if (word == currentSentence.lastWord) return false

        words.add(word)
        return true
    }

    fun reset() {
        words = mutableListOf()
    }

    override fun toString(): String {
        return "SentenceBuilder(curr= $currentSentence, exp= $expectedSentence)"
    }

}