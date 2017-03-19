package com.ediposouza

import com.ediposouza.ui.LoggerView
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import com.ediposouza.util.ScreenFuncs
import com.ediposouza.util.images.ImageFuncs
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.App
import tornadofx.FX
import tornadofx.alert
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.CompletableFuture
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
class Main : App(LoggerView::class) {

    val TRAY_TIP = "TES Legends Tracker"
    val TRAY_ICON = "ic_legend.png"
    val DELAY_WINDOW_DETECTION = 5_000L
    val DELAY_ELDER_SCROLL_SCREENSHOT = 1_000L
    val ELDER_SCROLL_LEGENDS_WINDOW_TITLE = "The Elder Scrolls: Legends"

    val legendsIcon by lazy { javaClass.getResourceAsStream("/$TRAY_ICON") }

    var lastScreenshotDHash = ""

    override fun start(stage: Stage) {
        super.start(stage.apply {
            initStyle(StageStyle.UTILITY)
            isAlwaysOnTop = true
            height = 300.0
            width = 200.0
            x = 0.0
            y = Screen.getPrimary().visualBounds.height - height
        })

        stage.close()
        configureSystemTrayIcon()
        CompletableFuture.runAsync {
            startElderScrollDetection()
        }
    }

    private fun configureSystemTrayIcon() {
        trayicon(legendsIcon, TRAY_TIP, false, true) {
            popupMenu = PopupMenu().apply {
                add(MenuItem("Show Log").apply {
                    addActionListener {
                        Platform.runLater {
                            FX.primaryStage.show()
                        }
                    }
                })
                add(MenuItem("About").apply {
                    addActionListener {
                        Platform.runLater {
                            alert(Alert.AlertType.INFORMATION, "About", "TES Legends Tracker \nby Edipo2s")
                        }
                    }
                })
                add(MenuItem("Exit").apply {
                    addActionListener {
                        Platform.exit()
                    }
                })
            }
        }
    }

    private fun startElderScrollDetection() {
        while (true) {
            Logger.d("Waiting elder scroll legends..")
            if (ScreenFuncs.getActiveWindowTitle() == ELDER_SCROLL_LEGENDS_WINDOW_TITLE) {
                Logger.d("Elder scroll legends detected!")
                startElderScrollRecognition()
            }
            Thread.sleep(DELAY_WINDOW_DETECTION)
        }
    }

    private fun startElderScrollRecognition() {
        while (true) {
            Logger.d("Waiting arena window..")
//            ScreenFuncs.takeScreenshot()?.apply {
//                val screenshotDHash = Recognizer.calcDHash(this)
//                if (screenshotDHash == lastScreenshotDHash) {
//                    Logger.d("Waiting..")
//                } else {
//                    lastScreenshotDHash = screenshotDHash
//                    recognizeArenaPick(this)
//                }
//            }
            Thread.sleep(DELAY_ELDER_SCROLL_SCREENSHOT)
        }
    }

    private fun recognizeArenaPick(image: BufferedImage) {
        Logger.d("Using ${ImageFuncs.referenceConfig.SCREEN_REFERENCE} as screen reference")
        Logger.d("Image size: ${image.width}x${image.height}")
        recognizeCard(ImageFuncs.getArenaCardCropped(image, 1))
        recognizeCard(ImageFuncs.getArenaCardCropped(image, 2))
        recognizeCard(ImageFuncs.getArenaCardCropped(image, 3))
    }

    private fun recognizeCard(cardImage: BufferedImage, outputFile: Boolean = false): String {
        if (outputFile) {
            val tmpFileName = "recognize_${System.currentTimeMillis()}.png"
            File("src/main/resources/Test/Tmp").apply {
                if (!exists()) {
                    mkdir()
                }
            }
            ImageIO.write(cardImage, "png", File("src/main/resources/Test/Tmp/$tmpFileName"))
        }
        return Recognizer.recognizeCardDHash(Recognizer.calcDHash(cardImage))
    }

}