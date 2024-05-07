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
            "اهلا انا اسمي احمد", sequenceOfGestures(
                rhResult(handSign("Open_Palm"))
            )
        ),
        Word(
            2,
            "انا",
            sequenceOfGestures(
                rhResult(handSign(label = "Closed_Fist")),
            )
        ),
        Word(
            3,
            "اسمي",
            sequenceOfGestures(
                rhResult(handSign("Thumb_Up"))
            )
        ),
        Word(
            4,
            "is",
            sequenceOfGestures(
                rhResult(handSign("Thumb_Down"))
            )
        ),
        Word(
            4,
            "احمد",
            sequenceOfGestures(
                frameResult(handSign("Pointing_Up")),
                frameResult(handSign("Victory")),
                frameResult(handSign("Thumb_Up")),
                frameResult(handSign("Open_Palm")),
                frameResult(handSign("ILoveYou")),
            )
        )
    )

}