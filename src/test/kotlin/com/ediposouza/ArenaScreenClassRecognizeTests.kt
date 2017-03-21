package com.ediposouza

import com.ediposouza.data.ScreensDHash
import com.ediposouza.extensions.getArenaClassSelectedCroppedImage
import com.ediposouza.extensions.getArenaPicksRemainingCroppedImage
import com.ediposouza.util.images.ReferenceConfig1366x768
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Created by ediposouza on 06/03/17.
 */
class ArenaScreenClassRecognizeTests : BaseRecognizeTests() {

    @Before
    fun setUp() {
        App.referenceConfig = ReferenceConfig1366x768()
    }

    @Test
    fun testArenaPicksRemainig() {
        recognizeArenaScreen("Pick1024x768.png", "Pick")
    }

    @Test
    fun testArenaClassSelectedAssassin() {
        recognizeArenaClassSelected("ClassAssassin.png", "Assassin")
    }

    @Test
    fun testArenaClassSelectedCrusader() {
        recognizeArenaClassSelected("ClassCrusader.png", "Crusader")
    }

    @Test
    fun testArenaClassSelectedMage() {
        recognizeArenaClassSelected("ClassMage.png", "Mage")
    }

    @Test
    fun testArenaClassSelectedScout() {
        recognizeArenaClassSelected("ClassScout.png", "Scout")
    }

    @Test
    fun testArenaClassSelectedSorcerer() {
        recognizeArenaClassSelected("ClassSorcerer.png", "Sorcerer")
    }

    @Test
    fun testArenaClassSelectedSpellword() {
        recognizeArenaClassSelected("ClassSpellword.png", "Spellword")
    }

    private fun recognizeArenaScreen(testFileName: String, screen: String) {
        val croppedImage = getFileImage(testFileName).getArenaPicksRemainingCroppedImage()
        assertThat(recognizeImage(croppedImage, ScreensDHash.SCREENS_DHASH)).isEqualTo(screen)
    }

    private fun recognizeArenaClassSelected(testFileName: String, cls: String) {
        val croppedImage = getFileImage(testFileName).getArenaClassSelectedCroppedImage()
        assertThat(recognizeImage(croppedImage, ScreensDHash.CLASS_SELECTED_DHASH)).isEqualTo(cls)
    }

}