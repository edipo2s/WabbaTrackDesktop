package com.ediposouza

import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognition
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
object Main {

    @JvmStatic fun main(args: Array<String>) {
        listPHashs()
//        testArenaPick()
    }

    private fun listPHashs() {
        val cards = File(javaClass.getResource("/Cards").toURI())
        listPHashsFolder(cards, cards.path)
    }

    private fun listPHashsFolder(folder: File, path: String) {
        folder.listFiles().forEach {
            if (it.isDirectory) {
                listPHashsFolder(it, path)
            } else {
                simpleRecognition(it.path.removePrefix(path))
            }
        }
    }

    private fun simpleRecognition(imageName: String) {
        val image = ImageFuncs.getCardImage(imageName)
        if (image != null) {
            val cardShortName = imageName.substring(imageName.lastIndexOf("/") + 1, imageName.indexOf("."))
            Logger.d("\"$cardShortName\" to \"${Recognition.calcDHash(image)}\",")
        }
    }

    private fun testArenaPick() {
        val image = ImageIO.read(javaClass.getResource("/Pick.png"))
        Logger.d(Recognition.recognize(ImageFuncs.getArenaPickImage(image, 1)))
        Logger.d(Recognition.recognize(ImageFuncs.getArenaPickImage(image, 2)))
        Logger.d(Recognition.recognize(ImageFuncs.getArenaPickImage(image, 3)))
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