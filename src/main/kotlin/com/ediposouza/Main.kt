package com.ediposouza

import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognition
import com.ediposouza.util.ReferenceConfig1024x768
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
object Main {

    const val SCREENSHOT_DELAY: Long = 1_000

    var lastScreenshotDHash = ""

    @JvmStatic fun main(args: Array<String>) {
//        while(true) {
//            Thread.sleep(SCREENSHOT_DELAY)
//            ImageFuncs.takeScreenshot()?.apply {
//                val screenshotDHash = Recognition.calcDHash(this)
//                if (screenshotDHash == lastScreenshotDHash) {
//                    Logger.d("Waiting..")
//                } else {
//                    lastScreenshotDHash = screenshotDHash
//                    recognizeArenaPick(this)
//                }
//            }
//        }
        ImageFuncs.referenceConfig = ReferenceConfig1024x768()
        testArenaPick()
    }

    private fun testArenaPick() {
        val image = ImageIO.read(javaClass.getResource("/Test/Pick.png"))
        recognizeArenaPick(image)
    }

    private fun recognizeArenaPick(image: BufferedImage) {
        Logger.d("Image size: ${image.width}x${image.height}")
        recognizeCard(ImageFuncs.getArenaPickImageScaled(image, 1))
        recognizeCard(ImageFuncs.getArenaPickImageScaled(image, 2))
        recognizeCard(ImageFuncs.getArenaPickImageScaled(image, 3))
    }

    private fun recognizeCard(cardImage: BufferedImage) {
        val tmpFileName = "recognize_${System.currentTimeMillis()}.png"
        File("src/main/resources/Test/Tmp").apply {
            if (!exists()) {
                mkdir()
            }
        }
        ImageIO.write(cardImage, "png", File("src/main/resources/Test/Tmp/$tmpFileName"))
        Logger.d(Recognition.recognizeDHash(Recognition.calcDHash(cardImage)))
    }

}