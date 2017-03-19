package com.ediposouza.util

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.util.images.ImageFuncs
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenshotProcessor {

    fun process(screenshot: BufferedImage) {
        with(ImageFuncs.getArenaClassSelectedCroppedImage(screenshot)) {
            Recognizer.recognizeArenaClassSelectImage(this)?.apply {
                Logger.i("\nArena Class ${this} Detected!")
                saveCroppedImage()
                return
            }
        }
        with(ImageFuncs.getArenaPicksRemainingCroppedImage(screenshot)) {
            Recognizer.recognizeArenaScreenImage(this)?.apply {
                Logger.i("\nArena Screen ${this} Detected!")
                saveCroppedImage()
                recognizeArenaPick(screenshot, 1)
                recognizeArenaPick(screenshot, 2)
                recognizeArenaPick(screenshot, 3)
                return
            }
        }
    }

    private fun recognizeArenaPick(image: BufferedImage, pick: Int) {
        with(ImageFuncs.getArenaCardCropped(image, pick)) {
            saveCroppedImage()
            TESLTrackerData.getCard(Recognizer.recognizeCardImage(this))?.apply {
                Logger.i("--$name: $arenaTier")
            }
        }
    }

}