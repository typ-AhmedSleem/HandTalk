package com.typ.handtalk.core.repository

import com.typ.handtalk.core.enums.MovingDirection
import com.typ.handtalk.core.models.MovingSign

object Signs {

    class Separator : MovingSign(
        label = "OPEN_PALM",
        distance = 5,
        direction = MovingDirection.LEFT,
    )

}