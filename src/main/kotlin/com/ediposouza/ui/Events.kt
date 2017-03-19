package com.ediposouza.ui

import com.ediposouza.util.Logger
import tornadofx.FXEvent

/**
 * Created by Edipo on 18/03/2017.
 */
class LogEvent(val text: String, val level: Logger.Level) : FXEvent()