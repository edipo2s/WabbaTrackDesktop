package com.ediposouza.executor

import com.ediposouza.data.PHash
import com.ediposouza.extensions.*
import com.ediposouza.model.MatchMode
import com.ediposouza.state.ArenaState
import com.ediposouza.state.GameState
import com.ediposouza.state.StateHandler
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 18/03/2017.
 */
object ScreenExecutor {

    var screenRecognized = false
    var lastScreenRecognized = ""

    fun process(screenshot: BufferedImage) {
//        Logger.i("Checking game screen: ${LocalTime.now()}")
        screenRecognized = false
        //Screens check
        launch(CommonPool) {
            if (lastScreenRecognized != PHash.SCREEN_MAIN_MODE_CASUAL &&
                    lastScreenRecognized != PHash.SCREEN_MAIN_MODE_RANKED &&
                    lastScreenRecognized != PHash.SCREEN_MAIN_MODE_PRATICE &&
                    screenshot.getScreenMainCrop().matchScreen(PHash.SCREEN_MAIN)) {
                Logger.i("Main Screen Detected!", true)
                StateHandler.currentTESLState = null
                screenRecognized = true
            }
        }
        launch(CommonPool) {
            if (screenshot.getScreenMainModeCrop().matchScreen(PHash.SCREEN_MAIN_MODE_CASUAL)) {
                Logger.i("Casual Match Mode detected!", false)
                GameState.matchMode = MatchMode.CASUAL
                screenRecognized = true
            }
        }
        launch(CommonPool) {
            if (screenshot.getScreenMainModeCrop().matchScreen(PHash.SCREEN_MAIN_MODE_RANKED)) {
                Logger.i("Ranked Match Mode detected!", false)
                GameState.matchMode = MatchMode.RANKED
                screenRecognized = true
            }
        }
        launch(CommonPool) {
            if (screenshot.getScreenMainModeCrop().matchScreen(PHash.SCREEN_MAIN_MODE_PRATICE)) {
                Logger.i("Pratice Match Mode detected!", false)
                GameState.matchMode = MatchMode.PRATICE
                screenRecognized = true
            }
        }
        // Game
        launch(CommonPool) {
            if (screenshot.getScreenGameCrop().matchScreen(PHash.SCREEN_GAME)) {
                Logger.i("Game Screen Detected!", true)
                StateHandler.currentTESLState = GameState
                screenRecognized = true
            }
        }
        // Arena
        launch(CommonPool) {
            if (screenshot.getScreenArenaClassesCrop().matchScreen(PHash.SCREEN_ARENA_CLASSES)) {
                Logger.i("Arena Classes Screen Detected!", true)
                StateHandler.currentTESLState = ArenaState.apply {
                    pickNumber = 1
                }
                screenRecognized = true
            }
        }
        launch(CommonPool) {
            if (screenshot.getScreenArenaPicksCrop().matchScreen(PHash.SCREEN_ARENA_PICKS)) {
                Logger.i("Arena Picks Screen Detected!", true)
                StateHandler.currentTESLState = ArenaState
                screenRecognized = true
            }
        }
        launch(CommonPool) {
            if (screenshot.getScreenArenaDashboardCrop().matchScreen(PHash.SCREEN_ARENA_DASHBOARD)) {
                Logger.i("Arena Dashboard Screen Detected!", true)
                Logger.i("Match Mode set to Arena!", true)
                GameState.matchMode = MatchMode.ARENA
                StateHandler.currentTESLState = ArenaState.apply {
                    finishPicks = true
                }
                screenRecognized = true
            }
        }
    }

    private fun BufferedImage.matchScreen(screen: String): Boolean {
        if (Recognizer.recognizeScreenImage(this) == screen && lastScreenRecognized != screen) {
            lastScreenRecognized = screen
            return true
        } else {
            return false
        }
    }

}