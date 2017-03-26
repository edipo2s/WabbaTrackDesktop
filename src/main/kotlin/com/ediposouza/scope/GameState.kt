package com.ediposouza.scope

import com.ediposouza.handler.StateHandler
import com.ediposouza.model.CardSlot
import com.ediposouza.ui.DeckTrackerWidget
import javafx.application.Platform

/**
 * Created by ediposouza on 24/03/17.
 */
object GameState : StateHandler.TESLState {

    private val deckTracker by lazy { DeckTrackerWidget() }
    private var deckCardsSlot: List<CardSlot> = listOf()

    init {

    }

    override fun onResume() {
        showDeckTracker()
    }

    override fun onPause() {
        hideDeckTracker()
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