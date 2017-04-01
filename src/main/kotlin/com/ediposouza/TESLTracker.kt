package com.ediposouza

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.handler.ScreenHandler
import com.ediposouza.handler.StateHandler
import com.ediposouza.model.Card
import com.ediposouza.model.CardPick
import com.ediposouza.model.CardSlot
import com.ediposouza.state.ArenaState
import com.ediposouza.state.GameState
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
import tornadofx.alert
import java.awt.*
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

        val SHOW_TEST_MENU = true

        var referenceConfig: ReferenceConfig = ReferenceConfig1366x768()
        val screenSize: Rectangle2D by lazy { Screen.getPrimary().visualBounds }

        val iconName = "/ic_legend.png".takeIf { com.sun.jna.Platform.isWindows() } ?: "/ic_legend_osx.png"
        val jarPath = URLDecoder.decode(TESLTracker::class.java.protectionDomain.codeSource.location.file, "UTF-8")
        val legendsIcon: Image by lazy { Image(iconName) }

        private var lastScreenshotDHash = ""

        fun redetectScreen() {
            lastScreenshotDHash = ""
            ScreenHandler.lastScreenRecognized = ""
        }
    }

    val APP_NAME = "TES Legends Tracker"
    val DELAY_WINDOW_DETECTION = 5_000L
    val ELDER_SCROLL_SPS = 1    //Screenshot Per Second
    val ELDER_SCROLL_LEGENDS_WINDOW_TITLE = "The Elder Scrolls: Legends"

    val legendsIconStream: InputStream by lazy { TESLTracker::class.java.getResourceAsStream(iconName) }

    var waitingScreenshotChangeWasLogged = false

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
        TESLTrackerData.cards
        CompletableFuture.runAsync {
            startElderScrollDetection()
        }
    }

    private fun configureSystemTrayIcon() {
        if (!SystemTray.isSupported()) {
            Logger.d("Tray Icon not supported")
        }
        trayicon(legendsIconStream, APP_NAME, false, true) {
            popupMenu = PopupMenu().apply {
                add(MenuItem("Login").apply {
                    addActionListener {

                    }
                })
                add(MenuItem("Show Log").apply {
                    addActionListener {
                        Platform.runLater {
                            FX.primaryStage.show()
                        }
                    }
                })
                add(MenuItem("Show Deck Tracker").apply {
                    addActionListener {
                        Platform.runLater {
                            GameState.deckTracker.isVisible = true
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
                if (SHOW_TEST_MENU) {
                    add(Menu("Test").apply {
                        add(MenuItem("Show Arena Tier Test").apply {
                            addActionListener {
                                Platform.runLater {
                                    ArenaState.setTierPicks(Triple(CardPick(Card.DUMMY, 20, listOf()),
                                            CardPick(Card.DUMMY, 20, listOf()), CardPick(Card.DUMMY, 20, listOf())))
                                }
                            }
                        })
                        add(MenuItem("Show Arena Tier with Synergy Test").apply {
                            addActionListener {
                                Platform.runLater {
                                    ArenaState.setTierPicks(Triple(CardPick(Card.DUMMY, 20, listOf(Card.DUMMY, Card.DUMMY)),
                                            CardPick(Card.DUMMY, 20, listOf(Card.DUMMY)), CardPick(Card.DUMMY, 20, listOf(Card.DUMMY))))
                                }
                            }
                        })
                        add(MenuItem("Show Deck Test").apply {
                            addActionListener {
                                Platform.runLater {
                                    val cardsSlot = ArenaState.picks.groupBy(Card::shortName)
                                            .map { CardSlot(it.value.first(), it.value.size) }
                                    StateHandler.currentTESLState = GameState.apply {
                                        setDeckCardsSlot(cardsSlot)
                                    }
                                }
                            }
                        })
                        add(MenuItem("Draw Test").apply {
                            addActionListener {
                                Platform.runLater {
                                    GameState.deckTracker.trackCardDraw(TESLTrackerData.getCard("baronoftear")!!)
                                }
                            }
                        })
                    })
                }
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
                StateHandler.currentTESLState?.onResume()
                startElderScrollRecognition()
                Logger.i("Waiting Elder scroll legends..")
                StateHandler.currentTESLState?.onPause()
            }
            Thread.sleep(DELAY_WINDOW_DETECTION)
        }
    }

    private fun startElderScrollRecognition() {
        TESLTrackerData.updateCardDB()
        Logger.i("Start screenshot game screens..")
        while (true) {
            if (!analyseScreenshot(ScreenFuncs.takeScreenshot())) {
                ScreenHandler.lastScreenRecognized = ""
                break
            }
            Thread.sleep(1000L / ELDER_SCROLL_SPS)
        }
    }

    private fun analyseScreenshot(screenshot: BufferedImage?): Boolean {
        if (screenshot == null) {
            return false
        }
        val screenshotDHash = Recognizer.calcPHash(screenshot)
        if (Recognizer.isScreenshotDifferent(screenshotDHash, lastScreenshotDHash) ||
                !(StateHandler.currentTESLState?.hasValidState() ?: false)) {
            lastScreenshotDHash = screenshotDHash
            waitingScreenshotChangeWasLogged = false
            if (!ScreenHandler.process(screenshot)) {
                return isTESLegendsScreenActive() || isTESLegendsTrackerWindow() || isTESLegendsTrackerPopupWindow()
            }
        } else if (!waitingScreenshotChangeWasLogged) {
            Logger.d("Waiting screen change..")
            waitingScreenshotChangeWasLogged = true
        }
        return true
    }

    private fun isTESLegendsScreenActive() = ScreenFuncs.getActiveWindowTitle().contains(ELDER_SCROLL_LEGENDS_WINDOW_TITLE)

    private fun isTESLegendsTrackerWindow() = ScreenFuncs.getActiveWindowTitle().contains(APP_NAME)

    private fun isTESLegendsTrackerPopupWindow() = ScreenFuncs.getActiveWindowTitle().isEmpty()

}