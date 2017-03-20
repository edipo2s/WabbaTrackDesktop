package com.ediposouza

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.ui.LoggerView
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import com.ediposouza.util.ScreenFuncs
import com.ediposouza.util.ScreenshotProcessor
import com.ediposouza.util.images.ImageFuncs
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.App
import tornadofx.FX
import tornadofx.Rest
import tornadofx.alert
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.image.BufferedImage
import java.util.concurrent.CompletableFuture

/**
 * Created by ediposouza on 06/03/17.
 */
class Main : App(LoggerView::class) {

    val TRAY_TIP = "TES Legends Tracker"
    val DELAY_WINDOW_DETECTION = 5_000L
    val DELAY_ELDER_SCROLL_SCREENSHOT = 2_000L
    val ELDER_SCROLL_LEGENDS_WINDOW_TITLE = "The Elder Scrolls: Legends"

    val firebaseDB: Rest by inject()
    val legendsIcon by lazy { javaClass.getResourceAsStream("/ic_legend.png") }

    var lastScreenshotDHash = ""
    var lastScreenshotDHashLogged = false

    init {
        firebaseDB.baseURI = "https://tes-legends-assistant.firebaseio.com"
    }

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
            TESLTrackerData.updateCardDB(firebaseDB)
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
        Logger.i("Waiting Elder scroll legends..")
        while (true) {
            if (ScreenFuncs.getActiveWindowTitle() == ELDER_SCROLL_LEGENDS_WINDOW_TITLE) {
                Logger.i("Elder scroll legends detected!")
                startElderScrollRecognition()
            }
            Thread.sleep(DELAY_WINDOW_DETECTION)
        }
    }

    private fun startElderScrollRecognition() {
        Logger.d("Using ${ImageFuncs.referenceConfig.SCREEN_REFERENCE} as reference")
        with(Screen.getPrimary().visualBounds) {
            Logger.d("Image size: ${width.toInt()}x${height.toInt()}")
        }
        Logger.i("Start screenshot game screens..")
        while (true) {
            analyseScreenshot(ScreenFuncs.takeScreenshot())
            Thread.sleep(DELAY_ELDER_SCROLL_SCREENSHOT)
        }
    }

    private fun analyseScreenshot(screenshot: BufferedImage?) {
        if (screenshot == null) {
            return
        }
        val screenshotDHash = Recognizer.calcDHash(screenshot)
        if (Recognizer.isScreenshotDifferent(screenshotDHash, lastScreenshotDHash)) {
            lastScreenshotDHash = screenshotDHash
            lastScreenshotDHashLogged = false
            ScreenshotProcessor.process(screenshot)
        } else if (!lastScreenshotDHashLogged) {
            Logger.d("Waiting screen change..")
            lastScreenshotDHashLogged = true
        }
    }

}