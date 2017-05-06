package com.ediposouza.ui

import com.ediposouza.TESLTracker
import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.control.ContextMenu
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.VBox
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import tornadofx.View
import tornadofx.add
import tornadofx.imageview

/**
 * Created by Edipo on 20/03/2017.
 */
class MainWidget : View(TESLTracker.APP_NAME) {

    companion object {

        val contextMenu = ContextMenu()

    }

    private var animationRunning = false
    private var xOffset = 0.0
    private var yOffset = 0.0

    override val root = VBox().apply {
        with(TESLTracker.referenceConfig) {
            val mainSize = ImageFuncs.getScreenScaledSize(APP_MAIN_WIDTH, APP_MAIN_HEIGHT)
            add(imageview {
                image = Image(TESLTracker::class.java.getResourceAsStream("/UI/main.png"),
                        mainSize.width.toDouble(), mainSize.height.toDouble(), true, true)
            })
        }
        alignment = Pos.TOP_CENTER
        background = Background.EMPTY
        prefHeight = TESLTracker.screenSize.height / 2.0
        setOnMousePressed { me ->
            xOffset = me.sceneX
            yOffset = me.sceneY
            if (me.isSecondaryButtonDown) {
                contextMenu.show(primaryStage, me.screenX, me.screenY)
            }
        }
        setOnMouseDragged { me ->
            primaryStage.x = me.screenX - xOffset
            primaryStage.y = me.screenY - yOffset
        }
    }

    var isVisible: Boolean
        get() = primaryStage.isShowing
        set(value) {
            Platform.runLater {
                if (value) {
                    primaryStage.show()
                    startShowAnimation()
                } else {
                    primaryStage.close()
                }
            }
        }

    init {
        Platform.runLater {
            startShowAnimation()
            primaryStage.icons += TESLTracker.legendsIcon
        }
    }

    private fun startShowAnimation() {
        if (animationRunning) {
            return
        }
        animationRunning = true
        Logger.d("start Floating icon animation")
        with(primaryStage.scene.window) {
            val initialXLocation = x
            val animXLocation = x - 50
            launch(JavaFx) {
                while (x > animXLocation) {
                    x -= 2
                    delay(15)
                }
                delay(200)
                while (x < initialXLocation) {
                    x += 2
                    delay(15)
                }
                animationRunning = false
            }
        }
    }

}