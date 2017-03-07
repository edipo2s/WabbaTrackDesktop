package com.ediposouza.util

import java.awt.image.BufferedImage

/**
 * Created by ediposouza on 06/03/17.
 */
object Recognition {

    const val PHASH_SIZE = 10

    fun recognize(image: BufferedImage): String {
        val dHash = calcDHash(image)
        var cardShortName = ""
        var lessDistance = Int.MAX_VALUE
        CardsDHash.CARDS_DHASH.forEach {
            val dHashDistance = calcDHashDistance(dHash, it.value)
            Logger.d(" -- $dHashDistance from ${it.key}")
            if (dHashDistance < lessDistance) {
                cardShortName = it.key
                lessDistance = dHashDistance
            }
        }
        return cardShortName
    }

    fun calcDHash(image: BufferedImage): String {
        val grayImage = ImageFuncs.toGrayscale(ImageFuncs.getScaledImage(image))
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

}