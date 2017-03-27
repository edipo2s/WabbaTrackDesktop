package com.ediposouza

import com.ediposouza.data.DHash
import com.ediposouza.data.DHashCards
import com.ediposouza.extensions.getGameCardDrawCrop
import com.ediposouza.extensions.getGameOpponentClassCrop
import com.ediposouza.extensions.getGamePlayerClassCrop
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
    fun testGameCardDrawClass() {
        val cardSimilarity = Recognizer.Similarity.DHASH_DISTANCE_SIMILARITY_LOW
        val croppedImage = getFileImage("Game/CardDraw.png").getGameCardDrawCrop()
        assertThat(recognizeImage(croppedImage, DHashCards.LIST, similarity = cardSimilarity)).isEqualTo("quinrawlburglar")
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
        recognizPlayer("Game/PlayerArcher.png", "Archer", BufferedImage::getGamePlayerClassCrop)
        recognizPlayer("Game/PlayerAssassin.png", "Assassin", BufferedImage::getGamePlayerClassCrop)
        recognizPlayer("Game/PlayerBattlemage.png", "Battlemage", BufferedImage::getGamePlayerClassCrop)
        recognizPlayer("Game/PlayerCrusader.png", "Crusader", BufferedImage::getGamePlayerClassCrop)
        recognizPlayer("Game/PlayerMage.png", "Mage", BufferedImage::getGamePlayerClassCrop)
        recognizPlayer("Game/PlayerMonk.png", "Monk", BufferedImage::getGamePlayerClassCrop)
        recognizPlayer("Game/PlayerScout.png", "Scout", BufferedImage::getGamePlayerClassCrop)
        recognizPlayer("Game/PlayerSorcerer.png", "Sorcerer", BufferedImage::getGamePlayerClassCrop)
        recognizPlayer("Game/PlayerSpellword.png", "Spellword", BufferedImage::getGamePlayerClassCrop)
        recognizPlayer("Game/PlayerWarrior.png", "Warrior", BufferedImage::getGamePlayerClassCrop)
    }

    private fun recognizeOpponent(testFileName: String, result: String, crop: (BufferedImage) -> BufferedImage) {
        val croppedImage = crop(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, DHash.GAME_OPPONENT_CLASS_LIST)).isEqualTo(result)
    }

    private fun recognizPlayer(testFileName: String, result: String, crop: (BufferedImage) -> BufferedImage) {
        val croppedImage = crop(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, DHash.GAME_PLAYER_CLASS_LIST)).isEqualTo(result)
    }

}