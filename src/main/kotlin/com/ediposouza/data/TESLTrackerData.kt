package com.ediposouza.data

import com.ediposouza.TESLTracker
import com.ediposouza.model.*
import com.ediposouza.util.Logger
import com.firebase.client.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tornadofx.Rest
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * Created by Edipo on 19/03/2017.
 */
object TESLTrackerData {

    val FIREBASE_URI = "https://tes-legends-assistant.firebaseio.com/"

    val NODE_CARDS = "cards"
    val NODE_USERS = "users"
    val NODE_USERS_MATCHES = "matches"

    val firebase by lazy { Firebase(FIREBASE_URI) }
    var firebaseDatabaseAPI: Rest = Rest().apply {
        baseURI = FIREBASE_URI
    }

    private val cardsDBFile by lazy {
        File("${TESLTracker.jarPath}/data").let {
            if (!it.exists()) {
                it.mkdirs()
            }
            File(it, "cardsDB.json")
        }
    }

    var cards = mutableListOf<Card>()
    var cardsAllClass = listOf<String>()
    var cardsByClass = mapOf<String, List<String>>()

    init {
        if (cardsDBFile.exists()) {
            val listCardType = object : TypeToken<ArrayList<Card>>() {}.type
            cards = Gson().fromJson(FileReader(cardsDBFile).readText(), listCardType)
            cards.groupBy { it.set.name }.forEach { set, cards ->
                Logger.d("Imported ${set.capitalize()} set with ${cards.size} cards.")
            }
        }
    }

    fun initialize() {
    }

    fun updateCardDB() {
        Logger.d("Updating cards database")
        firebase.app.goOnline()
        firebase.child(NODE_CARDS).addListenerForSingleValueEvent(object : ValueEventListener {
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
                firebase.app.goOffline()
                saveCardsDB()
            }

            override fun onCancelled(error: FirebaseError?) {
                Logger.e(error?.toException())
                firebase.app.goOffline()
            }
        })
    }

    fun getCard(shortName: String?): Card? {
        return cards.find { it.shortName == shortName }
    }

    fun getCardFromClass(deckClass: DeckClass): List<String> {
        return cardsByClass[deckClass.name.toLowerCase()] ?: cardsAllClass
    }

    fun saveMatch(newMatch: Match, onSuccess: () -> Unit) {
        if (TESLTrackerAuth.userUuid == null) {
            Logger.e("Do login to save Matchs")
            return
        }
        val userMatchesPath = "$NODE_USERS/${TESLTrackerAuth.userUuid}/$NODE_USERS_MATCHES/${newMatch.uuid}"
        val newMatchData = Gson().toJson(FirebaseParsers.MatchParser().fromMatch(newMatch))
        val userAccessToken = TESLTrackerAuth.userAccessToken
        firebaseDatabaseAPI.execute(Rest.Request.Method.PUT, "$userMatchesPath.json?access_token=$userAccessToken",
                newMatchData.byteInputStream()) { processor ->
            processor.addHeader("Content-Type", "application/json")
        }.one().apply {
            onSuccess()
        }
    }

    private fun saveCardsDB() {
        try {
            val cardsJson = Gson().toJson(cards)
            FileWriter(cardsDBFile).apply {
                write(cardsJson)
                flush()
            }
        } catch (e: Exception) {
            Logger.e(e)
        }
    }

    open class SimpleChildEventListener : ChildEventListener {
        override fun onCancelled(p0: FirebaseError?) {}

        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}

        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}

        override fun onChildAdded(p0: DataSnapshot?, p1: String?) {}

        override fun onChildRemoved(p0: DataSnapshot?) {}
    }

}