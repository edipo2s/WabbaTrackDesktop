package com.ediposouza

import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognition
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
object CalcCardsDHash {

    @JvmStatic fun main(args: Array<String>) {
        listPHashs()
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
                calcDHash(it.path.removePrefix(path))
            }
        }
    }

    private fun calcDHash(imageName: String) {
        val image = ImageFuncs.getCardImage(imageName)
        if (image != null) {
            val cardShortName = imageName.substring(imageName.lastIndexOf("/") + 1, imageName.indexOf("."))
            ImageIO.write(image, "png", File("src/main/resources/Test/Images/$cardShortName.png"))
            Logger.d("\"$cardShortName\" to \"${Recognition.calcDHash(image)}\",")
        }
    }

}