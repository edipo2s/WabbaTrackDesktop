package com.ediposouza.util

import com.ediposouza.data.DHashCards
import com.ediposouza.data.PHash
import com.ediposouza.extensions.*
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.awt.image.BufferedImage

/**
 * Created by ediposouza on 06/03/17.
 */
open class BaseResolutionTests : BaseRecognizeTests() {

    open val arenaPickClass = ""
    open val initialCardsDraw1 = ""
    open val initialCardsDraw2 = ""
    open val initialCardsDraw3 = ""
    open val gameCardDraw = ""
    open val gameCardDrawProphecy = ""
    open val gameOpponentClass_CardDraw = ""
    open val gameOpponentClass_PlayFirst = ""
    open val gamePlayerClass_CardDraw = ""
    open val gamePlayerClass_PlayFirst = ""
    open val gamePlayerRank_Player = ""
    open val gamePlayerRank_Opponent = ""

    open val arenaPicks1 = ""
    open val arenaPicks2 = ""
    open val arenaPicks3 = ""
    open val deckBuilderCards1 = "improvisedweapon"
    open val deckBuilderCards2 = "nordfirebrand"
    open val deckBuilderCards3 = "covenantmarauder"
    open val deckBuilderCards4 = "fieryimp"
    open val deckBuilderCards5 = "intimidate"

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
    fun testScreenDeckBuilder() {
        recognizeScreen("ScreenDeckBuilderEmpty.png", PHash.SCREEN_DECK_BUILDER, BufferedImage::getScreenDeckBuilderCrop)
        recognizeScreen("ScreenDeckBuilderEmpty.png", PHash.SCREEN_DECK_BUILDER_EMPTY, BufferedImage::getScreenDeckBuilderEmptyCrop)
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
    open fun testArenaPickClass() {
        recognizeArenaPickClass("ScreenArenaPicks.png", arenaPickClass)
    }

    protected fun recognizeScreen(testFileName: String, screen: String?, cropFun: (BufferedImage) -> BufferedImage?) {
        cropFun(getFileImage(testFileName))?.apply {
            assertThat(recognizeImage(this, PHash.SCREENS_LIST)).isEqualTo(screen)
        }
    }

    protected fun recognizeArenaPickClass(testFileName: String, cls: String) {
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
    open fun testResult() {
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
    open fun testInitialCardsDraw() {
        var croppedImage = getFileImage("Game/InitialCardsDraw.png").getGameInitialCardDrawCrop(1)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo(initialCardsDraw1)
        croppedImage = getFileImage("Game/InitialCardsDraw.png").getGameInitialCardDrawCrop(2)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo(initialCardsDraw2)
        croppedImage = getFileImage("Game/InitialCardsDraw.png").getGameInitialCardDrawCrop(3)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo(initialCardsDraw3)
    }

    @Test
    open fun testGameCardDraw() {
        val croppedImage = getFileImage("Game/CardDraw.png").getGameCardDrawCrop()
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo(gameCardDraw)
    }

    @Test
    fun testGameCardDrawProphecy() {
        val croppedImage = getFileImage("Game/CardProphecy.png").getGameCardDrawProphecyCrop()
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo(gameCardDrawProphecy)
    }

    @Test
    open fun testGameOpponentClass() {
        recognizeOpponent("Game/CardDraw.png", gameOpponentClass_CardDraw, BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/PlayFirst.png", gameOpponentClass_PlayFirst, BufferedImage::getGameOpponentClassCrop)
    }

    @Test
    open fun testGamePlayerClass() {
        recognizePlayer("Game/CardDraw.png", gamePlayerClass_CardDraw, BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayFirst.png", gamePlayerClass_PlayFirst, BufferedImage::getGamePlayerClassCrop)
    }

    @Test
    open fun testGamePlayerRank() {
        var croppedImage = getFileImage("Game/Rank.png").getGamePlayerRankCrop().apply { saveCroppedImage() }
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo(gamePlayerRank_Player)
        croppedImage = getFileImage("Game/Rank.png").getGameOpponentRankCrop().apply { saveCroppedImage() }
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo(gamePlayerRank_Opponent)
    }

    protected fun recognizeOpponent(testFileName: String, result: String, crop: (BufferedImage) -> BufferedImage) {
        val croppedImage = crop(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, PHash.GAME_OPPONENT_CLASS_LIST)).isEqualTo(result)
    }

    protected fun recognizePlayer(testFileName: String, result: String, crop: (BufferedImage) -> BufferedImage) {
        val croppedImage = crop(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_CLASS_LIST)).isEqualTo(result)
    }

    // -- Arena Tests --

    @Test
    fun testArenaPicks() {
        recognizeArenaPick("ScreenArenaPicks.png", arenaPicks1, arenaPicks2, arenaPicks3)
    }

    private fun recognizeArenaPick(testFileName: String, card1: String, card2: String, card3: String) {
        val croppedImage1 = getFileImage(testFileName).getArenaCardCrop(1)
        assertThat(Recognizer.recognizeCardImage(croppedImage1)).isEqualTo(card1)
        val croppedImage2 = getFileImage(testFileName).getArenaCardCrop(2)
        assertThat(Recognizer.recognizeCardImage(croppedImage2)).isEqualTo(card2)
        val croppedImage3 = getFileImage(testFileName).getArenaCardCrop(3)
        assertThat(Recognizer.recognizeCardImage(croppedImage3)).isEqualTo(card3)
    }

    // -- Deck Tests --

    @Test
    open fun testDeckBuilderCards() {
        val croppedImage1 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(1)
        assertThat(Recognizer.recognizeCardImage(croppedImage1)).isEqualTo(deckBuilderCards1)
        val croppedImage2 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(2)
        assertThat(Recognizer.recognizeCardImage(croppedImage2)).isEqualTo(deckBuilderCards2)
        val croppedImage3 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(3)
        assertThat(Recognizer.recognizeCardImage(croppedImage3)).isEqualTo(deckBuilderCards3)
        val croppedImage4 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(4)
        assertThat(Recognizer.recognizeCardImage(croppedImage4)).isEqualTo(deckBuilderCards4)
        val croppedImage5 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(5)
        assertThat(Recognizer.recognizeCardImage(croppedImage5)).isEqualTo(deckBuilderCards5)
    }

    @Test
    fun testDeckBuilderNoneLeftCard() {
        val croppedImage1 = getFileImage("Deck/DeckBuilderNoneLeft.png").getDeckBuilderNoneLeftCardCrop(1)
        assertThat(Recognizer.recognizeImageInMap(croppedImage1, PHash.DECK_ITEMS_LIST)).isNotEqualTo(PHash.DECK_ITEM_NONE_LEFT)
        val croppedImage2 = getFileImage("Deck/DeckBuilderNoneLeft.png").getDeckBuilderNoneLeftCardCrop(2)
        assertThat(Recognizer.recognizeImageInMap(croppedImage2, PHash.DECK_ITEMS_LIST)).isEqualTo(PHash.DECK_ITEM_NONE_LEFT)
        val croppedImage3 = getFileImage("Deck/DeckBuilderNoneLeft.png").getDeckBuilderNoneLeftCardCrop(3)
        assertThat(Recognizer.recognizeImageInMap(croppedImage3, PHash.DECK_ITEMS_LIST)).isNotEqualTo(PHash.DECK_ITEM_NONE_LEFT)
        val croppedImage4 = getFileImage("Deck/DeckBuilderNoneLeft.png").getDeckBuilderNoneLeftCardCrop(4)
        assertThat(Recognizer.recognizeImageInMap(croppedImage4, PHash.DECK_ITEMS_LIST)).isNotEqualTo(PHash.DECK_ITEM_NONE_LEFT)
        val croppedImage5 = getFileImage("Deck/DeckBuilderNoneLeft.png").getDeckBuilderNoneLeftCardCrop(5)
        assertThat(Recognizer.recognizeImageInMap(croppedImage5, PHash.DECK_ITEMS_LIST)).isNotEqualTo(PHash.DECK_ITEM_NONE_LEFT)
    }

}