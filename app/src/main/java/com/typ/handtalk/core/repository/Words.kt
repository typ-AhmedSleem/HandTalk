package com.typ.handtalk.core.repository

import com.typ.handtalk.core.models.Word
import com.typ.handtalk.utils.frameResult
import com.typ.handtalk.utils.handSign
import com.typ.handtalk.utils.rhResult
import com.typ.handtalk.utils.sequenceOfGestures

object Words {

    internal val WORDS = arrayOf(
        Word(
            1,
            "السلام",
            sequenceOfGestures(
                rhResult("48"),
//                rhResult("31"),
            )
        ),
        Word(
            2,
            "صباح",
            sequenceOfGestures(
//                rhResult("44"),
                rhResult("40"),
            )
        ),
        Word(
            3,
            "ممكن",
            sequenceOfGestures(
                rhResult("35"), // 12
            )
        ),
        Word(
            4,
            "كلية",
            sequenceOfGestures(
//                rhResult("43"),
                frameResult(
                    rhs = handSign("43"),
                    lhs = handSign("47"),
                )
            )
        ),
        Word(
            5,
            "الم", sequenceOfGestures(
                rhResult("17"),
                rhResult("23"),
            )
        ),
        Word(
            6,
            "شكراً", sequenceOfGestures(
                rhResult("36"),
            )
        ),
        Word(
            7,
            "دكتور", sequenceOfGestures(
                rhResult("19"),
//                rhResult("14"),
            )
        ),
        Word(
            8,
            "المعدة", sequenceOfGestures(
                rhResult("7"),
            )
        ),
        Word(
            9,
            "الباطنة", sequenceOfGestures(
                rhResult("7"),
            )
        ),
        Word(
            10,
            "مكانها", sequenceOfGestures(
                rhResult("20"),
                rhResult("26"),
            )
        ),
        Word(
            11,
            "في", sequenceOfGestures(
                rhResult("13"),
            )
        ),
        Word(
            12,
            "فين", sequenceOfGestures(
                rhResult("50"),
            )
        ),
        Word(
            13,
            "سؤال", sequenceOfGestures(
                rhResult("51"),
            )
        ),
        Word(
            14,
            "حقوق", sequenceOfGestures(
                rhResult("39"),
            )
        ),
        Word(
            15,
            "عليكم",
            sequenceOfGestures(
                rhResult("32"),
            )
        ),
        Word(
            16,
            "ورحمة",
            sequenceOfGestures(
                rhResult("33"),
            )
        ),
        Word(
            17,
            "الله",
            sequenceOfGestures(
                rhResult("41"),
            )
        ),
        Word(
            18,
            "وبركاته",
            sequenceOfGestures(
                rhResult("49"),
            )
        ),
        Word(
            19,
            "الخير",
            sequenceOfGestures(
                rhResult("38"),
//                rhResult("37"),
            )
        ),

        //  Region: Repeating word with same overlapping signs
//        Word(
//            7,
//            "دكتور", sequenceOfGestures(
//                rhResult("14"),
//            )
//        ),
        // End Region
    )

}