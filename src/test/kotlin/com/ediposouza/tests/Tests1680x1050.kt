package com.ediposouza.tests

import com.ediposouza.TESLTracker
import com.ediposouza.extensions.getDeckBuilderFirstLineCardCrop
import com.ediposouza.resolution.ReferenceConfig1680x1050
import com.ediposouza.util.BaseResolutionTests
import com.ediposouza.util.Recognizer
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

/**
 * Created by ediposouza on 06/03/17.
 */
class Tests1680x1050 : BaseResolutionTests() {

    override val arenaPickClass = "Archer"
    override val initialCardsDraw1 = "sanctuarypet"
    override val initialCardsDraw2 = "quicksilvercrossbow"
    override val initialCardsDraw3 = "snaketoothnecklace"
    override val gameCardDraw = "unstoppablerage"
    override val gameCardDrawProphecy = "brotherhoodslayer"
    override val gameOpponentClass_CardDraw = "Sorcerer"
    override val gameOpponentClass_PlayFirst = "Battlemage"
    override val gamePlayerClass_CardDraw = "Archer"
    override val gamePlayerClass_PlayFirst = "Archer"
    override val gamePlayerRank_Player = "7"
    override val gamePlayerRank_Opponent = "7"

    override val arenaPicks1 = "territorialviper"
    override val arenaPicks2 = "underworldvigilante"
    override val arenaPicks3 = "elixiroflightfeet"

    @Before
    fun setUp() {
        TESLTracker.referenceConfig = ReferenceConfig1680x1050()
    }

    @Test
    override fun testDeckBuilderCards() {
        val croppedImage1 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(5)
        Truth.assertThat(Recognizer.recognizeCardImage(croppedImage1)).isNotEqualTo(deckBuilderCards1)
        val croppedImage2 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(4)
        Truth.assertThat(Recognizer.recognizeCardImage(croppedImage2)).isNotEqualTo(deckBuilderCards2)
    }

}