package com.ediposouza.ui

import com.ediposouza.TESLTracker
import com.ediposouza.extensions.toFXImage
import com.ediposouza.model.Card
import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import javafx.scene.layout.Background
import javafx.scene.layout.StackPane
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
class CardWidget(val card: Card, posX: Int, posY: Int) : JFrame() {

    private lateinit var cardSize: Dimension

    private val contextMenu = ContextMenu(MenuItem("Hide").apply {
        setOnAction {
            this@CardWidget.isVisible = false
        }
    })

    init {
        type = Window.Type.UTILITY
        isUndecorated = true
        isAlwaysOnTop = true
        background = java.awt.Color(0, 0, 0, 0)

        with(TESLTracker.referenceConfig) {
            cardSize = ImageFuncs.getScreenScaledSize(ARENA_PICK_SELECT_WIDTH, ARENA_PICK_SELECT_HEIGHT)
        }

        val cardMargin = 20
        val maxY = TESLTracker.screenSize.height - cardSize.height - cardMargin
        setLocation(posX - cardSize.width - cardMargin, posY.takeIf { it < maxY } ?: maxY.toInt())

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
        val layout = StackPane().apply {
            add(imageview {
                val cardSetName = card.set.name.toLowerCase().capitalize()
                val cardAttrName = card.attr.name.toLowerCase().capitalize()
                val cardImagePath = "/CardsWebp/$cardSetName/$cardAttrName/${card.shortName}.webp"
                var cardFullImage = TESLTracker::class.java.getResourceAsStream("/CardsWebp/card_back.webp")
                try {
                    cardFullImage = TESLTracker::class.java.getResourceAsStream(cardImagePath)
                } catch (e: Exception) {
                    Logger.e(e)
                }
                image = cardFullImage?.toFXImage()
                fitHeight = cardSize.height.toDouble()
                fitWidth = cardSize.width.toDouble()
            })
            background = Background.EMPTY
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

}