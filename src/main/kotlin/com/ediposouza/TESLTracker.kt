package com.ediposouza

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.handler.ScreenHandler
import com.ediposouza.handler.StateHandler
import com.ediposouza.ui.LoggerView
import com.ediposouza.util.*
import javafx.application.Platform
import javafx.geometry.Rectangle2D
import javafx.scene.control.Alert
import javafx.scene.image.Image
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.App
import tornadofx.FX
import tornadofx.Rest
import tornadofx.alert
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.URLDecoder
import java.util.concurrent.CompletableFuture
import javax.swing.SwingUtilities

/**
 * Created by ediposouza on 06/03/17.
 */
class TESLTracker : App(LoggerView::class) {

    companion object {

        var referenceConfig: ReferenceConfig = ReferenceConfig1366x768()
        val screenSize: Rectangle2D by lazy { Screen.getPrimary().visualBounds }

        val iconName = "/ic_legend.png".takeIf { com.sun.jna.Platform.isWindows() } ?: "/ic_legend_osx.png"
        val jarPath = URLDecoder.decode(TESLTracker::class.java.protectionDomain.codeSource.location.file, "UTF-8")
        val legendsIcon: Image by lazy { Image(iconName) }

        private var lastScreenshotDHash = ""

        fun redetectScreen() {
            lastScreenshotDHash = ""
        }
    }

    val APP_NAME = "TES Legends Tracker"
    val DELAY_WINDOW_DETECTION = 5_000L
    val DELAY_ELDER_SCROLL_SCREENSHOT = 2_000L
    val ELDER_SCROLL_LEGENDS_WINDOW_TITLE = "The Elder Scrolls: Legends"

    val firebaseDB: Rest by inject()
    val legendsIconStream: InputStream by lazy { TESLTracker::class.java.getResourceAsStream(iconName) }

    var waitingScreenshotChangeWasLogged = false

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
            y = screenSize.height - height
        })

        stage.close()
        configureSystemTrayIcon()
        CompletableFuture.runAsync {
            TESLTrackerData.updateCardDB(firebaseDB)
            startElderScrollDetection()
        }
    }

    private fun configureSystemTrayIcon() {
        if (!SystemTray.isSupported()) {
            Logger.d("Tray Icon not supported")
        }
        trayicon(legendsIconStream, APP_NAME, false, true) {
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
                        System.exit(0)
                    }
                })
            }
            SwingUtilities.invokeLater {
                displayMessage(APP_NAME, "$APP_NAME started.", TrayIcon.MessageType.NONE)
            }
        }
    }

    private fun startElderScrollDetection() {
        Logger.d("Using ${referenceConfig.SCREEN_REFERENCE} as reference")
        with(screenSize) {
            Logger.d("Image size: ${width.toInt()}x${height.toInt()}")
        }
        Logger.i("Waiting Elder scroll legends..")
        while (true) {
            if (isTESLegendsScreenActive()) {
                Logger.i("Elder scroll legends detected!")
                startElderScrollRecognition()
                Logger.i("Waiting Elder scroll legends..")
                fire(HideArenaTierEvent())
            }
            Thread.sleep(DELAY_WINDOW_DETECTION)
        }
    }

    private fun startElderScrollRecognition() {
        Logger.i("Start screenshot game screens..")
        while (true) {
            if (!analyseScreenshot(ScreenFuncs.takeScreenshot())) {
                ScreenHandler.lastScreenRecognized = ""
                break
            }
            Thread.sleep(DELAY_ELDER_SCROLL_SCREENSHOT)
        }
    }

    private fun analyseScreenshot(screenshot: BufferedImage?): Boolean {
        if (screenshot == null) {
            return false
        }
        val screenshotDHash = Recognizer.calcDHash(screenshot)
        if (Recognizer.isScreenshotDifferent(screenshotDHash, lastScreenshotDHash) &&
                StateHandler.currentTESLState?.hasValidState() ?: true) {
            lastScreenshotDHash = screenshotDHash
            waitingScreenshotChangeWasLogged = false
            if (!ScreenHandler.process(screenshot)) {
                return isTESLegendsScreenActive()
            }
        } else if (!waitingScreenshotChangeWasLogged) {
            Logger.d("Waiting screen change..")
            waitingScreenshotChangeWasLogged = true
        }
        return true
    }

    private fun isTESLegendsScreenActive() = ScreenFuncs.getActiveWindowTitle().contains(ELDER_SCROLL_LEGENDS_WINDOW_TITLE)

}