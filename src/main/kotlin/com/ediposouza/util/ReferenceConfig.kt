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

    open val APP_MAIN_HEIGHT = 0
    open val APP_MAIN_WIDTH = 0
    open val APP_MAIN_X = 0
    open val APP_MAIN_Y = 0

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

    open val SCREEN_MAIN_MODE_HEIGHT = 0
    open val SCREEN_MAIN_MODE_WIDTH = 0
    open val SCREEN_MAIN_MODE_X = 0
    open val SCREEN_MAIN_MODE_Y = 0

    open val SCREEN_GAME_HEIGHT = 0
    open val SCREEN_GAME_WIDTH = 0
    open val SCREEN_GAME_X = 0
    open val SCREEN_GAME_Y = 0

    open val SCREEN_ARENA_CLASSES_HEIGHT = 0
    open val SCREEN_ARENA_CLASSES_WIDTH = 0
    open val SCREEN_ARENA_CLASSES_X = 0
    open val SCREEN_ARENA_CLASSES_Y = 0

    open val SCREEN_ARENA_PICKS_HEIGHT = 0
    open val SCREEN_ARENA_PICKS_WIDTH = 0
    open val SCREEN_ARENA_PICKS_X = 0
    open val SCREEN_ARENA_PICKS_Y = 0

    open val SCREEN_ARENA_PICK_NUMBER_HEIGHT = 0
    open val SCREEN_ARENA_PICK_NUMBER_WIDTH = 0
    open val SCREEN_ARENA_PICK_NUMBER_X = 0
    open val SCREEN_ARENA_PICK_NUMBER_Y = 0

    open val SCREEN_ARENA_DASHBOARD_HEIGHT = 0
    open val SCREEN_ARENA_DASHBOARD_WIDTH = 0
    open val SCREEN_ARENA_DASHBOARD_X = 0
    open val SCREEN_ARENA_DASHBOARD_Y = 0

    // -- Game --

    open val GAME_WIN_HEIGHT = 0
    open val GAME_WIN_WIDTH = 0
    open val GAME_WIN_X = 0
    open val GAME_WIN_Y = 0

    open val GAME_WIN2_HEIGHT = 0
    open val GAME_WIN2_WIDTH = 0
    open val GAME_WIN2_X = 0
    open val GAME_WIN2_Y = 0

    open val GAME_LOSS_HEIGHT = 0
    open val GAME_LOSS_WIDTH = 0
    open val GAME_LOSS_X = 0
    open val GAME_LOSS_Y = 0

    open val GAME_LOSS2_HEIGHT = 0
    open val GAME_LOSS2_WIDTH = 0
    open val GAME_LOSS2_X = 0
    open val GAME_LOSS2_Y = 0

    open val GAME_YOUR_TURN_HEIGHT = 0
    open val GAME_YOUR_TURN_WIDTH = 0
    open val GAME_YOUR_TURN_X = 0
    open val GAME_YOUR_TURN_Y = 0

    open val GAME_CARD_DRAW_HEIGHT = 0
    open val GAME_CARD_DRAW_WIDTH = 0
    open val GAME_CARD_DRAW_X = 0
    open val GAME_CARD_DRAW_Y = 0

    open val GAME_CARD_GENERATED_HEIGHT = 0
    open val GAME_CARD_GENERATED_WIDTH = 0
    open val GAME_CARD_GENERATED_X = 0
    open val GAME_CARD_GENERATED_Y = 0

    open val CARD_INITIAL_DRAW_HEIGHT = 0
    open val CARD_INITIAL_DRAW_WIDTH = 0
    open val CARD_INITIAL_DRAW_FIRST_X = 0
    open val CARD_INITIAL_DRAW_SECOND_X = 0
    open val CARD_INITIAL_DRAW_THIRD_X = 0
    open val CARD_INITIAL_DRAW_Y = 0

    open val GAME_PLAYER_FIRST_HEIGHT = 0
    open val GAME_PLAYER_FIRST_WIDTH = 0
    open val GAME_PLAYER_FIRST_X = 0
    open val GAME_PLAYER_FIRST_Y = 0

    open val GAME_PLAYER_SECOND_HEIGHT = 0
    open val GAME_PLAYER_SECOND_WIDTH = 0
    open val GAME_PLAYER_SECOND_X = 0
    open val GAME_PLAYER_SECOND_Y = 0

    open val GAME_PLAYER_CLASS_HEIGHT = 0
    open val GAME_PLAYER_CLASS_WIDTH = 0
    open val GAME_PLAYER_CLASS_X = 0
    open val GAME_PLAYER_CLASS_Y = 0

    open val GAME_OPPONENT_CLASS_HEIGHT = 0
    open val GAME_OPPONENT_CLASS_WIDTH = 0
    open val GAME_OPPONENT_CLASS_X = 0
    open val GAME_OPPONENT_CLASS_Y = 0

    // -- Arena --

    open val ARENA_PICK_CLASS_HEIGHT = 0
    open val ARENA_PICK_CLASS_WIDTH = 0
    open val ARENA_PICK_CLASS_X = 0
    open val ARENA_PICK_CLASS_Y = 0

    open val ARENA_PICK_CARD_HEIGHT = 0
    open val ARENA_PICK_CARD_WIDTH = 0
    open val ARENA_PICK_CARD_START_Y = 0
    open val ARENA_PICK_CARD_FIRST_X = 0
    open val ARENA_PICK_CARD_SECOND_X = 0
    open val ARENA_PICK_CARD_THIRD_X = 0

}