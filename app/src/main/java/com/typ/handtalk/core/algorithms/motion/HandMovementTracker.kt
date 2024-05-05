package com.typ.handtalk.core.algorithms.motion

import android.graphics.Point
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.typ.handtalk.core.algorithms.AbstractAlgorithm
import com.typ.handtalk.core.enums.MovingDirection
import com.typ.handtalk.core.models.signs.MovingSign
import com.typ.handtalk.core.resolvers.models.FrameResult

class HandMovementTracker : AbstractAlgorithm<FrameResult, MovingSign>() {

    // * Flags
    val isMoving: Boolean
        get() = state == HandState.MOVING
    val isIdle: Boolean
        get() = state == HandState.IDLE

    // * Runtime
    private var state = HandState.IDLE
    private var startingTimestamp: Long = 0L
    private var endingTimestamp: Long = 0L
    private var lastFrameTimestamp: Long = 0L

    // * Motion estimation
    private val estimator = MotionEstimator()

    // Estimator flags
    val travelledDistance: Point
        get() = estimator.travelledDistance
    val movingDirection: MovingDirection
        get() = estimator.direction

    override fun createNewRun() {
        TODO("Not yet implemented")
    }

    override fun cancelCurrentRun() {
        state = HandState.IDLE
        estimator.reset()
    }

    override fun obtainResult(): MovingSign {
        TODO("Not yet implemented")
    }

    override fun feed(payload: FrameResult) {
        throw Exception("Use feedFrame instead")
    }

    fun feedFrame(frame: FrameResult, fw: Int, fh: Int) {
        if (isIdle) {
            estimator.begin(getTrackingLandmark(frame), fw, fh)
            startingTimestamp = frame.timestamp
            state = HandState.MOVING
        }
        // * Update the endingTimestamp
        this.lastFrameTimestamp = frame.timestamp
        // * Update the lastFramePos
        val estimated = estimator.update(getTrackingLandmark(frame), fw, fh)
        if (estimated) {
            // Reset tracker
            estimator.reset()
            endingTimestamp = frame.timestamp
            state = HandState.IDLE
        }
        // * Calculate the direction of movement
//        Log.d("HandTracker", "direction: $movingDirection, distance: $travelledDistance")
    }

    private fun getTrackingLandmark(frame: FrameResult): NormalizedLandmark? {
        return frame.rightHand?.landmarks?.get(TRACKING_LANDMARK_POINT_IDX)
    }

    companion object {
        const val TRACKING_LANDMARK_POINT_IDX = 0 // MIDDLE_FINGER_MCP
        const val MOVEMENT_ACTION_THRESHOLD = 100 // in pixels
        const val MIN_MOVEMENT_DISTANCE = 250 // in pixels
    }

}