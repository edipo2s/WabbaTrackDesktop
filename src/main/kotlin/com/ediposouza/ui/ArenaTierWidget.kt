package com.ediposouza.ui

import com.ediposouza.App
import com.ediposouza.model.Card
import com.ediposouza.model.CardArenaTier
import com.ediposouza.util.ScreenshotProcessor
import com.ediposouza.util.images.ImageFuncs
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BorderPane
import java.awt.Color
import java.awt.Window
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * Created by Edipo on 20/03/2017.
 */
class ArenaTierWidget(val pickNumber: Int) : JFrame() {

    private val tierValueLabel by lazy { Label("0") }

    init {
        type = Window.Type.UTILITY
        isUndecorated = true
        isAlwaysOnTop = true
        background = Color(0, 0, 0, 0)
        with(App.referenceConfig) {
            val tierValueFirstPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_VALUE_FIRST_X, ARENA_PICK_VALUE_Y)
            val tierValueSecondPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_VALUE_SECOND_X, ARENA_PICK_VALUE_Y)
            val tierValueThirdPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_VALUE_THIRD_X, ARENA_PICK_VALUE_Y)
            val tierValueSize = ImageFuncs.getScreenScaledSize(ARENA_PICK_VALUE_WIDTH, ARENA_PICK_VALUE_HEIGHT)
            setSize(tierValueSize.first, tierValueSize.second)
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
                isVisible = true
            }
        }

    }

    private fun createFxScene(): Scene {
        val layout = BorderPane().apply {
            center = tierValueLabel
            background = Background.EMPTY
        }
        return Scene(layout).apply {
            fill = javafx.scene.paint.Color.TRANSPARENT
            stylesheets.add(ScreenshotProcessor.javaClass.getResource("/fontstyle.css").toExternalForm())
        }
    }

    fun setPickValue(arenaTier: Pair<Int, List<Card>>) {
        with(tierValueLabel) {
            text = "${arenaTier.first}".plus("*".takeIf { arenaTier.second.isNotEmpty() } ?: "")
            textFill = javafx.scene.paint.Color.web(when (arenaTier.first) {
                in 0..CardArenaTier.AVERAGE.value.minus(1) -> "#F44336"
                in CardArenaTier.AVERAGE.value..CardArenaTier.EXCELLENT.value.minus(1) -> "#BDBDBD"
                else -> "#4DB6AC"
            })
        }
    }

}