package com.ediposouza.util

import com.ediposouza.LogEvent
import tornadofx.FX

/**
 * Created by ediposouza on 06/03/17.
 */
object Logger {

    fun d(text: String) {
        print(text)
    }

    fun e(text: String) {
        print(text)
    }

    fun e(exception: Exception) {
        print(exception.message)
    }

    private fun print(text: String) {
        println(text)
        FX.eventbus.fire(LogEvent(text))
    }

}