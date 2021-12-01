package com.android.simplevkplayer.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.simplevkplayer.ui.Constants.TIMER_NOT_STARTED
import com.android.simplevkplayer.ui.Constants.TIMER_RUNNING
import com.android.simplevkplayer.ui.Constants.TIMER_STOPPED
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.schedule

@Singleton
open class MainTimer @Inject constructor() {
    private val mytag = "MainTimer"

    private val _time = MutableLiveData<Long>()
    val time: LiveData<Long>
        get() = _time

    private var startTime: Long = 0
    private var stopTime: Long = 0
    private val millisecondsPrecision = 100  // how precise is timer tick
    private var mainTimer = Timer()
    open var timerStatus = TIMER_NOT_STARTED

    init {
        Log.d(mytag, "init")
    }


    fun startMainTimer(resetTime: Boolean = true) {
        Log.d(mytag, "startMainTimer - start")
        if (resetTime) {
            startTime = Calendar.getInstance().time.time
            resetTime()
        } else {    // if not reset time at startTimer it is resume
            val stoppedTime = Calendar.getInstance().time.time - stopTime
            startTime += stoppedTime    // start time is modified by stop time, to correct work
        }
        stopTime = 0
        mainTimer.schedule(0, millisecondsPrecision.toLong()) {
            timerClick()
        }
        timerStatus =
            TIMER_RUNNING
    }

    private fun timerClick() {
        val workingTime: Long = Calendar.getInstance().time.time - startTime
        val tempMinutes = TimeUnit.MILLISECONDS.toMinutes(workingTime)
        _time.postValue(
            workingTime
        )
    }

    fun pauseTimer() {
        mainTimer.cancel()
        mainTimer = Timer()
        timerStatus =
            TIMER_STOPPED
        stopTime = Calendar.getInstance().time.time
    }

    fun resetTime() {
        _time.postValue(0)
        timerStatus =
            TIMER_NOT_STARTED
    }
}