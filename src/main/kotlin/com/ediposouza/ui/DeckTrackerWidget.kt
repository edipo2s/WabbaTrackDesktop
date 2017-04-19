package com.ediposouza.ui

import com.ediposouza.TESLTracker
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.extensions.getCardForSlotCrop
import com.ediposouza.extensions.makeDraggable
import com.ediposouza.extensions.toFXImage
import com.ediposouza.model.*
import com.ediposouza.state.GameState
import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.JFXPanel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.SnapshotParameters
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.control.MenuItem
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*
import java.awt.Dimension
import java.awt.Window
import java.io.InputStream
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * Created by Edipo on 18/03/2017.
 */
class DeckTrackerWidget : JFrame() {

    private val deckCardsSlot: ObservableList<CardSlot> = FXCollections.observableArrayList<CardSlot>()
    private var deckTrackerZoom: Float = 0.8f.takeIf { GameState.matchMode == MatchMode.ARENA } ?: 1.0f
    private lateinit var deckTrackerSize: Dimension

    val configIconStream: InputStream by lazy { TESLTracker::class.java.getResourceAsStream("/UI/ic_settings.png") }
    val defaultDeckCoverStream: InputStream by lazy { TESLTracker::class.java.getResourceAsStream("/UI/Class/Default.webp") }

    val contextMenu = ContextMenu(
            MenuItem("Hide").apply {
                setOnAction {
                    GameState.deckTracker.isVisible = false
                    GameState.shouldShowDeckTracker = false
                }
            },
            MenuItem("Zoom +").apply {
                setOnAction {
                    deckTrackerZoom -= 0.2f
                    contentPane.removeAll()
                    updateDeckCover()
                    addFXScene()
                }
            },
            MenuItem("Zoom -").apply {
                setOnAction {
                    deckTrackerZoom += 0.2f
                    contentPane.removeAll()
                    updateDeckCover()
                    addFXScene()
                }
            }
    )

