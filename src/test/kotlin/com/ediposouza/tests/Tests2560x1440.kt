package com.ediposouza.tests

import com.ediposouza.TESLTracker
import com.ediposouza.data.PHash
import com.ediposouza.extensions.getDeckBuilderFirstLineCardCrop
import com.ediposouza.extensions.getDeckBuilderNoneLeftCardCrop
import com.ediposouza.resolution.ReferenceConfig2560x1440
import com.ediposouza.util.BaseResolutionTests
import com.ediposouza.util.Recognizer
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

/**
 * Created by ediposouza on 06/03/17.
 */
class Tests2560x1440 : BaseResolutionTests() {

    override val arenaPickClass = "Monk"
    override val initialCardsDraw1 = "mantikora"
    override val initialCardsDraw2 = "execute"
    override val initialCardsDraw3 = "theblackdragon"
    override val gameCardDraw = "loyalhousecarl"
    override val gameCardDrawProphecy = "cloudrestillusionist"
    override val gameOpponentClass_CardDraw = "Agility"
    override val gameOpponentClass_PlayFirst = "Agility"
    override val gamePlayerClass_CardDraw = "Monk"
    override val gamePlayerClass_PlayFirst = "Monk"
    override val gamePlayerRank_Player = "6"
    override val gamePlayerRank_Opponent = "6"

    override val arenaPicks1 = "summersetshieldmage"
    override val arenaPicks2 = "sanctuaryraid"
    override val arenaPicks3 = "kvatchsoldier"

    @Before
    fun setUp() {
        TESLTracker.referenceConfig = ReferenceConfig2560x1440()
    }

    @Test
    override fun testDeckBuilderCards() {
        val croppedImage1 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(5)
        Truth.assertThat(Recognizer.recognizeCardImage(croppedImage1)).isNotEqualTo(deckBuilderCards1)
        val croppedImage2 = getFileImage("ScreenDeckBuilderEmpty.png").getDeckBuilderFirstLineCardCrop(4)
        Truth.assertThat(Recognizer.recognizeCardImage(croppedImage2)).isNotEqualTo(deckBuilderCards2)
    }

    @Test
    override fun testDeckBuilderNoneLeftCard() {
        val croppedImage1 = getFileImage("Deck/DeckBuilderNoneLeft.png").getDeckBuilderNoneLeftCardCrop(1)
        Truth.assertThat(Recognizer.recognizeImageInMap(croppedImage1, PHash.DECK_ITEMS_LIST)).isNotEqualTo(PHash.DECK_ITEM_NONE_LEFT)
        val croppedImage2 = getFileImage("Deck/DeckBuilderNoneLeft.png").getDeckBuilderNoneLeftCardCrop(2)
        Truth.assertThat(Recognizer.recognizeImageInMap(croppedImage2, PHash.DECK_ITEMS_LIST)).isNotEqualTo(PHash.DECK_ITEM_NONE_LEFT)
        val croppedImage3 = getFileImage("Deck/DeckBuilderNoneLeft.png").getDeckBuilderNoneLeftCardCrop(3)
        Truth.assertThat(Recognizer.recognizeImageInMap(croppedImage3, PHash.DECK_ITEMS_LIST)).isNotEqualTo(PHash.DECK_ITEM_NONE_LEFT)
        val croppedImage4 = getFileImage("Deck/DeckBuilderNoneLeft.png").getDeckBuilderNoneLeftCardCrop(4)
        Truth.assertThat(Recognizer.recognizeImageInMap(croppedImage4, PHash.DECK_ITEMS_LIST)).isNotEqualTo(PHash.DECK_ITEM_NONE_LEFT)
        val croppedImage5 = getFileImage("Deck/DeckBuilderNoneLeft.png").getDeckBuilderNoneLeftCardCrop(5)
        Truth.assertThat(Recognizer.recognizeImageInMap(croppedImage5, PHash.DECK_ITEMS_LIST)).isNotEqualTo(PHash.DECK_ITEM_NONE_LEFT)
    }

}