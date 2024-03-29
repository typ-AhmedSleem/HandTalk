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
package com.typ.handtalk.ui.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import kotlin.math.max

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: GestureRecognizerResult? = null
    private var pointPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
        strokeWidth = LANDMARK_STROKE_WIDTH
    }

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    private val linePaints = arrayOf(
        Paint().apply {
            isAntiAlias = true
            color = Color.argb(100, 255, 0, 0)
            style = Paint.Style.STROKE
            strokeWidth = LANDMARK_STROKE_WIDTH
        },
        Paint().apply {
            isAntiAlias = true
            color = Color.argb(100, 0, 255, 0)
            style = Paint.Style.STROKE
            strokeWidth = LANDMARK_STROKE_WIDTH
        }
    )

    fun clear() {
        results = null
        invalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        results?.let { result ->
            for (landmark in result.landmarks()) {
                for (normalizedLandmark in landmark) {
                    canvas.drawPoint(
                        normalizedLandmark.x() * imageWidth * scaleFactor,
                        normalizedLandmark.y() * imageHeight * scaleFactor,
                        pointPaint
                    )
                }

                result.handedness().forEachIndexed { idx, hand ->
                    HandLandmarker.HAND_CONNECTIONS.forEach {
                        canvas.drawLine(
                            result.landmarks()[idx][it!!.start()].x() * imageWidth * scaleFactor,
                            result.landmarks()[idx][it.start()].y() * imageHeight * scaleFactor,
                            result.landmarks()[idx][it.end()].x() * imageWidth * scaleFactor,
                            result.landmarks()[idx][it.end()].y() * imageHeight * scaleFactor,
                            linePaints[hand.first().index()]
                        )
                    }
                }
            }
        }
    }

    fun setResults(
        gestureRecognizerResult: GestureRecognizerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = gestureRecognizerResult

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)

        invalidate()
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 8F
    }
}
