package com.ediposouza.ui

import com.ediposouza.TESLTracker
import com.ediposouza.model.Card
import com.ediposouza.model.CardArenaTier
import com.ediposouza.util.images.ImageFuncs
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.add
import tornadofx.imageview
import tornadofx.stackpane
import tornadofx.vbox
import java.awt.Window
import javax.swing.JFrame
import javax.swing.SwingUtilities


/**
 * Created by Edipo on 20/03/2017.
 */
class ArenaTierWidget(val pickNumber: Int) : JFrame() {

    var tierValueSize = Pair(68, 70)

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
            alignment = Pos.CENTER
            font = Font.font(12.0)
            padding = Insets(0.0, 2.0, 2.0, 2.0)
            textFill = Color.WHITE
        }
    }

    private val tierValueLabel by lazy {
        Label("0").apply {
            font = Font.font(42.0)
            padding = Insets(0.0, 0.0, 5.0, 4.0)
        }
    }

    init {
        type = Window.Type.UTILITY
        isUndecorated = true
        isAlwaysOnTop = true
        background = java.awt.Color(0, 0, 0, 0)
        with(TESLTracker.referenceConfig) {
            val tierValueFirstPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_VALUE_FIRST_X, ARENA_PICK_VALUE_Y)
            val tierValueSecondPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_VALUE_SECOND_X, ARENA_PICK_VALUE_Y)
            val tierValueThirdPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_VALUE_THIRD_X, ARENA_PICK_VALUE_Y)
            tierValueSize = ImageFuncs.getScreenScaledSize(ARENA_PICK_VALUE_WIDTH, ARENA_PICK_VALUE_HEIGHT)
            setLocation(when (pickNumber) {
                1 -> tierValueFirstPos.first
                2 -> tierValueSecondPos.first
                else -> tierValueThirdPos.first
            }, tierValueFirstPos.second)
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
        val layout = VBox().apply {
            add(stackpane {
                add(imageview {
                    image = Image(TESLTracker::class.java.getResourceAsStream("/UI/arenaTierWidgetLayout.png"),
                            tierValueSize.second.toDouble(), tierValueSize.second.toDouble(), true, true)
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
            })
            background = Background.EMPTY
        }
        return Scene(layout).apply {
            fill = Color.TRANSPARENT
            stylesheets.add(TESLTracker::class.java.getResource("/UI/arenaTierWidget.css").toExternalForm())
        }
    }

    fun setPickValue(arenaTier: Triple<String, Int, List<Card>>) {
        isVisible = true
        nameValueLabel.text = arenaTier.first
        with(synergyValueLabel) {
            if (arenaTier.third.isEmpty()) {
                font = Font.font(1.0)
            }
            text = " Synergy with: " + arenaTier.third.map { "\n  ${it.name}" }.joinToString { it }
        }
        with(tierValueLabel) {
            text = "${arenaTier.second}"
            textFill = Color.web(when (arenaTier.second) {
                in 0..CardArenaTier.AVERAGE.value.minus(1) -> "#F44336"
                in CardArenaTier.AVERAGE.value..CardArenaTier.EXCELLENT.value.minus(1) -> "#BDBDBD"
                else -> "#4DB6AC"
            })
        }
    }

}