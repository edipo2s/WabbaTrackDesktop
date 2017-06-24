package com.ediposouza.ui

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.add
import tornadofx.stackpane
import java.awt.GraphicsDevice
import java.awt.Window
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * Created by Edipo on 20/03/2017.
 */
class MonitorNumberWidget(val monitorNumber: Int, graphicsDevice: GraphicsDevice) : JFrame() {

    init {
        type = Window.Type.UTILITY
        isUndecorated = true
        isAlwaysOnTop = true
        background = java.awt.Color(0, 0, 0, 0)

        contentPane.add(JFXPanel().apply {
            Platform.runLater {
                scene = createFxScene()
                SwingUtilities.invokeLater {
                    pack()
                    val x = (graphicsDevice.displayMode.width / 2.0) - (width / 2.0) + graphicsDevice.defaultConfiguration.bounds.x
                    val y = (graphicsDevice.displayMode.height / 2.0) - (height / 2.0) + graphicsDevice.defaultConfiguration.bounds.y
                    this@MonitorNumberWidget.setLocation(x.toInt(), y.toInt())
                    isVisible = true
                }
            }
        })

    }

    private fun createFxScene(): Scene {
        val layout = VBox().apply {
            add(stackpane {
                add(Label("$monitorNumber").apply {
                    font = Font.font(96.0)
                    textFill = Color.BLUE
                })
            })
            alignment = Pos.TOP_CENTER
            background = Background.EMPTY
        }
        return Scene(layout).apply {
            fill = Color.TRANSPARENT
        }
    }

}