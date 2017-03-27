package com.ediposouza.handler

import com.ediposouza.data.TESLTrackerData
import com.ediposouza.extensions.getGameCardDrawCrop
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import com.ediposouza.util.ScreenFuncs

/**
 * Created by Edipo on 27/03/2017.
 */
object GameHandler {

    fun processGame() {
        ScreenFuncs.takeScreenshot()?.apply {
            getGameCardDrawCrop().apply {
                TESLTrackerData.getCard(Recognizer.recognizeCardImage(this))?.apply {
                    Logger.i("--$name draw!")
                }
            }
        }
    }

}