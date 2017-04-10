package com.ediposouza.handler

import com.ediposouza.data.DHash
import com.ediposouza.data.DHashCards
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.extensions.*
import com.ediposouza.model.Card
import com.ediposouza.model.DeckClass
import com.ediposouza.util.Recognizer
import java.awt.image.BufferedImage

/**
 * Created by Edipo on 27/03/2017.
 */
object GameHandler {

    fun processPlayerGoFirst(screenshot: BufferedImage): Boolean? {
        return screenshot.getGamePlayerFirstCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.PLAYER_GAME_FIRST)
        } ?: screenshot.getGamePlayerSecondCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.PLAYER_GAME_SECOND)
        }
    }

    fun processPlayerDeckClass(screenshot: BufferedImage): DeckClass? {
        return screenshot.getGamePlayerClassCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_PLAYER_CLASS_LIST)?.let {
                DeckClass.of(it)
            }
        }
    }

    fun processOpponentDeckClass(screenshot: BufferedImage): DeckClass? {
        return screenshot.getGameOpponentClassCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_OPPONENT_CLASS_LIST)?.let {
                DeckClass.of(it)
            }
        }
    }

    fun processFirstCardDraws(screenshot: BufferedImage): Triple<String, String, String>? {
        return screenshot.getGameInitialCardDrawCrop(1).let {
            Recognizer.recognizeImageInMap(it, DHashCards.LIST)?.let { first ->
                screenshot.getGameInitialCardDrawCrop(2).let {
                    Recognizer.recognizeImageInMap(it, DHashCards.LIST)?.let { second ->
                        screenshot.getGameInitialCardDrawCrop(3).let {
                            Recognizer.recognizeImageInMap(it, DHashCards.LIST)?.let { third ->
                                Triple(first, second, third)
                            }
                        }
                    }
                }
            }
        }
    }

    fun processCardDraw(screenshot: BufferedImage): Card? {
        return screenshot.getGameCardDrawCrop().let {
            TESLTrackerData.getCard(Recognizer.recognizeCardImage(it))
        }
    }

    fun processMatchEnd(screenshot: BufferedImage): Boolean? {
        return screenshot.getGameWinCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.GAME_WIN)
        } ?: screenshot.getGameWin2Crop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.GAME_WIN2)
        } ?: screenshot.getGameLossCrop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.GAME_LOSS)?.apply {
                return false
            }
        } ?: screenshot.getGameLoss2Crop().let {
            Recognizer.recognizeImageInMap(it, DHash.GAME_ITEMS_LIST).equalsOrNull(DHash.GAME_LOSS2)?.apply {
                return false
            }
        }
    }

}