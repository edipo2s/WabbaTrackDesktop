package com.ediposouza.util

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by Edipo on 19/03/2017.
 */

fun BufferedImage.saveCroppedImage() {
    val tmpFileName = "recognize_${System.currentTimeMillis()}.png"
    File("src/main/resources/Test/Tmp").apply {
        if (!exists()) {
            mkdir()
        }
    }
    ImageIO.write(this, "png", File("src/main/resources/Test/Tmp/$tmpFileName"))
}
