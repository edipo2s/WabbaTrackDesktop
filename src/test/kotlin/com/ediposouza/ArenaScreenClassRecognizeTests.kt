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
        TESLTracker.referenceConfig = ReferenceConfig1366x768()
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
        recognizeScreen("ArenaClass/ClassCrusader.png", DHash.SCREEN_ARENA_CLASS_SELECT, BufferedImage::getScreenArenaClassSelectCrop)
    }

    @Test
    fun testScreenArenaPicks() {
        recognizeScreen("ArenaPick/ArenaPick01.png", DHash.SCREEN_ARENA_PICK_01, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick02.png", DHash.SCREEN_ARENA_PICK_02, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick03.png", DHash.SCREEN_ARENA_PICK_03, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick04.png", DHash.SCREEN_ARENA_PICK_04, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick05.png", DHash.SCREEN_ARENA_PICK_05, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick06.png", DHash.SCREEN_ARENA_PICK_06, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick07.png", DHash.SCREEN_ARENA_PICK_07, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick08.png", DHash.SCREEN_ARENA_PICK_08, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick09.png", DHash.SCREEN_ARENA_PICK_09, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick10.png", DHash.SCREEN_ARENA_PICK_10, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick11.png", DHash.SCREEN_ARENA_PICK_11, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick12.png", DHash.SCREEN_ARENA_PICK_12, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick13.png", DHash.SCREEN_ARENA_PICK_13, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick14.png", DHash.SCREEN_ARENA_PICK_14, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick15.png", DHash.SCREEN_ARENA_PICK_15, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick16.png", DHash.SCREEN_ARENA_PICK_16, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick17.png", DHash.SCREEN_ARENA_PICK_17, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick18.png", DHash.SCREEN_ARENA_PICK_18, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick19.png", DHash.SCREEN_ARENA_PICK_19, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick20.png", DHash.SCREEN_ARENA_PICK_20, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick21.png", DHash.SCREEN_ARENA_PICK_21, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick22.png", DHash.SCREEN_ARENA_PICK_22, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick23.png", DHash.SCREEN_ARENA_PICK_23, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick24.png", DHash.SCREEN_ARENA_PICK_24, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick25.png", DHash.SCREEN_ARENA_PICK_25, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick26.png", DHash.SCREEN_ARENA_PICK_26, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick27.png", DHash.SCREEN_ARENA_PICK_27, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick28.png", DHash.SCREEN_ARENA_PICK_28, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick29.png", DHash.SCREEN_ARENA_PICK_29, BufferedImage::getScreenArenaPickCrop)
        recognizeScreen("ArenaPick/ArenaPick30.png", DHash.SCREEN_ARENA_PICK_30, BufferedImage::getScreenArenaPickCrop)
    }

    @Test
    fun testScreenArenaDash() {
        recognizeScreen("ScreenArenaDash.png", DHash.SCREEN_ARENA_DASHBOARD, BufferedImage::getScreenArenaDashboardCrop)
    }

    @Test
    fun testArenaClassSelectedAssassin() {
        recognizeArenaClassSelected("ArenaClass/ClassAssassin.png", "Assassin")
    }

    @Test
    fun testArenaClassSelectedCrusader() {
        recognizeArenaClassSelected("ArenaClass/ClassCrusader.png", "Crusader")
    }

    @Test
    fun testArenaClassSelectedMage() {
        recognizeArenaClassSelected("ArenaClass/ClassMage.png", "Mage")
    }

    @Test
    fun testArenaClassSelectedScout() {
        recognizeArenaClassSelected("ArenaClass/ClassScout.png", "Scout")
    }

    @Test
    fun testArenaClassSelectedSorcerer() {
        recognizeArenaClassSelected("ArenaClass/ClassSorcerer.png", "Sorcerer")
    }

    @Test
    fun testArenaClassSelectedSpellword() {
        recognizeArenaClassSelected("ArenaClass/ClassSpellword.png", "Spellword")
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