package com.ediposouza

import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognition
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
object Main {

    @JvmStatic fun main(args: Array<String>) {
        testArenaPick()
    }

    private fun testArenaPick() {
        val image = ImageIO.read(javaClass.getResource("/Test/Pick.png"))
        recognize(ImageFuncs.getArenaPickImage(image, 1))
        recognize(ImageFuncs.getArenaPickImage(image, 2))
        recognize(ImageFuncs.getArenaPickImage(image, 3))
    }

    private fun recognize(image: BufferedImage) {
        ImageIO.write(image, "png", File("src/main/resources/Test/Tmp/recognize_${System.currentTimeMillis()}.png"))
        Logger.d(Recognition.recognize(image))
    }

    private fun saveScreenshot() {
        val screenFullImage = ImageFuncs.takeScreenshot()
        if (screenFullImage != null) {
            val grayscale = ImageFuncs.toGrayscale(screenFullImage)
            Logger.d(grayscale.toString())
            ImageIO.write(grayscale, "jpg", File("Screenshot.jpg"))
            println("A full screenshot saved!")
        }
    }

}