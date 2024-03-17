package com.typ.handtalk.core.models

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

class LeftHand(sign: HandSign?, landmarks: List<List<NormalizedLandmark>>) : Hand(idx = 0, sign, landmarks)