package com.ediposouza

import com.ediposouza.data.DHash
import com.ediposouza.data.DHashCards
import com.ediposouza.extensions.*
import com.ediposouza.util.BaseRecognizeTests
import com.ediposouza.util.Recognizer
import com.ediposouza.util.ReferenceConfig1920x1080
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
        recognizeScreen("ArenaPick/ArenaPick01.png", DHash.SCREEN_ARENA_PICKS, BufferedImage::getScreenArenaPicksCrop)
    }

    @Test
    fun testScreenArenaDash() {
        recognizeScreen("ScreenArenaDash.png", DHash.SCREEN_ARENA_DASHBOARD, BufferedImage::getScreenArenaDashboardCrop)
    }

    @Test
    fun testScreenArenaCardPicks() {
        recognizePickScreen("ArenaPick/ArenaPick01.png", DHash.SCREEN_ARENA_PICK_01, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick02.png", DHash.SCREEN_ARENA_PICK_02, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick03.png", DHash.SCREEN_ARENA_PICK_03, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick04.png", DHash.SCREEN_ARENA_PICK_04, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick05.png", DHash.SCREEN_ARENA_PICK_05, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick06.png", DHash.SCREEN_ARENA_PICK_06, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick07.png", DHash.SCREEN_ARENA_PICK_07, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick08.png", DHash.SCREEN_ARENA_PICK_08, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick09.png", DHash.SCREEN_ARENA_PICK_09, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick10.png", DHash.SCREEN_ARENA_PICK_10, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick11.png", DHash.SCREEN_ARENA_PICK_11, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick12.png", DHash.SCREEN_ARENA_PICK_12, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick13.png", DHash.SCREEN_ARENA_PICK_13, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick14.png", DHash.SCREEN_ARENA_PICK_14, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick15.png", DHash.SCREEN_ARENA_PICK_15, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick16.png", DHash.SCREEN_ARENA_PICK_16, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick17.png", DHash.SCREEN_ARENA_PICK_17, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick18.png", DHash.SCREEN_ARENA_PICK_18, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick19.png", DHash.SCREEN_ARENA_PICK_19, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick20.png", DHash.SCREEN_ARENA_PICK_20, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick21.png", DHash.SCREEN_ARENA_PICK_21, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick22.png", DHash.SCREEN_ARENA_PICK_22, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick23.png", DHash.SCREEN_ARENA_PICK_23, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick24.png", DHash.SCREEN_ARENA_PICK_24, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick25.png", DHash.SCREEN_ARENA_PICK_25, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick26.png", DHash.SCREEN_ARENA_PICK_26, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick27.png", DHash.SCREEN_ARENA_PICK_27, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick28.png", DHash.SCREEN_ARENA_PICK_28, BufferedImage::getScreenArenaPickNumberCrop)
        recognizePickScreen("ArenaPick/ArenaPick29.png", DHash.SCREEN_ARENA_PICK_29, BufferedImage::getScreenArenaPickNumberCrop)
    }

    @Test
    fun testArenaPickClass() {
        recognizeArenaPickClass("ArenaPick/ArenaPick29.png", "Sorcerer")
    }

    private fun recognizeScreen(testFileName: String, screen: String?, cropFun: (BufferedImage) -> BufferedImage?) {
        cropFun(getFileImage(testFileName))?.apply {
            assertThat(recognizeImage(this, DHash.SCREENS_LIST)).isEqualTo(screen)
        }
    }

    private fun recognizePickScreen(testFileName: String, screen: String?, cropFun: (BufferedImage) -> BufferedImage?) {
        cropFun(getFileImage(testFileName))?.apply {
            assertThat(recognizeImage(this, DHash.SCREENS_PICK_LIST)).isEqualTo(screen)
        }
    }

    private fun recognizeArenaPickClass(testFileName: String, cls: String) {
        val croppedImage = getFileImage(testFileName).getArenaPickClassCrop()
        assertThat(recognizeImage(croppedImage, DHash.CLASS_PICK_LIST)).isEqualTo(cls)
    }

    // -- Game Tests --


    @Test
    fun testRing() {
        var croppedImage = getFileImage("Game/PlayFirst.png").getGamePlayerFirstCrop()
        assertThat(recognizeImage(croppedImage, DHash.GAME_ITEMS_LIST)).isEqualTo(DHash.PLAYER_GAME_FIRST)
        croppedImage = getFileImage("Game/PlaySecond.png").getGamePlayerSecondCrop()
        assertThat(recognizeImage(croppedImage, DHash.GAME_ITEMS_LIST)).isEqualTo(DHash.PLAYER_GAME_SECOND)
    }

    @Test
    fun testResult() {
//        var croppedImage = getFileImage("Game/win.png").getGameWinCrop()
//        assertThat(recognizeImage(croppedImage, DHash.GAME_ITEMS_LIST)).isEqualTo(DHash.GAME_WIN)
        var croppedImage = getFileImage("Game/win2.png").getGameWin2Crop()
        assertThat(recognizeImage(croppedImage, DHash.GAME_ITEMS_LIST)).isEqualTo(DHash.GAME_WIN2)
//        croppedImage = getFileImage("Game/loss.png").getGameLossCrop()
//        assertThat(recognizeImage(croppedImage, DHash.GAME_ITEMS_LIST)).isEqualTo(DHash.GAME_LOSS)
        croppedImage = getFileImage("Game/loss2.png").getGameLoss2Crop()
        assertThat(recognizeImage(croppedImage, DHash.GAME_ITEMS_LIST)).isEqualTo(DHash.GAME_LOSS2)
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
        var croppedImage = getFileImage("Game/rank3.png").getGamePlayerRankCrop()
        assertThat(recognizeImage(croppedImage, DHash.GAME_PLAYER_RANK_LIST)).isEqualTo("3")
        croppedImage = getFileImage("Game/rank3.png").getGameOpponentRankCrop()
        assertThat(recognizeImage(croppedImage, DHash.GAME_PLAYER_RANK_LIST)).isEqualTo("4")
    }

    private fun recognizeOpponent(testFileName: String, result: String, crop: (BufferedImage) -> BufferedImage) {
        val croppedImage = crop(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, DHash.GAME_OPPONENT_CLASS_LIST)).isEqualTo(result)
    }

    private fun recognizePlayer(testFileName: String, result: String, crop: (BufferedImage) -> BufferedImage) {
        val croppedImage = crop(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, DHash.GAME_PLAYER_CLASS_LIST)).isEqualTo(result)
    }

    // -- Arena Tests --

    @Test
    fun testArenaPicks() {
        recognizeArenaPick("ArenaPick/ArenaPick01.png", "falkreathdefiler", "highrocksummoner", "watchcommander")
        recognizeArenaPick("ArenaPick/ArenaPick02.png", "abeceannavigator", "histspeaker", "stealerofsecrets")
        recognizeArenaPick("ArenaPick/ArenaPick03.png", "treeminder", "stormholdhenchman", "daggersinthedark")
        recognizeArenaPick("ArenaPick/ArenaPick04.png", "deadlydraugr", "lillandrilhexmage", "gristlehidedreugh")
        recognizeArenaPick("ArenaPick/ArenaPick05.png", "knifetothethroat", "stalwartally", "stalkingshadowscale")
        recognizeArenaPick("ArenaPick/ArenaPick06.png", "glenumbrasorceress", "firestorm", "lowlandtroll")
        recognizeArenaPick("ArenaPick/ArenaPick07.png", "farsightnereid", "healinghands", "reachmanshaman")
        recognizeArenaPick("ArenaPick/ArenaPick08.png", "camlornhero", "mapleshield", "healinghands")
        recognizeArenaPick("ArenaPick/ArenaPick09.png", "lowlandtroll", "skilledblacksmith", "mummify")
        recognizeArenaPick("ArenaPick/ArenaPick10.png", "windkeepspellsword", "oldgatewarden", "stalkingshadowscale")
        recognizeArenaPick("ArenaPick/ArenaPick11.png", "knifetothethroat", "shockingwamasu", "lesserward")
        recognizeArenaPick("ArenaPick/ArenaPick12.png", "stampedingmammoth", "hallsofthedwemer", "heroicrebirth")
        recognizeArenaPick("ArenaPick/ArenaPick13.png", "healinghands", "lesserward", "reachmanshaman")
        recognizeArenaPick("ArenaPick/ArenaPick14.png", "gloomwraith", "spearofembers", "drestormentor")
        recognizeArenaPick("ArenaPick/ArenaPick15.png", "daggersinthedark", "lurkingmummy", "palaceconspirator")
        recognizeArenaPick("ArenaPick/ArenaPick16.png", "crystaltowercrafter", "stonetoothscrapper", "stalkingshadowscale")
        recognizeArenaPick("ArenaPick/ArenaPick17.png", "lionguardstrategist", "strongholdincubator", "nightshadow")
        recognizeArenaPick("ArenaPick/ArenaPick18.png", "lightningbolt", "shockingwamasu", "spiderworker")
        recognizeArenaPick("ArenaPick/ArenaPick19.png", "dwarvensphere", "palaceconspirator", "swampleviathan")
        recognizeArenaPick("ArenaPick/ArenaPick20.png", "lurkingmummy", "dragontailsavior", "lillandrilhexmage")
        recognizeArenaPick("ArenaPick/ArenaPick21.png", "palaceconspirator", "lesserward", "dragontailsavior")
        recognizeArenaPick("ArenaPick/ArenaPick22.png", "suppress", "dresrenegade", "yewshield")
        recognizeArenaPick("ArenaPick/ArenaPick23.png", "deadlydraugr", "wrothgarartisan", "treeminder")
        recognizeArenaPick("ArenaPick/ArenaPick24.png", "frenziedwitchman", "enchantedplate", "lightningbolt")
        recognizeArenaPick("ArenaPick/ArenaPick25.png", "fharundefender", "redoranenforcer", "healinghands")
        recognizeArenaPick("ArenaPick/ArenaPick26.png", "stonetoothscrapper", "swampleviathan", "steelsword")
        recognizeArenaPick("ArenaPick/ArenaPick27.png", "youngmammoth", "elixirofconflict", "royalsage")
        recognizeArenaPick("ArenaPick/ArenaPick28.png", "spiderworker", "treeminder", "lightningbolt")
        recognizeArenaPick("ArenaPick/ArenaPick29.png", "soulsplit", "spiderworker", "redoranenforcer")
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