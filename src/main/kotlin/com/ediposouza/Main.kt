package com.ediposouza

import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognition
import com.sun.jna.Native
import com.sun.jna.PointerType
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.win32.StdCallLibrary
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.stage.Stage
import javafx.stage.StageStyle
import tornadofx.App
import tornadofx.alert
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.script.ScriptEngineManager
import javax.script.ScriptException

/**
 * Created by ediposouza on 06/03/17.
 */
class Main : App(MainView::class) {

    val TRAY_TIP = "TES Legends Tracker"
    val TRAY_ICON = "ic_legend.png"
    val WINDOW_DETECTION_DELAY = 5_000L
    val ELDER_SCROLL_SCREENSHOT_DELAY = 1_000L
    val ELDER_SCROLL_LEGENDS_WINDOW_TITLE = "The Elder Scrolls: Legends"

    var lastScreenshotDHash = ""

    override fun start(stage: Stage) {
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.isIconified = true
        super.start(stage)

        stage.close()
        configureSystemTrayIcon()
//        startElderScrollDetection()
    }

    private fun configureSystemTrayIcon() {
        trayicon(javaClass.getResourceAsStream("/$TRAY_ICON"), TRAY_TIP, false, true) {
            popupMenu = PopupMenu().apply {
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
            Logger.d("Waiting elder scroll legends window..")
            if (getActiveWindowTitle() == ELDER_SCROLL_LEGENDS_WINDOW_TITLE) {
                Logger.d("Elder scroll legends window detected!")
                startElderScrollRecognition()
            }
            Thread.sleep(WINDOW_DETECTION_DELAY)
        }
    }

    private fun startElderScrollRecognition() {
        while (true) {
            Logger.d("Waiting arena window..")
            ImageFuncs.takeScreenshot()?.apply {
                val screenshotDHash = Recognition.calcDHash(this)
                if (screenshotDHash == lastScreenshotDHash) {
                    Logger.d("Waiting..")
                } else {
                    lastScreenshotDHash = screenshotDHash
                    recognizeArenaPick(this)
                }
            }
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
        return Recognition.recognizeCardDHash(Recognition.calcDHash(cardImage))
    }

    interface User32 : StdCallLibrary {
        fun GetForegroundWindow(): WinDef.HWND   // add this
        fun GetWindowTextA(hWnd: PointerType, lpString: ByteArray, nMaxCount: Int): Int

        companion object {
            val INSTANCE = Native.loadLibrary("user32", User32::class.java)
        }
    }

    fun getActiveWindowTitle(): String {
        var titleStr = ""
        if (com.sun.jna.Platform.isWindows()) {
            val windowText = ByteArray(512)
            val hwnd = User32.INSTANCE.GetForegroundWindow() // then you can call it!
            User32.INSTANCE.GetWindowTextA(hwnd, windowText, 512)
            titleStr = Native.toString(windowText)
        } else if (com.sun.jna.Platform.isMac()) {
            val script = "tell application \"System Events\"\n" +
                    "\tname of application processes whose frontmost is tru\n" +
                    "end"
            val appleScript = ScriptEngineManager().getEngineByName("AppleScript")
            try {
                titleStr = appleScript.eval(script) as String
            } catch (e: ScriptException) {
                e.printStackTrace()
            }

        } else {
            titleStr = "Platform is not Support"
        }
        return titleStr
    }

}