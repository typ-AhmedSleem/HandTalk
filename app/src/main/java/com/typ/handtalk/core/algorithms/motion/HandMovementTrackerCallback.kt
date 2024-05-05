package com.typ.handtalk.core.algorithms.motion

interface HandMovementTrackerCallback {

    fun onHandMovementStart()

    fun onHandMoving()

    fun onHandMovementEnd()

}