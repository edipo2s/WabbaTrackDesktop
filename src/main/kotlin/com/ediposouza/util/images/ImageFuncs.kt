package com.ediposouza.util.images

import com.ediposouza.TESLTracker
import com.ediposouza.util.Logger
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
object ImageFuncs {

    fun getFileImage(file: File): BufferedImage? {
        try {
            val image = ImageIO.read(file)
            return image
        } catch (e: Exception) {
            Logger.e("Error loading ${file.name} - ${e.message}")
            return null
        }
    }

    fun getScreenScaledPosition(referenceX: Int, referenceY: Int): Pair<Int, Int> {
        val screenBounds = TESLTracker.screenSize
        return getScaledPosition(screenBounds.width.toInt(), screenBounds.height.toInt(), referenceX, referenceY)
    }

    fun getScreenScaledSize(referenceX: Int, referenceY: Int): Pair<Int, Int> {
        val screenBounds = TESLTracker.screenSize
        return getScaledSize(screenBounds.height.toInt(), referenceX, referenceY)
    }

    fun getScaledPosition(screenWidth: Int, screenHeight: Int, referenceX: Int, referenceY: Int): Pair<Int, Int> {
        val normalizedCardX = referenceX / TESLTracker.referenceConfig.SCREEN_REFERENCE_WIDTH.toDouble()
        val normalizedCardY = referenceY / TESLTracker.referenceConfig.SCREEN_REFERENCE_HEIGHT.toDouble()

        val ratio = TESLTracker.referenceConfig.SCREEN_REFERENCE_RATIO / (screenWidth.toDouble() / screenHeight)
        val imageScaledPositionPickStartX = (screenWidth * ratio * normalizedCardX) + (screenWidth.toDouble() * (1 - ratio) / 2)
        val imageScaledPositionPickStartY = normalizedCardY * screenHeight
        return imageScaledPositionPickStartX.toInt() to imageScaledPositionPickStartY.toInt()
    }

    fun getScaledSize(imageHeight: Int, referenceX: Int, referenceY: Int): Pair<Int, Int> {
        val scaleFactor = imageHeight.toDouble() / TESLTracker.referenceConfig.SCREEN_REFERENCE_HEIGHT
        return (scaleFactor * referenceX).toInt() to (scaleFactor * referenceY).toInt()
    }

}