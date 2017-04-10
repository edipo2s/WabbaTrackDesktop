package com.ediposouza.state

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.handler.GameHandler
import com.ediposouza.handler.StateHandler
import com.ediposouza.model.*
import com.ediposouza.ui.DeckTrackerWidget
import com.ediposouza.util.Logger
import com.ediposouza.util.ScreenFuncs
import javafx.application.Platform
import java.awt.image.BufferedImage
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Created by ediposouza on 24/03/17.
 */
object GameState : StateHandler.TESLState, Runnable {

    val GAME_RECOGNIZER_SPS = 3    //Screenshot Per Second

    val deckTracker by lazy { DeckTrackerWidget() }
    private var deckCardsSlot: List<CardSlot> = listOf()

    val playerGoFirstLock = "lock"
    val playerDeckClassLock = "lock"
    val opponentDeckClassLock = "lock"
    val cardDrawLock = "lock"
    val endMatchLock = "lock"

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
                if (!firstCardDrawsTracked) {
                    Thread({ GameHandler.processFirstCardDraws(this)?.run { firstCardDraws = this } }).start()
                }
                if (playerGoFirst == null) {
                    processPlayerGoFirst(this)
                }
                if (playerDeckClass == null) {
                    processPlayerDeck(this)
                }
                if (opponentDeckClass == null) {
                    processOpponentDeck(this)
                }
                processCardDraw(this)
                processEndMatch(this)
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

    private fun processPlayerGoFirst(screenshot: BufferedImage) {
        Thread({
            GameHandler.processPlayerGoFirst(screenshot)?.run {
                synchronized(playerGoFirstLock) {
                    if (playerGoFirst == null) {
                        playerGoFirst = this
                        Logger.i("--PlayerGoFirst!".takeIf { this } ?: "--PlayerGoSecond!")
                    }
                }
            }
        }).start()
    }

    private fun processPlayerDeck(screenshot: BufferedImage) {
        Thread({
            GameHandler.processPlayerDeckClass(screenshot)?.run {
                synchronized(playerDeckClassLock) {
                    if (playerDeckClass == null) {
                        playerDeckClass = this
                        Logger.i("--PlayerDeckClass: $this!")
                    }
                }
            }
        }).start()
    }

    private fun processOpponentDeck(screenshot: BufferedImage) {
        Thread({
            GameHandler.processOpponentDeckClass(screenshot)?.run {
                synchronized(opponentDeckClassLock) {
                    if (opponentDeckClass == null) {
                        opponentDeckClass = this
                        Logger.i("--OpponentDeckClass: $this!")
                    }
                }
            }
        }).start()
    }

    private fun processCardDraw(screenshot: BufferedImage) {
        Thread({
            GameHandler.processCardDraw(screenshot)?.run {
                synchronized(cardDrawLock) {
                    if (lastCardDraw != this) {
                        lastCardDraw = this
                        deckTracker.trackCardDraw(this)
                        Logger.i("--$name draw!")
                        firstCardDraws?.apply {
                            Logger.d("Tracking first cards draw: $firstCardDraws")
                            TESLTrackerData.getCard(first)?.apply { deckTracker.trackCardDraw(this) }
                            TESLTrackerData.getCard(second)?.apply { deckTracker.trackCardDraw(this) }
                            TESLTrackerData.getCard(third)?.apply { deckTracker.trackCardDraw(this) }
                            firstCardDraws = null
                            firstCardDrawsTracked = true
                        }
                        Thread({
                            Thread.sleep(3000L)
                            lastCardDraw = null
                        }).start()
                    }
                }
            }
        }).start()
    }

    private fun processEndMatch(screenshot: BufferedImage) {
        Thread({
            GameHandler.processMatchEnd(screenshot)?.run {
                synchronized(endMatchLock) {
                    val win = this
                    if (playerDeckClass != null) {
                        Logger.i("--Player Win!".takeIf { win } ?: "--Player Lose!")
                        val result = "Win".takeIf { win } ?: "Loss"
                        Logger.d("${playerDeckClass?.name} vs ${opponentDeckClass?.name} - $result")
                        saveMatch(win)
                        resetState()
                    }
                }
            }
        }).start()
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
        if (playerGoFirst != null && playerDeckClass != null && opponentDeckClass != null &&
                matchMode != null && matchMode != MatchMode.PRATICE) {
            val newUuid = LocalDateTime.now().withNano(0).toString()
            val currentSeason = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM"))
            TESLTrackerData.saveMatch(Match(newUuid, playerGoFirst!!, MatchDeck("", playerDeckClass!!, DeckType.OTHER),
                    MatchDeck("", opponentDeckClass!!, DeckType.OTHER), matchMode!!, currentSeason, 0, false, win)) {
                Logger.i("Match saved!")
            }
        }
    }

}