    val deckCoverName by lazy {
        Label().apply {
            textFill = Color.WHITE
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val deckAttr1Image by lazy {
        ImageView().apply {
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val deckAttr1Label by lazy {
        Label().apply {
            textFill = Color.WHITE
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val deckAttr2Image by lazy {
        ImageView().apply {
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val deckAttr2Label by lazy {
        Label().apply {
            textFill = Color.WHITE
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val deckInfoLabel by lazy {
        Label().apply {
            textFill = Color.WHITE
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val deckCoverPane by lazy {
        BorderPane().apply {
            top = BorderPane().apply {
                left = deckCoverName.apply {
                    padding = Insets(2.0, 0.0, 0.0, 4.0)
                }
                right = ImageView().apply {
                    image = Image(configIconStream)
                    padding = Insets(0.0, 0.0, 0.0, 2.0)
                    setOnMousePressed { me ->
                        if (me.isPrimaryButtonDown) {
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

        addFXScene()
    }

    private fun addFXScene() {
        JFXPanel().apply {
            contentPane.add(this)
            Platform.runLater {
                scene = createFxScene()
                SwingUtilities.invokeLater {
                    pack()
                    isVisible = true
                }
            }
        }
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
            })
            add(BorderPane().apply {
                left = VBox().apply {
                    add(HBox().apply {
                        alignment = Pos.CENTER_LEFT
                        add(deckAttr1Image)
                        add(deckAttr1Label)
                    })
                    add(HBox().apply {
                        alignment = Pos.CENTER_LEFT
                        add(deckAttr2Image)
                        add(deckAttr2Label)
                    })
                    add(deckInfoLabel.apply {
                        alignment = Pos.CENTER
                        maxWidth = cellSize.width.toDouble() + cellSize.height * 1.5
                    })
                }
                background = Background.EMPTY
                padding = Insets(2.0, 2.0, 2.0, 5.0)
                maxWidth = cellSize.width.toDouble() + cellSize.height * 1.5
                style = "-fx-background-color: #000000AA; " +
                        "-fx-background-radius: 5.0;"
            })
            add(listview<CardSlot> {
                items = deckCardsSlot
                background = Background.EMPTY
                prefHeight = deckTrackerSize.height.toDouble()
                prefWidth = deckTrackerSize.width.toDouble()
                setCellFactory {
                    CardSlotCell(cellSize).apply {
                        background = Background.EMPTY
                        prefWidthProperty().bind(this@listview.widthProperty().subtract(2))
                        prefHeight = cellSize.height.toDouble() + 1
                    }
                }
                makeDraggable(this@DeckTrackerWidget)
//                    style = "-fx-background-color: #00FF00; "
            })
            background = Background.EMPTY
        }

        return Scene(layout).apply {
            fill = Color.TRANSPARENT
            stylesheets.add(TESLTracker::class.java.getResource("/UI/deckTrackerWidget.css").toExternalForm())
        }
    }

    fun setDeckCardsSlot(cardsSlot: List<CardSlot>) {
        isVisible = true
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
                            CompletableFuture.runAsync {
                                Thread.sleep(500L)
                                recentChanged = false
                            }
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
        deckCoverName.text = deckClassName ?: ""
        deckCoverPane.background = Background(BackgroundImage(deckCoverStream.toFXImage(), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize(cellSize.width.toDouble() + cellSize.height * 1.5,
                        cellSize.height * 1.5, false, false, false, false)))
        val deckAttr1 = deckClass?.attr1 ?: CardAttribute.NEUTRAL
        val deckAttr2 = deckClass?.attr2 ?: CardAttribute.NEUTRAL
        val deckAttrSize = cellSize.height * 0.5
        with(deckAttr1Image) {
            image = Image(TESLTracker::class.java.getResourceAsStream("/UI/Attribute/${deckAttr1.name.toLowerCase().capitalize()}.png"))
            fitHeight = deckAttrSize
            fitWidth = deckAttrSize
            isVisible = deckAttr1 != CardAttribute.NEUTRAL
        }
        with(deckAttr2Image) {
            image = Image(TESLTracker::class.java.getResourceAsStream("/UI/Attribute/${deckAttr2.name.toLowerCase().capitalize()}.png"))
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
            deckAttr1Label.text = ": $cardsAttr1Qtd cards - $cardsAttr1Chance% Hit"
            val deckAttr2 = deckClass?.attr2 ?: CardAttribute.NEUTRAL
            val cardsAttr2Qtd = filter { it.card.dualAttr2 == deckAttr2 }.sumBy { it.currentQtd }
            val cardsAttr2Chance = Bindings.format("%.1f", 100 * cardsAttr2Qtd / cardsLeftQtd).value
            deckAttr2Label.text = ": $cardsAttr2Qtd cards - $cardsAttr2Chance% Hit"
            val deckCardsSlotProphecies = filter { it.card.keywords.contains(CardKeyword.PROPHECY) }
            val cardsPropheciesQtd = deckCardsSlotProphecies.sumBy { it.currentQtd }
            val cardsPropheciesHitChance = Bindings.format("%.1f", 100 * cardsPropheciesQtd / cardsLeftQtd).value
            with(deckInfoLabel) {
                text = "$cardsPropheciesQtd Prophecies - $cardsPropheciesHitChance% Hit"
            }
        }
    }

    class CardSlotCell(val cardSize: Dimension) : ListCell<CardSlot>() {

        override fun updateItem(item: CardSlot?, empty: Boolean) {
            super.updateItem(item, empty)
            if (item != null) {
                var changeIndicator: BorderPane? = null
                graphic = StackPane().apply {
                    add(stackpane {
                        imageview {
                            val cardSetName = item.card.set.name.toLowerCase().capitalize()
                            val cardAttrName = item.card.attr.name.toLowerCase().capitalize()
                            val cardImagePath = "/CardsWebp/$cardSetName/$cardAttrName/${item.card.shortName}.webp"
                            var cardFullImage = ImageIO.read(TESLTracker::class.java.getResourceAsStream("/CardsWebp/card_back.webp"))
                            try {
                                cardFullImage = ImageIO.read(TESLTracker::class.java.getResourceAsStream(cardImagePath))
                            } catch (e: Exception) {
                                Logger.e(e)
                            }
                            image = cardFullImage?.getCardForSlotCrop()?.toFXImage()
                            fitHeight = cardSize.height.toDouble()
                            fitWidth = cardSize.width.toDouble() * 0.8
                            clip = Rectangle(fitWidth, fitHeight).apply {
                                arcWidth = 20.0
                                arcHeight = 20.0
                            }
                            SnapshotParameters().apply {
                                fill = Color.TRANSPARENT
                                val roundedImage = snapshot(this, null)
                                clip = null
                                effect = DropShadow(20.0, Color.BLACK)
                                image = roundedImage
                            }
                            opacity = 0.85.takeIf { item.currentQtd > 0 } ?: 0.2
                        }
                        padding = Insets(0.0, 0.0, 0.0, cardSize.width * 0.2)
                        style = "-fx-background-color: linear-gradient(to right, ${item.card.attr.colorHex}, #000000AA); " +
                                "-fx-background-radius: 25.0;"
                    })
                    add(borderpane {
                        left = imageview {
                            val cardCost = item.card.cost
                            image = Image(TESLTracker::class.java.getResourceAsStream("/UI/Magicka/Magicka$cardCost.png"))
                            fitHeight = cardSize.height.toDouble()
                            fitWidth = cardSize.height.toDouble()
                        }
                        center = label {
                            text = item.card.name
                            textFill = when {
                                item.currentQtd > 0 && item.currentQtd < item.qtd -> Color.YELLOW
                                item.currentQtd <= 0 -> Color.DARKGRAY
                                else -> Color.WHITE
                            }
                            effect = DropShadow(5.0, Color.BLACK)
                            padding = Insets(0.0, 0.0, 0.0, 3.0)
                            minWidth = cardSize.width * 0.9
                            maxWidth = cardSize.width * 0.9
                        }
                        right = label {
                            alignment = Pos.CENTER
                            textFill = Color.WHITE
                            effect = DropShadow(5.0, Color.BLACK)
                            val cardQtd = item.currentQtd
                            if (cardQtd > 1) {
                                text = "$cardQtd"
                            }
                            style = "-fx-font: 16px 'Planewalker';"
                            padding = Insets(0.0, 5.0, 0.0, 0.0)
                            prefWidth = cardSize.height.toDouble() / 2
                        }
                    })
                    add(borderpane {
                        changeIndicator = this
                        background = Background.EMPTY
                        style = "-fx-background-color: #FFFF00AA; " +
                                "-fx-background-radius: 25.0;"
                        opacity = 0.0
                    })
                    background = Background.EMPTY
                    maxWidth = cardSize.width.toDouble() + cardSize.height
                    style = "-fx-background-color: #000000AA; " +
                            "-fx-background-radius: 25.0;"
                    if (item.recentChanged) {
                        item.recentChanged = false
                        changeIndicator?.opacity = 1.0
                        CompletableFuture.runAsync {
                            var changeIndicatorOpacity = changeIndicator?.opacity ?: 0.0
                            while (changeIndicatorOpacity > 0.0) {
//                                Logger.d("$changeIndicatorOpacity")
                                changeIndicator?.opacity = changeIndicatorOpacity - 0.1
                                changeIndicatorOpacity = changeIndicator?.opacity ?: 0.0
                                Thread.sleep(100)
                            }
                        }
                    }
                }
            }
        }
    }

}