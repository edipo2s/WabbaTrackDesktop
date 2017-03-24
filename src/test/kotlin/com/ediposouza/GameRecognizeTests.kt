package com.ediposouza

import com.ediposouza.data.DHash
import com.ediposouza.extensions.getGameOpponentClassCrop
import com.ediposouza.util.images.ReferenceConfig1366x768
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
    fun testGameOpponentClass() {
        recognize("Game/OpponentArcher.png", "Archer", BufferedImage::getGameOpponentClassCrop)
        recognize("Game/OpponentAssassin.png", "Assassin", BufferedImage::getGameOpponentClassCrop)
//        recognize("Game/OpponentBattlemag.png", "Battlemage", BufferedImage::getGameOpponentClassCrop)
//        recognize("Game/OpponentCrusader.png", "Crusader", BufferedImage::getGameOpponentClassCrop)
//        recognize("Game/OpponentMage.png", "Mage", BufferedImage::getGameOpponentClassCrop)
        recognize("Game/OpponentMonk.png", "Monk", BufferedImage::getGameOpponentClassCrop)
        recognize("Game/OpponentScout.png", "Scout", BufferedImage::getGameOpponentClassCrop)
//        recognize("Game/OpponentSorcerer.png", "Sorcerer", BufferedImage::getGameOpponentClassCrop)
        recognize("Game/OpponentSpellword.png", "Spellword", BufferedImage::getGameOpponentClassCrop)
//        recognize("Game/OpponentWarrior.png", "Warrior", BufferedImage::getGameOpponentClassCrop)
    }

    private fun recognize(testFileName: String, result: String, crop: (BufferedImage) -> BufferedImage) {
        val croppedImage = crop(getFileImage(testFileName))
        assertThat(recognizeImage(croppedImage, DHash.GAME_OPPONENT_CLASS_LIST)).isEqualTo(result)
    }

}