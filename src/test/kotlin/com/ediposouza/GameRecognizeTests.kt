package com.ediposouza

import com.ediposouza.data.DHash
import com.ediposouza.data.DHashCards
import com.ediposouza.extensions.getGameCardDrawCrop
import com.ediposouza.extensions.getGameInitialCardDrawCrop
import com.ediposouza.extensions.getGameOpponentClassCrop
import com.ediposouza.extensions.getGamePlayerClassCrop
import com.ediposouza.util.BaseRecognizeTests
import com.ediposouza.util.Recognizer
import com.ediposouza.util.ReferenceConfig1366x768
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.awt.image.BufferedImage

/**
 * Created by ediposouza on 06/03/17.
 */
class GameRecognizeTests : BaseRecognizeTests() {

    @Before
    fun setUp() {
        TESLTracker.referenceConfig = ReferenceConfig1366x768()
    }

    @Test
    fun testGameCardDrawPHashWithDHash() {
        val croppedImage = getFileImage("Game/CardDraw2.png").getGameCardDrawCrop()
        Recognizer.recognizeCardImage(croppedImage)
    }

    @Test
    fun testGameCardDraw() {
        val croppedImage = getFileImage("Game/CardDraw.png").getGameCardDrawCrop()
        assertThat(recognizeImage(croppedImage, DHashCards.LIST)).isEqualTo("quinrawlburglar")
    }

    @Test
    fun testGameCardsInitialDraw() {
        var croppedImage = getFileImage("Game/InitialCards.png").getGameInitialCardDrawCrop(1)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST, true)).isEqualTo("elusiveschemer")
        croppedImage = getFileImage("Game/InitialCards.png").getGameInitialCardDrawCrop(2)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST, true)).isEqualTo("blacksapprotector")
        croppedImage = getFileImage("Game/InitialCards.png").getGameInitialCardDrawCrop(3)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST, true)).isEqualTo("blacksapprotector")
        croppedImage = getFileImage("Game/InitialCards2.png").getGameInitialCardDrawCrop(1)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST, true)).isEqualTo("elusiveschemer")
        croppedImage = getFileImage("Game/InitialCards2.png").getGameInitialCardDrawCrop(2)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST, true)).isEqualTo("ungolimthelistener")
        croppedImage = getFileImage("Game/InitialCards2.png").getGameInitialCardDrawCrop(3)
        assertThat(recognizeImage(croppedImage, DHashCards.LIST, true)).isEqualTo("shriekingharpy")
    }

    @Test
    fun testGameOpponentClass() {
        recognizeOpponent("Game/OpponentArcher.png", "Archer", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentAssassin.png", "Assassin", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentBattlemage.png", "Battlemage", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentCrusader.png", "Crusader", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentMage.png", "Mage", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentMonk.png", "Monk", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentScout.png", "Scout", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentSorcerer.png", "Sorcerer", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentSpellword.png", "Spellword", BufferedImage::getGameOpponentClassCrop)
        recognizeOpponent("Game/OpponentWarrior.png", "Warrior", BufferedImage::getGameOpponentClassCrop)
    }

    @Test
    fun testGamePlayerClass() {
        recognizePlayer("Game/PlayerArcher.png", "Archer", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerAssassin.png", "Assassin", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerBattlemage.png", "Battlemage", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerCrusader.png", "Crusader", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerMage.png", "Mage", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerMonk.png", "Monk", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerScout.png", "Scout", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerSorcerer.png", "Sorcerer", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerSpellword.png", "Spellword", BufferedImage::getGamePlayerClassCrop)
        recognizePlayer("Game/PlayerWarrior.png", "Warrior", BufferedImage::getGamePlayerClassCrop)
    }

    private fun recognizeOpponent(testFileName: String, result: String, crop: (BufferedImage) -> BufferedImage) {
        val croppedImage = crop(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, DHash.GAME_OPPONENT_CLASS_LIST)).isEqualTo(result)
    }

    private fun recognizePlayer(testFileName: String, result: String, crop: (BufferedImage) -> BufferedImage) {
        val croppedImage = crop(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, DHash.GAME_PLAYER_CLASS_LIST)).isEqualTo(result)
    }

}