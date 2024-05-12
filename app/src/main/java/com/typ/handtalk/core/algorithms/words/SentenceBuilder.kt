package com.typ.handtalk.core.algorithms.words

import android.util.Log
import com.typ.handtalk.core.algorithms.words.enums.SentenceFlag
import com.typ.handtalk.core.models.Sentence
import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.repository.Sentences

class SentenceBuilder {

    private var mismatchCounter = 3

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

    val isValid: Boolean
        get() {
            val expected = this.expectedSentence ?: return false
            return currentSentence.startsSameAs(expected)
        }

    /**
     * Append a word to the current sentence.
     * @return true if the word is appended, false otherwise
     */
    fun appendWord(word: Word): SentenceFlag {
        Log.v("SentenceBuilder", "Trying to append word: $word")
        // ===== 1st case: Sentence is empty ===== //
        val current = currentSentence
        if (current.isEmpty) {
            Log.d("SentenceBuilder", "Empty sentence. Going on 1st scenario.")
            // Append the word
            words.add(word)
            Log.d("SentenceBuilder", "Appended word to sentence. words: $words")
            // Check if it expects a sentence or not
            if (expectedSentence != null) {
                // Adding this word will make a valid sentence later
                Log.d("SentenceBuilder", "Ensured that expected sentence is not null.")
                return SentenceFlag.WORD_ACCEPTED
            }
            // Sentence starting with this word has no expected sentence
            Log.i("SentenceBuilder", "No expected sentence. Treated as SingleWord.")
            return SentenceFlag.SINGLE_WORD
        } else {
            Log.i("SentenceBuilder", "Non-empty sentence. Going on 2nd scenario.")
            // ===== 2nd case: Sentence is not empty ===== //
            if (isSentenceCompleted) {
                // Sentence is completed
                Log.d("SentenceBuilder", "Sentence is completed. $this")
                return SentenceFlag.SENTENCE_COMPLETED
            }
            if (word == currentSentence.lastWord) {
                // Repeating word detected
                Log.d("SentenceBuilder", "Repeating word detected. word= $word, builder= $this")
                return SentenceFlag.REPEATING_WORD
            }
            val expected = expectedSentence!!
            // Check if word is matching the next expected word in the sentence
            val nextWord = expected.getNextWordBetween(current)
            Log.d("SentenceBuilder", "nextWord: $nextWord. same= ${word == nextWord}")
            if (word == nextWord) {
                words.add(word)
                Log.d("SentenceBuilder", "Appended word to sentence. words: $words")
                // Check if sentence is completed
                if (isSentenceCompleted) {
                    Log.d("SentenceBuilder", "Sentence is completed. ${this.currentSentence.arabic}")
                    return SentenceFlag.SENTENCE_COMPLETED
                } else {
                    // Sentence is not completed
                    Log.d("SentenceBuilder", "Sentence is not completed. ${this.currentSentence.arabic}")
                    return SentenceFlag.WORD_ACCEPTED
                }
            } else {
                // Word mismatch detected
                Log.d("SentenceBuilder", "Word mismatch detected. word= $word, builder= $this")
                mismatchCounter--
                if (mismatchCounter == 0) {
                    // Mismatch counter is exhausted
                    Log.i("SentenceBuilder", "Mismatch counter exhausted.")
                    return SentenceFlag.WORD_REJECTED
                } else {
                    // Mismatch counter still fresh
                    Log.i("SentenceBuilder", "Mismatch counter still fresh. rem=$mismatchCounter")
                    return SentenceFlag.WORD_MISMATCH
                }
            }

        }

//        // Check if the last word is same as the the given word
//        if (word == currentSentence.lastWord) return SentenceFlag.REPEATING_WORD
//
//        words.add(word)
//        return SentenceFlag.WORD_ACCEPTED
    }

    fun reset() {
        words = mutableListOf()
        resetMismatchCounter()
    }

    fun resetMismatchCounter() {
        mismatchCounter = DEFAULT_MISMATCH_COUNT
    }

    override fun toString(): String {
        return "SentenceBuilder(isValid= $isValid, isSentenceCompleted= $isSentenceCompleted, curr= $currentSentence, exp= $expectedSentence)"
    }

    companion object {
        private const val DEFAULT_MISMATCH_COUNT = 3
    }

}