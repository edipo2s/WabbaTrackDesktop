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
abstract class BaseCalcDHash {

    abstract fun getCroppedImage(fullImage: BufferedImage, type: Int): BufferedImage?

    protected fun getDHashFile(file: File, cropFolder: String = "", type: Int = 0) {
        calcImageFileDHash(file, cropFolder, type)
    }

    protected fun getDHashFolderFiles(folder: File, path: String, cropFolder: String = "", type: Int = 0) {
        folder.listFiles().forEach {
            if (it.isDirectory) {
                getDHashFolderFiles(it, path, cropFolder, type)
            } else {
                calcImageFileDHash(it, cropFolder, type)
            }
        }
    }

    private fun calcImageFileDHash(file: File, cropFolder: String, type: Int = 0) {
        ImageFuncs.getFileImage(file)?.apply {
            getCroppedImage(this, type)?.apply {
                calcDHash(this, file.name, cropFolder)
            }
        }
    }

    private fun calcDHash(image: BufferedImage, imageName: String, cropFolder: String) {
        var cropFolderPath = "src/main/resources/Test/Crops"
        if (cropFolder.isNotEmpty()) {
            cropFolderPath += "/$cropFolder"
        }
        File(cropFolderPath).apply {
            if (!exists()) {
                mkdir()
            }
        }
        val imageShortName = imageName.substring(0, imageName.indexOf("."))
        Logger.d("\"$imageShortName\" to \"${Recognition.calcDHash(image)}\",")
        ImageIO.write(image, "png", File("$cropFolderPath/$imageShortName.png"))
    }
}