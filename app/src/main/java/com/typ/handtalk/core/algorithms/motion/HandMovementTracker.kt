package com.typ.handtalk.core.algorithms.motion

import android.graphics.Point
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.typ.handtalk.core.algorithms.AbstractAlgorithm
import com.typ.handtalk.core.enums.MovingDirection
import com.typ.handtalk.core.models.ImageShape
import com.typ.handtalk.core.models.signs.MovingSign
import com.typ.handtalk.core.resolvers.models.FrameResult
import com.typ.handtalk.utils.motionInfo
import kotlin.math.absoluteValue

class HandMovementTracker(
    private val callback: HandMovementTrackerCallback
) : AbstractAlgorithm<FrameResult, MovingSign>() {

    // * Flags
    val isMoving: Boolean
        get() = state == HandState.MOVING
    val isIdle: Boolean
        get() = state == HandState.IDLE

    // * Runtime
    var state = HandState.IDLE
    var startingTimestamp: Long = 0L
        private set
    var lastFrameTimestamp: Long = 0L
        private set

    // * Motion estimation
    private val estimator = MotionEstimator()

    // Accessors
    val travelledDistance: Point
        get() = estimator.travelledDistance
    val movingDirection: MovingDirection
        get() = estimator.direction

    val currentResult: HandMotionInfo?
        get() {
            // Direction
            val direction = movingDirection
            if (direction == MovingDirection.UNKNOWN) return null

            // Distance
            val distance = travelledDistance
            val travelledThanThreshold = distance.let {
                when (direction) {
                    MovingDirection.LEFT_TO_RIGHT,
                    MovingDirection.RIGHT_TO_LEFT -> {
                        it.x.absoluteValue >= MotionEstimator.MIN_MOVEMENT_DISTANCE
                    }

                    MovingDirection.UP_TO_DOWN,
                    MovingDirection.DOWN_TO_TOP -> {
                        it.y.absoluteValue >= MotionEstimator.MIN_MOVEMENT_DISTANCE
                    }

                    else -> false
                }
            }

            return if (travelledThanThreshold) return motionInfo(distance, direction) else null
        }

    override fun createNewRun() {
        throw Exception("Use beginTracking instead")
    }

    override fun cancelCurrentRun() {
        throw Exception("Use stopTracking instead")
    }

    override fun obtainResult(): MovingSign {
        throw Exception("Use travelledDistance and movingDirection instead")
    }

    override fun feed(payload: FrameResult) {
        throw Exception("Use feedFrame instead")
    }

    fun beginTracking(frame: FrameResult, shape: ImageShape, notifyCallback: Boolean = true) {
        if (isIdle) {
            resetTracker()
            startingTimestamp = frame.timestamp
            estimator.begin(getTrackingLandmark(frame), shape)
            if (notifyCallback) {
                callback.onBeginHandTracking()
            }
        }
    }

    fun stopTracking() {
        resetTracker()
        callback.onStopHandTracking(null)
    }

    private fun resetTracker() {
        estimator.reset()
        state = HandState.IDLE
        startingTimestamp = 0L
        lastFrameTimestamp = 0L
    }

    fun feedFrame(frame: FrameResult, shape: ImageShape) {
        // * Check if the time difference btw current frame and starting frame is more than the allowed timeout
        val landmark = getTrackingLandmark(frame)
        if (isStartingFrameInvalid(frame.timestamp)) {
            startingTimestamp = frame.timestamp
            estimator.begin(landmark, shape)
        }
        if (isIdle) state = HandState.MOVING
        // * Update the endingTimestamp
        this.lastFrameTimestamp = frame.timestamp
        // * Update the lastFramePos
        val info = estimator.update(landmark, shape)
        if (info != null) {
            // * Gesture has been recognized * //
            // Notify
            callback.onStopHandTracking(motionInfo(travelledDistance, movingDirection))
            // Reset tracker
            resetTracker()
            return
        }
        // * Notify movement
        callback.onHandMoving(estimator.lastFramePos)
    }

    private fun getTrackingLandmark(frame: FrameResult): NormalizedLandmark? {
        return frame.rightHand?.landmarks?.get(TRACKING_LANDMARK_POINT_IDX)
    }

    private fun isStartingFrameInvalid(currTimestamp: Long): Boolean {
        return currTimestamp - startingTimestamp > STARTING_FRAME_VALID_TIME
    }

    companion object {
        const val TAG = "HandTracker"
        const val TRACKING_LANDMARK_POINT_IDX = 9 // MIDDLE_FINGER_MCP
        const val STARTING_FRAME_VALID_TIME = 1000 // in ms
    }

}