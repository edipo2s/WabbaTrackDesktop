package com.ediposouza.state

import com.ediposouza.TESLTracker
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.handler.ArenaHandler
import com.ediposouza.handler.StateHandler
import com.ediposouza.model.Card
import com.ediposouza.model.CardPick
import com.ediposouza.model.CardSlot
import com.ediposouza.model.DeckClass
import com.ediposouza.ui.ArenaTierWidget
import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import com.ediposouza.util.Mixpanel
import com.ediposouza.util.ScreenFuncs
import com.google.gson.Gson
import javafx.application.Platform
import org.jnativehook.GlobalScreen
import org.jnativehook.NativeHookException
import org.jnativehook.mouse.NativeMouseEvent
import org.jnativehook.mouse.NativeMouseListener
import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.concurrent.CompletableFuture
import java.util.logging.Level

/**
 * Created by ediposouza on 24/03/17.
 */
object ArenaState : StateHandler.TESLState {

    const val ARENA_RECOGNIZER_SPS = 2    //Screenshot Per Second

    const val arenaPickLock = "lock"
    const val cardPicksToSelectLock = "lock"
    const val saveArenaPicksLock = "lock"

    private val card1ArenaTierStage by lazy { ArenaTierWidget(1) }
    private val card2ArenaTierStage by lazy { ArenaTierWidget(2) }
    private val card3ArenaTierStage by lazy { ArenaTierWidget(3) }

    private val arenaStateFile by lazy {
        File(File(TESLTracker.jarPath).parentFile, "data").let {
            if (!it.exists()) {
                it.mkdirs()
            }
            File(it, "arenaPicks.json")
        }
    }

    var classSelect: DeckClass? = null
        set(value) {
            field = value
            if (value != null) {
                Logger.i("classSelect: $value")
            }
        }

    var pickNumber: Int = 0
        set(value) {
            field = value
            when {
                value > 0 -> Logger.d("PickNumber: $pickNumber")
                value == 1 && cardPicksToSelect == null -> {
                    resetState()
                    classSelect = ArenaHandler.processArenaClass(ScreenFuncs.takeScreenshot())
                    Mixpanel.postEventArenaStart(classSelect ?: DeckClass.NEUTRAL)
                }
            }
        }

    var finishPicks = false
        set(value) {
            field = value
            if (value) {
                threadRunning = false
                hidePicksTier()
                GameState.setDeckCardsSlot(picks
                        .groupBy(Card::shortName)
                        .map { CardSlot(it.value.first(), it.value.size) })
                Platform.runLater {
                    GameState.deckTracker.isVisible = true
                }
                Mixpanel.postEventShowDeckTrackerFromArenaDeck()
            }
        }

    val mouseListener = object : NativeMouseListener {
        override fun nativeMousePressed(p0: NativeMouseEvent?) {
        }

        override fun nativeMouseClicked(p0: NativeMouseEvent?) {
            p0?.point?.apply { takeLastCardPicked(this, cardPicksToSelect) }
        }

        override fun nativeMouseReleased(p0: NativeMouseEvent?) {
        }

    }

    val picks = mutableListOf<Card>()
    var threadRunning: Boolean = false
    var lastPickNumberRecognized: Int? = null
    var cardPicksToSelect: Triple<CardPick, CardPick, CardPick>? = null

    init {
        if (arenaStateFile.exists()) {
            val cards = Gson().fromJson(FileReader(arenaStateFile).readText(), List::class.java)
            picks.addAll(cards.map { TESLTrackerData.getCard(it?.toString()) ?: Card.DUMMY })
            Logger.d("Restored ${picks.size} picks")
        }
    }

    override fun onResume() {
        Logger.i("ArenaState onResume")
        if (pickNumber > 0 && !finishPicks) {
            showPicksTier()
        }
        if (finishPicks) {
            hidePicksTier()
        }
        threadRunning = true
        runStateThread()
    }

    override fun onPause() {
        Logger.i("ArenaState onPause")
        hidePicksTier()
        GameState.deckTracker.isVisible = false
        threadRunning = false
    }

    override fun hasValidState(): Boolean {
        if (cardPicksToSelect == null) {
            return false
        }
        return cardPicksToSelect?.first != cardPicksToSelect?.second &&
                cardPicksToSelect?.second != cardPicksToSelect?.third
    }

    override fun resetState() {
        classSelect = null
        finishPicks = false
        picks.clear()
        saveArenaPicks()
        Logger.d("Arena state reset")
    }

    fun runStateThread() {
        CompletableFuture.runAsync {
            while (ArenaState.threadRunning && !finishPicks) {
                ScreenFuncs.takeScreenshot()?.apply {
                    processPickNumber(this)
                    if (cardPicksToSelect == null && pickNumber > 0) {
                        processPickCards(this)
                    }
                }
                Thread.sleep(1000L / ARENA_RECOGNIZER_SPS)
            }
        }
    }

