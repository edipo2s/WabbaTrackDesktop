package com.ediposouza.state

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.handler.GameHandler
import com.ediposouza.handler.GameHandler.processCardDrawProphecy
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
import java.util.concurrent.CompletableFuture

/**
 * Created by ediposouza on 24/03/17.
 */
object GameState : StateHandler.TESLState {

    const val GAME_RECOGNIZER_SPS = 1    //Screenshot Per Second
    const val GAME_RECOGNIZER_DRAW_SPS = 2    //Screenshot Per Second
    const val GAME_RECOGNIZER_CARD_DELAY = 4    //Screenshot Per Second

    const val playerGoFirstLock = "lock"
    const val playerDeckClassLock = "lock"
    const val opponentDeckClassLock = "lock"
    const val cardDrawLock = "lock"
    const val cardDrawProphecyLock = "lock"
    const val cardGenerateLock = "lock"
    const val endMatchLock = "lock"

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
    var cardGenerated: Card? = null
    var cardGeneratedDetected: Boolean? = null
    var shouldShowDeckTracker: Boolean = true

    override fun onResume() {
        showDeckTracker()
        Logger.i("GameState onResume")
        threadRunning = true
        runStateThread()
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

    fun runStateThread() {
        CompletableFuture.runAsync {
            while (threadRunning) {
                ScreenFuncs.takeScreenshot()?.apply {
                    processCardDraw(this)
                    processCardDrawProphecy(this)
                    processCardGenerate(this)
                    if (!firstCardDrawsTracked) {
                        processCardFirstDraws(this)
                    }
                }
                Thread.sleep(1000L / GAME_RECOGNIZER_DRAW_SPS)
            }
        }
        CompletableFuture.runAsync {
            while (threadRunning) {
                ScreenFuncs.takeScreenshot()?.apply {
                    if (playerGoFirst == null) {
                        processPlayerGoFirst(this)
                    }
                    if (playerDeckClass == null) {
                        processPlayerDeck(this)
                    }
                    if (opponentDeckClass == null) {
                        processOpponentDeck(this)
                    }
                    processEndMatch(this)
                }
                Thread.sleep(1000L / GAME_RECOGNIZER_SPS)
            }
        }
    }

    fun setDeckCardsSlot(cardsSlot: List<CardSlot>) {
        deckCardsSlot = cardsSlot
        Platform.runLater {
            deckTracker.setDeckCardsSlot(cardsSlot)
        }
    }

    private fun processCardFirstDraws(screenshot: BufferedImage) {
        CompletableFuture.runAsync {
            if (!firstCardDrawsTracked) {
                GameHandler.processFirstCardDraws(screenshot)?.run {
                    firstCardDraws = this
                }
            }
        }
    }

    private fun processPlayerGoFirst(screenshot: BufferedImage) {
        CompletableFuture.runAsync {
            GameHandler.processPlayerGoFirst(screenshot)?.run {
                synchronized(playerGoFirstLock) {
                    if (playerGoFirst == null) {
                        playerGoFirst = this
                        Logger.i("--PlayerGoFirst!".takeIf { this } ?: "--PlayerGoSecond!")
                    }
                }
            }
        }
    }

    private fun processPlayerDeck(screenshot: BufferedImage) {
        CompletableFuture.runAsync {
            GameHandler.processPlayerDeckClass(screenshot)?.run {
                synchronized(playerDeckClassLock) {
                    if (playerDeckClass == null) {
                        playerDeckClass = this
                        Logger.i("--PlayerDeckClass: $this!")
                    }
                }
            }
        }
    }

    private fun processOpponentDeck(screenshot: BufferedImage) {
        CompletableFuture.runAsync {
            GameHandler.processOpponentDeckClass(screenshot)?.run {
                synchronized(opponentDeckClassLock) {
                    if (opponentDeckClass == null) {
                        opponentDeckClass = this
                        Logger.i("--OpponentDeckClass: $this!")
                    }
                }
            }
        }
    }

    private fun processCardGenerate(screenshot: BufferedImage) {
        CompletableFuture.runAsync {
            GameHandler.processCardGenerated(screenshot)?.run {
                synchronized(cardGenerateLock) {
                    if (cardGeneratedDetected != this) {
                        cardGeneratedDetected = this
                        Logger.i("--Card generated!")
                        CompletableFuture.runAsync {
                            Thread.sleep(3000L)
                            cardGeneratedDetected = null
                        }
                    }
                }
            }
        }
    }

    private fun processCardDraw(screenshot: BufferedImage) {
        CompletableFuture.runAsync {
            GameHandler.processCardDrawProphecy(screenshot)?.run {
                synchronized(cardDrawProphecyLock) {
                    if (lastCardDraw != this) {
                        lastCardDraw = this
                        deckTracker.trackCardDraw(this)
                        Logger.i("--$name prophecy draw!")
                        CompletableFuture.runAsync {
                            Thread.sleep(1000L * GAME_RECOGNIZER_CARD_DELAY)
                            lastCardDraw = null
                        }
                    }
                }
            }
            GameHandler.processCardDraw(screenshot)?.run {
                synchronized(cardDrawLock) {
                    if (lastCardDraw != this) {
                        lastCardDraw = this
                        if (cardGeneratedDetected ?: false) {
                            cardGenerated = this
                            Logger.i("--$name generated!")
                        } else {
                            deckTracker.trackCardDraw(this)
                            Logger.i("--$name draw!")
                        }
                        CompletableFuture.runAsync {
                            Thread.sleep(1000L * GAME_RECOGNIZER_CARD_DELAY)
                            lastCardDraw = null
                            if (cardGeneratedDetected ?: false) {
                                cardGeneratedDetected = null
                            }
                        }
                        firstCardDraws?.apply {
                            Logger.d("Tracking first cards draw: $firstCardDraws")
                            TESLTrackerData.getCard(first)?.apply { deckTracker.trackCardDraw(this) }
                            TESLTrackerData.getCard(second)?.apply { deckTracker.trackCardDraw(this) }
                            TESLTrackerData.getCard(third)?.apply { deckTracker.trackCardDraw(this) }
                            firstCardDraws = null
                            firstCardDrawsTracked = true
                        }
                    }
                }
            }
        }
    }

    private fun processEndMatch(screenshot: BufferedImage) {
        CompletableFuture.runAsync {
            GameHandler.processMatchEnd(screenshot)?.run {
                synchronized(endMatchLock) {
                    if (playerGoFirst != null) {
                        val win = this
                        Logger.i("--Player Win!".takeIf { win } ?: "--Player Lose!")
                        if (playerDeckClass != null) {
                            val result = "Win".takeIf { win } ?: "Loss"
                            Logger.d("${playerDeckClass?.name} vs ${opponentDeckClass?.name} - $result")
                            saveMatch(win)
                            resetState()
                        }
                    }
                }
            }
        }
    }

    private fun showDeckTracker() {
        if (deckCardsSlot.isNotEmpty() && shouldShowDeckTracker) {
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