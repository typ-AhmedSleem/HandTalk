package com.typ.handtalk.core.models

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class RightHand(sign: HandSign?, landmarks: List<List<NormalizedLandmark>>) : Hand(idx = 1, sign, landmarks)