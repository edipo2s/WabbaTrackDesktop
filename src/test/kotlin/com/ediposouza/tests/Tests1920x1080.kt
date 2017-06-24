package com.ediposouza.tests

import com.ediposouza.TESLTracker
import com.ediposouza.extensions.getDeckBuilderFirstLineCardCrop
import com.ediposouza.resolution.ReferenceConfig1920x1080
import com.ediposouza.util.BaseResolutionTests
import com.ediposouza.util.Recognizer
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

/**
 * Created by ediposouza on 06/03/17.
 */
class Tests1920x1080 : BaseResolutionTests() {

    override val arenaPickClass = "Sorcerer"
    override val initialCardsDraw1 = "histgrove"
    override val initialCardsDraw2 = "shadowfenpriest"
    override val initialCardsDraw3 = "piercingjavelin"
    override val gameCardDraw = "execute"
    override val gameCardDrawProphecy = "fateweaver"
    override val gameOpponentClass_CardDraw = "Strength"
    override val gameOpponentClass_PlayFirst = "Sorcerer"
    override val gamePlayerClass_CardDraw = "Spellsword"
    override val gamePlayerClass_PlayFirst = "Spellsword"
    override val gamePlayerRank_Player = "3"
    override val gamePlayerRank_Opponent = "4"

    override val arenaPicks1 = "falkreathdefiler"
    override val arenaPicks2 = "highrocksummoner"
    override val arenaPicks3 = "watchcommander"

    @Before
    fun setUp() {
        TESLTracker.referenceConfig = ReferenceConfig1920x1080()
    }

    @Test
    override fun testDeckBuilderCards() {
        val croppedImage1 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(5)
        Truth.assertThat(Recognizer.recognizeCardImage(croppedImage1)).isNotEqualTo(deckBuilderCards1)
        val croppedImage2 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(4)
        Truth.assertThat(Recognizer.recognizeCardImage(croppedImage2)).isNotEqualTo(deckBuilderCards2)
    }

}