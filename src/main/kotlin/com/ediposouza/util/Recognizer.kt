package com.ediposouza.util

import com.ediposouza.data.DHashCards
import com.ediposouza.data.PHash
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.handler.StateHandler
import com.ediposouza.state.ArenaState
import com.ediposouza.state.CollectionState
import com.ediposouza.state.GameState
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp

/**
 * Created by ediposouza on 06/03/17.
 */
object Recognizer {

    private const val PHASH_SIZE = 32
    private const val PHASH_SMALLER_SIZE = 12
    private const val PHASH_SIMILARITY_THRESHOLD = 40
    private const val PHASH_SIMILARITY_HIGH_THRESHOLD = 5

    var c: Array<Double> = Array(PHASH_SIZE, { 0.0 })

    init {
        for (i in 1..PHASH_SIZE - 1) {
            c[i] = 1.0
        }
        c[0] = 1 / Math.sqrt(2.0)
    }

    fun recognizeCardImage(image: BufferedImage): String? {
        val classSelect = when (StateHandler.currentTESLState) {
            is CollectionState -> CollectionState.deckClass
            is GameState -> GameState.playerDeckClass
            is ArenaState -> ArenaState.classSelect
            else -> null
        }
        return classSelect?.let {
            val cardsFromClass = TESLTrackerData.getCardFromClass(it)
            val classPHash = DHashCards.LIST.filter { cardsFromClass.contains(it.value) }
//            Logger.d("Filtering dhash by $classSelect class with ${classPHash.size} cards")
            recognizeImageInMap(image, classPHash)
        } ?: recognizeImageInMap(image, DHashCards.LIST)
    }

    fun recognizeScreenImage(image: BufferedImage): String? {
        return recognizeImageInMap(image, PHash.SCREENS_LIST)
    }

    fun recognizeImageInMap(image: BufferedImage, hashMap: Map<String, String>): String? {
        val pHash = calcPHash(image)
        val pHashDistances = hashMap
                .map { it.value to calcHashDistance(pHash, it.key) }
                .filter { it.second < PHASH_SIMILARITY_THRESHOLD }
                .sortedBy { it.second }
//        pHashDistances.forEach { Logger.d("${it.second} - ${it.first}") }
        return pHashDistances.minBy { it.second }?.first?.removeSuffix("_alt")
    }

    fun isScreenshotDifferent(screenshot1Hash: String, screenshot2Hash: String): Boolean {
        if (screenshot1Hash.length != screenshot2Hash.length) {
            return true
        }
        val screenshotDistance = calcHashDistance(screenshot1Hash, screenshot2Hash)
//        Logger.d("Different distance: $screenshotDistance")
        return screenshotDistance > PHASH_SIMILARITY_HIGH_THRESHOLD
    }

    private fun calcHashDistance(s1: String, s2: String): Int {
        if (s1.length != s2.length) {
            Logger.d("Different sizes: ${s1.length}, ${s2.length}")
            throw IllegalArgumentException()
        }
        return (0..s1.length - 1).count { s1[it] != s2[it] }
    }

    fun getScaledImage(image: BufferedImage, size: Int): BufferedImage {
        val tmp = image.getScaledInstance(size, size, BufferedImage.SCALE_FAST)
        val scaledImage = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        scaledImage.graphics.drawImage(tmp, 0, 0, null)
        return scaledImage
    }

    fun toGrayscale(image: BufferedImage): BufferedImage {
        val cs = ColorSpace.getInstance(ColorSpace.CS_GRAY)
        return ColorConvertOp(cs, null).filter(image, null)
    }

//    -- PHash --

    fun calcPHash(image: BufferedImage): String {

        /* 1. Reduce size.
         * Like Average Hash, pHash starts with a small image.
         * However, the image is larger than 8x8; 32x32 is a good size.
         * This is really done to simplify the DCT computation and not
         * because it is needed to reduce the high frequencies.
         */
        /* 2. Reduce color.
         * The image is reduced to a grayscale just to further simplify
         * the number of computations.
         */
        var hash = ""
        val img = toGrayscale(getScaledImage(image, PHASH_SIZE))
        val vals = Array(img.width, { Array(img.height, { 0.0 }) })
        for (x in 0..img.width - 1) {
            for (y in 0..img.height - 1) {
                vals[x][y] = img.getRGB(x, y).toDouble()
            }
        }

        /* 3. Compute the DCT.
         * The DCT separates the image into a collection of frequencies
         * and scalars. While JPEG uses an 8x8 DCT, this algorithm uses
         * a 32x32 DCT.
         */
//        val start = System.currentTimeMillis()
        val dctVals = applyDCT(vals)
//        Logger.d("${System.currentTimeMillis() - start}")

        /* 4. Reduce the DCT.
         * This is the magic step. While the DCT is 32x32, just keep the
         * top-left 8x8. Those represent the lowest frequencies in the
         * picture.
         */
        /* 5. Compute the average value.
         * Like the Average Hash, compute the mean DCT value (using only
         * the 8x8 DCT low-frequency values and excluding the first term
         * since the DC coefficient can be significantly different from
         * the other values and will throw off the average).
         */
        var total = 0.0
        for (x in 0..PHASH_SMALLER_SIZE - 1) {
            for (y in 0..PHASH_SMALLER_SIZE - 1) {
                total += dctVals[x][y]
            }
        }
        total -= dctVals[0][0]
        val avg = total / ((PHASH_SMALLER_SIZE * PHASH_SMALLER_SIZE) - 1.0)

        /* 6. Further reduce the DCT.
         * This is the magic step. Set the 64 hash bits to 0 or 1
         * depending on whether each of the 64 DCT values is above or
         * below the average value. The result doesn't tell us the
         * actual low frequencies; it just tells us the very-rough
         * relative scale of the frequencies to the mean. The result
         * will not vary as long as the overall structure of the image
         * remains the same; this can survive gamma and color histogram
         * adjustments without a problem.
         */
        for (x in 0..PHASH_SMALLER_SIZE - 1) {
            for (y in 0..PHASH_SMALLER_SIZE - 1) {
                if (x != 0 && y != 0) {
                    hash += "1".takeIf { dctVals[x][y] > avg } ?: "0"
                }
            }
        }
        return hash
    }

    fun applyDCT(f: Array<Array<Double>>): Array<Array<Double>> {
        val N = PHASH_SIZE
        val F = Array(N, { Array(N, { 0.0 }) })
        for (u in 0..N - 1) {
            for (v in 0..N - 1) {
                var sum = 0.0
                for (i in 0..N - 1) {
                    for (j in 0..N - 1) {
                        sum += Math.cos(((2 * i + 1) / (2.0 * N)) * u * Math.PI) *
                                Math.cos(((2 * j + 1) / (2.0 * N)) * v * Math.PI) * (f[i][j])
                    }
                }
                sum *= ((c[u] * c[v]) / 4.0)
                F[u][v] = sum
            }
        }
        return F
    }

}