    private fun processPickNumber(screenshot: BufferedImage) {
        CompletableFuture.runAsync {
            ArenaHandler.processArenaPickNumber(screenshot)?.run {
                synchronized(arenaPickLock) {
                    if (lastPickNumberRecognized != this) {
                        lastPickNumberRecognized = this
                        pickNumber = this
                        cardPicksToSelect = null
                        Logger.i("Arena Pick $pickNumber Detected!", true)
                    }
                }
            }
        }
    }

    fun processPickCards(screenshot: BufferedImage) {
        CompletableFuture.runAsync {
            ArenaHandler.processArenaPick(screenshot)?.run {
                synchronized(cardPicksToSelectLock) {
                    cardPicksToSelect = this
                    Logger.i("--${this.first.card.name}: ${this.first.card.arenaTier}")
                    Logger.i("--${this.second.card.name}: ${this.second.card.arenaTier}")
                    Logger.i("--${this.third.card.name}: ${this.third.card.arenaTier}")
                    ArenaState.setTierPicks(this)
                }
            }
        }
    }

    fun saveArenaPicks() {
        synchronized(saveArenaPicksLock) {
            try {
                val picksJson = Gson().toJson(picks.map(Card::shortName).toList())
                FileWriter(arenaStateFile).apply {
                    write(picksJson)
                    flush()
                }
            } catch (e: Exception) {
                Logger.e(e)
            }
        }
    }

    fun setTierPicks(cardsPick: Triple<CardPick, CardPick, CardPick>) {
        cardPicksToSelect = cardsPick
        Platform.runLater {
            card1ArenaTierStage.setPickValue(cardsPick.first.takeIf { pickNumber > 1 } ?: cardsPick.first.withoutSynergyList())
            card2ArenaTierStage.setPickValue(cardsPick.second.takeIf { pickNumber > 1 } ?: cardsPick.second.withoutSynergyList())
            card3ArenaTierStage.setPickValue(cardsPick.third.takeIf { pickNumber > 1 } ?: cardsPick.third.withoutSynergyList())
            showPicksTier()
        }
    }

    private fun showPicksTier() {
        card1ArenaTierStage.isVisible = true
        card2ArenaTierStage.isVisible = true
        card3ArenaTierStage.isVisible = true
        startMouseClickCapture()
    }

    private fun hidePicksTier() {
        card1ArenaTierStage.isVisible = false
        card2ArenaTierStage.isVisible = false
        card3ArenaTierStage.isVisible = false
        stopMouseClickCapture()
    }

    private fun startMouseClickCapture(retry: Int = 0) {
        try {
            GlobalScreen.registerNativeHook()
            GlobalScreen.removeNativeMouseListener(mouseListener)
            GlobalScreen.addNativeMouseListener(mouseListener)
            java.util.logging.Logger.getLogger(GlobalScreen::class.java.`package`.name).apply {
                level = Level.WARNING
                useParentHandlers = false
            }
        } catch (ex: NativeHookException) {
            Logger.e("There was a problem registering the native hook.")
            if (retry < 3) {
                startMouseClickCapture(retry + 1)
            }
        }
    }

    private fun stopMouseClickCapture() {
        GlobalScreen.removeNativeMouseListener(mouseListener)
        GlobalScreen.unregisterNativeHook()
    }

    private fun takeLastCardPicked(mousePos: Point, lastArenaCardPicks: Triple<CardPick, CardPick, CardPick>?) {
        with(TESLTracker.referenceConfig) {
            val cardSize = ImageFuncs.getScreenScaledSize(ARENA_PICK_SELECT_WIDTH, ARENA_PICK_SELECT_HEIGHT)
            val cardFirstPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_SELECT_FIRST_X, ARENA_PICK_SELECT_Y)
            val cardSecondPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_SELECT_SECOND_X, ARENA_PICK_SELECT_Y)
            val cardThirdPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_SELECT_THIRD_X, ARENA_PICK_SELECT_Y)

            testMouseInCardPos(mousePos, cardFirstPos, cardSize, lastArenaCardPicks?.first?.card)
            testMouseInCardPos(mousePos, cardSecondPos, cardSize, lastArenaCardPicks?.second?.card)
            testMouseInCardPos(mousePos, cardThirdPos, cardSize, lastArenaCardPicks?.third?.card)
        }
    }

    private fun testMouseInCardPos(mousePos: Point, cardPos: Point, cardSize: Dimension, card: Card?) {
        if (Rectangle(cardPos.x, cardPos.y, cardSize.width, cardSize.height).contains(mousePos)) {
            picks.add(card ?: Card.DUMMY)
            Logger.i("${card?.name} Picked")
            Mixpanel.postEventArenaPick(card ?: Card.DUMMY)
            if (pickNumber == 30) {
                finishPicks = true
            }
        }
    }

}