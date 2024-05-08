package com.typ.handtalk.core.repository

import com.typ.handtalk.core.models.Conversation

object Conversations {

    @JvmStatic
    val CONVERSATIONS = arrayOf(
        Conversation(
            1,
            arrayOf(
                Sentences.SENTENCES[0],
                Sentences.SENTENCES[1],
                Sentences.SENTENCES[2],
                Sentences.SENTENCES[3],
            )
        ),
        Conversation(
            2,
            arrayOf(
                Sentences.SENTENCES[4],
                Sentences.SENTENCES[5],
                Sentences.SENTENCES[6],
                Sentences.SENTENCES[7],
            )
        )
    )

}