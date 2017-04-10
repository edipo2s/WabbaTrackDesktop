package com.ediposouza.model

/**
 * Created by ediposouza on 25/01/17.
 */
enum class MatchMode {

    RANKED,
    CASUAL,
    ARENA,
    PRATICE

}

data class MatchDeck(

        val name: String? = null,
        val cls: DeckClass,
        val type: DeckType,
        val deck: String? = null,
        val version: String? = null

)

data class Match(

        val uuid: String,
        val first: Boolean,
        val player: MatchDeck,
        val opponent: MatchDeck,
        val mode: MatchMode,
        val season: String,
        val rank: Int,
        val legend: Boolean,
        val win: Boolean

)