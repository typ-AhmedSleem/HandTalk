package com.typ.handtalk.core.algorithms.motion

import android.graphics.Point
import android.util.Log
import androidx.core.graphics.minus
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.typ.handtalk.core.enums.MovingDirection
import com.typ.handtalk.utils.emptyPoint
import com.typ.handtalk.utils.toScaledPoint
import kotlin.math.absoluteValue

/**
 * Contains necessary code that calculates
 * hand travelled distance and direction of movement.
 */
class MotionEstimator {

    // Frame dimensions
    private var frameWidth = 0
    private var frameHeight = 0
    private val scaleFactor = 3.75f

    private var firstFramePos = emptyPoint()
    private var lastFramePos = emptyPoint()

    private var lastKnownDirection = MovingDirection.UNKNOWN

    val travelledDistance: Point
        get() = lastFramePos - firstFramePos

    val direction: MovingDirection
        get() {
            return if (travelledDistance.x.absoluteValue > MIN_MOVEMENT_DISTANCE) {
                if (travelledDistance.x > 0) MovingDirection.RIGHT_TO_LEFT else MovingDirection.LEFT_TO_RIGHT
            } else if (travelledDistance.y.absoluteValue > MIN_MOVEMENT_DISTANCE) {
                if (travelledDistance.y > 0) MovingDirection.DOWN_TO_TOP else MovingDirection.UP_TO_DOWN
            } else {
                MovingDirection.UNKNOWN
            }
        }

    // Positions
    val startX: Int
        get() = firstFramePos.x
    val startY: Int
        get() = firstFramePos.y
    val endX: Int
        get() = lastFramePos.x
    val endY: Int
        get() = lastFramePos.y

    fun begin(landmark: NormalizedLandmark?, frameWidth: Int, frameHeight: Int) {
        if (landmark == null) return
        firstFramePos = landmark.toScaledPoint(
            (frameWidth * scaleFactor).toInt(),
            (frameHeight * scaleFactor).toInt(),
        )
    }

    fun update(landmark: NormalizedLandmark?, frameWidth: Int, frameHeight: Int): Boolean {
        if (landmark == null) return false
        this.frameWidth = frameWidth
        this.frameHeight = frameHeight
        this.lastFramePos = landmark.toScaledPoint(
            (frameWidth * scaleFactor).toInt(),
            (frameHeight * scaleFactor).toInt(),
        )

        val dir = direction
        if (dir != lastKnownDirection) {
            if (dir == MovingDirection.UNKNOWN) return false
            lastKnownDirection = dir
            Log.d("HandTracker", "direction: $dir, movedY: $startY -> $endY, distanceY: ${travelledDistance.y}")
            return true
        }
        return false
    }

    fun reset() {
        firstFramePos = emptyPoint()
        lastFramePos = emptyPoint()
        frameHeight = 0
        frameWidth = 0
    }

    companion object {
        const val MIN_MOVEMENT_DISTANCE = 250 // in pixels
    }

}