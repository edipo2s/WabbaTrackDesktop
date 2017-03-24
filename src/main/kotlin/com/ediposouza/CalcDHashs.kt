package com.ediposouza

import com.ediposouza.extensions.*
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import com.ediposouza.util.images.ImageFuncs
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
object CalcDHashs {

    val CALC_CARDS_DHASH = true

    val CROP_FOLDER_ARENA = "Arena"
    val CROP_FOLDER_CARDS = "Cards"
    val CROP_FOLDER_GAME = "Game"
    val CROP_FOLDER_SCREENS = "Screens"

    @JvmStatic fun main(args: Array<String>) {
        Logger.d("--Cards--")
        if (CALC_CARDS_DHASH) {
            getDHashFolderFiles("/Cards", CROP_FOLDER_CARDS, BufferedImage::getCardCrop)
        }

        Logger.d("--Screens--")
        getDHashFile("/Screens/Main.png", CROP_FOLDER_SCREENS, BufferedImage::getScreenMainCrop)
        getDHashFile("/Screens/Game.png", CROP_FOLDER_SCREENS, BufferedImage::getScreenGameCrop)
        getDHashFile("/Screens/ArenaClasses.png", CROP_FOLDER_SCREENS, BufferedImage::getScreenArenaClassesCrop)
        getDHashFile("/Screens/ArenaClassSelect.png", CROP_FOLDER_SCREENS, BufferedImage::getScreenArenaClassSelectCrop)
        getDHashFile("/Screens/ArenaDash.png", CROP_FOLDER_SCREENS, BufferedImage::getScreenArenaDashboardCrop)
        getDHashFolderFiles("/Screens/ArenaPick", CROP_FOLDER_SCREENS, BufferedImage::getScreenArenaPickCrop)

        Logger.d("--Arena Class Select--")
        getDHashFolderFiles("/Arena", CROP_FOLDER_ARENA, BufferedImage::getArenaClassSelectCrop)

        Logger.d("--Game--")
        getDHashFolderFiles("/Game", CROP_FOLDER_GAME, BufferedImage::getGameOpponentClassCrop)
    }

    fun getDHashFile(relativePath: String, cropFolder: String = "", cropFun: (BufferedImage) -> BufferedImage?) {
        val file = File(TESLTracker::class.java.getResource(relativePath).toURI())
        calcImageFileDHash(file, cropFolder, cropFun)
    }

    fun getDHashFolderFiles(relativePath: String, cropFolder: String = "", cropFun: (BufferedImage) -> BufferedImage?) {
        val folder = File(TESLTracker::class.java.getResource(relativePath).toURI())
        getDHashFolderFiles(folder, folder.path, cropFolder, cropFun)
    }

    private fun getDHashFolderFiles(folder: File, path: String, cropFolder: String = "", cropFun: (BufferedImage) -> BufferedImage?) {
        folder.listFiles().forEach {
            if (it.isDirectory) {
                getDHashFolderFiles(it, path, cropFolder, cropFun)
            } else {
                calcImageFileDHash(it, cropFolder, cropFun)
            }
        }
    }

    private fun calcImageFileDHash(file: File, cropFolder: String, cropFun: (BufferedImage) -> BufferedImage?) {
        ImageFuncs.getFileImage(file)?.apply {
            cropFun(this)?.apply {
                calcDHash(this, file.name, cropFolder)
            }
        }
    }

    private fun calcDHash(image: BufferedImage, imageName: String, cropFolder: String) {
        var cropFolderPath = "src/main/resources/Crops"
        if (cropFolder.isNotEmpty()) {
            cropFolderPath += "/$cropFolder"
        }
        File(cropFolderPath).apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val imageShortName = imageName.substring(0, imageName.indexOf("."))
        Logger.d("\"${Recognizer.calcDHash(image)}\" to \"$imageShortName\",")
        ImageIO.write(image, "png", File("$cropFolderPath/$imageShortName.png"))
    }

}