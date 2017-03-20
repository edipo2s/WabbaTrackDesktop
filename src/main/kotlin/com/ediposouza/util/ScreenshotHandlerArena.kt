package com.ediposouza.util

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.model.*
import com.ediposouza.util.images.ImageFuncs
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenshotHandlerArena {

    fun processArenaClassSelectScreenshot(className: String, croppedScreenshot: BufferedImage) {
        croppedScreenshot.saveCroppedImage()
    }

    fun processArenaPickScreenshot(croppedScreenshot: BufferedImage) {
        croppedScreenshot.saveCroppedImage()
        recognizeArenaPick(croppedScreenshot, 1)
        recognizeArenaPick(croppedScreenshot, 2)
        recognizeArenaPick(croppedScreenshot, 3)
    }

    private fun recognizeArenaPick(image: BufferedImage, pick: Int) {
        with(ImageFuncs.getArenaCardCropped(image, pick)) {
            saveCroppedImage()
            TESLTrackerData.getCard(Recognizer.recognizeCardImage(this))?.apply {
                Logger.i("--$name: $arenaTier")
                val arenaTierValue = calcArenaValue(this, listOf())
                Platform.runLater {
                    showPickValue(pick, arenaTierValue.first, arenaTierValue.second)
                }
            }
        }
    }

    private fun showPickValue(pick: Int, value: Int, synergyWith: List<Card>) {
        val layout = BorderPane().apply {
            center = Label("$value".takeIf { synergyWith.isEmpty() } ?: "$value*").apply {
                textFill = Color.web(when (value) {
                    in 0..CardArenaTier.AVERAGE.value.minus(1) -> "#F44336"
                    in CardArenaTier.AVERAGE.value..CardArenaTier.EXCELLENT.value.minus(1) -> "#212121"
                    else -> "#4DB6AC"
                })
            }
        }
        with(ImageFuncs.referenceConfig) {
            val tierValueFirstPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_VALUE_FIRST_X, ARENA_PICK_VALUE_Y)
            val tierValueSecondPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_VALUE_SECOND_X, ARENA_PICK_VALUE_Y)
            val tierValueThirdPos = ImageFuncs.getScreenScaledPosition(ARENA_PICK_VALUE_THIRD_X, ARENA_PICK_VALUE_Y)
            val tierValueSize = ImageFuncs.getScreenScaledSize(ARENA_PICK_VALUE_WIDTH, ARENA_PICK_VALUE_HEIGHT)
            Stage(StageStyle.TRANSPARENT).apply {
                scene = Scene(layout, tierValueSize.first.toDouble(), tierValueSize.second.toDouble()).apply {
                    stylesheets.add(ScreenshotProcessor.javaClass.getResource("/fontstyle.css").toExternalForm())
                }
                x = when (pick) {
                    1 -> tierValueFirstPos.first.toDouble()
                    2 -> tierValueSecondPos.first.toDouble()
                    else -> tierValueThirdPos.first.toDouble()
                }
                y = tierValueFirstPos.second.toDouble()
                opacity = 0.5
                isAlwaysOnTop = true
                show()
            }
        }
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