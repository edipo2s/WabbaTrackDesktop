package com.ediposouza.ui

import com.ediposouza.TESLTracker
import com.ediposouza.data.PHash
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.extensions.getCardForSlotCrop
import com.ediposouza.extensions.getScreenDeckBuilderCrop
import com.ediposouza.extensions.makeDraggable
import com.ediposouza.extensions.toFXImage
import com.ediposouza.model.*
import com.ediposouza.state.GameState
import com.ediposouza.util.*
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.JFXPanel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.MenuItem
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import tornadofx.*
import java.awt.Dimension
import java.awt.Window
import java.io.InputStream
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * Created by Edipo on 18/03/2017.
 */
class DeckTrackerWidget : JFrame() {

    private val deckCardsSlot: ObservableList<CardSlot> = FXCollections.observableArrayList<CardSlot>()
    private var deckTrackerZoom: Float = 0.8f.takeIf { GameState.matchMode == MatchMode.ARENA } ?: 0.9f
    private lateinit var deckTrackerSize: Dimension

    var deckName: String? = null
    private val configIconStream: InputStream by lazy { TESLTracker::class.java.getResourceAsStream("/UI/ic_settings.png") }
    private val defaultDeckCoverStream: InputStream by lazy { TESLTracker::class.java.getResourceAsStream("/UI/Class/Default.webp") }

    val jfxPanel by lazy {
        JFXPanel().apply {
            Platform.runLater {
                scene = createFxScene()
                SwingUtilities.invokeLater {
                    pack()
                    isVisible = true
                }
            }
        }
    }

    val contextMenu = ContextMenu(
            MenuItem("Build").apply {
                setOnAction {
                    launch(CommonPool) {
                        delay(250)
                        ScreenFuncs.takeScreenshot()?.getScreenDeckBuilderCrop()?.let {
                            if (Recognizer.recognizeImageInMap(it, PHash.SCREENS_LIST) == PHash.SCREEN_DECK_BUILDER) {
                                this@DeckTrackerWidget.isVisible = false
                                BuildDeck.buildDeck(deckName, deckCardSlots = deckCardsSlot)
                                this@DeckTrackerWidget.isVisible = true
                                Mixpanel.postEventBuildDeckFromTracker(deckName)
                            } else {
                                TESLTracker.showMessage("To build a deck, please first go to deck builder screen")
                            }
                        }
                    }
                }
            },
            MenuItem("Decrease Size").apply {
                setOnAction {
                    deckTrackerZoom -= 0.1f
                    Platform.runLater {
                        jfxPanel.scene = createFxScene()
                        updateDeckCover()
                    }
                    Mixpanel.postEventDeckTrackerDecreaseZoom()
                }
            },
            MenuItem("Increase Size").apply {
                setOnAction {
                    deckTrackerZoom += 0.1f
                    Platform.runLater {
                        jfxPanel.scene = createFxScene()
                        updateDeckCover()
                    }
                    Mixpanel.postEventDeckTrackerIncreaseZoom()
                }
            },
            MenuItem("Hide Deck").apply {
                setOnAction {
                    GameState.deckTracker.isVisible = false
                    GameState.shouldShowDeckTracker = false
                    Mixpanel.postEventDeckTrackerHide()
                }
            }
    )

    val deckCoverName by lazy {
        Label().apply {
            textFill = Color.WHITE
            effect = DropShadow(5.0, Color.BLACK)
            style = "-fx-font: 12px 'Legends';"
        }
    }

