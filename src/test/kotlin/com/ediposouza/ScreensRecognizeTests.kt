package com.ediposouza

import com.ediposouza.data.DHash
import com.ediposouza.extensions.*
import com.ediposouza.resolution.ReferenceConfig1366x768
import com.ediposouza.util.BaseRecognizeTests
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage

/**
 * Created by ediposouza on 06/03/17.
 */
class ScreenClassRecognizeTests : BaseRecognizeTests() {

    @Before
    fun setUp() {
        TESLTracker.referenceConfig = ReferenceConfig1366x768()
    }

    @Test
    fun testScreenMain() {
        recognizeScreen("ScreenMain.png", DHash.SCREEN_MAIN, BufferedImage::getScreenMainCrop)
    }

    @Test
    fun testScreenMainMode() {
        recognizeScreen("ScreenMainModeCasual.png", DHash.SCREEN_MAIN_MODE_CASUAL, BufferedImage::getScreenMainModeCrop)
        recognizeScreen("ScreenMainModeRanked.png", DHash.SCREEN_MAIN_MODE_RANKED, BufferedImage::getScreenMainModeCrop)
        recognizeScreen("ScreenMainModePratice.png", DHash.SCREEN_MAIN_MODE_PRATICE, BufferedImage::getScreenMainModeCrop)
        recognizeScreen("ScreenMainModePratice2.png", DHash.SCREEN_MAIN_MODE_PRATICE, BufferedImage::getScreenMainModeCrop)
    }

    @Test
    fun testScreenGame() {
        recognizeScreen("Game/PlayFirst.png", DHash.SCREEN_GAME, BufferedImage::getScreenGameCrop)
    }

    @Test
    fun testScreenArenaClasses() {
        recognizeScreen("ScreenArenaClasses.png", DHash.SCREEN_ARENA_CLASSES, BufferedImage::getScreenArenaClassesCrop)
    }

    @Test
    fun testScreenArenaPicks() {
        recognizeScreen("ScreenArenaPicks.png", DHash.SCREEN_ARENA_PICKS, BufferedImage::getScreenArenaPicksCrop)
    }

    @Test
    fun testScreenArenaDash() {
        recognizeScreen("ScreenArenaDash.png", DHash.SCREEN_ARENA_DASHBOARD, BufferedImage::getScreenArenaDashboardCrop)
    }

    @Test
    fun testArenaPickClass() {
        recognizeArenaPickClass("ArenaClass/PickArcher.png", "Archer")
        recognizeArenaPickClass("ArenaClass/PickAssassin.png", "Assassin")
        recognizeArenaPickClass("ArenaClass/PickBattlemage.png", "Battlemage")
        recognizeArenaPickClass("ArenaClass/PickCrusader.png", "Crusader")
        recognizeArenaPickClass("ArenaClass/PickMage.png", "Mage")
        recognizeArenaPickClass("ArenaClass/PickMonk.png", "Monk")
        recognizeArenaPickClass("ArenaClass/PickScout.png", "Scout")
        recognizeArenaPickClass("ArenaClass/PickSorcerer.png", "Sorcerer")
        recognizeArenaPickClass("ArenaClass/PickSpellsword.png", "Spellsword")
        recognizeArenaPickClass("ArenaClass/PickWarrior.png", "Warrior")
    }

    private fun recognizeScreen(testFileName: String, screen: String?, cropFun: (BufferedImage) -> BufferedImage?) {
        cropFun(getFileImage(testFileName))?.apply {
            assertThat(recognizeImage(this, DHash.SCREENS_LIST)).isEqualTo(screen)
        }
    }

    private fun recognizeArenaPickClass(testFileName: String, cls: String) {
        val croppedImage = getFileImage(testFileName).getArenaPickClassCrop()
        assertThat(recognizeImage(croppedImage, DHash.CLASS_PICK_LIST)).isEqualTo(cls)
    }

}