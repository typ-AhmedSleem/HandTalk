package com.typ.handtalk.core.repository

import com.typ.handtalk.core.enums.MovingDirection
import com.typ.handtalk.core.models.signs.MovingSign

object Signs {

    class Separator : MovingSign(
        label = "OPEN_PALM",
        score = 0.5f,
        distance = 5,
        direction = MovingDirection.RIGHT_TO_LEFT,
    )

}