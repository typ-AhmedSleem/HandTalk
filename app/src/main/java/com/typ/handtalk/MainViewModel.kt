/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.typ.handtalk

import androidx.lifecycle.ViewModel
import com.google.mediapipe.tasks.core.Delegate
import com.typ.handtalk.core.recognizer.HandSignRecognizer

class MainViewModel : ViewModel() {
    private var _delegate: Delegate = Delegate.GPU
    private var _minHandDetectionConfidence: Float = HandSignRecognizer.DEFAULT_HAND_DETECTION_CONFIDENCE
    private var _minHandTrackingConfidence: Float = HandSignRecognizer.DEFAULT_HAND_TRACKING_CONFIDENCE
    private var _minHandPresenceConfidence: Float = HandSignRecognizer.DEFAULT_HAND_PRESENCE_CONFIDENCE

    val currentDelegate: Delegate
        get() = _delegate
    val currentMinHandDetectionConfidence: Float
        get() = _minHandDetectionConfidence
    val currentMinHandTrackingConfidence: Float
        get() = _minHandTrackingConfidence
    val currentMinHandPresenceConfidence: Float
        get() = _minHandPresenceConfidence

    fun setDelegate(delegate: Delegate) {
        _delegate = delegate
    }

    fun setMinHandDetectionConfidence(confidence: Float) {
        _minHandDetectionConfidence = confidence
    }

    fun setMinHandTrackingConfidence(confidence: Float) {
        _minHandTrackingConfidence = confidence
    }

    fun setMinHandPresenceConfidence(confidence: Float) {
        _minHandPresenceConfidence = confidence
    }
}
