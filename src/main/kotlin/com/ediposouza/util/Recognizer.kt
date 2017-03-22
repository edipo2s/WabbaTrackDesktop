package com.ediposouza.util

import com.ediposouza.data.DHash
import com.ediposouza.data.DHashCards
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp

/**
 * Created by ediposouza on 06/03/17.
 */
object Recognizer {

    const val DHASH_SIZE = 16
    const val DHASH_MAX_DISTANCE = 60
    const val DHASH_DISTANCE_TOLERANCE_SUPER_HIGH = DHASH_MAX_DISTANCE * 0.15
    const val DHASH_DISTANCE_TOLERANCE_HIGH = DHASH_MAX_DISTANCE * 0.85
    const val DHASH_DISTANCE_TOLERANCE_LOW = DHASH_MAX_DISTANCE * 0.5

    fun recognizeCardImage(image: BufferedImage): String? {
        return recognizeImageInMap(image, DHashCards.LIST, true)
    }

    fun recognizeScreenImage(image: BufferedImage, highTolerance: Boolean = false): String? {
        return recognizeImageInMap(image, DHash.SCREENS_LIST, highTolerance)
    }

    fun recognizeArenaClassSelectImage(image: BufferedImage): String? {
        return recognizeImageInMap(image, DHash.CLASS_SELECTED_LIST)
    }

    fun recognizeImageInMap(image: BufferedImage, dHashMap: Map<String, String>, highTolerance: Boolean = false): String? {
        val result = recognizeDHashInMap(calcDHash(image), dHashMap, highTolerance)
        Logger.d("${result.first} - ${result.second})")
        return result.first
    }

    fun recognizeDHashInMap(dHash: String, dHashMap: Map<String, String>, highTolerance: Boolean = false): Pair<String?, Int> {
        var cardShortName = ""
        var lessDistance = Int.MAX_VALUE
        dHashMap.forEach {
            val dHashDistance = calcDHashDistance(dHash, it.value)
            val tolerance = DHASH_DISTANCE_TOLERANCE_HIGH.takeIf { highTolerance } ?: DHASH_DISTANCE_TOLERANCE_LOW
//            Logger.d(" -- $dHashDistance from ${it.key}")
            if (dHashDistance < tolerance && dHashDistance < lessDistance) {
                cardShortName = it.key
                lessDistance = dHashDistance
            }
        }
        return cardShortName.takeIf(String::isNotEmpty) to lessDistance
    }

    fun calcDHash(image: BufferedImage): String {
        val grayImage = toGrayscale(getScaledImage(image))
        val difference = mutableListOf<Boolean>().apply {
            for (x in 0..DHASH_SIZE - 1) {
                for (y in 0..DHASH_SIZE - 2) {
                    val px = grayImage.getRGB(y, x)
                    val py = grayImage.getRGB(y + 1, x)
                    add(px > py)
                }
            }
        }

        var decimalValue = 0
        var result = ""
        for (i in 0..difference.size - 1) {
            if (difference[i]) {
                decimalValue += Math.pow(2.0, i.rem(8.0)).toInt()
            }
            if (i.rem(8.0) == 7.0) {
                val hex = String.format("%02x", decimalValue)
                result = String.format("%s%s", result, hex)
                decimalValue = 0
            }
        }
        return result
    }

    fun isScreenshotDifferent(screenshot1Hash: String, screenshot2Hash: String): Boolean {
        if (screenshot1Hash.length != screenshot2Hash.length) {
            return true
        }
        val calcDHashDistance = calcDHashDistance(screenshot1Hash, screenshot2Hash)
        Logger.d("Different distance: $calcDHashDistance")
        return calcDHashDistance > DHASH_DISTANCE_TOLERANCE_SUPER_HIGH
    }

    private fun calcDHashDistance(s1: String, s2: String): Int {
        if (s1.length != s2.length) {
            Logger.d("Different sizes: ${s1.length}, ${s2.length}")
            throw IllegalArgumentException()
        }
        return (0..s1.length - 1).count { s1[it] != s2[it] }
    }

    fun getScaledImage(image: BufferedImage): BufferedImage {
        val tmp = image.getScaledInstance(DHASH_SIZE, DHASH_SIZE, BufferedImage.SCALE_FAST)
        val scaledImage = BufferedImage(DHASH_SIZE, DHASH_SIZE, BufferedImage.TYPE_INT_RGB)
        scaledImage.graphics.drawImage(tmp, 0, 0, null)
        return scaledImage
    }

    fun toGrayscale(image: BufferedImage): BufferedImage {
        val cs = ColorSpace.getInstance(ColorSpace.CS_GRAY)
        return ColorConvertOp(cs, null).filter(image, null)
    }

}