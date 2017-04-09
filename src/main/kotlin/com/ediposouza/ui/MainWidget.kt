package com.ediposouza.ui

import com.ediposouza.TESLTracker
import com.ediposouza.extensions.makeDraggable
import com.ediposouza.util.ImageFuncs
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.ContextMenu
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.add
import tornadofx.imageview
import java.awt.Dimension
import java.awt.Window
import javax.swing.JFrame
import javax.swing.SwingUtilities

/**
 * Created by Edipo on 20/03/2017.
 */
class MainWidget : JFrame() {

    companion object {

        val contextMenu = ContextMenu()

    }

    private lateinit var mainSize: Dimension

    init {
        type = Window.Type.UTILITY
        isUndecorated = true
        isAlwaysOnTop = true
        background = java.awt.Color(0, 0, 0, 0)

        with(TESLTracker.referenceConfig) {
            val mainPos = ImageFuncs.getScreenScaledPosition(APP_MAIN_X, APP_MAIN_Y)
            mainSize = ImageFuncs.getScreenScaledSize(APP_MAIN_WIDTH, APP_MAIN_HEIGHT)
            setLocation(mainPos.x, mainPos.y)
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
            add(imageview {
                image = Image(TESLTracker::class.java.getResourceAsStream("/UI/main.png"),
                        mainSize.width.toDouble(), mainSize.height.toDouble(), true, true)
            })
            makeDraggable(this@MainWidget)
            alignment = Pos.TOP_CENTER
            background = Background.EMPTY
            prefHeight = TESLTracker.screenSize.height / 2
        }
        return Scene(layout).apply {
            fill = Color.TRANSPARENT
            setOnMousePressed { me ->
                if (me.isSecondaryButtonDown) {
                    contextMenu.show(root, me.screenX, me.screenY)
                }
            }
        }
    }

}