package com.ediposouza.util

import com.ediposouza.util.images.ImageFuncs
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenshotProcessor {

    fun process(screenshot: BufferedImage): Boolean {
        //Arena screenshot check
        with(ImageFuncs.getArenaClassSelectedCroppedImage(screenshot)) {
            Recognizer.recognizeArenaClassSelectImage(this)?.apply {
                Logger.i("\nArena Class ${this} Detected!")
                ScreenshotHandlerArena.processArenaClassSelectScreenshot(this, this@with)
                return true
            }
        }
        with(ImageFuncs.getArenaPicksRemainingCroppedImage(screenshot)) {
            Recognizer.recognizeArenaScreenImage(this)?.apply {
                Logger.i("\nArena Screen ${this} Detected!")
                ScreenshotHandlerArena.processArenaPickScreenshot(this@with)
                return true
            }
        }
        return false
    }

}