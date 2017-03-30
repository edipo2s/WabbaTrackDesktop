package com.ediposouza.handler

import com.ediposouza.data.DHash
import com.ediposouza.extensions.*
import com.ediposouza.state.ArenaState
import com.ediposouza.state.GameState
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenHandler {

    var lastScreenRecognized = ""

    fun process(screenshot: BufferedImage): Boolean {
//        Logger.i("Checking game screen")
        //Screens check
        if (screenshot.getScreenMainCrop().matchScreen(DHash.SCREEN_MAIN)) {
            Logger.i("Main Screen Detected!", true)
            StateHandler.currentTESLState = null
            return true
        }
        if (screenshot.getScreenGameCrop().matchScreen(DHash.SCREEN_GAME)) {
            Logger.i("Game Screen Detected!", true)
            StateHandler.currentTESLState = GameState
            return true
        }
        //Arena screens check
        if (screenshot.getScreenArenaClassesCrop().matchScreen(DHash.SCREEN_ARENA_CLASSES)) {
            Logger.i("Arena Classes Screen Detected!", true)
            StateHandler.currentTESLState = ArenaState.apply {
                resetState()
            }
            return true
        }
        if (screenshot.getScreenArenaPickCrop().matchScreenPickList(DHash.SCREENS_ARENA_PICK)) {
            Logger.i("Arena Pick Screen Detected!", true)
            StateHandler.currentTESLState = ArenaState.apply {
                pickNumber = DHash.SCREENS_ARENA_PICK.indexOf(lastScreenRecognized) + 1
                ArenaHandler.processArenaPick()
            }
            return true
        }
        if (screenshot.getScreenArenaDashboardCrop().matchScreen(DHash.SCREEN_ARENA_DASHBOARD)) {
            Logger.i("Arena Dashboard Screen Detected!", true)
            StateHandler.currentTESLState = ArenaState.apply {
                finishPicks = true
            }
            return true
        }
        return false
    }

    private fun BufferedImage.matchScreen(screen: String): Boolean {
        saveCroppedImage()
        if (Recognizer.recognizeScreenImage(this) == screen && lastScreenRecognized != screen) {
            lastScreenRecognized = screen
            return true
        } else {
            return false
        }
    }

    private fun BufferedImage.matchScreenPickList(screens: List<String>): Boolean {
        val recognizedScreen = Recognizer.recognizeScreenPickImage(this)
        if (screens.contains(recognizedScreen) && lastScreenRecognized != recognizedScreen) {
            lastScreenRecognized = recognizedScreen!!
            return true
        } else {
            return false
        }
    }

}