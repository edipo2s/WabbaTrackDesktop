package com.ediposouza.handler

import com.ediposouza.data.DHash
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.extensions.getArenaCardCrop
import com.ediposouza.extensions.getArenaPickClassCrop
import com.ediposouza.extensions.getScreenArenaPickNumberCrop
import com.ediposouza.model.*
import com.ediposouza.state.ArenaState
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ArenaHandler {

    fun processArenaClass(screenshot: BufferedImage?): DeckClass? {
        screenshot?.getArenaPickClassCrop()?.apply {
            return DeckClass.of(Recognizer.recognizeImageInMap(this, DHash.CLASS_PICK_LIST))
        }
        return null
    }

    fun processArenaPickNumber(screenshot: BufferedImage): Int? {
        return Recognizer.recognizeScreenPickImage(screenshot.getScreenArenaPickNumberCrop())?.let {
            DHash.SCREENS_ARENA_PICK.indexOf(it) + 1
        }
    }

    fun processArenaPick(screenshot: BufferedImage, retryNumber: Int = 0): Triple<CardPick, CardPick, CardPick>? {
        val arenaTier1Value = recognizeArenaPick(screenshot, 1)
        val arenaTier2Value = recognizeArenaPick(screenshot, 2)
        val arenaTier3Value = recognizeArenaPick(screenshot, 3)
        if (arenaTier1Value.card != arenaTier2Value.card && arenaTier1Value.card != arenaTier3Value.card &&
                arenaTier2Value.card != arenaTier3Value.card) {
            return Triple(arenaTier1Value, arenaTier2Value, arenaTier3Value)
        } else {
            Thread.sleep(1000L)
            Logger.e("Duplicate pick, retrying detection")
            if (retryNumber < 3) {
                return processArenaPick(screenshot, retryNumber + 1)
            }
        }
        return null
    }

    private fun recognizeArenaPick(image: BufferedImage, pick: Int): CardPick {
        with(image.getArenaCardCrop(pick)) {
            TESLTrackerData.getCard(Recognizer.recognizeCardImage(this))?.apply {
                Logger.i("--$name: $arenaTier")
                return calcArenaValue(this, ArenaState.picks)
            }
        }
        return CardPick(Card.DUMMY, 0, ArenaState.picks)
    }

    private fun calcArenaValue(card: Card, picksBefore: List<Card>): CardPick {
        val arenaTier: CardArenaTier = card.arenaTier
        val cardsSynergy = mutableListOf<Card>()
        val value = arenaTier.value
        var totalValueExtra = 0
        picksBefore.forEach { draftedCard ->
            var extraValue = calcCardSynergyPoints(card.arenaTierPlus, draftedCard)
            extraValue += calcCardSynergyPoints(draftedCard.arenaTierPlus, card, true)
            if (extraValue > 0) {
                totalValueExtra += extraValue
                cardsSynergy.add(draftedCard)
            }
        }
        return CardPick(card, value + totalValueExtra, cardsSynergy)
    }

    private fun calcCardSynergyPoints(arenaTierPlus: CardArenaTierPlus?, draftedCard: Card, reverseCalc: Boolean = false): Int {
        if (arenaTierPlus == null) {
            return 0
        }
        val extraPoints = arenaTierPlus.type.extraPoints
        return when (arenaTierPlus.type) {
            CardArenaTierPlusType.ATTACK -> getExtraPointsForIntValue(arenaTierPlus, draftedCard.attack)
            CardArenaTierPlusType.COST -> getExtraPointsForIntValue(arenaTierPlus, draftedCard.cost)
            CardArenaTierPlusType.HEALTH -> getExtraPointsForIntValue(arenaTierPlus, draftedCard.health)
            CardArenaTierPlusType.ATTR -> extraPoints.takeIf {
                !reverseCalc && (draftedCard.attr == CardAttribute.valueOf(arenaTierPlus.value.toUpperCase()) ||
                        draftedCard.dualAttr1 == CardAttribute.valueOf(arenaTierPlus.value.toUpperCase()) ||
                        draftedCard.dualAttr2 == CardAttribute.valueOf(arenaTierPlus.value.toUpperCase()))
            } ?: 0
            CardArenaTierPlusType.KEYWORD -> extraPoints.takeIf {
                draftedCard.keywords.filter { it.name == arenaTierPlus.value.toUpperCase() }.isNotEmpty()
            } ?: 0
            CardArenaTierPlusType.RACE -> extraPoints.takeIf {
                draftedCard.race.name == arenaTierPlus.value.toUpperCase()
            } ?: 0
            CardArenaTierPlusType.STRATEGY -> 0
            CardArenaTierPlusType.TEXT -> extraPoints.takeIf {
                draftedCard.text.contains(arenaTierPlus.value)
            } ?: 0
            CardArenaTierPlusType.TYPE -> extraPoints.takeIf {
                draftedCard.type.name == arenaTierPlus.value.toUpperCase()
            } ?: 0
            else -> 0
        }
    }

    private fun getExtraPointsForIntValue(arenaTierPlus: CardArenaTierPlus, numberField: Int): Int {
        return arenaTierPlus.type.extraPoints.takeIf {
            when (arenaTierPlus.operator) {
                CardArenaTierPlusOperator.EQUALS -> numberField == arenaTierPlus.value.toInt()
                CardArenaTierPlusOperator.GREAT -> numberField > arenaTierPlus.value.toInt()
                CardArenaTierPlusOperator.MINOR -> numberField < arenaTierPlus.value.toInt()
                else -> false
            }
        } ?: 0
    }

}