package com.ediposouza

import com.ediposouza.extensions.*
import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
object CalcDHashs {

    val CALC_CARDS_HASH = false
    val CALC_ARENA_PICKS_HASH = true

    val CROP_FOLDER_ARENA = "Arena"
    val CROP_FOLDER_CARDS = "Cards"
    val CROP_FOLDER_GAME = "Game"
    val CROP_FOLDER_SCREENS = "Screens"

    @JvmStatic fun main(args: Array<String>) {
        Logger.d("--Cards--")
        if (CALC_CARDS_HASH) {
            getDHashFolderFiles("/Cards", CROP_FOLDER_CARDS, BufferedImage::getCardCrop)
        }

        Logger.d("--Screens--")
        getDHashFile("/Screens/Main.png", CROP_FOLDER_SCREENS, BufferedImage::getScreenMainCrop)
        getDHashFile("/Screens/Game.png", CROP_FOLDER_SCREENS, BufferedImage::getScreenGameCrop)
        getDHashFile("/Screens/ArenaClasses.png", CROP_FOLDER_SCREENS, BufferedImage::getScreenArenaClassesCrop)
        getDHashFile("/Screens/ArenaDash.png", CROP_FOLDER_SCREENS, BufferedImage::getScreenArenaDashboardCrop)
        if (CALC_ARENA_PICKS_HASH) {
            getDHashFolderFiles("/Screens/ArenaPick", CROP_FOLDER_SCREENS, BufferedImage::getScreenArenaPickCrop)
        }

        Logger.d("--Arena Class Select--")
        getDHashFolderFiles("/ArenaClass", CROP_FOLDER_ARENA, BufferedImage::getArenaPickClassCrop)

        Logger.d("--Game--")
        getDHashFile("/Game/PlayFirst.png", CROP_FOLDER_GAME, BufferedImage::getGamePlayerFirstCrop)
        getDHashFile("/Game/PlaySecond.png", CROP_FOLDER_GAME, BufferedImage::getGamePlayerSecondCrop)
        getDHashFile("/Game/Win.png", CROP_FOLDER_GAME, BufferedImage::getGameWinCrop)
        getDHashFile("/Game/Win2.png", CROP_FOLDER_GAME, BufferedImage::getGameWin2Crop)
        getDHashFile("/Game/Loss.png", CROP_FOLDER_GAME, BufferedImage::getGameLossCrop)
        getDHashFile("/Game/Loss2.png", CROP_FOLDER_GAME, BufferedImage::getGameLoss2Crop)
        getDHashFile("/Game/YourTurn.png", CROP_FOLDER_GAME, BufferedImage::getGameYourTurnCrop)
        getDHashFile("/Game/CardDraw.png", CROP_FOLDER_GAME, BufferedImage::getGameCardDrawCrop)
        getDHashFolderFiles("/Game/PlayerClass", CROP_FOLDER_GAME, BufferedImage::getGamePlayerClassCrop)
        getDHashFolderFiles("/Game/OpponentClass", CROP_FOLDER_GAME, BufferedImage::getGameOpponentClassCrop)
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
        Logger.d("\"${Recognizer.calcPHash(image)}\" to \"$imageShortName\",")
        ImageIO.write(image, "png", File("$cropFolderPath/$imageShortName.png"))
    }

}