package com.ediposouza

import com.ediposouza.data.ArenaDHash
import com.ediposouza.util.images.ImageFuncs
import com.ediposouza.util.images.ReferenceConfig1366x768
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Created by ediposouza on 06/03/17.
 */
class ArenaRecognizeTests : BaseRecognizeTests() {

    @Before
    fun setUp() {
        ImageFuncs.referenceConfig = ReferenceConfig1366x768()
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
        val croppedImage = ImageFuncs.getArenaPicksRemainingCroppedImage(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, ArenaDHash.ARENA_SCREENS_DHASH)).isEqualTo(screen)
    }

    private fun recognizeArenaClassSelected(testFileName: String, cls: String) {
        val croppedImage = ImageFuncs.getArenaClassSelectedCroppedImage(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, ArenaDHash.CLASS_SELECTED_DHASH)).isEqualTo(cls)
    }

}