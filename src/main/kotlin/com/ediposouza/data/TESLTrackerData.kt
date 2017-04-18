package com.ediposouza.data

import com.ediposouza.TESLTracker
import com.ediposouza.model.*
import com.ediposouza.util.Logger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import tornadofx.Rest
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.concurrent.CompletableFuture

/**
 * Created by Edipo on 19/03/2017.
 */
object TESLTrackerData {

    val NODE_CARDS = "cards"
    val NODE_USERS = "users"
    val NODE_USERS_DECKS = "decks"
    val NODE_USERS_MATCHES = "matches"

    var firebaseDatabaseAPI: Rest = Rest().apply {
        baseURI = "https://tes-legends-assistant.firebaseio.com/"
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
    var decks = mutableListOf<Deck>()

    init {
        if (cardsDBFile.exists()) {
            val listCardType = object : TypeToken<ArrayList<Card>>() {}.type
            cards = Gson().fromJson(FileReader(cardsDBFile).readText(), listCardType)
            analyseCardDB()
        }
    }

    fun updateCardDB() {
        Logger.d("Updating cards database")
        cards.clear()
        with(firebaseDatabaseAPI.get("$NODE_CARDS.json").one()) {
            cards.addAll(entries.flatMap { (set, setAttrsJson) ->
                (setAttrsJson as javax.json.JsonObject).entries.flatMap { (attr, attrCardsJson) ->
                    (attrCardsJson as javax.json.JsonObject).entries.map { (cardShortName, cardAttrsJson) ->
                        val cardParser = Gson().fromJson(cardAttrsJson.toString(), FirebaseParsers.CardParser::class.java)
                        cardParser.toCard(cardShortName, CardSet.of(set), CardAttribute.of(attr))
                    }
                }
            })
        }
        analyseCardDB()
        saveCardsDB()
    }

    private fun analyseCardDB() {
        cards.groupBy { it.set.title }.forEach { set, cards ->
            Logger.d("Imported ${set.capitalize()} set with ${cards.size} cards.")
        }
        cardsAllClass = cards.map(Card::shortName)
        cardsByClass = DeckClass.values().map { deckCls ->
            deckCls.name.toLowerCase() to cards.filter {
                it.attr == CardAttribute.NEUTRAL ||
                        it.dualAttr1 == deckCls.attr1 || it.dualAttr1 == deckCls.attr2 ||
                        it.dualAttr2 == deckCls.attr1 || it.dualAttr2 == deckCls.attr2
            }.map(Card::shortName)
        }.toMap()
    }

    fun getCard(shortName: String?): Card? {
        val card = cards.find { it.shortName == shortName }
        if (card == null && shortName != null) {
            Logger.e("Card $shortName not found")
        }
        return card
    }

    fun getCardFromClass(deckClass: DeckClass): List<String> {
        return cardsByClass[deckClass.name.toLowerCase()] ?: cardsAllClass
    }

    fun updateDecksDB(onSuccess: (() -> Unit)? = null) {
        Logger.d("Updating decks database")
        decks.clear()
        if (TESLTrackerAuth.userUuid == null) {
            Logger.e("Do login to get decks")
            return
        }
        val userAccessToken = TESLTrackerAuth.userAccessToken
        val userDecksPath = "$NODE_USERS/${TESLTrackerAuth.userUuid}/$NODE_USERS_DECKS"
        with(firebaseDatabaseAPI.get("$userDecksPath/private.json?access_token=$userAccessToken").one()) {
            decks.addAll(entries.map { (deckUuid, deckAttrsJson) ->
                val deckParser = Gson().fromJson(deckAttrsJson.toString(), FirebaseParsers.DeckParser::class.java)
                deckParser.toDeck(deckUuid, true)
            })
        }
        onSuccess?.invoke()
        Logger.d("Decks: ${decks.map(Deck::name).toSet()}")
    }

    fun saveMatch(newMatch: Match, onSuccess: () -> Unit) {
        if (TESLTrackerAuth.userUuid == null) {
            Logger.e("Do login to save Matches")
            return
        }
        val userMatchesPath = "$NODE_USERS/${TESLTrackerAuth.userUuid}/$NODE_USERS_MATCHES/${newMatch.uuid}"
        val newMatchData = Gson().toJson(FirebaseParsers.MatchParser().fromMatch(newMatch))
        val userAccessToken = TESLTrackerAuth.userAccessToken
        try {
            firebaseDatabaseAPI.execute(Rest.Request.Method.PUT, "$userMatchesPath.json?access_token=$userAccessToken",
                    newMatchData.byteInputStream()) { processor ->
                processor.addHeader("Content-Type", "application/json")
            }.one().apply {
                onSuccess()
            }
        } catch (e: Exception) {
            reAuthUser {
                saveMatch(newMatch, onSuccess)
            }
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

    private fun reAuthUser(retry: Int = 0, onSuccess: () -> Unit) {
        Logger.d("start reAuth User")
        CompletableFuture.runAsync {
            if (TESLTrackerAuth.login()) {
                onSuccess()
            } else {
                if (retry < 3) {
                    Logger.e("Error while logging. Retrying...")
                    reAuthUser(retry + 1, onSuccess)
                } else {
                    Logger.e("Error while reAuth User")
                }
            }
        }
    }

}