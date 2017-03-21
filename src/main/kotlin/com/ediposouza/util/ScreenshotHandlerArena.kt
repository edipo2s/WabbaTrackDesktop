package com.ediposouza.util

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.model.*
import com.ediposouza.ui.ShowArenaTierEvent
import com.ediposouza.util.images.ImageFuncs
import tornadofx.FX
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenshotHandlerArena {

    fun processArenaClassSelectScreenshot(className: String, screenshot: BufferedImage) {
        screenshot.saveCroppedImage()
    }

    fun processArenaPickScreenshot(screenshot: BufferedImage) {
        screenshot.saveCroppedImage()
        val arenaTier1Value = recognizeArenaPick(screenshot, 1)
        val arenaTier2Value = recognizeArenaPick(screenshot, 2)
        val arenaTier3Value = recognizeArenaPick(screenshot, 3)
        FX.eventbus.fire(ShowArenaTierEvent(arenaTier1Value, arenaTier2Value, arenaTier3Value))
    }

    private fun recognizeArenaPick(image: BufferedImage, pick: Int): Pair<Int, List<Card>> {
        with(ImageFuncs.getArenaCardCropped(image, pick)) {
            saveCroppedImage()
            TESLTrackerData.getCard(Recognizer.recognizeCardImage(this))?.apply {
                Logger.i("--$name: $arenaTier")
                return calcArenaValue(this, listOf())
            }
        }
        return Pair(0, listOf())
    }

    private fun calcArenaValue(card: Card, picksBefore: List<Card>): Pair<Int, List<Card>> {
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
        return Pair(value + totalValueExtra, cardsSynergy)
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