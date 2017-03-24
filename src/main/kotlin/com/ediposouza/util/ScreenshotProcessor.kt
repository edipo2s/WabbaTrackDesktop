package com.ediposouza.util

import com.ediposouza.data.DHash
import com.ediposouza.extensions.*
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenshotProcessor {

    var lastScreenRecognized = ""

    fun process(screenshot: BufferedImage): Boolean {
        //Screens check
        if (screenshot.getScreenMainCrop().matchScreen(DHash.SCREEN_MAIN)) {
            Logger.i("\nMain Screen Detected!")
            return true
        }
        if (screenshot.getScreenGameCrop().matchScreen(DHash.SCREEN_GAME)) {
            Logger.i("\nGame Screen Detected!")
            screenshot.saveCroppedImage()
            return true
        }
        //Arena screens check
        if (screenshot.getScreenArenaClassesCrop().matchScreen(DHash.SCREEN_ARENA_CLASSES)) {
            Logger.i("\nArena Classes Screen Detected!")
            return true
        }
        if (screenshot.getScreenArenaClassSelectCrop().matchScreen(DHash.SCREEN_ARENA_CLASS_SELECT)) {
            Logger.i("\nArena Class Select Screen Detected!")
            Recognizer.recognizeArenaClassSelectImage(screenshot.getArenaClassSelectCrop())?.apply {
                Logger.i("\nArena Class ${this} Detected!")
                ScreenshotHandlerArena.processArenaClassSelectScreenshot(this, screenshot)
                return true
            }
            return true
        }
        if (screenshot.getScreenArenaPickCrop().matchScreenList(DHash.SCREENS_ARENA_PICK,
                Recognizer.Similarity.DHASH_DISTANCE_SIMILARITY_SUPER_HIGH)) {
            Logger.i("\nArena Pick Screen Detected!")
            ScreenshotHandlerArena.processArenaPickScreenshot(screenshot)
            return true
        }
        if (screenshot.getScreenArenaDashboardCrop().matchScreen(DHash.SCREEN_ARENA_DASHBOARD)) {
            Logger.i("\nArena Dashboard Screen Detected!")
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