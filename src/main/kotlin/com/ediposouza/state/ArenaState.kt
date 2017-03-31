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
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.logging.Level

/**
 * Created by ediposouza on 24/03/17.
 */
object ArenaState : StateHandler.TESLState {

    private val card1ArenaTierStage by lazy { ArenaTierWidget(1) }
    private val card2ArenaTierStage by lazy { ArenaTierWidget(2) }
    private val card3ArenaTierStage by lazy { ArenaTierWidget(3) }

    private val arenaStateFile by lazy {
        File("${TESLTracker.jarPath}/data").let {
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

    var lastArenaTierPicks: Triple<CardPick, CardPick, CardPick>? = null
        set(value) {
            field = value
        }

    var pickNumber: Int = 0
        set(value) {
            field = value
            when {
                value > 0 -> Logger.d("PickNumber: $pickNumber")
                value == 1 -> {
                    resetState()
                    classSelect = ArenaHandler.processArenaClass(ScreenFuncs.takeScreenshot())
                }
            }
        }

    val picks = mutableListOf<Card>()

    var finishPicks = false
        set(value) {
            field = value
            if (value) {
                GameState.setDeckCardsSlot(picks
                        .groupBy(Card::shortName)
                        .map { CardSlot(it.value.first(), it.value.size) })
            }
        }

    val mouseListener = object : NativeMouseListener {
        override fun nativeMousePressed(p0: NativeMouseEvent?) {
        }

        override fun nativeMouseClicked(p0: NativeMouseEvent?) {
            p0?.point?.apply { takeLastCardPicked(this) }
        }

        override fun nativeMouseReleased(p0: NativeMouseEvent?) {
        }

    }

    init {
        if (arenaStateFile.exists()) {
            val cards = Gson().fromJson(FileReader(arenaStateFile).readText(), List::class.java)
            Logger.i("Read $cards")
            picks.addAll(cards.map { TESLTrackerData.getCard(it?.toString()) ?: Card.DUMMY })
            Logger.i("Restored ${picks.size} picks")
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
    }

    override fun onPause() {
        Logger.i("ArenaState onPause")
        saveArenaState()
        hidePicksTier()
    }

    override fun hasValidState(): Boolean {
        if (lastArenaTierPicks == null) {
            return true
        }
        return lastArenaTierPicks?.first != lastArenaTierPicks?.second ||
                lastArenaTierPicks?.second != lastArenaTierPicks?.third
    }

    override fun resetState() {
        classSelect = null
        finishPicks = false
        picks.clear()
        saveArenaState()
    }

    private fun saveArenaState() {
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

    fun setTierPicks(cardsPick: Triple<CardPick, CardPick, CardPick>) {
        lastArenaTierPicks = cardsPick
        Platform.runLater {
            card1ArenaTierStage.setPickValue(cardsPick.first)
            card2ArenaTierStage.setPickValue(cardsPick.second)
            card3ArenaTierStage.setPickValue(cardsPick.third)
        }
    }

    private fun showPicksTier() {
        card1ArenaTierStage.isVisible = true
        card2ArenaTierStage.isVisible = true
        card3ArenaTierStage.isVisible = true
        try {
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeMouseListener(mouseListener)
            java.util.logging.Logger.getLogger(GlobalScreen::class.java.`package`.name).apply {
                level = Level.WARNING
                useParentHandlers = false
            }
        } catch (ex: NativeHookException) {
            System.err.println("There was a problem registering the native hook.")
        }
    }

    private fun hidePicksTier() {
        card1ArenaTierStage.isVisible = false
        card2ArenaTierStage.isVisible = false
        card3ArenaTierStage.isVisible = false
        GlobalScreen.removeNativeMouseListener(mouseListener)
        GlobalScreen.unregisterNativeHook()
    }

    private fun takeLastCardPicked(mousePos: Point) {
        with(TESLTracker.referenceConfig) {
            val cardSize = ImageFuncs.getScreenScaledSize(ARENA_PICK_SELECT_WIDTH, ARENA_PICK_SELECT_HEIGHT)
            val cardFirstPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_SELECT_FIRST_X, ARENA_PICK_SELECT_Y)
            val cardSecondPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_SELECT_SECOND_X, ARENA_PICK_SELECT_Y)
            val cardThirdPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_SELECT_THIRD_X, ARENA_PICK_SELECT_Y)

            testMouseInCardPos(mousePos, cardFirstPos, cardSize, lastArenaTierPicks?.first?.card)
            testMouseInCardPos(mousePos, cardSecondPos, cardSize, lastArenaTierPicks?.second?.card)
            testMouseInCardPos(mousePos, cardThirdPos, cardSize, lastArenaTierPicks?.third?.card)
        }
    }

    private fun testMouseInCardPos(mousePos: Point, cardPos: Point, cardSize: Dimension, card: Card?) {
        if (Rectangle(cardPos.x, cardPos.y, cardSize.width, cardSize.height).contains(mousePos)) {
            picks.add(card ?: Card.DUMMY)
            Logger.i("${card?.name} Picked")
        }
    }

}