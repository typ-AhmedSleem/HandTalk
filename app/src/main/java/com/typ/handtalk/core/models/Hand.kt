package com.typ.handtalk.core.models

import com.google.mediapipe.formats.proto.LandmarkProto.Landmark
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

open class Hand(
    val idx: Int,
    var sign: HandSign?,
    var landmarks: List<List<NormalizedLandmark>>
)