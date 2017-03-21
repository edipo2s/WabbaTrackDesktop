package com.ediposouza.data

/**
 * Created by Edipo on 18/03/2017.
 */
object DHash {

    val SCREEN_MAIN = "Main"
    val SCREEN_GAME = "Game"
    val SCREEN_ARENA_CLASSES = "ArenaClasses"
    val SCREEN_ARENA_CLASS_SELECT = "ArenaClassSelect"
    val SCREEN_ARENA_PICK = "ArenaPick"
    val SCREEN_ARENA_DASHBOARD = "ArenaDashboard"

    val SCREENS_LIST = mapOf(
            "" to "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
            SCREEN_MAIN to "0c038f8253c19aa065d02a28159c0916060a63093009b801ba81bc233e0e",
            SCREEN_GAME to "855c454b862e679613034db1c4ddd26a61ba746e3836d6aa9a961d15c950",
            SCREEN_ARENA_CLASSES to "481191cc6d812b026401da58a80424a26858492ac468101419889c324e29",
            SCREEN_ARENA_CLASS_SELECT to "0000201b1400294014200a000580024001a0005000280014000a00a54008",
            SCREEN_ARENA_PICK to "000000000024084255248c614620a25053a2495514000000040000000080",
            SCREEN_ARENA_DASHBOARD to "01002800006038008a26003ba0b4bdf974789bbdeb7c69b2119375c6bcca"
    )

    val CLASS_SELECTED_LIST = mapOf(
            "" to "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",
            "Assassin" to "e683b141f9606c301e102f496b16318a1b658d0843082108100400028000",
            "Crusader" to "d811b008f11570a1e9b8317809298b8e03c70363025926383d8a0cc7aad4",
            "Mage" to "db124408a200a100150069402990288a02640348020c0128000000000000",
            "Scout" to "25019560292005f10438635c29c6309793678d09c60ea101300008000000",
            "Sorcerer" to "5274a99204310258010c61442992318913654d18060c0100000000000000",
            "Spellword" to "25f419a585cc527a235de96a4929d2964364354c16080509010000000000"
    )
}