package com.ediposouza

import com.ediposouza.util.CardsDHash
import com.ediposouza.util.ImageFuncs
import com.ediposouza.util.ReferenceConfig1024x768
import com.ediposouza.util.ReferenceConfig1366x768
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Created by ediposouza on 06/03/17.
 */
class ArenaCardPickRecognizeTests : BaseRecognizeTests() {

    @Before
    fun setUp() {
        ImageFuncs.referenceConfig = ReferenceConfig1366x768()
    }

    @Test
    fun testArenaPick1024x768() {
        recognizeArenaPick("Pick1024x768.png", "gardenerofswords", "elusiveschemer", "improvisedweapon")
    }

    @Test
    fun testArenaPick1280x768() {
        recognizeArenaPick("Pick1280x768.png", "gardenerofswords", "elusiveschemer", "improvisedweapon")
    }

    @Test
    fun testArenaPick1360x768() {
        recognizeArenaPick("Pick1360x768.png", "fharundefender", "treeminder", "execute")
    }

    @Test
    fun testArenaPick1366x768() {
        recognizeArenaPick("Pick1366x768.png", "fifthlegiontrainer", "septimguardsman", "snowwolf")
    }

    @Test
    fun testArenaPick1024x768WithAlternativeReferenceConfig() {
        ImageFuncs.referenceConfig = ReferenceConfig1024x768()
        recognizeArenaPick("Pick1024x768.png", "gardenerofswords", "elusiveschemer", "improvisedweapon")
    }

    @Test
    fun testArenaPick1280x768WithAlternativeReferenceConfig() {
        ImageFuncs.referenceConfig = ReferenceConfig1024x768()
        recognizeArenaPick("Pick1280x768.png", "gardenerofswords", "elusiveschemer", "improvisedweapon")
    }

    @Test
    fun testArenaPick1360x768WithAlternativeReferenceConfig() {
        ImageFuncs.referenceConfig = ReferenceConfig1024x768()
        recognizeArenaPick("Pick1360x768.png", "fharundefender", "treeminder", "execute")
    }

    @Test
    fun testArenaPick1366x768WithAlternativeReferenceConfig() {
        ImageFuncs.referenceConfig = ReferenceConfig1024x768()
        recognizeArenaPick("Pick1366x768.png", "fifthlegiontrainer", "septimguardsman", "snowwolf")
    }

    private fun recognizeArenaPick(testFileName: String, card1: String, card2: String, card3: String) {
        val croppedImage1 = ImageFuncs.getArenaCardCropped(getFileImage(testFileName), 1)
        assertThat(recognizeImage(croppedImage1, CardsDHash.CARDS_DHASH)).isEqualTo(card1)
        val croppedImage2 = ImageFuncs.getArenaCardCropped(getFileImage(testFileName), 2)
        assertThat(recognizeImage(croppedImage2, CardsDHash.CARDS_DHASH)).isEqualTo(card2)
        val croppedImage3 = ImageFuncs.getArenaCardCropped(getFileImage(testFileName), 3)
        assertThat(recognizeImage(croppedImage3, CardsDHash.CARDS_DHASH)).isEqualTo(card3)
    }

}