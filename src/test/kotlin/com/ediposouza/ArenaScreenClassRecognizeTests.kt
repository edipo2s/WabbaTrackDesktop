package com.ediposouza

import com.ediposouza.data.DHash
import com.ediposouza.extensions.*
import com.ediposouza.util.images.ReferenceConfig1366x768
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage

/**
 * Created by ediposouza on 06/03/17.
 */
class ArenaScreenClassRecognizeTests : BaseRecognizeTests() {

    @Before
    fun setUp() {
        App.referenceConfig = ReferenceConfig1366x768()
    }

    @Test
    fun testScreenMain() {
        recognizeScreen("ScreenMain.png", DHash.SCREEN_MAIN, BufferedImage::getScreenMainCrop)
    }

    @Test
    fun testScreenGame() {
        recognizeScreen("ScreenGame.png", DHash.SCREEN_GAME, BufferedImage::getScreenGameCrop)
    }

    @Test
    fun testScreenArenaClasses() {
        recognizeScreen("ScreenArenaClasses.png", DHash.SCREEN_ARENA_CLASSES, BufferedImage::getScreenArenaClassesCrop)
    }

    @Test
    fun testScreenArenaClassSelect() {
        recognizeScreen("ClassCrusader.png", DHash.SCREEN_ARENA_CLASS_SELECT, BufferedImage::getScreenArenaClassSelectCrop)
    }

    @Test
    fun testScreenArenaPick() {
        recognizeScreen("Pick1024x768.png", DHash.SCREEN_ARENA_PICK, BufferedImage::getScreenArenaPickCrop)
    }

    @Test
    fun testScreenArenaDash() {
        recognizeScreen("ScreenArenaDash.png", DHash.SCREEN_ARENA_DASHBOARD, BufferedImage::getScreenArenaDashboardCrop)
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

    private fun recognizeScreen(testFileName: String, screen: String, cropFun: (BufferedImage) -> BufferedImage?) {
        cropFun(getFileImage(testFileName))?.apply {
            assertThat(recognizeImage(this, DHash.SCREENS_LIST, true)).isEqualTo(screen)
        }
    }

    private fun recognizeArenaClassSelected(testFileName: String, cls: String) {
        val croppedImage = getFileImage(testFileName).getArenaClassSelectCrop()
        assertThat(recognizeImage(croppedImage, DHash.CLASS_SELECTED_LIST)).isEqualTo(cls)
    }

}