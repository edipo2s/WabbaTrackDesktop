package com.ediposouza.ui

import com.ediposouza.model.Card
import com.ediposouza.util.Logger
import tornadofx.FXEvent

/**
 * Created by Edipo on 18/03/2017.
 */
class LogEvent(val text: String, val level: Logger.Level) : FXEvent()

class ShowArenaTierEvent(val pick1ArenaTier: Pair<Int, List<Card>>,
                         val pick2ArenaTier: Pair<Int, List<Card>>,
                         val pick3ArenaTier: Pair<Int, List<Card>>) : FXEvent()