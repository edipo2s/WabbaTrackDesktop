package com.ediposouza.util

import com.ediposouza.LogBreakLineEvent
import com.ediposouza.LogEvent
import tornadofx.FX

/**
 * Created by ediposouza on 06/03/17.
 */
object Logger {

    enum class Level(val prefix: String) {
        ALL("a"),
        INFO("i"),
        DEBUG("d"),
        ERROR("e")
    }

    fun d(text: String, breakLine: Boolean = false) {
        if (breakLine) {
            FX.eventbus.fire(LogBreakLineEvent())
        }
        print(text, Level.DEBUG)
    }

    fun i(text: String, breakLine: Boolean = false) {
        if (breakLine) {
            FX.eventbus.fire(LogBreakLineEvent())
        }
        print(text, Level.INFO)
    }

    fun e(text: String, breakLine: Boolean = false) {
        if (breakLine) {
            FX.eventbus.fire(LogBreakLineEvent())
        }
        print(text, Level.ERROR)
    }

    fun e(exception: Exception) {
        print(exception.message ?: "", Level.ERROR)
    }

    private fun print(text: String, level: Level) {
        println(text)
        try {
            FX.eventbus.fire(LogEvent(text, level))
        } catch (e: Exception) {
        }
    }

}