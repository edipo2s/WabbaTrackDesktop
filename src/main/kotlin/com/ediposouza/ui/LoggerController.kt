package com.ediposouza.ui

import com.ediposouza.util.Logger
import tornadofx.Controller

/**
 * Created by Edipo on 18/03/2017.
 */
class LoggerController : Controller() {

    private val loggerView by inject<LoggerView>()

    private val logs = mutableMapOf<String, Logger.Level>()

    init {
        subscribe<LogEvent> {
            logText(it.text, it.level)
        }
        subscribe<LogBreakLineEvent> {
            loggerView.textArea.appendText("\n")
        }
    }

    fun logText(text: String, level: Logger.Level) {
        logs.put(text, level)
        val selectedLogViewIndex = loggerView.logLevelView.selectionModel.selectedIndex
        if (selectedLogViewIndex == Logger.Level.ALL.ordinal || selectedLogViewIndex == level.ordinal) {
            loggerView.textArea.appendText("\n${level.prefix}: $text")
        }
    }

    fun changeLogLevelView(level: Logger.Level?) {
        loggerView.textArea.clear()
        logs.filter { level == Logger.Level.ALL || level == it.value }.forEach {
            logText(it.key, it.value)
        }
    }

}