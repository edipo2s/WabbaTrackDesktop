package com.ediposouza.ui

import com.ediposouza.LogBreakLineEvent
import com.ediposouza.LogEvent
import com.ediposouza.TESLTracker
import com.ediposouza.util.Logger
import tornadofx.Controller
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by Edipo on 18/03/2017.
 */
class LoggerController : Controller() {

    private val loggerView by inject<LoggerView>()

    private val logs = mutableMapOf<String, Logger.Level>()
    private val saveLogLock = "lock"

    private val logsFileWriter by lazy {
        FileWriter(File(File(TESLTracker.jarPath).parentFile, "data").let {
            if (!it.exists()) {
                it.mkdirs()
            }
            File(it, "logs_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm"))}.txt")
        })
    }

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
        val line = "\n${level.prefix}: $text"
        if (selectedLogViewIndex == Logger.Level.ALL.ordinal || selectedLogViewIndex == level.ordinal) {
            loggerView.textArea.appendText(line)
        }
        saveLog(line)
    }

    fun changeLogLevelView(level: Logger.Level?) {
        loggerView.textArea.clear()
        logs.filter { level == Logger.Level.ALL || level == it.value }.forEach {
            logText(it.key, it.value)
        }
    }

    fun saveLog(line: String) {
        synchronized(saveLogLock) {
            try {
                logsFileWriter.apply {
                    append(line)
                    flush()
                }
            } catch (e: Exception) {
                Logger.e(e)
            }
        }
    }

}