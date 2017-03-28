package com.ediposouza.handler

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.extensions.getGameCardDrawCrop
import com.ediposouza.extensions.saveCroppedImage
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 27/03/2017.
 */
object GameHandler {

    var lastCropDHash = ""

    fun processGame(screenshot: BufferedImage) {
        screenshot.getGameCardDrawCrop().apply {
            if (Recognizer.calcDHash(this) != lastCropDHash) {
                saveCroppedImage()
                TESLTrackerData.getCard(Recognizer.recognizeCardImage(this))?.apply {
                    Logger.i("--$name draw!")
                }
            }
        }
    }

}