package com.ediposouza.handler

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.extensions.getArenaCardCrop
import com.ediposouza.extensions.saveCroppedImage
import com.ediposouza.model.*
import com.ediposouza.scope.ArenaState
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import com.ediposouza.util.ScreenFuncs
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ArenaHandler {

    fun processArenaPick(retryNumber: Int = 0) {
        ScreenFuncs.takeScreenshot()?.apply {
            val arenaTier1Value = recognizeArenaPick(this, 1)
            val arenaTier2Value = recognizeArenaPick(this, 2)
            val arenaTier3Value = recognizeArenaPick(this, 3)
            if (arenaTier1Value.card != arenaTier2Value.card && arenaTier1Value.card != arenaTier3Value.card &&
                    arenaTier2Value.card != arenaTier3Value.card) {
                ArenaState.setTierPicks(Triple(arenaTier1Value, arenaTier2Value, arenaTier3Value))
            } else {
                Thread.sleep(1000L)
                Logger.e("Duplicate pick, retrying detection")
                if (retryNumber < 3) {
                    processArenaPick(retryNumber + 1)
                }
            }
        }
    }

    private fun recognizeArenaPick(image: BufferedImage, pick: Int): CardPick {
        with(image.getArenaCardCrop(pick)) {
            saveCroppedImage()
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