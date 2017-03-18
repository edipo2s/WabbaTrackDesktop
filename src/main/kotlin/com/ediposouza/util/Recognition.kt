package com.ediposouza.util

import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp

/**
 * Created by ediposouza on 06/03/17.
 */
object Recognition {

    const val PHASH_SIZE = 16

    fun recognizeCardDHash(dHash: String): String {
        return recognizeDHashInMap(dHash, CardsDHash.CARDS_DHASH)
    }

    fun recognizeDHashInMap(dHash: String, dHashMap: Map<String, String>): String {
        var cardShortName = ""
        var lessDistance = Int.MAX_VALUE
        dHashMap.forEach {
            val dHashDistance = calcDHashDistance(dHash, it.value)
            if (dHashDistance < lessDistance) {
                cardShortName = it.key
                lessDistance = dHashDistance
            }
        }
        return cardShortName
    }

    fun calcDHash(image: BufferedImage): String {
        val grayImage = toGrayscale(getScaledImage(image))
        val difference = mutableListOf<Boolean>().apply {
            for (x in 0..PHASH_SIZE - 1) {
                for (y in 0..PHASH_SIZE - 2) {
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

    private fun calcDHashDistance(s1: String, s2: String): Int {
        if (s1.length != s2.length) {
            throw IllegalArgumentException()
        }
        return (0..s1.length - 1).count { s1[it] != s2[it] }
    }

    fun getScaledImage(image: BufferedImage): BufferedImage {
        val tmp = image.getScaledInstance(Recognition.PHASH_SIZE, Recognition.PHASH_SIZE, BufferedImage.SCALE_FAST)
        val scaledImage = BufferedImage(Recognition.PHASH_SIZE, Recognition.PHASH_SIZE, BufferedImage.TYPE_INT_RGB)
        scaledImage.graphics.drawImage(tmp, 0, 0, null)
        return scaledImage
    }

    fun toGrayscale(image: BufferedImage): BufferedImage {
        val cs = ColorSpace.getInstance(ColorSpace.CS_GRAY)
        return ColorConvertOp(cs, null).filter(image, null)
    }

}