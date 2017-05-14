package com.ediposouza.executor

import com.ediposouza.TESLTracker
import com.ediposouza.data.PHash
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.extensions.getDeckBuilderFirstLineCardCrop
import com.ediposouza.extensions.getDeckBuilderNoneLeftCardCrop
import com.ediposouza.extensions.getScreenDeckBuilderEmptyCrop
import com.ediposouza.model.Card
import com.ediposouza.model.CardAttribute
import com.ediposouza.model.CardSlot
import com.ediposouza.model.DeckClass
import com.ediposouza.resolution.ReferenceConfig
import com.ediposouza.state.CollectionState
import com.ediposouza.state.StateHandler
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import com.ediposouza.util.ScreenFuncs
import com.tulskiy.keymaster.common.Provider
import java.awt.Robot
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

/**
 * Created by ediposouza on 11/05/17.
 */
object DeckBuildExecutor {

    val robot: Robot by lazy { Robot() }
    val keyProvider: Provider by lazy { Provider.getCurrentProvider(true) }
    val picksAttrList: MutableSet<CardAttribute> = HashSet()
    var isRunning = false

    fun buildDeck(deckName: String?, deckCards: Map<String, Int>? = null, deckCardSlots: List<CardSlot>? = null) {
        if (!TESLTracker.usingSupportedResolution) {
            TESLTracker.showMessageUnsupportedResolution()
            return
        }
        TESLTracker.showMessage("Deck build started, you can hit ESC to cancel.")
        keyProvider.register(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)) {
            isRunning = false
            keyProvider.stop()
        }
        val slots = deckCardSlots ?: deckCards?.map { CardSlot(TESLTrackerData.getCard(it.key) ?: Card.DUMMY, it.value) }
        val deckCls = DeckClass.getClasses(slots?.groupBy { it.card.dualAttr1 }?.keys?.toList() ?: listOf())
        picksAttrList.clear()
        isRunning = true
        StateHandler.currentTESLState = CollectionState.apply { deckClass = deckCls.firstOrNull() }
        clearFilters()
        clearDeck()
        pickDeckCards(slots ?: listOf())
        deckName?.let {
            changeDeckName(it)
        }
        isRunning = false
        keyProvider.stop()
    }

    private fun clearFilters() {
        mouseMove({ DECK_BUILDER_CLEAR_FILTER_MAGICKA_1_X }, { DECK_BUILDER_CLEAR_FILTER_MAGICKA_1_Y })
        mouseClick()
        mouseMove({ DECK_BUILDER_CLEAR_FILTER_MAGICKA_0_X }, { DECK_BUILDER_CLEAR_FILTER_MAGICKA_0_Y })
        mouseClick()
        mouseClick()
        mouseMove({ DECK_BUILDER_CLEAR_FILTER_RARITY_X }, { DECK_BUILDER_CLEAR_FILTER_RARITY_Y })
        mouseClick()
        mouseMove({ DECK_BUILDER_CLEAR_FILTER_RARITY_ALL_X }, { DECK_BUILDER_CLEAR_FILTER_RARITY_ALL_Y })
        mouseClick()
        mouseMove({ DECK_BUILDER_CLEAR_FILTER_SET_X }, { DECK_BUILDER_CLEAR_FILTER_SET_Y })
        mouseClick()
        mouseMove({ DECK_BUILDER_CLEAR_FILTER_SET_ALL_X }, { DECK_BUILDER_CLEAR_FILTER_SET_ALL_Y })
        mouseClick()
        mouseMove({ DECK_BUILDER_SEARCH_FIELD_X }, { DECK_BUILDER_SEARCH_FIELD_Y })
        mouseClick()
        pauseShort()
        with(robot) {
            keyPress(KeyEvent.VK_BACK_SPACE)
            keyRelease(KeyEvent.VK_BACK_SPACE)
        }
        pauseLong()
        mouseMove({ DECK_BUILDER_CLEAR_FILTER_ATTRIBUTES_X }, { DECK_BUILDER_CLEAR_FILTER_ATTRIBUTES_Y })
        pauseShort()
        mouseClick()
        pauseShort()
    }

    private fun clearDeck() {
        mouseMove({ DECK_BUILDER_CLEAR_CARDS_FIRST_CARD_X }, { DECK_BUILDER_CLEAR_CARDS_FIRST_CARD_Y })
        var i = 0
        do {
            val isSecondDeckCardEmpty = ScreenFuncs.takeScreenshot()?.getScreenDeckBuilderEmptyCrop()?.let {
                Recognizer.recognizeImageInMap(it, PHash.SCREENS_LIST) == PHash.SCREEN_DECK_BUILDER_EMPTY
            } ?: false
            mouseClick()
            mouseClick()
            mouseClick()
            i += 1
        } while (isRunning && !isSecondDeckCardEmpty && i < 25)
    }

    private fun pickDeckCards(deckCards: List<CardSlot>) {
        deckCards.forEach { slot ->
            if (!isRunning) {
                return
            }
            searchCard(slot.card.name)
            pauseLong()
            // Pause if there's a class change
            if (picksAttrList.size < 2) {
                picksAttrList.add(slot.card.dualAttr1)
                picksAttrList.add(slot.card.dualAttr2)
                if (picksAttrList.size >= 2) {
                    Logger.d("Class Change")
                    pauseShort()
                    pauseLong()
                }
            }
            var cardsFoundScreenshot = ScreenFuncs.takeScreenshot()
            (1..2).forEach {
                cardsFoundScreenshot?.let {
                    if (Recognizer.recognizeCardImage(it.getDeckBuilderFirstLineCardCrop(1)) == PHash.DECK_ITEM_CARD_EMPTY) {
                        pauseLong()
                        cardsFoundScreenshot = ScreenFuncs.takeScreenshot()
                    }
                }
            }
            cardsFoundScreenshot?.let {
                val cardShortName = slot.card.shortName
                var cardQtdNeed = slot.qtd
                for (cardPosition in 1..5) {
                    if (Recognizer.recognizeCardImage(it.getDeckBuilderFirstLineCardCrop(cardPosition)) == cardShortName) {
                        // Check Premium version
                        if (Recognizer.recognizeCardImage(it.getDeckBuilderFirstLineCardCrop(cardPosition + 1)) == cardShortName) {
                            val cardsPicked = pickCard(cardPosition + 1, cardQtdNeed)
                            cardQtdNeed -= cardsPicked
                        }
                        if (cardQtdNeed > 0) {
                            val cardsPicked = pickCard(cardPosition, cardQtdNeed)
                            cardQtdNeed -= cardsPicked
                        }
                    }
                    if (cardQtdNeed <= 0) {
                        break
                    }
                }
                if (cardQtdNeed == slot.qtd) {
                    Logger.d("Card not found ${slot.card.name}")
                }
            }
        }
    }

    private fun searchCard(cardName: String) {
        mouseMove({ DECK_BUILDER_SEARCH_FIELD_X }, { DECK_BUILDER_SEARCH_FIELD_Y })
        mouseClick()
        pauseShort()
        selectAllAndClear()
        copyAndPaste(cardName)
        pauseLong()
    }

    private fun pickCard(cardPosition: Int, qtd: Int): Int {
        mouseMove({ DECK_BUILDER_FIRST_LINE_CLICK_CARD_X + (cardPosition - 1) * DECK_BUILDER_FIRST_LINE_CLICK_CARD_DISTANCE_X },
                { DECK_BUILDER_FIRST_LINE_CLICK_CARD_Y })
        pauseShort()
        var cardsPicked = 0
        for (i in 1..qtd) {
            cardsPicked += 1
            mouseClick()
            if (i < qtd) {
                pauseShort()
                val noneLeftCardCrop = ScreenFuncs.takeScreenshot()?.getDeckBuilderNoneLeftCardCrop(cardPosition)
                if (noneLeftCardCrop != null &&
                        Recognizer.recognizeImageInMap(noneLeftCardCrop, PHash.DECK_ITEMS_LIST) == PHash.DECK_ITEM_NONE_LEFT) {
                    Logger.d("Card None Left")
                    break
                }
            }
        }
        return cardsPicked
    }

    private fun changeDeckName(deckName: String) {
        if (isRunning) {
            mouseMove({ DECK_BUILDER_DECK_COVER_X }, { DECK_BUILDER_DECK_COVER_Y })
            mouseClick()
            Thread.sleep(1000)
            copyAndPaste(deckName)
            mouseMove({ DECK_BUILDER_DECK_COVER_CONFIRM_X }, { DECK_BUILDER_DECK_COVER_CONFIRM_Y })
            mouseClick()
        }
    }

    private fun copyAndPaste(deckName: String) {
        val stringSelection = StringSelection(deckName)
        Toolkit.getDefaultToolkit().systemClipboard.setContents(stringSelection, stringSelection)
        with(robot) {
            keyPress(KeyEvent.VK_CONTROL)
            keyPress(KeyEvent.VK_V)
            keyRelease(KeyEvent.VK_V)
            keyRelease(KeyEvent.VK_CONTROL)
            keyPress(KeyEvent.VK_ENTER)
            keyRelease(KeyEvent.VK_ENTER)
        }
    }

    private fun selectAllAndClear() {
        with(robot) {
            keyPress(KeyEvent.VK_CONTROL)
            keyPress(KeyEvent.VK_A)
            keyRelease(KeyEvent.VK_A)
            keyRelease(KeyEvent.VK_CONTROL)
            keyPress(KeyEvent.VK_BACK_SPACE)
        }
    }

    private fun mouseMove(x: ReferenceConfig.() -> Int, y: ReferenceConfig.() -> Int) {
        with(TESLTracker.referenceConfig) {
            robot.mouseMove(x(), y())
        }
    }

    private fun mouseClick() {
        robot.mousePress(InputEvent.BUTTON1_MASK)
        pauseShort()
        robot.mouseRelease(InputEvent.BUTTON1_MASK)
        Thread.sleep(50)
    }

    private fun pauseShort() {
        Thread.sleep(150)
    }

    private fun pauseLong() {
        Thread.sleep(350)
    }

}