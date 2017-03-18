package com.ediposouza.util

import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
object ImageFuncs {

    var referenceConfig: ReferenceConfig = ReferenceConfig1366x768()

    fun getCardImage(fileName: String): BufferedImage? {
        try {
            val image = ImageIO.read(javaClass.getResource("/Cards/$fileName"))
            return image.getSubimage(referenceConfig.FULL_CARD_START_X, referenceConfig.FULL_CARD_START_Y,
                    referenceConfig.FULL_CARD_WIDTH, referenceConfig.FULL_CARD_HEIGHT)
        } catch (e: Exception) {
            Logger.e("Error loading $fileName - ${e.message}")
            return null
        }
    }

    fun getScaledImage(image: BufferedImage): BufferedImage {
        val tmp = image.getScaledInstance(Recognition.PHASH_SIZE, Recognition.PHASH_SIZE, BufferedImage.SCALE_FAST)
        val scaledIamge = BufferedImage(Recognition.PHASH_SIZE, Recognition.PHASH_SIZE, BufferedImage.TYPE_INT_RGB)
        scaledIamge.graphics.drawImage(tmp, 0, 0, null)
        return scaledIamge
    }

    fun toGrayscale(image: BufferedImage): BufferedImage {
        val cs = ColorSpace.getInstance(ColorSpace.CS_GRAY)
        return ColorConvertOp(cs, null).filter(image, null)
    }

    fun getArenaPickImage(image: BufferedImage, position: Int): BufferedImage {
        val cardX = when (position) {
            1 -> referenceConfig.ARENA_PICK_CARD_FIRST_X
            2 -> referenceConfig.ARENA_PICK_CARD_SECOND_X
            else -> referenceConfig.ARENA_PICK_CARD_THIRD_X
        }
        return image.getSubimage(cardX, referenceConfig.ARENA_PICK_CARD_START_Y, referenceConfig.ARENA_PICK_CARD_WIDTH,
                referenceConfig.ARENA_PICK_CARD_HEIGHT)
    }

    fun getArenaPickImageScaled(image: BufferedImage, position: Int): BufferedImage {
        val cardPositionPickStartX = when (position) {
            1 -> referenceConfig.ARENA_PICK_CARD_FIRST_X
            2 -> referenceConfig.ARENA_PICK_CARD_SECOND_X
            else -> referenceConfig.ARENA_PICK_CARD_THIRD_X
        }
        val cardPosition = getCardPosition(image, cardPositionPickStartX, referenceConfig.ARENA_PICK_CARD_START_Y)
        val cardSize = getCardSize(image, referenceConfig.ARENA_PICK_CARD_WIDTH, referenceConfig.ARENA_PICK_CARD_HEIGHT)
        return image.getSubimage(cardPosition.first, cardPosition.second, cardSize.first, cardSize.second)
    }

    private fun getCardPosition(image: BufferedImage, referenceX: Int, referenceY: Int): Pair<Int, Int> {
        val normalizedCardX = referenceX / referenceConfig.SCREEN_REFERENCE_WIDTH.toDouble()
        val normalizedCardY = referenceY / referenceConfig.SCREEN_REFERENCE_HEIGHT.toDouble()

        val ratio = referenceConfig.SCREEN_REFERENCE_RATIO / (image.width.toDouble() / image.height)
        val imageCardPositionPickStartX = (image.width * ratio * normalizedCardX) + (image.width.toDouble() * (1 - ratio) / 2)
        val imageCardPositionPickStartY = normalizedCardY * image.height
        return imageCardPositionPickStartX.toInt() to imageCardPositionPickStartY.toInt()
    }

    private fun getCardSize(image: BufferedImage, referenceX: Int, referenceY: Int): Pair<Int, Int> {
        val scaleFactor = image.height.toDouble() / referenceConfig.SCREEN_REFERENCE_HEIGHT
        return (scaleFactor * referenceX).toInt() to (scaleFactor * referenceY).toInt()
    }

    fun takeScreenshot(): BufferedImage? {
        try {
            val screenRect = Rectangle(Toolkit.getDefaultToolkit().screenSize)
            return Robot().createScreenCapture(screenRect)
        } catch (e: Exception) {
            Logger.e(e)
            return null
        }
    }

}