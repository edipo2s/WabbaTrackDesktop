package com.ediposouza.data

import com.ediposouza.model.CardModel
import com.ediposouza.util.Logger
import tornadofx.Rest

/**
 * Created by Edipo on 19/03/2017.
 */
object TESLTrackerData {

    val CARDS = mutableListOf<CardModel>()

    fun updateCardDB(firebaseDB: Rest) {
        CARDS.clear()
        Logger.d("Getting json")
        with(firebaseDB.get("cards/core.json").one()) {
            keys.forEach {
                val attr = it
                val cards = getJsonObject(attr)
                cards.keys.forEach {
                    val card = com.ediposouza.model.CardModel(it, attr).apply {
                        updateModel(cards.getJsonObject(it))
                    }
                    CARDS.add(card)
//                    Logger.d("$card")
                }
            }
        }
    }

}