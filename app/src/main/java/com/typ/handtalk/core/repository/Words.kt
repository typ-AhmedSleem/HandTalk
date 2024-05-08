package com.typ.handtalk.core.repository

import com.typ.handtalk.core.models.Word
import com.typ.handtalk.utils.rhResult
import com.typ.handtalk.utils.sequenceOfGestures

object Words {

    internal val WORDS = arrayOf(
        Word(
            1,
            "السلام عليكم ورحمة الله وبركاته",
            sequenceOfGestures(
                rhResult("11"),
                rhResult("1"),
                rhResult("2"),
                rhResult("5"),
                rhResult("4"),
                rhResult("6"),
            )
        ),
        Word(
            2,
            "صباح الخير",
            sequenceOfGestures(
                rhResult("7"),
                rhResult("9"),
                rhResult("19"),
                rhResult("11"),
            )
        ),
        Word(
            3,
            "ممكن",
            sequenceOfGestures(
                rhResult("12"),
            )
        ),
        Word(
            4,
            "كليه",
            sequenceOfGestures(
                rhResult("17"),
                rhResult("18"),
            )
        ),
        Word(
            5,
            "الم", sequenceOfGestures(
                rhResult("29"),
                rhResult("30"),
            )
        ),
        Word(
            6,
            "شكراً", sequenceOfGestures(
                rhResult("11"), // todo: change to 13
            )
        ),
        Word(
            7,
            "دكتور", sequenceOfGestures(
                rhResult("23"),
                rhResult("24"),
            )
        ),
        Word(
            8,
            "المعده", sequenceOfGestures(
                rhResult("40"),
            )
        ),
        Word(
            9,
            "باطنة", sequenceOfGestures(
                rhResult("48"),
                rhResult("47"),
            )
        ),
        Word(
            10,
            "مكان", sequenceOfGestures(
                rhResult("38"),
                rhResult("41"),
            )
        ),
        Word(
            11,
            "في", sequenceOfGestures(
                rhResult("28"),
            )
        ),
    )

}