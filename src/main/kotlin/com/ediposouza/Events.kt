package com.ediposouza

import com.ediposouza.model.Card
import com.ediposouza.util.Logger
import tornadofx.FXEvent

/**
 * Created by Edipo on 18/03/2017.
 */
class LogEvent(val text: String, val level: Logger.Level) : FXEvent()

class LogBreakLineEvent : FXEvent()

class ShowArenaTierEvent(val pick1ArenaTier: Triple<String, Int, List<Card>>,
                         val pick2ArenaTier: Triple<String, Int, List<Card>>,
                         val pick3ArenaTier: Triple<String, Int, List<Card>>) : FXEvent()

class HideArenaTierEvent : FXEvent()