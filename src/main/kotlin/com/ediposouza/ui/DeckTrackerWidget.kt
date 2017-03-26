package com.ediposouza.ui

import com.ediposouza.TESLTracker
import com.ediposouza.extensions.getCardForSlotCrop
import com.ediposouza.extensions.makeDraggable
import com.ediposouza.model.CardSlot
import com.ediposouza.util.ImageFuncs
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.JFXPanel
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ListCell
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Background
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*
import java.awt.Dimension
import java.awt.Window
import java.io.File
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * Created by Edipo on 18/03/2017.
 */
class DeckTrackerWidget : JFrame() {

    private val deckCardsSlot: ObservableList<CardSlot> = FXCollections.observableArrayList<CardSlot>()
    private lateinit var deckTrackerSize: Dimension
    private lateinit var deckTitle: Label

    init {
        type = Window.Type.UTILITY
        isUndecorated = true
        isAlwaysOnTop = true
        background = java.awt.Color(0, 0, 0, 0)
        with(TESLTracker.referenceConfig) {
            val deckTrackerPos = ImageFuncs.getScreenScaledPosition(DECK_TRACKER_X, DECK_TRACKER_Y)
            setLocation(deckTrackerPos.x, deckTrackerPos.y)
            deckTrackerSize = ImageFuncs.getScreenScaledSize(DECK_TRACKER_WIDTH, DECK_TRACKER_HEIGHT)
            size = deckTrackerSize
        }

        val fxPanel = JFXPanel()
        contentPane.add(fxPanel)

        Platform.runLater {
            fxPanel.scene = createFxScene()
            SwingUtilities.invokeLater {
                pack()
                isVisible = true
            }
        }

    }

    private fun createFxScene(): Scene {
        with(TESLTracker.referenceConfig) {
            val cellSize = ImageFuncs.getScreenScaledSize(DECK_TRACKER_CARD_WIDTH, DECK_TRACKER_CARD_HEIGHT)
            val layout = VBox().apply {
                deckTitle = label {
                    alignment = Pos.TOP_CENTER
                    textFill = Color.WHITE
                    effect = DropShadow(5.0, Color.BLACK)
                    text = "Arena Deck"
                    style = "-fx-background-color: #000000AA; " +
                            "-fx-background-radius: 25.0;"
                    prefWidth = cellSize.width.toDouble() + cellSize.height * 1.5
                    makeDraggable(this@DeckTrackerWidget)
                }
                add(deckTitle)
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
                    addEventFilter(MouseEvent.MOUSE_PRESSED) { event -> event?.consume() }
                    makeDraggable(this@DeckTrackerWidget)
                })
                background = Background.EMPTY
            }

            return Scene(layout).apply {
                fill = Color.TRANSPARENT
                stylesheets.add(TESLTracker::class.java.getResource("/UI/deckTrackerWidget.css").toExternalForm())
            }
        }
    }

    fun setDeckCardsSlot(deckCardsSlot: List<CardSlot>) {
        isVisible = true
        this.deckCardsSlot.apply {
            clear()
            addAll(deckCardsSlot)
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
                    add(imageview {
                        val cardSetName = item.card.set.name.toLowerCase().capitalize()
                        val cardAttrName = item.card.attr.name.toLowerCase().capitalize()
                        val cardImagePath = "/Cards/$cardSetName/$cardAttrName/${item.card.shortName}.png"
                        val cardFullImage = ImageFuncs.getFileImage(File(TESLTracker::class.java.getResource(cardImagePath).toURI()))
                        image = SwingFXUtils.toFXImage(cardFullImage?.getCardForSlotCrop(), null)
                        fitHeight = cardSize.height.toDouble()
                        fitWidth = cardSize.width.toDouble()
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
                            textFill = Color.WHITE
                            effect = DropShadow(5.0, Color.BLACK)
                            padding = Insets(0.0, 0.0, 0.0, 3.0)
                            minWidth = cardSize.width * 0.9
                            maxWidth = cardSize.width * 0.9
                        }
                        right = label {
                            alignment = Pos.CENTER
                            textFill = Color.WHITE
                            effect = DropShadow(5.0, Color.BLACK)
                            val cardQtd = item.qtd
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