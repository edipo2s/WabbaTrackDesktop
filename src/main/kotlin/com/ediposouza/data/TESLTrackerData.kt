package com.ediposouza.data

import com.ediposouza.model.*
import com.ediposouza.util.Logger
import com.firebase.client.DataSnapshot
import com.firebase.client.Firebase
import com.firebase.client.FirebaseError
import com.firebase.client.ValueEventListener

/**
 * Created by Edipo on 19/03/2017.
 */
object TESLTrackerData {

    val firebase by lazy { Firebase("https://tes-legends-assistant.firebaseio.com/") }

    val cards = mutableListOf<Card>()
    var cardsAllClass = listOf<String>()
    var cardsByClass = mapOf<String, List<String>>()

    fun updateCardDB() {
        cards.clear()
        Logger.d("Updating cards database")
        firebase.child("cards").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(ds: DataSnapshot?) {
                cards.clear()
                cards.addAll(ds?.children?.flatMap {
                    val set = CardSet.of(it.key)
                    it.children.flatMap {
                        val attr = CardAttribute.of(it.key)
                        it.children.map {
                            it.getValue(FirebaseParsers.CardParser::class.java).toCard(it.key, set, attr)
                        }
                    }
                } ?: listOf())
                cards.groupBy { it.set.name }.forEach { set, cards ->
                    Logger.d("Imported ${set.capitalize()} set with ${cards.size} cards.")
                }
                cardsAllClass = cards.map(Card::shortName)
                cardsByClass = DeckClass.values().map { deckCls ->
                    deckCls.name.toLowerCase() to cards.filter {
                        it.dualAttr1 == deckCls.attr1 || it.dualAttr1 == deckCls.attr2 ||
                                it.dualAttr2 == deckCls.attr1 || it.dualAttr2 == deckCls.attr2
                    }.map(Card::shortName)
                }.toMap()
            }

            override fun onCancelled(error: FirebaseError?) {
                Logger.e(error?.toException())
            }
        })
    }

    fun getCard(shortName: String?): Card? {
        return cards.find { it.shortName == shortName }
    }

    fun getCardFromClass(cls: String?): List<String> {
        return cardsByClass[cls?.toLowerCase()] ?: cardsAllClass
    }

}