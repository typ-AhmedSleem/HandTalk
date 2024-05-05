package com.typ.handtalk.core.algorithms.motion

import android.graphics.Point
import androidx.core.graphics.minus
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.typ.handtalk.core.enums.MovingDirection
import com.typ.handtalk.core.models.ImageShape
import com.typ.handtalk.utils.emptyImageShape
import com.typ.handtalk.utils.emptyPoint
import com.typ.handtalk.utils.motionInfo
import com.typ.handtalk.utils.toScaledPoint
import kotlin.math.absoluteValue

/**
 * Contains necessary code that calculates
 * hand travelled distance and direction of movement.
 */
class MotionEstimator {

    // Frame info
    private val scaleFactor = 3.75f
    private var frameShape = emptyImageShape()
    private val widthSF = (frameShape.width * scaleFactor).toInt()
    private val heightSF = (frameShape.height * scaleFactor).toInt()

    var firstFramePos = emptyPoint()
        private set
    var lastFramePos = emptyPoint()
        private set

    var lastKnownDirection = MovingDirection.UNKNOWN
        private set

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

    fun begin(landmark: NormalizedLandmark?, shape: ImageShape) {
        if (landmark == null) return
        frameShape = shape
        firstFramePos = landmark.toScaledPoint(widthSF, heightSF)
    }

    fun update(landmark: NormalizedLandmark?, shape: ImageShape): HandMotionInfo? {
        if (landmark == null) return null
        this.frameShape = shape
        this.lastFramePos = landmark.toScaledPoint(widthSF, heightSF)

        val distance = this.travelledDistance
        val direction = this.direction
        if (direction != lastKnownDirection) {
            if (direction == MovingDirection.UNKNOWN) return null
            lastKnownDirection = direction
//            Log.d("HandTracker", "direction: $dir, movedY: $startY -> $endY, distanceY: ${travelledDistance.y}")
            return motionInfo(distance, direction)
        }
        return null
    }

    fun reset() {
        firstFramePos = emptyPoint()
        lastFramePos = emptyPoint()
        frameShape = emptyImageShape()
    }

    companion object {
        const val MIN_MOVEMENT_DISTANCE = 250 // in pixels
    }

}