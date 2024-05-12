package com.typ.handtalk.core.repository

import com.typ.handtalk.core.models.Sentence
import com.typ.handtalk.core.models.Word

object Sentences {

    @JvmStatic
    fun getSentenceByFirstWord(firstWord: Word): Sentence? {
        return Conversations.DEFAULT_CONVERSATION.sentences.firstOrNull { it.words.contains(firstWord) }
    }

    @JvmStatic
    fun getSentence(idx: Int): Sentence {
        return SENTENCES[idx]
    }

    @JvmStatic
    fun getSentenceByWords(words: Array<Word>): Sentence? {
        return SENTENCES.firstOrNull { it.words.asList().containsAll(words.toList()) }
    }

    @JvmStatic
    val SENTENCES = arrayOf(
        // =========== CONVERSATION 1 =========== //
        Sentence(
            arrayOf(
                Words.WORDS[0],
                Words.WORDS[14],
                Words.WORDS[15],
                Words.WORDS[16],
                Words.WORDS[17],
            )
        ),
        Sentence(
            arrayOf(
                Words.WORDS[2],
                Words.WORDS[12],
            )
        ),
        Sentence(
            arrayOf(
                Words.WORDS[3],
                Words.WORDS[13],
                Words.WORDS[9],
                Words.WORDS[11],
            )
        ),
        Sentence(
            arrayOf(Words.WORDS[5])
        ),
        // ========== CONVERSATION 2 ========== //
        Sentence(
            arrayOf(
                Words.WORDS[1],
            )
        ),
        Sentence(
            arrayOf(
                Words.WORDS[11],
                Words.WORDS[6],
                Words.WORDS[7],
            )
        ),
        Sentence(
            arrayOf(
                Words.WORDS[4],
                Words.WORDS[10],
                Words.WORDS[8],
            )
        ),
        Sentence(
            arrayOf(
                Words.WORDS[5],
                Words.WORDS[6],
            )
        ),
    )

}