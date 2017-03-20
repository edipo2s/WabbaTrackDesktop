package com.ediposouza.util

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.util.images.ImageFuncs
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenshotProcessor {

    fun process(screenshot: BufferedImage) {
        with(ImageFuncs.getArenaClassSelectedCroppedImage(screenshot)) {
            Recognizer.recognizeArenaClassSelectImage(this)?.apply {
                Logger.i("\nArena Class ${this} Detected!")
                saveCroppedImage()
                return
            }
        }
        with(ImageFuncs.getArenaPicksRemainingCroppedImage(screenshot)) {
            Recognizer.recognizeArenaScreenImage(this)?.apply {
                Logger.i("\nArena Screen ${this} Detected!")
                saveCroppedImage()
                recognizeArenaPick(screenshot, 1)
                recognizeArenaPick(screenshot, 2)
                recognizeArenaPick(screenshot, 3)
                return
            }
        }
    }

    private fun recognizeArenaPick(image: BufferedImage, pick: Int) {
        with(ImageFuncs.getArenaCardCropped(image, pick)) {
            saveCroppedImage()
            TESLTrackerData.getCard(Recognizer.recognizeCardImage(this))?.apply {
                Logger.i("--$name: $arenaTier")
                val arenaTierValue = when (arenaTier) {
                    "Terrible" -> 10
                    "Poor" -> 20
                    "Average" -> 30
                    "Good" -> 50
                    "Excellent" -> 70
                    else -> 90
                }
                showPickValue(pick, arenaTierValue)
            }
        }
    }

    private fun showPickValue(pick: Int, value: Int) {
        Stage(StageStyle.TRANSPARENT).apply {
            scene = Scene(BorderPane().apply {
                center = Label("$value")
            }, 75.0, 75.0).apply {
                stylesheets.add(ScreenshotProcessor.javaClass.getResource("/fontstyle.css").toExternalForm())
            }
            x = when (pick) {
                1 -> 290.0
                2 -> 520.0
                else -> 750.0
            }
            y = 410.0
            opacity = 0.5
            isAlwaysOnTop = true
            show()
        }
    }

}