package com.ediposouza

import com.ediposouza.data.DHashCards
import com.ediposouza.data.PHash
import com.ediposouza.extensions.*
import com.ediposouza.resolution.ReferenceConfig1366x768
import com.ediposouza.util.BaseRecognizeTests
import com.ediposouza.util.Recognizer
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage

/**
 * Created by ediposouza on 06/03/17.
 */
class Tests1366x768 : BaseRecognizeTests() {

    @Before
    fun setUp() {
        TESLTracker.referenceConfig = ReferenceConfig1366x768()
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
        var croppedImage = getFileImage("Game/win.png").getGameWinCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_ITEMS_LIST)).isEqualTo(PHash.GAME_WIN)
        croppedImage = getFileImage("Game/win2.png").getGameWin2Crop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_ITEMS_LIST)).isEqualTo(PHash.GAME_WIN2)
        croppedImage = getFileImage("Game/loss.png").getGameLossCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_ITEMS_LIST)).isEqualTo(PHash.GAME_LOSS)
        croppedImage = getFileImage("Game/loss2.png").getGameLoss2Crop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_ITEMS_LIST)).isEqualTo(PHash.GAME_LOSS2)
    }

    @Test
    fun testInitialCardsDraw() {
        var croppedImage = getFileImage("Game/InitialCardsDraw.png").getGameInitialCardDrawCrop(1)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo("elusiveschemer")
        croppedImage = getFileImage("Game/InitialCardsDraw.png").getGameInitialCardDrawCrop(2)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo("blacksapprotector")
        croppedImage = getFileImage("Game/InitialCardsDraw.png").getGameInitialCardDrawCrop(3)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo("blacksapprotector")
    }

    @Test
    fun testGameCardDraw() {
        val croppedImage = getFileImage("Game/CardDraw.png").getGameCardDrawCrop()
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo("quinrawlburglar")
    }

    @Test
    fun testGameCardDrawProphecy() {
        val croppedImage = getFileImage("Game/CardProphecy.png").getGameCardDrawProphecyCrop()
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo("blacksapprotector")
    }

    @Test
    fun testGameOpponentClass() {
        recognizeOpponent("Game/OpponentClass/OpponentAgility.png", "Agility", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentArcher.png", "Archer", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentAssassin.png", "Assassin", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentBattlemage.png", "Battlemage", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentCrusader.png", "Crusader", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentEndurance.png", "Endurance", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentIntelligence.png", "Intelligence", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentMage.png", "Mage", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentMonk.png", "Monk", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentNeutral.png", "Neutral", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentScout.png", "Scout", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentSorcerer.png", "Sorcerer", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentSpellsword.png", "Spellsword", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentStrength.png", "Strength", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentWarrior.png", "Warrior", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentClass/OpponentWillpower.png", "Willpower", BufferedImage::getGameOpponentClassCrop)
    }

    @Test
    fun testGamePlayerClass() {
        recognizePlayer("Game/PlayerClass/PlayerAgility.png", "Agility", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerArcher.png", "Archer", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerAssassin.png", "Assassin", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerBattlemage.png", "Battlemage", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerCrusader.png", "Crusader", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerEndurance.png", "Endurance", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerIntelligence.png", "Intelligence", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerMage.png", "Mage", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerMonk.png", "Monk", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerNeutral.png", "Neutral", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerScout.png", "Scout", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerSorcerer.png", "Sorcerer", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerSpellsword.png", "Spellsword", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerStrength.png", "Strength", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerWarrior.png", "Warrior", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerClass/PlayerWillpower.png", "Willpower", BufferedImage::getGamePlayerClassCrop)
    }

    @Test
    fun testGamePlayerRank() {
        var croppedImage = getFileImage("Game/PlayerRank/rankLegend.png").getGameOpponentRankCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo("0")
        croppedImage = getFileImage("Game/PlayerRank/rank1.png").getGamePlayerRankCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo("3")
        croppedImage = getFileImage("Game/PlayerRank/rank1.png").getGameOpponentRankCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo("1")
        croppedImage = getFileImage("Game/PlayerRank/rank2.png").getGameOpponentRankCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo("2")
        croppedImage = getFileImage("Game/PlayerRank/rank3.png").getGameOpponentRankCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo("3")
        croppedImage = getFileImage("Game/PlayerRank/rank4.png").getGameOpponentRankCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo("4")
        croppedImage = getFileImage("Game/PlayerRank/rank5.png").getGameOpponentRankCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo("5")
        croppedImage = getFileImage("Game/PlayerRank/rank6.png").getGameOpponentRankCrop()
        assertThat(recognizeImage(croppedImage, PHash.GAME_PLAYER_RANK_LIST)).isEqualTo("6")
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
        recognizeArenaPick("ScreenArenaPicks.png", "giantsnake", "spiderlair", "brilliantexperiment")
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