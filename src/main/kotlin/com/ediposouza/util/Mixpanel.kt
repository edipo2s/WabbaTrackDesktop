package com.ediposouza.util

import com.ediposouza.TESLTracker
import com.ediposouza.data.TESLTrackerAuth
import com.ediposouza.model.Card
import com.ediposouza.model.DeckClass
import com.google.gson.JsonParser
import com.mixpanel.mixpanelapi.ClientDelivery
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject
import oshi.SystemInfo
import java.io.InputStreamReader

/**
 * Created by Edipo on 09/04/2017.
 */
object Mixpanel {

    private val keysFileStream by lazy { InputStreamReader(TESLTracker::class.java.getResourceAsStream("/client_secrets.json")) }

    private val messageBuilder by lazy {
        val json = JsonParser().parse(keysFileStream).asJsonObject
        val mixpanelKey = json.get("mixpanel_api_key").asString
        keysFileStream.close()
        MessageBuilder(mixpanelKey)
    }

    private val mixpanelAPI by lazy { MixpanelAPI() }

    private val GUEST = "Guest"
    private val SERIAL by lazy { SystemInfo().hardware.computerSystem.serialNumber }

    fun trackUser() {
        launch(CommonPool) {
            val userName = TESLTrackerAuth.userName ?: GUEST
            val userProps = mutableMapOf("\$first_name" to userName)
            if (TESLTrackerAuth.isUserLogged()) {
                userProps.put("\$email", TESLTrackerAuth.userEmail ?: "")
            }
            val delivery = ClientDelivery().apply {
                addMessage(messageBuilder.set(TESLTrackerAuth.userUuid ?: GUEST, JSONObject(userProps)))
            }
            mixpanelAPI.deliver(delivery)
        }
    }

    fun postEventShowStatistics() = postEvent("ShowStatistics")
    fun postEventGameDetected() = postEvent("GameDetected")

    fun postEventGameResult(playerCls: DeckClass, opponentCls: DeckClass, result: String) {
        postEvent("GameResult", mutableMapOf(
                "playerCls" to playerCls.name,
                "opponentCls" to opponentCls.name,
                "result" to result
        ))
    }

    fun postEventDeckImported(deckName: String) {
        postEvent("DeckImported", mutableMapOf("name" to deckName))
    }

    fun postEventShowDeckTrackerFromMyDecks(deckName: String) {
        postEvent("ShowDeckTracker", mutableMapOf(
                "name" to deckName,
                "mode" to "MyDecks"
        ))
    }

    fun postEventShowDeckTrackerFromImportedDecks(deckName: String) {
        postEvent("ShowDeckTracker", mutableMapOf(
                "name" to deckName,
                "mode" to "ImportedDecks"
        ))
    }

    fun postEventShowDeckTrackerFromArenaDeck() = postEvent("ShowDeckTracker", mutableMapOf("mode" to "ArenaDeck"))
    fun postEventHideDeckTracker() = postEvent("HideDeckTracker")
    fun postEventArenaStart(cls: DeckClass) = postEvent("ArenaStart", mutableMapOf("Cls" to cls.name))
    fun postEventArenaPick(card: Card) = postEvent("ArenaPick", mutableMapOf("Card" to card.shortName))
    fun postEventAndroidTESLegendsTracker() = postEvent("AndroidTESLegendsTracker")

    private fun postEvent(eventName: String, eventProps: MutableMap<String, String> = mutableMapOf()) {
        launch(CommonPool) {
            val userID = TESLTrackerAuth.userUuid ?: GUEST
            val pcID = TESLTrackerAuth.userUuid ?: SERIAL
            val delivery = ClientDelivery().apply {
                addMessage(messageBuilder.event(userID, eventName, JSONObject(eventProps.apply {
                    put("pcId", pcID)
                })))
            }
            mixpanelAPI.deliver(delivery)
        }
    }

}