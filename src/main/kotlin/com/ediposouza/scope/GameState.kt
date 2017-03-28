package com.ediposouza.scope

import com.ediposouza.handler.GameHandler
import com.ediposouza.handler.StateHandler
import com.ediposouza.model.CardSlot
import com.ediposouza.ui.DeckTrackerWidget
import com.ediposouza.util.Logger
import com.ediposouza.util.ScreenFuncs
import javafx.application.Platform

/**
 * Created by ediposouza on 24/03/17.
 */
object GameState : StateHandler.TESLState {

    private val deckTracker by lazy { DeckTrackerWidget() }
    private var deckCardsSlot: List<CardSlot> = listOf()

    var threadRunning: Boolean = false

    init {

    }

    override fun onResume() {
//        showDeckTracker()
        Logger.i("GameState onResume")
        threadRunning = true
        Thread(Runnable {
            while (threadRunning) {
                Logger.i("GameState screenshot")
                ScreenFuncs.takeScreenshot()?.apply {
                    GameHandler.processGame(this)
                }
                Thread.sleep(1000)
            }
        }).start()
    }

    override fun onPause() {
//        hideDeckTracker()
        Logger.i("GameState onPause")
        threadRunning = false
    }

    override fun resetState() {
    }

    fun setDeckCardsSlot(cardsSlot: List<CardSlot>) {
        deckCardsSlot = cardsSlot
        Platform.runLater {
            deckTracker.setDeckCardsSlot(cardsSlot)
        }
    }

    private fun showDeckTracker() {
        deckTracker.isVisible = true
    }

    private fun hideDeckTracker() {
        deckTracker.isVisible = false
    }

}