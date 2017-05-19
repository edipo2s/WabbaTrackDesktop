package com.ediposouza.tests

import com.ediposouza.extensions.getDeckBuilderFirstLineCardCrop

/**
 * Created by ediposouza on 06/03/17.
 */
class Tests1920x1080 : com.ediposouza.util.BaseResolutionTests() {

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

    @org.junit.Before
    fun setUp() {
        com.ediposouza.TESLTracker.Companion.referenceConfig = com.ediposouza.resolution.ReferenceConfig1920x1080()
    }

    @org.junit.Test
    override fun testDeckBuilderCards() {
        val croppedImage1 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(5)
        com.google.common.truth.Truth.assertThat(com.ediposouza.util.Recognizer.recognizeCardImage(croppedImage1)).isNotEqualTo(deckBuilderCards1)
        val croppedImage2 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(4)
        com.google.common.truth.Truth.assertThat(com.ediposouza.util.Recognizer.recognizeCardImage(croppedImage2)).isNotEqualTo(deckBuilderCards2)
    }

}