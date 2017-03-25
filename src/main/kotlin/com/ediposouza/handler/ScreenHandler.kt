package com.ediposouza.handler

import com.ediposouza.data.DHash
import com.ediposouza.extensions.*
import com.ediposouza.scope.ArenaState
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenHandler {

    var lastScreenRecognized = ""

    fun process(screenshot: BufferedImage): Boolean {
        //Screens check
        if (screenshot.getScreenMainCrop().matchScreen(DHash.SCREEN_MAIN)) {
            Logger.i("Main Screen Detected!", true)
            StateHandler.currentTESLState = null
            return true
        }
        if (screenshot.getScreenGameCrop().matchScreen(DHash.SCREEN_GAME)) {
            Logger.i("Game Screen Detected!", true)
            StateHandler.currentTESLState = null
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
        if (screenshot.getScreenArenaClassSelectCrop().matchScreen(DHash.SCREEN_ARENA_CLASS_SELECT)) {
            Logger.i("Arena Class Select Screen Detected!", true)
            StateHandler.currentTESLState = ArenaState.apply {
                Recognizer.recognizeArenaClassSelectImage(screenshot.getArenaClassSelectCrop())?.apply {
                    Logger.i("Arena Class ${this} Detected!", true)
                    lastClassSelectViews = this
                    return true
                }
            }
            return true
        }
        if (screenshot.getScreenArenaPickCrop().matchScreenList(DHash.SCREENS_ARENA_PICK,
                Recognizer.Similarity.DHASH_DISTANCE_SIMILARITY_SUPER_HIGH)) {
            Logger.i("Arena Pick Screen Detected!", true)
            StateHandler.currentTESLState = ArenaState.apply {
                pickNumber = DHash.SCREENS_ARENA_PICK.indexOf(lastScreenRecognized) + 1
                showTierPicks(ArenaHandler.processArenaPickScreenshot(screenshot))
            }
            return true
        }
        if (screenshot.getScreenArenaDashboardCrop().matchScreen(DHash.SCREEN_ARENA_DASHBOARD)) {
            Logger.i("Arena Dashboard Screen Detected!", true)
            StateHandler.currentTESLState = ArenaState
            return true
        }
        return false
    }

    private fun BufferedImage.matchScreen(screen: String): Boolean {
        if (Recognizer.recognizeScreenImage(this) == screen && lastScreenRecognized != screen) {
            lastScreenRecognized = screen
            return true
        } else {
            return false
        }
    }

    private fun BufferedImage.matchScreenList(screens: List<String>,
                                              similarity: Double = Recognizer.Similarity.DHASH_DISTANCE_SIMILARITY_LOW): Boolean {
        val recognizedScreen = Recognizer.recognizeScreenImage(this, similarity)
        if (screens.contains(recognizedScreen) && lastScreenRecognized != recognizedScreen) {
            lastScreenRecognized = recognizedScreen!!
            return true
        } else {
            return false
        }
    }

}