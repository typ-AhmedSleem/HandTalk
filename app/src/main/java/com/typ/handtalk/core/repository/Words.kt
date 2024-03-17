package com.typ.handtalk.core.repository

import com.typ.handtalk.core.models.HandSign
import com.typ.handtalk.core.models.Word

object Words {

    internal val WORDS_1 = arrayOf(
        Word(1, "Hello", arrayOf(
            HandSign("OPEN_PALM"),
        )),
        Word(2, "My", arrayOf(
            HandSign("CLOSED_FIST")
        )),
        Word(3, "Name", arrayOf(

        )),
        Word(4, "is", emptyArray()),
    )

}