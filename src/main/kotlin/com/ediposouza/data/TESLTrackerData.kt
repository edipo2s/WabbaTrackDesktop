package com.ediposouza.data

import com.ediposouza.model.Card
import com.ediposouza.model.CardAttribute
import com.ediposouza.model.CardSet
import com.ediposouza.model.FirebaseParsers
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
            }

            override fun onCancelled(error: FirebaseError?) {
                Logger.e(error?.toException())
            }
        })
    }

    fun getCard(shortName: String?): Card? {
        return cards.find { it.shortName == shortName }
    }

}