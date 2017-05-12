package com.ediposouza

import com.ediposouza.data.DHashCards
import com.ediposouza.data.PHash
import com.ediposouza.extensions.*
import com.ediposouza.resolution.ReferenceConfig1920x1080
import com.ediposouza.util.BaseRecognizeTests
import com.ediposouza.util.Recognizer
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage

/**
 * Created by ediposouza on 06/03/17.
 */
class Tests1920x1080 : BaseRecognizeTests() {

    @Before
    fun setUp() {
        TESLTracker.referenceConfig = ReferenceConfig1920x1080()
    }

    @Test
    fun testScreenMain() {
        recognizeScreen("ScreenMain.png", PHash.SCREEN_MAIN, BufferedImage::getScreenMainCrop)
    }

    @Test
    fun testScreenMainMode() {
        recognizeScreen("ScreenMainModeCasual.png", PHash.SCREEN_MAIN_MODE_CASUAL, BufferedImage::getScreenMainModeCrop)
        recognizeScreen("ScreenMainModeRanked.png", PHash.SCREEN_MAIN_MODE_RANKED, BufferedImage::getScreenMainModeCrop)
        recognizeScreen("ScreenMainModePratice.png", PHash.SCREEN_MAIN_MODE_PRATICE, BufferedImage::getScreenMainModeCrop)
        recognizeScreen("ScreenMainModePratice2.png", PHash.SCREEN_MAIN_MODE_PRATICE, BufferedImage::getScreenMainModeCrop)
    }

    @Test
    fun testScreenGame() {
        recognizeScreen("Game/PlayFirst.png", PHash.SCREEN_GAME, BufferedImage::getScreenGameCrop)
    }

    @Test
    fun testScreenArenaClasses() {
        recognizeScreen("ScreenArenaClasses.png", PHash.SCREEN_ARENA_CLASSES, BufferedImage::getScreenArenaClassesCrop)
    }

    @Test
    fun testScreenArenaPicks() {
        recognizeScreen("ScreenArenaPicks.png", PHash.SCREEN_ARENA_PICKS, BufferedImage::getScreenArenaPicksCrop)
    }

    @Test
    fun testScreenArenaDash() {
        recognizeScreen("ScreenArenaDash.png", PHash.SCREEN_ARENA_DASHBOARD, BufferedImage::getScreenArenaDashboardCrop)
    }

    @Test
    fun testArenaPickClass() {
        recognizeArenaPickClass("ScreenArenaPicks.png", "Sorcerer")
    }

    private fun recognizeScreen(testFileName: String, screen: String?, cropFun: (BufferedImage) -> BufferedImage?) {
        cropFun(getFileImage(testFileName))?.apply {
            assertThat(recognizeImage(this, PHash.SCREENS_LIST)).isEqualTo(screen)
        }
    }

    private fun recognizeArenaPickClass(testFileName: String, cls: String) {
        val croppedImage = getFileImage(testFileName).getArenaPickClassCrop()
        assertThat(recognizeImage(croppedImage, PHash.CLASS_PICK_LIST)).isEqualTo(cls)
    }

    // -- Game Tests --

    @Test
    fun testRing() {
        var croppedImage = getFileImage("Game/PlayFirst.png").getGamePlayerFirstCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_ITEMS_LIST)).isEqualTo(PHash.PLAYER_GAME_FIRST)
        croppedImage = getFileImage("Game/PlaySecond.png").getGamePlayerSecondCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_ITEMS_LIST)).isEqualTo(PHash.PLAYER_GAME_SECOND)
    }

    @Test
    fun testResult() {
//        var croppedImage = getFileImage("Game/win.png").getGameWinCrop()
//        assertThat(recognizeImage(croppedImage, PHash.GAME_ITEMS_LIST)).isEqualTo(PHash.GAME_WIN)
        var croppedImage = getFileImage("Game/win2.png").getGameWin2Crop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_ITEMS_LIST)).isEqualTo(PHash.GAME_WIN2)
//        croppedImage = getFileImage("Game/loss.png").getGameLossCrop()
//        assertThat(recognizeImage(croppedImage, PHash.GAME_ITEMS_LIST)).isEqualTo(PHash.GAME_LOSS)
        croppedImage = getFileImage("Game/loss2.png").getGameLoss2Crop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_ITEMS_LIST)).isEqualTo(PHash.GAME_LOSS2)
    }

    @Test
    fun testInitialCardsDraw() {
        var croppedImage = getFileImage("Game/InitialCardsDraw.png").getGameInitialCardDrawCrop(1)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo("histgrove")
        croppedImage = getFileImage("Game/InitialCardsDraw.png").getGameInitialCardDrawCrop(2)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo("shadowfenpriest")
        croppedImage = getFileImage("Game/InitialCardsDraw.png").getGameInitialCardDrawCrop(3)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo("piercingjavelin")
    }

    @Test
    fun testGameCardDraw() {
        val croppedImage = getFileImage("Game/CardDraw.png").getGameCardDrawCrop()
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo("execute")
    }

    @Test
    fun testGameCardDrawProphecy() {
        val croppedImage = getFileImage("Game/CardProphecy.png").getGameCardDrawProphecyCrop()
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo("fateweaver")
    }

    @Test
    fun testGameOpponentClass() {
        recognizeOpponent("Game/CardDraw.png", "Strength", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/CardGenerated.png", "Archer", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/PlayFirst.png", "Battlemage", BufferedImage::getGameOpponentClassCrop)
    }

    @Test
    fun testGamePlayerClass() {
        recognizePlayer("Game/CardDraw.png", "Spellsword", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayFirst.png", "Assassin", BufferedImage::getGamePlayerClassCrop)
    }

    @Test
    fun testGamePlayerRank() {
        var croppedImage = getFileImage("Game/Rank.png").getGamePlayerRankCrop().apply { saveCroppedImage() }
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo("3")
        croppedImage = getFileImage("Game/Rank.png").getGameOpponentRankCrop().apply { saveCroppedImage() }
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo("4")
    }

    private fun recognizeOpponent(testFileName: String, result: String, crop: (BufferedImage) -> BufferedImage) {
        val croppedImage = crop(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, PHash.GAME_OPPONENT_CLASS_LIST)).isEqualTo(result)
    }

    private fun recognizePlayer(testFileName: String, result: String, crop: (BufferedImage) -> BufferedImage) {
        val croppedImage = crop(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_CLASS_LIST)).isEqualTo(result)
    }

    // -- Arena Tests --

    @Test
    fun testArenaPicks() {
        recognizeArenaPick("ScreenArenaPicks.png", "falkreathdefiler", "highrocksummoner", "watchcommander")
    }

    private fun recognizeArenaPick(testFileName: String, card1: String, card2: String, card3: String) {
        val croppedImage1 = getFileImage(testFileName).getArenaCardCrop(1)
        assertThat(Recognizer.recognizeCardImage(croppedImage1)).isEqualTo(card1)
        val croppedImage2 = getFileImage(testFileName).getArenaCardCrop(2)
        assertThat(Recognizer.recognizeCardImage(croppedImage2)).isEqualTo(card2)
        val croppedImage3 = getFileImage(testFileName).getArenaCardCrop(3)
        assertThat(Recognizer.recognizeCardImage(croppedImage3)).isEqualTo(card3)
    }

}