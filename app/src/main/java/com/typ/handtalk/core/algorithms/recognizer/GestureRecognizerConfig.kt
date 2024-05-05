package com.typ.handtalk.core.algorithms.recognizer

import com.google.mediapipe.tasks.core.Delegate

class GestureRecognizerConfig(
    var delegate: Delegate = Delegate.GPU,
    var minHandDetectionConfidence: Float = HandSignRecognizer.DEFAULT_HAND_DETECTION_CONFIDENCE,
    var minHandTrackingConfidence: Float = HandSignRecognizer.DEFAULT_HAND_TRACKING_CONFIDENCE,
    var minHandPresenceConfidence: Float = HandSignRecognizer.DEFAULT_HAND_PRESENCE_CONFIDENCE
)
