package com.ediposouza.state

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.handler.GameHandler
import com.ediposouza.handler.StateHandler
import com.ediposouza.model.*
import com.ediposouza.ui.DeckTrackerWidget
import com.ediposouza.util.Logger
import com.ediposouza.util.ScreenFuncs
import javafx.application.Platform
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Created by ediposouza on 24/03/17.
 */
object GameState : StateHandler.TESLState, Runnable {

    val GAME_RECOGNIZER_SPS = 5    //Screenshot Per Second

    val deckTracker by lazy { DeckTrackerWidget() }
    private var deckCardsSlot: List<CardSlot> = listOf()

    var threadRunning: Boolean = false
    var firstCardDraws: Triple<String, String, String>? = null
    var firstCardDrawsTracked: Boolean = false
    var playerGoFirst: Boolean? = null
    var playerDeckClass: DeckClass? = null
    var opponentDeckClass: DeckClass? = null
    var lastCardDraw: Card? = null
    var matchMode: MatchMode? = null

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
        matchMode = null
        firstCardDraws = null
        firstCardDrawsTracked = false
        deckTracker.resetDraws()
    }

    override fun run() {
        while (threadRunning) {
            ScreenFuncs.takeScreenshot()?.apply {
                Logger.e("${LocalTime.now()}")
                if (!firstCardDrawsTracked) {
                    GameHandler.processFirstCardDraws(this)?.apply {
                        firstCardDraws = this
                        Logger.d("First cards draw: $firstCardDraws")
                    }
                }
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
                    firstCardDraws?.apply {
                        Logger.d("Tracking first cards draw: $firstCardDraws")
                        TESLTrackerData.getCard(first)?.apply { deckTracker.trackCardDraw(this) }
                        TESLTrackerData.getCard(second)?.apply { deckTracker.trackCardDraw(this) }
                        TESLTrackerData.getCard(third)?.apply { deckTracker.trackCardDraw(this) }
                        firstCardDrawsTracked = true
                    }
                    deckTracker.trackCardDraw(this)
                    Thread({
                        Thread.sleep(3000L)
                        lastCardDraw = null
                    }).start()
                }
                GameHandler.processMatchEnd(this)?.let { win ->
                    val result = "Win".takeIf { win } ?: "Loss"
                    Logger.d("${playerDeckClass?.name} vs ${opponentDeckClass?.name} - $result")
                    saveMatch(win)
                    resetState()
                }
            }
            Thread.sleep(1000L / GAME_RECOGNIZER_SPS)
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

    fun saveMatch(win: Boolean) {
        if (playerGoFirst != null && playerDeckClass != null && opponentDeckClass != null && matchMode != null) {
            val newUuid = LocalDateTime.now().withNano(0).toString()
            val currentSeason = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM"))
            TESLTrackerData.saveMatch(Match(newUuid, playerGoFirst!!, MatchDeck("", playerDeckClass!!, DeckType.OTHER),
                    MatchDeck("", opponentDeckClass!!, DeckType.OTHER), matchMode!!, currentSeason, 0, false, win)) {
                Logger.i("Match saved!")
            }
        }
    }

}