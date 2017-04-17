package com.ediposouza.ui

import com.ediposouza.TESLTracker
import com.ediposouza.model.Card
import com.ediposouza.model.CardArenaTier
import com.ediposouza.model.CardPick
import com.ediposouza.model.CardSlot
import com.ediposouza.state.ArenaState
import com.ediposouza.util.ImageFuncs
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*
import java.awt.Dimension
import java.awt.Window
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * Created by Edipo on 20/03/2017.
 */
class ArenaTierWidget(val pickNumber: Int) : JFrame() {

    private lateinit var cardSize: Dimension
    private lateinit var tierValueSize: Dimension

    private val nameValueLabel by lazy {
        Label("").apply {
            alignment = Pos.CENTER
            font = Font.font(16.0)
            padding = Insets(0.0, 2.0, 2.0, 2.0)
            textFill = Color.WHITE
        }
    }

    private val synergyValueLabel by lazy {
        Label("").apply {
            alignment = Pos.TOP_LEFT
            font = Font.font(1.0)
            padding = Insets(0.0, 3.0, 2.0, 3.0)
            textFill = Color.WHITE
        }
    }

    private val tierValueLabel by lazy {
        Label("0").apply {
            font = Font.font(42.0)
            padding = Insets(0.0, 0.0, 5.0, 4.0)
        }
    }

    private val contextMenu = ContextMenu(MenuItem("Detect Again").apply {
        setOnAction {
            ArenaState.lastPickNumberRecognized = null
        }
    })

    init {
        type = Window.Type.UTILITY
        isUndecorated = true
        isAlwaysOnTop = true
        background = java.awt.Color(0, 0, 0, 0)

        with(TESLTracker.referenceConfig) {
            val tierValueFirstPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_NUMBER_FIRST_X, ARENA_PICK_NUMBER_Y)
            val tierValueSecondPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_NUMBER_SECOND_X, ARENA_PICK_NUMBER_Y)
            val tierValueThirdPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_NUMBER_THIRD_X, ARENA_PICK_NUMBER_Y)
            tierValueSize = ImageFuncs.getScreenScaledSize(ARENA_PICK_NUMBER_WIDTH, ARENA_PICK_NUMBER_HEIGHT)
            setLocation(when (pickNumber) {
                1 -> tierValueFirstPos.x
                2 -> tierValueSecondPos.x
                else -> tierValueThirdPos.x
            }, tierValueFirstPos.y)

            cardSize = ImageFuncs.getScreenScaledSize(ARENA_PICK_SELECT_WIDTH, ARENA_PICK_SELECT_HEIGHT)
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
        val layout = VBox().apply {
            add(stackpane {
                add(pane { minWidth = cardSize.width.toDouble() })
                add(imageview {
                    image = Image(TESLTracker::class.java.getResourceAsStream("/UI/arenaTierWidgetLayout.png"),
                            tierValueSize.width.toDouble(), tierValueSize.height.toDouble(), true, true)
                })
                add(tierValueLabel)
                alignment = Pos.CENTER
            })
            add(vbox {
                add(nameValueLabel)
                add(synergyValueLabel)
                alignment = Pos.CENTER
                VBox.setMargin(this, Insets(2.0, 0.0, 0.0, 0.0))
                style = "-fx-background-color: #000000AA; " +
                        "-fx-background-radius: 5.0;"
                maxWidth = Region.USE_PREF_SIZE
                minHeight = Region.USE_PREF_SIZE
            })
            alignment = Pos.TOP_CENTER
            background = Background.EMPTY
            prefHeight = TESLTracker.screenSize.height / 2
        }
        return Scene(layout).apply {
            fill = Color.TRANSPARENT
            stylesheets.add(TESLTracker::class.java.getResource("/UI/arenaTierWidget.css").toExternalForm())
            setOnMousePressed { me ->
                if (me.isSecondaryButtonDown) {
                    contextMenu.show(root, me.screenX, me.screenY)
                }
            }
        }
    }

    fun setPickValue(arenaTier: CardPick) {
        nameValueLabel.text = arenaTier.card.name
        with(synergyValueLabel) {
            style = "-fx-font-size: ${1.takeIf { arenaTier.synergy.isEmpty() } ?: 12};"
            text = "Synergy:" + arenaTier.synergy.groupBy(Card::shortName)
                    .map { CardSlot(it.value.first(), it.value.size) }
                    .map { (card, qtd) -> "\n ${card.name}" + ("".takeIf { qtd == 1 } ?: " x$qtd") }
                    .sorted()
                    .joinToString { it }
        }
        with(tierValueLabel) {
            text = "${arenaTier.value}"
            textFill = Color.web(when (arenaTier.value) {
                in 0..CardArenaTier.AVERAGE.value.minus(1) -> "#F44336"
                in CardArenaTier.AVERAGE.value..CardArenaTier.EXCELLENT.value.minus(1) -> "#BDBDBD"
                else -> "#4DB6AC"
            })
        }
    }

}