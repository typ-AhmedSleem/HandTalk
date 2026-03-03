package com.typ.handtalk.impl.ui.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.typ.handtalk.domain.models.FrameResult
import com.typ.handtalk.domain.models.Hand
import kotlin.math.max

/**
 * Custom [View] that draws hand landmark points and connection lines
 * on top of the camera preview. Adapted from the legacy OverlayView
 * to use the KMP domain [FrameResult] and [Hand] models.
 */
class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var currentResult: FrameResult? = null
    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    private val pointPaint = Paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
        strokeWidth = LANDMARK_STROKE_WIDTH
    }

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

    fun drawLandmarks(result: FrameResult, imageHeight: Int, imageWidth: Int) {
        this.currentResult = result
        this.imageHeight = imageHeight
        this.imageWidth = imageWidth
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
        invalidate()
    }

    fun clear() {
        currentResult = null
        invalidate()
    }

    private fun drawHandPoints(canvas: Canvas, hand: Hand) {
        for (landmark in hand.landmarks) {
            canvas.drawPoint(
                landmark.x * imageWidth * scaleFactor,
                landmark.y * imageHeight * scaleFactor,
                pointPaint
            )
        }
    }

    private fun drawHandConnections(canvas: Canvas, hand: Hand) {
        HandLandmarker.HAND_CONNECTIONS.forEach { connection ->
            val startLandmark = hand.landmarks[connection.start()]
            val endLandmark = hand.landmarks[connection.end()]
            canvas.drawLine(
                startLandmark.x * imageWidth * scaleFactor,
                startLandmark.y * imageHeight * scaleFactor,
                endLandmark.x * imageWidth * scaleFactor,
                endLandmark.y * imageHeight * scaleFactor,
                linePaints[hand.idx]
            )
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        currentResult?.forEachHand { hand ->
            drawHandPoints(canvas, hand)
            drawHandConnections(canvas, hand)
        }
    }

    companion object {
        private const val LANDMARK_STROKE_WIDTH = 10F
    }
}
