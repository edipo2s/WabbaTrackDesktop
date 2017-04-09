package com.ediposouza.ui

import com.ediposouza.TESLTracker
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.extensions.getCardForSlotCrop
import com.ediposouza.extensions.makeDraggable
import com.ediposouza.model.Card
import com.ediposouza.model.CardSlot
import com.ediposouza.model.DeckClass
import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.JFXPanel
import javafx.embed.swing.SwingFXUtils
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
import java.io.File
import java.io.InputStream
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * Created by Edipo on 18/03/2017.
 */
class DeckTrackerWidget : JFrame() {

    private val deckCardsSlot: ObservableList<CardSlot> = FXCollections.observableArrayList<CardSlot>()
    private lateinit var deckTrackerSize: Dimension

    val configIconStream: InputStream by lazy { TESLTracker::class.java.getResourceAsStream("/UI/ic_settings.png") }
    val defaultDeckCoverStream: InputStream by lazy { TESLTracker::class.java.getResourceAsStream("/UI/Class/Default.png") }

    val contextMenu = ContextMenu(MenuItem("Hide").apply {
        setOnAction {
            this@DeckTrackerWidget.isVisible = false
        }
    })

    val deckCoverName by lazy {
        Label().apply {
            textFill = Color.WHITE
            effect = DropShadow(5.0, Color.BLACK)
        }
    }

    val deckCoverPane by lazy {
        BorderPane().apply {
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
            background = Background(BackgroundImage(Image(defaultDeckCoverStream), BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT))
            makeDraggable(this@DeckTrackerWidget)
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
            val screenHeightUseful = TESLTracker.screenSize.height.toInt()
            deckTrackerSize = Dimension(ImageFuncs.getScreenScaledSize(DECK_TRACKER_WIDTH, 0).width, screenHeightUseful)
            size = deckTrackerSize
        }

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
        with(TESLTracker.referenceConfig) {
            val cellSize = ImageFuncs.getScreenScaledSize(DECK_TRACKER_CARD_WIDTH, DECK_TRACKER_CARD_HEIGHT)
            val layout = VBox().apply {
                add(deckCoverPane.apply {
                    maxWidth = cellSize.width.toDouble() + cellSize.height * 1.5
                    minHeight = cellSize.height * 1.5
                })
                add(listview<CardSlot> {
                    items = deckCardsSlot
                    background = Background.EMPTY
                    prefHeight = deckTrackerSize.height.toDouble()
                    prefWidth = deckTrackerSize.width.toDouble()
                    setCellFactory {
                        CardSlotCell().apply {
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
    }

    fun setDeckCardsSlot(cardsSlot: List<CardSlot>) {
        isVisible = true
        deckCardsSlot.apply {
            clear()
            if (TESLTrackerData.cards.size > 0) {
                addAll(cardsSlot.sortedBy { it.card.name }.sortedBy { it.card.cost })
            }
        }
        val coverFileName = DeckClass.getClasses(deckCardsSlot.groupBy { it.card.attr }.keys.toList()).firstOrNull()
        val deckCoverStream = TESLTracker::class.java.getResourceAsStream("/UI/Class/${coverFileName ?: "Default"}.png")
        deckCoverName.text = coverFileName?.name?.toLowerCase()?.capitalize() ?: ""
        deckCoverPane.background = Background(BackgroundImage(Image(deckCoverStream), BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT))
    }

    fun trackCardDraw(card: Card) {
        Platform.runLater {
            with(deckCardsSlot) {
                find { it.card == card }?.apply {
                    indexOf(this).takeIf { it >= 0 }?.let {
                        set(it, this.apply {
                            currentQtd -= 1
                        })
                    }
                }
            }
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

    class CardSlotCell : ListCell<CardSlot>() {

        val cardSize by lazy {
            with(TESLTracker.referenceConfig) {
                ImageFuncs.getScreenScaledSize(DECK_TRACKER_CARD_WIDTH, DECK_TRACKER_CARD_HEIGHT)
            }
        }

        override fun updateItem(item: CardSlot?, empty: Boolean) {
            super.updateItem(item, empty)
            if (item != null) {
                graphic = StackPane().apply {
                    add(stackpane {
                        imageview {
                            val cardSetName = item.card.set.name.toLowerCase().capitalize()
                            val cardAttrName = item.card.attr.name.toLowerCase().capitalize()
                            val cardImagePath = "/Cards/$cardSetName/$cardAttrName/${item.card.shortName}.png"
                            var cardFullImage = ImageFuncs.getFileImage(File(TESLTracker::class.java.getResource("/Cards/card_back.png").toURI()))
                            try {
                                cardFullImage = ImageFuncs.getFileImage(File(TESLTracker::class.java.getResource(cardImagePath).toURI()))
                            } catch (e: Exception) {
                                Logger.e(e)
                            }
                            image = SwingFXUtils.toFXImage(cardFullImage?.getCardForSlotCrop(), null)
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
                            opacity = 0.8.takeIf { item.currentQtd > 0 } ?: 0.2
                        }
                        padding = Insets(0.0, 0.0, 0.0, cardSize.width * 0.2)
                        style = "-fx-background-color: linear-gradient(to right, ${item.card.attr.colorHex}, #000000AA); " +
                                "-fx-background-radius: 25.0;"
                    })
                    add(borderpane {
                        left = imageview {
                            val cardCost = item.card.cost
                            image = Image(TESLTracker::class.java.getResourceAsStream("/UI/deckMagika$cardCost.png"))
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
                    background = Background.EMPTY
                    maxWidth = cardSize.width.toDouble() + cardSize.height
                    style = "-fx-background-color: #000000AA; " +
                            "-fx-background-radius: 25.0;"
                }
            }
        }
    }

}