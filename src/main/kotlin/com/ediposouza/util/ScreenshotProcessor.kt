package com.ediposouza.util

import com.ediposouza.extensions.getArenaClassSelectedCroppedImage
import com.ediposouza.extensions.getArenaPicksRemainingCroppedImage
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenshotProcessor {

    fun process(screenshot: BufferedImage): Boolean {
        with(screenshot) {
            //Arena screenshot check
            Recognizer.recognizeArenaClassSelectImage(getArenaClassSelectedCroppedImage())?.apply {
                Logger.i("\nArena Class ${this} Detected!")
                ScreenshotHandlerArena.processArenaClassSelectScreenshot(this, screenshot)
                return true
            }
            Recognizer.recognizeArenaScreenImage(getArenaPicksRemainingCroppedImage())?.apply {
                Logger.i("\nArena Screen ${this} Detected!")
                ScreenshotHandlerArena.processArenaPickScreenshot(screenshot)
                return true
            }
        }
        return false
    }

}