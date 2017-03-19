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
        Logger.d("Updating cards database")
        with(firebaseDB.get("cards.json").one()) {
            keys.forEach {
                val set = it
                val setAttrs = getJsonObject(set)
                setAttrs.keys.forEach {
                    val attr = it
                    val cards = setAttrs.getJsonObject(attr)
                    cards.keys.forEach {
                        val card = CardModel(it, attr, set).apply {
                            updateModel(cards.getJsonObject(it))
                        }
                        CARDS.add(card)
//                        Logger.d("$card")
                    }
                }
            }
        }
        CARDS.groupBy { it.set }.forEach { set, cards ->
            Logger.d("Imported ${set.capitalize()} set with ${cards.size} cards.")
        }
    }

    fun getCard(shortName: String?): CardModel? {
        return CARDS.find { it.shortname == shortName }
    }

}