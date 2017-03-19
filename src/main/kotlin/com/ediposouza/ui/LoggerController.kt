package com.ediposouza.ui

import tornadofx.Controller

/**
 * Created by Edipo on 18/03/2017.
 */
class LoggerController : Controller() {

    private val loggerView by inject<LoggerView>()

    init {
        subscribe<LogEvent> {
            logText(it.text)
        }
    }

    fun logText(text: String) {
        loggerView.textArea.text += "\n$text"
    }

}