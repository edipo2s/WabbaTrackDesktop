package com.ediposouza.util.images

import com.ediposouza.util.Logger
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
object ImageFuncs {

    var referenceConfig: ReferenceConfig = ReferenceConfig1366x768()

    fun getFileImage(file: File): BufferedImage? {
        try {
            val image = ImageIO.read(file)
            return image
        } catch (e: Exception) {
            Logger.e("Error loading ${file.name} - ${e.message}")
            return null
        }
    }

    fun getCardCroppedImage(fullImage: BufferedImage): BufferedImage? {
        return fullImage.getSubimage(referenceConfig.FULL_CARD_START_X, referenceConfig.FULL_CARD_START_Y,
                referenceConfig.FULL_CARD_WIDTH, referenceConfig.FULL_CARD_HEIGHT)
    }

    fun getArenaPicksRemainingCroppedImage(image: BufferedImage): BufferedImage {
        val pickPosition = getScaledPosition(image, referenceConfig.ARENA_PICKS_REMAINING_START_X,
                referenceConfig.ARENA_PICKS_REMAINING_START_Y)
        val pickSize = getScaledSize(image, referenceConfig.ARENA_PICKS_REMAINING_WIDTH,
                referenceConfig.ARENA_PICKS_REMAINING_HEIGHT)
        return image.getSubimage(pickPosition.first, pickPosition.second, pickSize.first, pickSize.second)
    }

    fun getArenaClassSelectedCroppedImage(image: BufferedImage): BufferedImage {
        val pickPosition = getScaledPosition(image, referenceConfig.ARENA_CLASS_SELECTED_START_X,
                referenceConfig.ARENA_CLASS_SELECTED_START_Y)
        val pickSize = getScaledSize(image, referenceConfig.ARENA_CLASS_SELECTED_WIDTH,
                referenceConfig.ARENA_CLASS_SELECTED_HEIGHT)
        return image.getSubimage(pickPosition.first, pickPosition.second, pickSize.first, pickSize.second)
    }

    fun getArenaCardCropped(image: BufferedImage, pickPosition: Int): BufferedImage {
        val cardPositionPickStartX = when (pickPosition) {
            1 -> referenceConfig.ARENA_PICK_CARD_FIRST_X
            2 -> referenceConfig.ARENA_PICK_CARD_SECOND_X
            else -> referenceConfig.ARENA_PICK_CARD_THIRD_X
        }
        val cardPosition = getScaledPosition(image, cardPositionPickStartX, referenceConfig.ARENA_PICK_CARD_START_Y)
        val cardSize = getScaledSize(image, referenceConfig.ARENA_PICK_CARD_WIDTH, referenceConfig.ARENA_PICK_CARD_HEIGHT)
        return image.getSubimage(cardPosition.first, cardPosition.second, cardSize.first, cardSize.second)
    }

    private fun getScaledPosition(image: BufferedImage, referenceX: Int, referenceY: Int): Pair<Int, Int> {
        val normalizedCardX = referenceX / referenceConfig.SCREEN_REFERENCE_WIDTH.toDouble()
        val normalizedCardY = referenceY / referenceConfig.SCREEN_REFERENCE_HEIGHT.toDouble()

        val ratio = referenceConfig.SCREEN_REFERENCE_RATIO / (image.width.toDouble() / image.height)
        val imageScaledPositionPickStartX = (image.width * ratio * normalizedCardX) + (image.width.toDouble() * (1 - ratio) / 2)
        val imageScaledPositionPickStartY = normalizedCardY * image.height
        return imageScaledPositionPickStartX.toInt() to imageScaledPositionPickStartY.toInt()
    }

    private fun getScaledSize(image: BufferedImage, referenceX: Int, referenceY: Int): Pair<Int, Int> {
        val scaleFactor = image.height.toDouble() / referenceConfig.SCREEN_REFERENCE_HEIGHT
        return (scaleFactor * referenceX).toInt() to (scaleFactor * referenceY).toInt()
    }

}