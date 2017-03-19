package com.ediposouza.util

import com.ediposouza.data.TESLTrackerData
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
        val card1 = TESLTrackerData.getCard(recognizeCard(ImageFuncs.getArenaCardCropped(image, 1)))
        val card2 = TESLTrackerData.getCard(recognizeCard(ImageFuncs.getArenaCardCropped(image, 2)))
        val card3 = TESLTrackerData.getCard(recognizeCard(ImageFuncs.getArenaCardCropped(image, 3)))
        Logger.i("${card1?.name}: ${card1?.arenaTier}")
        Logger.i("${card2?.name}: ${card2?.arenaTier}")
        Logger.i("${card3?.name}: ${card3?.arenaTier}")
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