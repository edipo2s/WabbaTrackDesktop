package com.ediposouza.executor

import com.ediposouza.TESLTracker
import com.ediposouza.TESLTracker.Companion.loading
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.model.CardAttribute
import com.ediposouza.model.Deck
import com.ediposouza.model.DeckClass
import com.ediposouza.model.DeckType
import com.ediposouza.util.Logger
import javafx.scene.control.ButtonType
import javafx.scene.control.TextInputDialog
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.javafx.JavaFx
import kotlinx.coroutines.experimental.launch
import org.jsoup.Jsoup
import java.util.*

/**
 * Created by ediposouza on 12/05/17.
 */
object DeckImportExecutor {

    val LEGENDS_DECK_URL_PREFIX = "https://www.legends-decks.com/deck/"

    fun importDeckFromLegendsClick(onSuccess: (Deck) -> Unit) {
        launch(JavaFx) {
            val alert = TextInputDialog("").apply {
                title = "${TESLTracker.Companion.APP_NAME} - Importing deck from Legends-Decks"
                contentText = "Url:"
            }
            with(alert.dialogPane) {
                scene.root = javafx.scene.Group()
                javafx.stage.Stage(javafx.stage.StageStyle.UTILITY).apply {
                    for (buttonType in buttonTypes) {
                        lookupButton(buttonType).setOnMouseClicked {
                            this@with.userData = buttonType
                            close()
                        }
                    }
                    initModality(javafx.stage.Modality.APPLICATION_MODAL)
                    isAlwaysOnTop = true
                    scene = javafx.scene.Scene(this@with)
                }.showAndWait()
                val button: Optional<ButtonType> = java.util.Optional.ofNullable(userData as? ButtonType)
                if (button.isPresent && button.get() == ButtonType.OK) {
                    val url = alert.editor.characters.toString()
                    loading.show()
                    launch(CommonPool) {
                        importFromLegendsDeck(url) {
                            onSuccess(it)
                        }
                    }
                }
            }
        }
    }

    private fun importFromLegendsDeck(url: String, onSuccess: (Deck) -> Unit) {
        if (!url.startsWith(LEGENDS_DECK_URL_PREFIX)) {
            Logger.e("Invalid url")
            return
        }
        TESLTrackerData.getPatches { patches ->
            Jsoup.connect(url).get().select(".wrapper .container")?.map {
                val deckUuid = url.removePrefix(LEGENDS_DECK_URL_PREFIX).substringBefore("/")
                val deckName = it.select(".col-lg-8 h1").first().text()
                val deckCls = it.select(".deck_head_image_attributes").map {
                    val cardCls1 = it.child(0).attr("alt").toUpperCase()
                    val cardCls2 = it.child(1).attr("alt").toUpperCase()
                    DeckClass.getClass(CardAttribute.valueOf(cardCls1), CardAttribute.valueOf(cardCls2))
                }.first() ?: DeckClass.NEUTRAL
                val deckType = DeckType.of(with(it.select(".panel-body .center").first().text()) {
                    substring(indexOfLast { it == ' ' } + 1).replace("-", "")
                })
                var deckCost = 0
                var deckPatchName = ""
                with(it.select(".inner_deck_head b").map { it.text() }) {
                    val craftingCostIndexOf = indexOf("Crafting Cost:")
                    deckCost = get(craftingCostIndexOf + 1).toInt()
                    deckPatchName = with(get(craftingCostIndexOf - 1)) {
                        substring(indexOfFirst { it == '(' } + 1, indexOfFirst { it == ')' })
                    }
                }
                val deckPatch = patches.find { it.legendsDeck == deckPatchName } ?: patches.last()
                val deckCards = it.select("#deck .card_deck")?.map {
                    val text = it.text().substring(it.text().indexOfFirst { it == ' ' } + 1)
                            .replace("-1", "").replace("-2", "").replace("-3", "").trim()
                    val cardName = text.substring(0, text.indexOfLast { it == ' ' })
                    cardName.replace(" ", "").replace("-", "").replace("'", "").replace(",", "").toLowerCase() to
                            text.substring(text.indexOfLast { it == ' ' } + 1).toInt()
                }?.toMap() ?: mapOf()

                val deckOwner = it.select(".deck_page_deck_author .inner_deck_head  a").first().text()

                TESLTrackerData.saveDeck(deckUuid, deckName, deckCls, deckType, deckCost, deckPatch.uuidDate,
                        deckCards, deckOwner) { savedDeck ->
                    onSuccess(savedDeck)
                }
            }
        }
    }

}