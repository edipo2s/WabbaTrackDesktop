package com.ediposouza.util

import com.ediposouza.util.images.ImageFuncs
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenshotProcessor {

    fun process(screenshot: BufferedImage) {
        with(ImageFuncs.getArenaClassSelectedCroppedImage(screenshot)) {
            Recognizer.recognizeArenaClassSelectImage(this)?.apply {
                Logger.d("Arena Class ${this} Detected!")
                saveCroppedImage()
                return
            }
        }
        with(ImageFuncs.getArenaPicksRemainingCroppedImage(screenshot)) {
            Recognizer.recognizeArenaScreenImage(this)?.apply {
                Logger.d("Arena Screen ${this} Detected!")
                saveCroppedImage()
                recognizeArenaPick(screenshot)
                return
            }
        }
    }

    private fun recognizeArenaPick(image: BufferedImage) {
        recognizeCard(ImageFuncs.getArenaCardCropped(image, 1))
        recognizeCard(ImageFuncs.getArenaCardCropped(image, 2))
        recognizeCard(ImageFuncs.getArenaCardCropped(image, 3))
    }

    private fun recognizeCard(cardImage: BufferedImage, outputFile: Boolean = false): String? {
        if (outputFile) {
            val tmpFileName = "recognize_${System.currentTimeMillis()}.png"
            File("src/main/resources/Test/Tmp").apply {
                if (!exists()) {
                    mkdir()
                }
            }
            ImageIO.write(cardImage, "png", File("src/main/resources/Test/Tmp/$tmpFileName"))
        }
        return Recognizer.recognizeCardImage(cardImage)
    }

}