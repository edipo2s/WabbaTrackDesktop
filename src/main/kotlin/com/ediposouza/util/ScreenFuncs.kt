package com.ediposouza.util

import com.ediposouza.TESLTracker
import com.ediposouza.extensions.alertAlwaysOnTop
import com.ediposouza.ui.MonitorNumberWidget
import com.sun.jna.Native
import com.sun.jna.Platform
import com.sun.jna.PointerType
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.win32.StdCallLibrary
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.awt.Robot
import java.awt.image.BufferedImage
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
            val INSTANCE by lazy { loadUser32() }

            private fun loadUser32(retry: Int = 0): User32? {
                return try {
                    Native.loadLibrary("user32", User32::class.java)
                } catch (e: Exception) {
                    Logger.e(e)
                    if (retry < 3) {
                        Thread.sleep(250)
                        loadUser32(retry + 1)
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun getActiveWindowTitle(): String {
        User32.INSTANCE?.let {
            var titleStr: String
            if (Platform.isWindows()) {
                val windowText = ByteArray(512)
                val hwnd = it.GetForegroundWindow()
                it.GetWindowTextA(hwnd, windowText, 512)
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
        }
        Logger.e("Initialization Error")
        TESLTracker.showMessage("Initialization error, please restart WabbaTrack.")
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
        Mixpanel.postEventDualMonitorDetected()
        if (TESLTracker.preferences.getInt(GAME_MONITORS_PREF, 0) == graphicDevices.size &&
                TESLTracker.preferences.keys().contains(GAME_MONITORS_PREF)) {
            onSuccess(graphicDevices[TESLTracker.preferences.getInt(GAME_MONITOR_SELECTED_PREF, 0)])
            return
        }
        val graphicsDeviceLast = graphicDevices.last()
        val monitor1Widget = MonitorNumberWidget(1, graphicsDeviceFirst).apply { isVisible = true }
        val monitor2Widget = MonitorNumberWidget(2, graphicsDeviceLast).apply { isVisible = true }
        alertAlwaysOnTop(Alert.AlertType.CONFIRMATION, "Dual Monitor", "In which monitor you will play Elder Scroll Legends?",
                ButtonType("1"), ButtonType("2")) { button ->
            monitor1Widget.isVisible = false
            monitor2Widget.isVisible = false
            TESLTracker.preferences.putInt(GAME_MONITORS_PREF, graphicDevices.size)
            if (button.text == "1") {
                TESLTracker.preferences.putInt(GAME_MONITOR_SELECTED_PREF, 0)
                onSuccess(graphicsDeviceFirst)
            } else {
                TESLTracker.preferences.putInt(GAME_MONITOR_SELECTED_PREF, 1)
                onSuccess(graphicsDeviceLast)
            }
        }
    }

}