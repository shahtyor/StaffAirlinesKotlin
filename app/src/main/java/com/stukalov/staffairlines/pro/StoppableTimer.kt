package com.stukalov.staffairlines.pro

import android.util.Log
import java.util.Timer
import java.util.TimerTask

class StoppableTimer(totalTime: Long)
{
    private var totalTimeInner = totalTime
    private var remainingTime = totalTime
    private var timer: Timer? = null
    private var startTime = 0L

    fun start()
    {
        val SM: StaffMethods = StaffMethods()
        timer = Timer()
        startTime = System.currentTimeMillis()
        Log.d("StoppableTimer", "Start: " + remainingTime.toString())
        timer?.schedule(object : TimerTask()
        {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - startTime
                remainingTime -= elapsedTime
                Log.d("StoppableTimer", remainingTime.toString())
                startTime += elapsedTime
                if (remainingTime <= 0) {
                    Log.d("StoppableTimer", "Finish: " + remainingTime.toString())
                    val res = SM.AskForRatingIfNeeded()
                    cancel()
                }
            }
        }, 0, 1000)
    }

    fun resume()
    {
        startTime = System.currentTimeMillis()
        remainingTime = totalTimeInner
    }

    fun stop()
    {
        timer?.cancel()
    }
}