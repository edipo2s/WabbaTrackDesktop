package com.ediposouza.data

import com.ediposouza.model.Card
import com.ediposouza.model.CardAttribute
import com.ediposouza.model.CardSet
import com.ediposouza.util.Logger
import tornadofx.Rest

/**
 * Created by Edipo on 19/03/2017.
 */
object TESLTrackerData {

    val CARDS = mutableListOf<Card>()

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
                        val card = Card.fromJson(it, CardAttribute.of(attr), CardSet.of(set), cards.getJsonObject(it))
                        CARDS.add(card)
                        Logger.d("$card")
                    }
                }
            }
        }
        CARDS.groupBy { it.set.name }.forEach { set, cards ->
            Logger.d("Imported ${set.capitalize()} set with ${cards.size} cards.")
        }
    }

    fun getCard(shortName: String?): Card? {
        return CARDS.find { it.shortName == shortName }
    }

}