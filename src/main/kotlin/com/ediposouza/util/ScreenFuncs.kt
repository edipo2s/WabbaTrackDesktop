package com.ediposouza.util

import com.sun.jna.Native
import com.sun.jna.Platform
import com.sun.jna.PointerType
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.win32.StdCallLibrary
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage
import javax.script.ScriptEngineManager
import javax.script.ScriptException

/**
 * Created by ediposouza on 18/03/17.
 */
object ScreenFuncs {

    //OSX need Download - Java para OS X 2015-001

    interface User32 : StdCallLibrary {
        fun GetForegroundWindow(): WinDef.HWND   // add this
        fun GetWindowTextA(hWnd: PointerType, lpString: ByteArray, nMaxCount: Int): Int

        companion object {
            val INSTANCE = Native.loadLibrary("user32", User32::class.java)
        }
    }

    fun getActiveWindowTitle(): String {
        var titleStr: String
        if (Platform.isWindows()) {
            val windowText = ByteArray(512)
            val hwnd = User32.INSTANCE.GetForegroundWindow() // then you can call it!
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
    }

    fun takeScreenshot(): BufferedImage? {
        try {
            val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)
            return Robot().createScreenCapture(screenRect)
        } catch (e: Exception) {
            Logger.e(e)
            return null
        }
    }

}