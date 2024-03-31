package com.typ.handtalk.core.models.hands

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.typ.handtalk.core.models.signs.HandSign

open class Hand(
    val idx: Int,
    var sign: HandSign?,
    var landmarks: List<List<NormalizedLandmark>>
)