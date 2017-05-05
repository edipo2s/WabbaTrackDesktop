package com.ediposouza.data

import com.ediposouza.TESLTracker
import com.ediposouza.extensions.getMD5
import com.ediposouza.model.*
import com.ediposouza.teslesgendstracker.data.Patch
import com.ediposouza.util.Logger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javafx.application.Platform
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import tornadofx.Rest
import java.io.*
import java.net.URL
import java.time.LocalDateTime

/**
 * Created by Edipo on 19/03/2017.
 */
object TESLTrackerData {

    val NODE_CARDS = "cards"
    val NODE_PATCHES = "patches"
    val NODE_USERS = "users"
    val NODE_USERS_DECKS = "decks"
    val NODE_DECKS = "decks"
    val NODE_DECKS_PRIVATE = "private"
    val NODE_DECKS_PUBLIC = "public"
    val NODE_USERS_MATCHES = "matches"

    val NODE_WABBATRACK = "wabbatrack"

    val UPDATE_FILE_NAME = "lastVersion.exe"

    var firebaseDatabaseAPI: Rest = Rest().apply {
        baseURI = "https://tes-legends-assistant.firebaseio.com/"
    }

    private val cardsDBFile by lazy {
        File(File(TESLTracker.jarPath).parentFile, "data").let {
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
            try {
                val listCardType = object : TypeToken<ArrayList<Card>>() {}.type
                cards = Gson().fromJson(FileReader(cardsDBFile).readText(), listCardType)
                analyseCardDB()
            } catch (e: Exception) {
                Logger.e("Error while loading cardsDB: ${e.message}")
                cardsDBFile.delete()
            }
        }
    }

