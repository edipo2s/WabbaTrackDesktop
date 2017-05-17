package com.ediposouza.util

import com.ediposouza.TESLTracker
import com.ediposouza.ui.MonitorNumberWidget
import com.sun.jna.Native
import com.sun.jna.Platform
import com.sun.jna.PointerType
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.win32.StdCallLibrary
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.Robot
import java.awt.image.BufferedImage
import java.util.*
import javax.script.ScriptEngineManager
import javax.script.ScriptException

/**
 * Created by ediposouza on 18/03/17.
 */
object ScreenFuncs {

    private const val GAME_MONITORS_PREF: String = "GameMonitors"
    private const val GAME_MONITOR_SELECTED_PREF: String = "GameMonitorSelected"

    //OSX need Download - Java para OS X 2015-001

    interface User32 : StdCallLibrary {
        fun GetForegroundWindow(): WinDef.HWND   // add this
        fun GetWindowTextA(hWnd: PointerType, lpString: ByteArray, nMaxCount: Int): Int

        companion object {
            val INSTANCE = Native.loadLibrary("user32", User32::class.java)
        }
    }

    fun getActiveWindowTitle(): String {
        try {
            var titleStr: String
            if (Platform.isWindows()) {
                val windowText = ByteArray(512)
                val hwnd = User32.INSTANCE.GetForegroundWindow()
                User32.INSTANCE.GetWindowTextA(hwnd, windowText, 512)
                titleStr = Native.toString(windowText)
            } else if (Platform.isMac()) {
                val script = "tell application \"System Events\"\n" +
                        "	name of application processes whose frontmost is true\n" +
                        "end tell"
                val appleScript = ScriptEngineManager().getEngineByName("AppleScript")
                try {
                    titleStr = appleScript.eval(script).toString()
                } catch (e: ScriptException) {
                    titleStr = ""
                }

            } else {
                titleStr = "Platform is not Support"
            }
//        Logger.d(titleStr)
            return titleStr
        } catch (e: Exception) {
            Logger.e("Initialization Error")
            TESLTracker.showMessage("Initialization error, please restart WabbaTrack.")
        }
        return ""
    }

    fun takeScreenshot(): BufferedImage? {
        try {
            val screenRect = TESLTracker.graphicsDevice.defaultConfiguration.bounds
            return Robot().createScreenCapture(screenRect)
        } catch (e: Exception) {
            Logger.e(e)
            return null
        }
    }

    fun clearGameMonitorPref() {
        TESLTracker.preferences.remove(GAME_MONITORS_PREF)
        TESLTracker.preferences.remove(GAME_MONITOR_SELECTED_PREF)
    }

    fun getGameMonitor(onSuccess: (GraphicsDevice) -> Unit) {
        val graphicDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().screenDevices
        val graphicsDeviceFirst = graphicDevices.first()
        if (graphicDevices.size == 1) {
            onSuccess(graphicsDeviceFirst)
            return
        }
        if (TESLTracker.preferences.getInt(GAME_MONITORS_PREF, 0) == graphicDevices.size &&
                TESLTracker.preferences.keys().contains(GAME_MONITORS_PREF)) {
            onSuccess(graphicDevices[TESLTracker.preferences.getInt(GAME_MONITOR_SELECTED_PREF, 0)])
            return
        }
        val graphicsDeviceLast = graphicDevices.last()
        val monitor1Widget = MonitorNumberWidget(1, graphicsDeviceFirst).apply {
            isVisible = true
        }
        val monitor2Widget = MonitorNumberWidget(2, graphicsDeviceLast).apply {
            isVisible = true
        }
        val alert = Alert(Alert.AlertType.CONFIRMATION, "In which monitor you will play Elder Scroll Legends?",
                ButtonType("1"), ButtonType("2"))
        alert.title = "Dual Monitor"
        val root = alert.dialogPane
        root.scene.root = Group()
        Stage(StageStyle.UTILITY).apply {
            for (buttonType in root.buttonTypes) {
                root.lookupButton(buttonType).setOnMouseClicked {
                    root.userData = buttonType
                    close()
                }
            }
            initModality(Modality.APPLICATION_MODAL)
            isAlwaysOnTop = true
            scene = Scene(root)
        }.showAndWait()
        val button: Optional<ButtonType> = Optional.ofNullable(root.userData as ButtonType)
        monitor1Widget.isVisible = false
        monitor2Widget.isVisible = false
        if (button.isPresent) {
            TESLTracker.preferences.putInt(GAME_MONITORS_PREF, graphicDevices.size)
            if (button.get().text == "1") {
                TESLTracker.preferences.putInt(GAME_MONITOR_SELECTED_PREF, 0)
                onSuccess(graphicsDeviceFirst)
            } else {
                TESLTracker.preferences.putInt(GAME_MONITOR_SELECTED_PREF, 1)
                onSuccess(graphicsDeviceLast)
            }
        }
    }

}