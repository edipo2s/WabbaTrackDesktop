package com.ediposouza.state

import com.ediposouza.model.DeckClass
import com.ediposouza.util.Logger
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.awt.image.BufferedImage

/**
 * Created by ediposouza on 12/05/17.
 */
object CollectionState : StateHandler.TESLState {

    val COLLECTION_RECOGNIZER_DELAY = 1000L

    var threadRunning: Boolean = false
    var deckClass: DeckClass? = null
    var lastScreenshotPHash: String = ""
    var waitingScreenshotChangeWasLogged: Boolean = false

    override fun onResume() {
        GameState.hideDeckTracker()
        Logger.i("GameState onResume")
        GameState.threadRunning = true
        launch(CommonPool) {
            runStateThread()
        }
    }

    override fun onPause() {
        Logger.i("ArenaState onPause")
        threadRunning = false
        deckClass = null
    }

    fun runStateThread() {
        launch(CommonPool) {
            while (ArenaState.threadRunning) {
                delay(COLLECTION_RECOGNIZER_DELAY)
            }
        }
    }

    private fun proccessCollectionScreenshot(screenshot: BufferedImage) {

    }

}