package com.ediposouza.util

import com.ediposouza.TESLTracker
import java.awt.Dimension
import java.awt.Point
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

    fun getScreenScaledPosition(referenceX: Int, referenceY: Int): Point {
        val screenBounds = TESLTracker.screenSize
        return getScaledPosition(screenBounds.width.toInt(), screenBounds.height.toInt(), referenceX, referenceY)
    }

    fun getScreenScaledSize(referenceX: Int, referenceY: Int): Dimension {
        val screenBounds = TESLTracker.screenSize
        return getScaledSize(screenBounds.height.toInt(), referenceX, referenceY)
    }

    fun getScaledPosition(screenWidth: Int, screenHeight: Int, referenceX: Int, referenceY: Int): Point {
        val normalizedCardX = referenceX / TESLTracker.referenceConfig.SCREEN_REFERENCE_WIDTH.toDouble()
        val normalizedCardY = referenceY / TESLTracker.referenceConfig.SCREEN_REFERENCE_HEIGHT.toDouble()

        val ratio = TESLTracker.referenceConfig.SCREEN_REFERENCE_RATIO / (screenWidth.toDouble() / screenHeight)
        val imageScaledPositionPickStartX = (screenWidth * ratio * normalizedCardX) + (screenWidth.toDouble() * (1 - ratio) / 2)
        val imageScaledPositionPickStartY = normalizedCardY * screenHeight
        return Point(imageScaledPositionPickStartX.toInt(), imageScaledPositionPickStartY.toInt())
    }

    fun getScaledSize(imageHeight: Int, referenceX: Int, referenceY: Int): Dimension {
        val scaleFactor = imageHeight.toDouble() / TESLTracker.referenceConfig.SCREEN_REFERENCE_HEIGHT
        return Dimension((scaleFactor * referenceX).toInt(), (scaleFactor * referenceY).toInt())
    }

}