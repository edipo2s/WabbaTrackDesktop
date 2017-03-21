package com.ediposouza.util

import com.ediposouza.data.DHash
import com.ediposouza.extensions.*
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenshotProcessor {

    fun process(screenshot: BufferedImage): Boolean {
        with(screenshot) {
            //Screens check
            if (Recognizer.recognizeScreenImage(getScreenMainCrop()) == DHash.SCREEN_MAIN) {
                Logger.i("\nMain Screen Detected!")
                return true
            }
            if (Recognizer.recognizeScreenImage(getScreenGameCrop()) == DHash.SCREEN_GAME) {
                Logger.i("\nGame Screen Detected!")
                return true
            }
            //Arena screens check
            if (Recognizer.recognizeScreenImage(getScreenArenaClassesCrop()) == DHash.SCREEN_ARENA_CLASSES) {
                Logger.i("\nArena Classes Screen Detected!")
                return true
            }
            if (Recognizer.recognizeScreenImage(getScreenArenaClassSelectCrop()) == DHash.SCREEN_ARENA_CLASS_SELECT) {
                Logger.i("\nArena Class Select Screen Detected!")
                Recognizer.recognizeArenaClassSelectImage(getArenaClassSelectCrop())?.apply {
                    Logger.i("\nArena Class ${this} Detected!")
                    ScreenshotHandlerArena.processArenaClassSelectScreenshot(this, screenshot)
                    return true
                }
                return true
            }
            if (Recognizer.recognizeScreenImage(getScreenArenaPickCrop()) == DHash.SCREEN_ARENA_PICK) {
                Logger.i("\nArena Pick Screen Detected!")
                return true
            }
            if (Recognizer.recognizeScreenImage(getScreenArenaDashboardCrop()) == DHash.SCREEN_ARENA_DASHBOARD) {
                Logger.i("\nArena Dashboard Screen Detected!")
                return true
            }
        }
        return false
    }

}