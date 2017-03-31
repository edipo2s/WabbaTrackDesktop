package com.ediposouza.state

import com.ediposouza.handler.GameHandler
import com.ediposouza.handler.StateHandler
import com.ediposouza.model.Card
import com.ediposouza.model.CardSlot
import com.ediposouza.model.DeckClass
import com.ediposouza.ui.DeckTrackerWidget
import com.ediposouza.util.Logger
import com.ediposouza.util.ScreenFuncs
import javafx.application.Platform

/**
 * Created by ediposouza on 24/03/17.
 */
object GameState : StateHandler.TESLState, Runnable {

    val deckTracker by lazy { DeckTrackerWidget() }
    private var deckCardsSlot: List<CardSlot> = listOf()

    var threadRunning: Boolean = false
    var playerGoFirst: Boolean? = null
    var playerDeckClass: DeckClass? = null
    var opponentDeckClass: DeckClass? = null
    var lastCardDraw: Card? = null

    init {

    }

    override fun onResume() {
        showDeckTracker()
        Logger.i("GameState onResume")
        threadRunning = true
        Thread(this).start()
    }

    override fun onPause() {
        hideDeckTracker()
        Logger.i("GameState onPause")
        threadRunning = false
    }

    override fun resetState() {
        playerGoFirst = null
        playerDeckClass = null
        opponentDeckClass = null
        lastCardDraw = null
    }

    override fun run() {
        while (threadRunning) {
            ScreenFuncs.takeScreenshot()?.apply {
                if (playerGoFirst == null) {
                    playerGoFirst = GameHandler.processPlayerGoFirst(this)
                }
                if (playerDeckClass == null) {
                    playerDeckClass = GameHandler.processPlayerDeckClass(this)
                }
                if (opponentDeckClass == null) {
                    opponentDeckClass = GameHandler.processOpponentDeckClass(this)
                }
                GameHandler.processCardDraw(this)?.apply {
                    lastCardDraw = this
                    deckTracker.trackCardDraw(this)
                }
                GameHandler.processMatchEnd(this)?.let { win ->
                    val result = "Win".takeIf { win } ?: "Loss"
                    Logger.d("${playerDeckClass?.name} vs ${opponentDeckClass?.name} - $result")
                    setDeckCardsSlot(deckCardsSlot)
                    threadRunning = false
                }
            }
            Thread.sleep(500)
        }
    }

    fun setDeckCardsSlot(cardsSlot: List<CardSlot>) {
        deckCardsSlot = cardsSlot
        Platform.runLater {
            deckTracker.setDeckCardsSlot(cardsSlot)
        }
    }

    private fun showDeckTracker() {
        if (deckCardsSlot.isNotEmpty()) {
            deckTracker.isVisible = true
        }
    }

    private fun hideDeckTracker() {
        deckTracker.isVisible = false
    }

}