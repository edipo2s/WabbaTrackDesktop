package com.ediposouza.handler

import com.ediposouza.data.DHash
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.extensions.*
import com.ediposouza.model.Card
import com.ediposouza.model.DeckClass
import com.ediposouza.scope.GameState
import com.ediposouza.scope.GameState.playerDeckClass
import com.ediposouza.util.Logger
import com.ediposouza.util.Recognizer
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 27/03/2017.
 */
object GameHandler {

    fun processPlayerGoFirst(screenshot: BufferedImage): Boolean? {
        return screenshot.getGamePlayerFirstCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.PLAYER_GAME_FIRST)?.apply {
                Logger.i("--PlayerGoFirst!")
            }
        } ?: screenshot.getGamePlayerSecondCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.PLAYER_GAME_SECOND)?.apply {
                Logger.i("--PlayerGoSecond!")
            }
        }
    }

    fun processPlayerDeckClass(screenshot: BufferedImage): DeckClass? {
        return screenshot.getGamePlayerClassCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_PLAYER_CLASS_LIST)?.let {
                DeckClass.of(it).apply {
                    Logger.i("--PlayerDeckClass: $playerDeckClass!")
                }
            }
        }
    }

    fun processOpponentDeckClass(screenshot: BufferedImage): DeckClass? {
        return screenshot.getGameOpponentClassCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_OPPONENT_CLASS_LIST)?.let {
                DeckClass.of(it).apply {
                    Logger.i("--OpponentDeckClass: $playerDeckClass!")
                }
            }
        }
    }

    fun processCardDraw(screenshot: BufferedImage): Card? {
        return screenshot.getGameCardDrawCrop().let {
            TESLTrackerData.getCard(Recognizer.recognizeCardImage(it))?.let {
                it.takeIf { it != GameState.lastCardDraw }?.apply {
                    Logger.i("--$name draw!")
                }
            }
        }
    }

    fun processMatchEnd(screenshot: BufferedImage): Boolean? {
        return screenshot.getGameWinCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.GAME_WIN)?.apply {
                Logger.i("-- Player Win!")
            }
        } ?: screenshot.getGameWin2Crop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.GAME_WIN2)?.apply {
                Logger.i("--Player Win!")
            }
        } ?: screenshot.getGameLossCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.GAME_LOSS)?.apply {
                Logger.i("--Player Lose!")
                return false
            }
        } ?: screenshot.getGameLoss2Crop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.GAME_LOSS2)?.apply {
                Logger.i("--Player Lose!")
                return false
            }
        }
    }

}