    fun updateCardDB() {
        Logger.d("Updating cards database")
        launch(CommonPool) {
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
        launch(CommonPool) {
            Logger.d("Updating decks database")
            decks.clear()
            if (!TESLTrackerAuth.isUserLogged()) {
                Logger.e("Do login to get decks")
            } else {
                val userAccessToken = TESLTrackerAuth.userAccessToken
                val userDecksPath = "$NODE_USERS/${TESLTrackerAuth.userUuid}/$NODE_USERS_DECKS"
                with(firebaseDatabaseAPI.get("$userDecksPath/$NODE_DECKS_PRIVATE.json?access_token=$userAccessToken").one()) {
                    decks.addAll(entries.map { (deckUuid, deckAttrsJson) ->
                        val deckParser = Gson().fromJson(deckAttrsJson.toString(), FirebaseParsers.DeckParser::class.java)
                        deckParser.toDeck(deckUuid, true)
                    })
                }
                val userOwnerFilter = "orderBy=%22owner%22&equalTo=%22${TESLTrackerAuth.userUuid}%22"
                with(firebaseDatabaseAPI.get("$NODE_DECKS/$NODE_DECKS_PUBLIC.json?$userOwnerFilter&access_token=$userAccessToken").one()) {
                    decks.addAll(entries.map { (deckUuid, deckAttrsJson) ->
                        val deckParser = Gson().fromJson(deckAttrsJson.toString(), FirebaseParsers.DeckParser::class.java)
                        deckParser.toDeck(deckUuid, false)
                    })
                }
                decks.sortBy(Deck::name)
                Platform.runLater {
                    onSuccess?.invoke()
                    Logger.d("Decks: ${decks.map(Deck::name).toSet()}")
                }
            }
        }
    }

    fun getPatches(onSuccess: (List<Patch>) -> Unit) {
        with(firebaseDatabaseAPI.get("$NODE_PATCHES.json").one()) {
            onSuccess(entries.map { (patchUuid, patchAttrsJson) ->
                val matchParser = Gson().fromJson(patchAttrsJson.toString(), FirebaseParsers.PatchParser::class.java)
                matchParser.toPatch(patchUuid)
            })
        }
    }

    fun deleteDecks(deck: Deck, onSuccess: () -> Unit) {
        launch(CommonPool) {
            if (!TESLTrackerAuth.isUserLogged()) {
                Logger.e("Do login to get decks")
            } else {
                val userDecksPath = "$NODE_USERS/${TESLTrackerAuth.userUuid}/$NODE_USERS_DECKS/$NODE_DECKS_PRIVATE"
                val decksPath = userDecksPath.takeIf { deck.private } ?: "$NODE_DECKS/$NODE_DECKS_PUBLIC"
                val userAccessToken = TESLTrackerAuth.userAccessToken
                with(firebaseDatabaseAPI.delete("$decksPath/${deck.uuid}.json?access_token=$userAccessToken").consume()) {
                    onSuccess()
                }
            }
        }
    }

    fun saveDeck(uuid: String, name: String, cls: DeckClass, type: DeckType, cost: Int, patch: String,
                 cards: Map<String, Int>, owner: String, onSuccess: (Deck) -> Unit) {
        var createdAt = LocalDateTime.now().withNano(0)
        var views = 0
        val likes = mutableListOf<String>()
        val comments = mutableListOf<DeckComment>()
        with(firebaseDatabaseAPI.get("$NODE_DECKS/$NODE_DECKS_PUBLIC.json?orderBy=%22\$key%22&equalTo=%22$uuid%22").one()) {
            if (entries.size > 0) {
                val entry = entries.first()
                val deckParser = Gson().fromJson(entry.value.toString(), FirebaseParsers.DeckParser::class.java)
                with(deckParser.toDeck(entry.key, true)) {
                    createdAt = this.createdAt
                    views = this.views
                    likes.addAll(likes)
                    comments.addAll(comments)
                }
            }
        }
        val newDeck = Deck(uuid, name, owner, false, type, cls, cost, createdAt,
                LocalDateTime.now().withNano(0), patch, likes, views, cards, listOf(), comments)
        val deckPath = "$NODE_DECKS/$NODE_DECKS_PUBLIC/$uuid"
        val deckData = Gson().toJson(FirebaseParsers.DeckParser().fromDeck(newDeck))
        try {
            firebaseDatabaseAPI.execute(Rest.Request.Method.PUT, "$deckPath.json", deckData.byteInputStream()) { processor ->
                processor.addHeader("Content-Type", "application/json")
            }.one().apply {
                onSuccess(newDeck)
            }
        } catch (e: Exception) {
            Logger.e("Error while saving deck: ${e.message}")
        }
    }

    fun saveMatch(newMatch: Match, onSuccess: () -> Unit) {
        if (!TESLTrackerAuth.isUserLogged()) {
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
        launch(CommonPool) {
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

    fun checkForUpdate(retry: Int = 0) {
        with(firebaseDatabaseAPI.get("$NODE_WABBATRACK.json").one()) {
            val lastVersion = entries.find { it.key == "lastVersion" }?.value.toString()
            if (lastVersion == TESLTracker.APP_VERSION) {
                Logger.d("App is updated")
                return
            }
            val md5 = entries.find { it.key == "md5" }?.value.toString().replace("\"", "")
            val downloadedUpdateFile = File(UPDATE_FILE_NAME)
            if (downloadedUpdateFile.exists()) {
                if (downloadedUpdateFile.getMD5() == md5) {
                    TESLTracker.showRestartToUpdateNow()
                    return
                } else {
                    downloadedUpdateFile.delete()
                }
            }
            Logger.i("New version detect, downloading version $lastVersion")
            val url = entries.find { it.key == "url" }?.value.toString()
            downloadFile(url, UPDATE_FILE_NAME) {
                Logger.d("Download Success")
                val downloadedMD5 = downloadedUpdateFile.getMD5()
                if (downloadedMD5 == md5) {
                    TESLTracker.showRestartToUpdateNow()
                } else {
                    Logger.e("Update file md5 don't match \nActual: $downloadedMD5 \nExpected: $md5")
                    downloadedUpdateFile.delete()
                    if (retry < 3) {
                        checkForUpdate(retry + 1)
                    }
                }
            }
        }
    }

    fun restartAppToUpdate() {
        val mainFile = File(TESLTracker.FILE_NAME)
        if (mainFile.delete()) {
            File(UPDATE_FILE_NAME).renameTo(mainFile)
            Runtime.getRuntime().exec(TESLTracker.FILE_NAME)
            TESLTracker.doExit()
        }
    }

    private fun downloadFile(fileUrl: String, fileName: String, onSuccess: () -> Unit) {
        val data = ByteArray(1024)
        var bis: BufferedInputStream? = null
        var fos: FileOutputStream? = null
        try {
            bis = BufferedInputStream(URL(fileUrl.replace("\"", "")).openStream())
            fos = FileOutputStream(fileName)
            do {
                val count = bis.read(data, 0, 1024)
                if (count > 0) {
                    fos.write(data, 0, count)
                }
            } while (count != -1)
            onSuccess()
        } catch (e: Exception) {
            Logger.e("Error while downloading update: \n ${e.message}")
        } finally {
            bis?.close()
            fos?.close()
        }
    }

}