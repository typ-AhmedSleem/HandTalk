package com.typ.handtalk.core.repository

import com.typ.handtalk.core.models.Word
import com.typ.handtalk.core.models.signs.HandSign
import com.typ.handtalk.utils.frameResult
import com.typ.handtalk.utils.sequenceOfGestures

object Words {

    internal val WORDS = arrayOf(
        Word(
            1,
            "Hello", sequenceOfGestures(
                frameResult(
                    HandSign("OPEN_PALM"),
                )
            )
        ),
        Word(
            2,
            "My",
            sequenceOfGestures(
                frameResult(
                    HandSign("CLOSED_FIST"),
                ),
            )
        ),
        Word(
            3,
            "Name",
            sequenceOfGestures(
                frameResult(
                    HandSign("THUMB_UP"),
                )
            )
        ),
        Word(
            4,
            "is",
            sequenceOfGestures(
                frameResult(
                    HandSign("THUMB_DOWN"),
                )
            )
        )
    )

}