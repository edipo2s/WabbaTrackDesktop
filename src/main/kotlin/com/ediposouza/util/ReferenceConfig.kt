package com.ediposouza.util

/**
 * Created by ediposouza on 16/03/17.
 */
abstract class ReferenceConfig {

    open val SCREEN_REFERENCE_WIDTH = 0
    open val SCREEN_REFERENCE_HEIGHT = 0

    val SCREEN_REFERENCE by lazy { "$SCREEN_REFERENCE_WIDTH x $SCREEN_REFERENCE_HEIGHT" }
    val SCREEN_REFERENCE_RATIO by lazy { SCREEN_REFERENCE_WIDTH.toDouble() / SCREEN_REFERENCE_HEIGHT.toDouble() }

    // -- APP --

    open val ARENA_PICK_SELECT_HEIGHT = 0
    open val ARENA_PICK_SELECT_WIDTH = 0
    open val ARENA_PICK_SELECT_FIRST_X = 0
    open val ARENA_PICK_SELECT_SECOND_X = 0
    open val ARENA_PICK_SELECT_THIRD_X = 0
    open val ARENA_PICK_SELECT_Y = 0

    open val ARENA_PICK_NUMBER_HEIGHT = 0
    open val ARENA_PICK_NUMBER_WIDTH = 0
    open val ARENA_PICK_NUMBER_FIRST_X = 0
    open val ARENA_PICK_NUMBER_SECOND_X = 0
    open val ARENA_PICK_NUMBER_THIRD_X = 0
    open val ARENA_PICK_NUMBER_Y = 0

    open val DECK_TRACKER_HEIGHT = 0
    open val DECK_TRACKER_WIDTH = 0
    open val DECK_TRACKER_X = 0
    open val DECK_TRACKER_Y = 0
    open val DECK_TRACKER_CARD_HEIGHT = 0
    open val DECK_TRACKER_CARD_WIDTH = 0

    open val FULL_CARD_HEIGHT = 0
    open val FULL_CARD_WIDTH = 0
    open val FULL_CARD_Y = 0
    open val FULL_CARD_X = 0
    open val FULL_CARD_DECK_IMAGE_X = 0
    open val FULL_CARD_DECK_IMAGE_Y = 0
    open val FULL_CARD_DECK_IMAGE_HEIGHT = 0
    open val FULL_CARD_DECK_IMAGE_WIDTH = 0

    // -- Screens --

    open val SCREEN_MAIN_HEIGHT = 0
    open val SCREEN_MAIN_WIDTH = 0
    open val SCREEN_MAIN_X = 0
    open val SCREEN_MAIN_Y = 0

    open val SCREEN_GAME_HEIGHT = 0
    open val SCREEN_GAME_WIDTH = 0
    open val SCREEN_GAME_X = 0
    open val SCREEN_GAME_Y = 0

    open val SCREEN_ARENA_CLASSES_HEIGHT = 0
    open val SCREEN_ARENA_CLASSES_WIDTH = 0
    open val SCREEN_ARENA_CLASSES_X = 0
    open val SCREEN_ARENA_CLASSES_Y = 0

    open val SCREEN_ARENA_CLASS_SELECT_HEIGHT = 0
    open val SCREEN_ARENA_CLASS_SELECT_WIDTH = 0
    open val SCREEN_ARENA_CLASS_SELECT_X = 0
    open val SCREEN_ARENA_CLASS_SELECT_Y = 0

    open val SCREEN_ARENA_PICK_HEIGHT = 0
    open val SCREEN_ARENA_PICK_WIDTH = 0
    open val SCREEN_ARENA_PICK_X = 0
    open val SCREEN_ARENA_PICK_Y = 0

    open val SCREEN_ARENA_DASHBOARD_HEIGHT = 0
    open val SCREEN_ARENA_DASHBOARD_WIDTH = 0
    open val SCREEN_ARENA_DASHBOARD_X = 0
    open val SCREEN_ARENA_DASHBOARD_Y = 0

    // -- Game --

    open val GAME_PLAYER_RING_HEIGHT = 0
    open val GAME_PLAYER_RING_WIDTH = 0
    open val GAME_PLAYER_RING_X = 0
    open val GAME_PLAYER_RING_Y = 0

    open val GAME_PLAYER_CLASS_HEIGHT = 0
    open val GAME_PLAYER_CLASS_WIDTH = 0
    open val GAME_PLAYER_CLASS_X = 0
    open val GAME_PLAYER_CLASS_Y = 0

    open val GAME_OPPONENT_CLASS_HEIGHT = 0
    open val GAME_OPPONENT_CLASS_WIDTH = 0
    open val GAME_OPPONENT_CLASS_X = 0
    open val GAME_OPPONENT_CLASS_Y = 0

    // -- Arena --

    open val ARENA_CLASS_SELECT_HEIGHT = 0
    open val ARENA_CLASS_SELECT_WIDTH = 0
    open val ARENA_CLASS_SELECT_X = 0
    open val ARENA_CLASS_SELECT_Y = 0

    open val ARENA_PICK_CARD_HEIGHT = 0
    open val ARENA_PICK_CARD_WIDTH = 0
    open val ARENA_PICK_CARD_START_Y = 0
    open val ARENA_PICK_CARD_FIRST_X = 0
    open val ARENA_PICK_CARD_SECOND_X = 0
    open val ARENA_PICK_CARD_THIRD_X = 0

}