    val deckAttr1Image by lazy {
        ImageView().apply {
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val deckAttr2Image by lazy {
        ImageView().apply {
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val infoAttr1Image by lazy {
        ImageView().apply {
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val infoAttr1Label by lazy {
        Label().apply {
            textFill = Color.WHITE
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val infoAttr2Image by lazy {
        ImageView().apply {
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val infoAttr2Label by lazy {
        Label().apply {
            textFill = Color.WHITE
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val infoLabel by lazy {
        Label().apply {
            textFill = Color.WHITE
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val deckCoverPane by lazy {
        BorderPane().apply {
            top = BorderPane().apply {
                left = VBox().apply {
                    add(deckCoverName)
                    add(HBox().apply {
                        add(deckAttr1Image)
                        add(label(" "))
                        add(deckAttr2Image)
                        padding = Insets(1.0, 0.0, 0.0, 0.0)
                    })
                    padding = Insets(2.0, 0.0, 0.0, 4.0)
                }
                right = ImageView().apply {
                    image = Image(configIconStream)
                    padding = Insets(0.0, 0.0, 0.0, 2.0)
                    setOnMousePressed { me ->
                        if (me.isPrimaryButtonDown || me.isSecondaryButtonDown) {
                            contextMenu.show(this, me.screenX, me.screenY)
                        }
                    }
                }
                makeDraggable(this@DeckTrackerWidget)
            }
            background = Background(BackgroundImage(defaultDeckCoverStream.toFXImage(), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT))
        }
    }

    init {
        type = Window.Type.UTILITY
        isUndecorated = true
        isAlwaysOnTop = true
        background = java.awt.Color(0, 0, 0, 0)

        with(TESLTracker.referenceConfig) {
            val deckTrackerPos = ImageFuncs.getScreenScaledPosition(DECK_TRACKER_X, DECK_TRACKER_Y)
            setLocation(deckTrackerPos.x, deckTrackerPos.y)
            val screenHeightUseful = (TESLTracker.screenSize.height * 1.5).toInt()
            deckTrackerSize = Dimension(ImageFuncs.getScreenScaledSize(DECK_TRACKER_WIDTH, 0).width, screenHeightUseful)
            size = deckTrackerSize
        }

        contentPane.add(jfxPanel)
    }

    private fun createFxScene(): Scene {
        val cellSize by lazy {
            with(TESLTracker.referenceConfig) {
                val cellBaseHeight = DECK_TRACKER_CARD_HEIGHT * deckTrackerZoom
                val cellBaseWidth = DECK_TRACKER_CARD_WIDTH * deckTrackerZoom
                ImageFuncs.getScreenScaledSize(cellBaseWidth.toInt(), cellBaseHeight.toInt())
            }
        }
        val layout = VBox().apply {
            add(deckCoverPane.apply {
                maxWidth = cellSize.width.toDouble() + cellSize.height * 1.5
                minHeight = cellSize.height * 1.5
                makeDraggable(this@DeckTrackerWidget)
            })
            add(listview<CardSlot> {
                items = deckCardsSlot
                background = Background.EMPTY
                prefHeightProperty().bind(Bindings.size(deckCardsSlot).multiply(cellSize.height + 1.1))
                prefWidth = cellSize.width * 1.3
                setCellFactory {
                    CardSlotCell(this@DeckTrackerWidget, cellSize).apply {
                        background = Background.EMPTY
                        prefWidthProperty().bind(this@listview.widthProperty().subtract(2))
                    }
                }
                fixedCellSize = cellSize.height + 1.0
                makeDraggable(this@DeckTrackerWidget)
//                style = "-fx-background-color: #00FF00; "
            })
            add(BorderPane().apply {
                left = VBox().apply {
                    add(HBox().apply {
                        alignment = Pos.CENTER_LEFT
                        add(infoAttr1Image)
                        add(infoAttr1Label)
                    })
                    add(HBox().apply {
                        alignment = Pos.CENTER_LEFT
                        add(infoAttr2Image)
                        add(infoAttr2Label)
                    })
                    add(infoLabel.apply {
                        alignment = Pos.CENTER
                        maxWidth = cellSize.width.toDouble() + cellSize.height * 1.5
                    })
                }
                background = Background.EMPTY
                padding = Insets(2.0, 2.0, 2.0, 5.0)
                maxWidth = cellSize.width.toDouble() + cellSize.height * 1.5
                style = "-fx-background-color: #000000AA; " +
                        "-fx-background-radius: 5.0;"
                makeDraggable(this@DeckTrackerWidget)
            })
            background = Background.EMPTY
        }

        return Scene(layout).apply {
            preferredSize = deckTrackerSize
            fill = Color.TRANSPARENT
            stylesheets.add(TESLTracker::class.java.getResource("/UI/deckTrackerWidget.css").toExternalForm())
        }
    }

    fun setDeckCardsSlot(cardsSlot: List<CardSlot>, name: String? = null) {
        isVisible = true
        deckName = name
        deckCardsSlot.apply {
            clear()
            if (TESLTrackerData.cards.size > 0) {
                addAll(cardsSlot.sortedBy { it.card.name }.sortedBy { it.card.cost })
            }
        }
        updateDeckCover()
    }

    fun trackCardDraw(card: Card) {
        Platform.runLater {
            with(deckCardsSlot) {
                find { it.card == card }?.apply {
                    indexOf(this).takeIf { it >= 0 }?.let {
                        set(it, this.apply {
                            currentQtd -= 1
                            recentChanged = true
                        })
                    }
                }
            }
            updateDeckInfo()
        }
    }

    fun resetDraws() {
        Platform.runLater {
            for (i in deckCardsSlot.indices) {
                deckCardsSlot[i] = deckCardsSlot[i].apply {
                    currentQtd = qtd
                }
            }
            updateDeckInfo()
        }
    }

    private fun updateDeckCover() {
        val deckClass = DeckClass.getClasses(deckCardsSlot.groupBy { it.card.attr }.keys.toList()).firstOrNull()
        val deckClassName = deckClass?.name?.toLowerCase()?.capitalize()
        val deckCoverStream = TESLTracker::class.java.getResourceAsStream("/UI/Class/${deckClassName ?: "Default"}.webp")
        val cellSize by lazy {
            with(TESLTracker.referenceConfig) {
                val cellBaseHeight = DECK_TRACKER_CARD_HEIGHT * deckTrackerZoom
                val cellBaseWidth = DECK_TRACKER_CARD_WIDTH * deckTrackerZoom
                ImageFuncs.getScreenScaledSize(cellBaseWidth.toInt(), cellBaseHeight.toInt())
            }
        }
        deckCoverName.text = deckName ?: deckClassName ?: ""
        deckCoverName.maxWidth = cellSize.width.toDouble()
        deckCoverPane.background = Background(BackgroundImage(deckCoverStream.toFXImage(), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize(cellSize.width.toDouble() + cellSize.height * 1.5,
                        cellSize.height * 1.5, false, false, false, false)))
        val deckAttr1 = deckClass?.attr1 ?: CardAttribute.NEUTRAL
        val deckAttr2 = deckClass?.attr2 ?: CardAttribute.NEUTRAL
        val deckAttrSize = cellSize.height * 0.6
        val deckAttr1Name = deckAttr1.name.toLowerCase().capitalize()
        val deckAttr2Name = deckAttr2.name.toLowerCase().capitalize()
        with(deckAttr1Image) {
            image = Image(TESLTracker::class.java.getResourceAsStream("/UI/Attribute/$deckAttr1Name.png"))
            fitHeight = deckAttrSize * 1.2
            fitWidth = deckAttrSize * 1.2
            isVisible = deckAttr1 != CardAttribute.NEUTRAL
        }
        with(deckAttr2Image) {
            image = Image(TESLTracker::class.java.getResourceAsStream("/UI/Attribute/$deckAttr2Name.png"))
            fitHeight = deckAttrSize * 1.2
            fitWidth = deckAttrSize * 1.2
            isVisible = deckAttr2 != CardAttribute.NEUTRAL
        }
        with(infoAttr1Image) {
            image = Image(TESLTracker::class.java.getResourceAsStream("/UI/Attribute/$deckAttr1Name.png"))
            fitHeight = deckAttrSize
            fitWidth = deckAttrSize
            isVisible = deckAttr1 != CardAttribute.NEUTRAL
        }
        with(infoAttr2Image) {
            image = Image(TESLTracker::class.java.getResourceAsStream("/UI/Attribute/$deckAttr2Name.png"))
            fitHeight = deckAttrSize
            fitWidth = deckAttrSize
            isVisible = deckAttr2 != CardAttribute.NEUTRAL
        }
        updateDeckInfo()
    }

    private fun updateDeckInfo() {
        with(deckCardsSlot) {
            val cardsLeftQtd = sumBy { it.currentQtd }.toDouble()
            val deckClass = DeckClass.getClasses(deckCardsSlot.groupBy { it.card.attr }.keys.toList()).firstOrNull()
            val deckAttr1 = deckClass?.attr1 ?: CardAttribute.NEUTRAL
            val cardsAttr1Qtd = filter { it.card.dualAttr1 == deckAttr1 }.sumBy { it.currentQtd }
            val cardsAttr1Chance = Bindings.format("%.1f", 100 * cardsAttr1Qtd / cardsLeftQtd).value
            infoAttr1Label.text = ": $cardsAttr1Qtd cards - $cardsAttr1Chance% Hit"
            val deckAttr2 = deckClass?.attr2 ?: CardAttribute.NEUTRAL
            val cardsAttr2Qtd = filter { it.card.dualAttr2 == deckAttr2 }.sumBy { it.currentQtd }
            val cardsAttr2Chance = Bindings.format("%.1f", 100 * cardsAttr2Qtd / cardsLeftQtd).value
            infoAttr2Label.text = ": $cardsAttr2Qtd cards - $cardsAttr2Chance% Hit"
            val deckCardsSlotProphecies = filter { it.card.keywords.contains(CardKeyword.PROPHECY) }
            val cardsPropheciesQtd = deckCardsSlotProphecies.sumBy { it.currentQtd }
            val cardsPropheciesHitChance = Bindings.format("%.1f", 100 * cardsPropheciesQtd / cardsLeftQtd).value
            with(infoLabel) {
                text = "$cardsPropheciesQtd Prophecies - $cardsPropheciesHitChance% Hit"
            }
        }
    }

    class CardSlotCell(val deckTrackerWidget: DeckTrackerWidget, val cardSize: Dimension) : ListCell<CardSlot>() {

        private var cardWidget: CardWidget? = null

        override fun updateItem(item: CardSlot?, empty: Boolean) {
            super.updateItem(item, empty)
            if (item != null) {
                val cardSetName = item.card.set.name.toLowerCase().capitalize()
                val cardAttrName = item.card.attr.name.toLowerCase().capitalize()
                val cardImagePath = "/CardsWebp/$cardSetName/$cardAttrName/${item.card.shortName}.webp"
                graphic = StackPane().apply {
                    add(stackpane {
                        imageview {
                            var cardFullImage = ImageIO.read(TESLTracker::class.java.getResourceAsStream("/CardsWebp/card_back.webp"))
                            try {
                                cardFullImage = ImageIO.read(TESLTracker::class.java.getResourceAsStream(cardImagePath))
                            } catch (e: Exception) {
                                Logger.e(e)
                            }
                            image = cardFullImage?.getCardForSlotCrop()?.toFXImage()
                            fitHeight = cardSize.height.toDouble() * 0.9
                            fitWidth = cardSize.width.toDouble() * 0.6
                            opacity = 1.0.takeIf { item.currentQtd > 0 } ?: 0.2
                        }
                        padding = Insets(0.0, 0.0, 0.0, cardSize.width * 0.5)
                        style = "-fx-background-radius: 25.0;"
                    })
                    add(imageview {
                        val deckClass = DeckClass.getClasses(listOf(item.card.dualAttr1, item.card.dualAttr2)).firstOrNull()
                        val deckClassName = deckClass?.name?.toLowerCase()?.capitalize()
                        val attrBorderImage = "/UI/Deck/frame${deckClassName ?: cardAttrName}.webp"
                        image = ImageIO.read(TESLTracker::class.java.getResourceAsStream(attrBorderImage))?.toFXImage()
                        fitHeight = cardSize.height.toDouble()
                        fitWidth = cardSize.width.toDouble() + cardSize.height.toDouble() / 2
                    })
                    add(borderpane {
                        left = hbox {
                            imageview {
                                val cardCost = item.card.cost
                                image = Image(TESLTracker::class.java.getResourceAsStream("/UI/Magicka/Magicka$cardCost.png"))
                                fitHeight = cardSize.height.toDouble()
                                fitWidth = cardSize.height.toDouble()
                            }
                            label {
                                alignment = Pos.CENTER_LEFT
                                text = item.card.name
                                textFill = when {
                                    item.currentQtd > 0 && item.currentQtd < item.qtd -> Color.YELLOW
                                    item.currentQtd <= 0 -> Color.DARKGRAY
                                    else -> Color.WHITE
                                }
                                effect = DropShadow(5.0, Color.BLACK)
                                padding = Insets(0.0, 0.0, 0.0, 2.0)
                                minWidth = cardSize.width * 0.6
                                maxWidth = cardSize.width * 0.6
                                minHeight = cardSize.height.toDouble()
                            }
                        }
                        right = stackpane {
                            val cardQtd = item.currentQtd
                            add(imageview {
                                image = Image(TESLTracker::class.java.getResourceAsStream("/UI/frameQtd.png"))
                                fitHeight = cardSize.height.toDouble()
                                fitWidth = cardSize.height.toDouble()
                                isVisible = cardQtd > 1
                            })
                            add(label {
                                alignment = Pos.CENTER
                                textFill = Color.WHITE
                                effect = DropShadow(5.0, Color.BLACK)
                                if (cardQtd > 1) {
                                    text = "$cardQtd"
                                }
                                padding = Insets(0.0, 2.0, 0.0, 0.0)
                                style = "-fx-font: 12px 'Legends';"
                            })
                            prefWidth = cardSize.height.toDouble() / 2
                        }
                    })
                    add(StackPane().apply {
                        opacity = 0.0
                        style = "-fx-background-color: #FFFF00AA; " +
                                "-fx-background-radius: 25.0;"
                        if (item.recentChanged) {
                            item.recentChanged = false
                            opacity = 1.0
                            launch(CommonPool) {
                                while (opacity > 0.0) {
                                    Platform.runLater {
                                        opacity -= 0.1
                                    }
                                    delay(100L)
                                }
                            }
                        }
                    })
                    background = Background.EMPTY
                    maxWidth = cardSize.width.toDouble() + cardSize.height
                }
                setOnMouseEntered {
                    val cardPosX = deckTrackerWidget.location.x
                    val cardPosY = deckTrackerWidget.location.y + (listView.items.indexOf(item) * cardSize.height)
                    cardWidget = CardWidget(item.card, cardPosX, cardPosY)
                    cardWidget?.isVisible = true
                }
                setOnMouseExited {
                    cardWidget?.isVisible = false
                    cardWidget = null
                }
            }
        }
    }

}