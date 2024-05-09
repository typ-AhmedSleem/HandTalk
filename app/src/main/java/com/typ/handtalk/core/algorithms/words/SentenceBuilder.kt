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