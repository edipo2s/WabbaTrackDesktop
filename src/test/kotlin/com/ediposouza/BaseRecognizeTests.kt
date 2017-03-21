package com.ediposouza

import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
abstract class BaseRecognizeTests {

    protected fun getFileImage(testFileName: String): BufferedImage {
        val image = ImageIO.read(javaClass.getResource("/Test/$testFileName"))
        val reference = App.referenceConfig.SCREEN_REFERENCE
        Logger.i("Using $reference as screen reference with Image size: ${image.width}x${image.height}")
        return image
    }

    protected fun recognizeImage(image: BufferedImage, dHashMap: Map<String, String>, outputFile: Boolean = false): String? {
        if (outputFile) {
            val tmpFileName = "recognize_${System.currentTimeMillis()}.png"
            File("src/test/resources/Test/Tmp").apply {
                if (!exists()) {
                    mkdir()
                }
            }
            ImageIO.write(image, "png", File("src/test/resources/Test/Tmp/$tmpFileName"))
        }
        return Recognizer.recognizeImageInMap(image, dHashMap)
    }

}