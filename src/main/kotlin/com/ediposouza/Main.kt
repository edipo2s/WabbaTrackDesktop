package com.ediposouza

import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognition
import com.sun.jna.Native
import com.sun.jna.Platform
import com.sun.jna.PointerType
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.win32.StdCallLibrary
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import javax.swing.JOptionPane

/**
 * Created by ediposouza on 06/03/17.
 */
object Main {

    val TRAY_TIP = "TES Legends Tracker"
    val TARY_ICON = "ic_legend.png"
    val WINDOW_DETECTION_DELAY = 5_000L
    val ELDER_SCROLL_SCREENSHOT_DELAY = 1_000L
    val ELDER_SCROLL_LEGENDS_WINDOW_TITLE = "The Elder Scrolls: Legends"

    var lastScreenshotDHash = ""

    @JvmStatic fun main(args: Array<String>) {
        createSystemTrayIcon()
//        startElderScrollDetection()
    }

    private fun createSystemTrayIcon() {
        if (SystemTray.isSupported()) {
            val systemTray = SystemTray.getSystemTray()
            with(TrayIcon(ImageIO.read(javaClass.getResource("/$TARY_ICON")), TRAY_TIP)) {
                PopupMenu().apply {
                    add(MenuItem("About").apply {
                        addActionListener { showAboutDialog() }
                    })
                    add(MenuItem("Exit").apply {
                        addActionListener {
                            systemTray.remove(this@with)
                            javafx.application.Platform.exit()
                        }
                    })
                    isImageAutoSize = true
                    popupMenu = this
                }
                systemTray.add(this)
            }
        }
    }

    private fun showAboutDialog() {
        JOptionPane.showMessageDialog(null, "TES Legends Tracker \nby Edipo2s")
//        Alert(AlertType.INFORMATION, "TES Legends Tracker \nby Edipo2s", ButtonType.OK).apply {
//            if (showAndWait().get() == ButtonType.OK) {
//                close()
//            }
//        }
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
        if (Platform.isWindows()) {
            val windowText = ByteArray(512)
            val hwnd = User32.INSTANCE.GetForegroundWindow() // then you can call it!
            User32.INSTANCE.GetWindowTextA(hwnd, windowText, 512)
            titleStr = Native.toString(windowText)
        } else if (Platform.isMac()) {
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