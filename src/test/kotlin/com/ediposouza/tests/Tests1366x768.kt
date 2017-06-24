package com.ediposouza.tests

import com.ediposouza.TESLTracker
import com.ediposouza.data.PHash
import com.ediposouza.extensions.*
import com.ediposouza.resolution.ReferenceConfig1366x768
import com.ediposouza.util.BaseResolutionTests
import com.ediposouza.util.Recognizer
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage

/**
 * Created by ediposouza on 06/03/17.
 */
class Tests1366x768 : BaseResolutionTests() {

    override val initialCardsDraw1 = "elusiveschemer"
    override val initialCardsDraw2 = "blacksapprotector"
    override val initialCardsDraw3 = "blacksapprotector"
    override val gameCardDraw = "quinrawlburglar"
    override val gameCardDrawProphecy = "blacksapprotector"

    override val arenaPicks1 = "giantsnake"
    override val arenaPicks2 = "spiderlair"
    override val arenaPicks3 = "brilliantexperiment"

    @Before
    fun setUp() {
        TESLTracker.referenceConfig = ReferenceConfig1366x768()
    }

    @Test
    override fun testArenaPickClass() {
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

    // -- Game Tests --

    @Test
    override fun testResult() {
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
    override fun testGameOpponentClass() {
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
    override fun testGamePlayerClass() {
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
    override fun testGamePlayerRank() {
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

    // -- Arena Tests --

    @Test
    override fun testDeckBuilderCards() {
        val croppedImage1 = getFileImage("Deck/DeckBuilderPick.png").getDeckBuilderFirstLineCardCrop(5)
        assertThat(Recognizer.recognizeCardImage(croppedImage1)).isNotEqualTo("shadowshift")
        val croppedImage2 = getFileImage("Deck/DeckBuilderPick.png").getDeckBuilderFirstLineCardCrop(4)
        assertThat(Recognizer.recognizeCardImage(croppedImage2)).isNotEqualTo("shadowshift")
        val croppedImage3 = getFileImage("Deck/DeckBuilderPick.png").getDeckBuilderFirstLineCardCrop(3)
        assertThat(Recognizer.recognizeCardImage(croppedImage3)).isNotEqualTo("shadowshift")
        val croppedImage4 = getFileImage("Deck/DeckBuilderPick.png").getDeckBuilderFirstLineCardCrop(2)
        assertThat(Recognizer.recognizeCardImage(croppedImage4)).isNotEqualTo("shadowshift")
        val croppedImage5 = getFileImage("Deck/DeckBuilderPick.png").getDeckBuilderFirstLineCardCrop(1)
        assertThat(Recognizer.recognizeCardImage(croppedImage5)).isEqualTo("shadowshift")
    }

}