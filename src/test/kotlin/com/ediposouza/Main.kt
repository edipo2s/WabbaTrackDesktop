package com.ediposouza

import com.ediposouza.util.*
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by ediposouza on 06/03/17.
 */
class MainTests {

    @Before
    fun setUp() {
        ImageFuncs.referenceConfig = ReferenceConfig1366x768()
    }

    @Test
    fun testArenaPick1024x768() {
        val image = ImageIO.read(javaClass.getResource("/Test/Pick1024x768.png"))
        recognizeArenaPick(image, "gardenerofswords", "elusiveschemer", "improvisedweapon")
    }

    @Test
    fun testArenaPick1280x768() {
        val image = ImageIO.read(javaClass.getResource("/Test/Pick1280x768.png"))
        recognizeArenaPick(image, "gardenerofswords", "elusiveschemer", "improvisedweapon")
    }

    @Test
    fun testArenaPick1360x768() {
        val image = ImageIO.read(javaClass.getResource("/Test/Pick1360x768.png"))
        recognizeArenaPick(image, "fharundefender", "treeminder", "execute")
    }

    @Test
    fun testArenaPick1366x768() {
        val image = ImageIO.read(javaClass.getResource("/Test/Pick1366x768.png"))
        recognizeArenaPick(image, "fifthlegiontrainer", "septimguardsman", "snowwolf")
    }

    @Test
    fun testArenaPick1024x768WithAlternativeReferenceConfig() {
        ImageFuncs.referenceConfig = ReferenceConfig1024x768()
        val image = ImageIO.read(javaClass.getResource("/Test/Pick1024x768.png"))
        recognizeArenaPick(image, "gardenerofswords", "elusiveschemer", "improvisedweapon")
    }

    @Test
    fun testArenaPick1280x768WithAlternativeReferenceConfig() {
        ImageFuncs.referenceConfig = ReferenceConfig1024x768()
        val image = ImageIO.read(javaClass.getResource("/Test/Pick1280x768.png"))
        recognizeArenaPick(image, "gardenerofswords", "elusiveschemer", "improvisedweapon")
    }

    @Test
    fun testArenaPick1360x768WithAlternativeReferenceConfig() {
        ImageFuncs.referenceConfig = ReferenceConfig1024x768()
        val image = ImageIO.read(javaClass.getResource("/Test/Pick1360x768.png"))
        recognizeArenaPick(image, "fharundefender", "treeminder", "execute")
    }

    @Test
    fun testArenaPick1366x768WithAlternativeReferenceConfig() {
        ImageFuncs.referenceConfig = ReferenceConfig1024x768()
        val image = ImageIO.read(javaClass.getResource("/Test/Pick1366x768.png"))
        recognizeArenaPick(image, "fifthlegiontrainer", "septimguardsman", "snowwolf")
    }

    private fun recognizeArenaPick(image: BufferedImage, card1: String, card2: String, card3: String) {
        Logger.d("Using ${ImageFuncs.referenceConfig.SCREEN_REFERENCE} as screen reference")
        Logger.d("Image size: ${image.width}x${image.height}")
        assertThat(recognizeCard(ImageFuncs.getArenaPickImageScaled(image, 1))).isEqualTo(card1)
        assertThat(recognizeCard(ImageFuncs.getArenaPickImageScaled(image, 2))).isEqualTo(card2)
        assertThat(recognizeCard(ImageFuncs.getArenaPickImageScaled(image, 3))).isEqualTo(card3)
    }

    private fun recognizeCard(cardImage: BufferedImage, outputFile: Boolean = false): String {
        if (outputFile) {
            val tmpFileName = "recognize_${System.currentTimeMillis()}.png"
            File("src/main/resources/Test/Tmp").apply {
                if (!exists()) {
                    mkdir()
                }
            }
            ImageIO.write(cardImage, "png", File("src/main/resources/Test/Tmp/$tmpFileName"))
        }
        return Recognition.recognizeDHash(Recognition.calcDHash(cardImage))
    }

}