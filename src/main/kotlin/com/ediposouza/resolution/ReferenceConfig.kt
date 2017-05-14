package com.ediposouza.resolution

/**
 * Created by ediposouza on 16/03/17.
 */
abstract class ReferenceConfig {

    open val SCREEN_REFERENCE_WIDTH = 0
    open val SCREEN_REFERENCE_HEIGHT = 0

    val SCREEN_REFERENCE by lazy { "${SCREEN_REFERENCE_WIDTH}x$SCREEN_REFERENCE_HEIGHT" }
    val SCREEN_REFERENCE_RATIO by lazy { SCREEN_REFERENCE_WIDTH.toDouble() / SCREEN_REFERENCE_HEIGHT.toDouble() }

    // -- APP --

    val APP_MAIN_X by lazy { SCREEN_REFERENCE_WIDTH - APP_MAIN_WIDTH / 2 }
    open val APP_MAIN_HEIGHT = 0
    open val APP_MAIN_WIDTH = 0
    open val APP_MAIN_Y = 0

    open val DECK_TRACKER_WIDTH = 0
    open val DECK_TRACKER_X = 0
    open val DECK_TRACKER_Y = 0
    open val DECK_TRACKER_CARD_HEIGHT = 0
    open val DECK_TRACKER_CARD_WIDTH = 0

    open val ARENA_PICK_WIDGET_FIRST_X = 0
    open val ARENA_PICK_WIDGET_SECOND_X = 0
    open val ARENA_PICK_WIDGET_THIRD_X = 0
    open val ARENA_PICK_WIDGET_Y = 0
    open val ARENA_PICK_WIDGET_HEIGHT = 0
    open val ARENA_PICK_WIDGET_WIDTH = 0

    val FULL_CARD_HEIGHT = 240
    val FULL_CARD_WIDTH = 212
    val FULL_CARD_X = 80
    val FULL_CARD_Y = 100
    val FULL_CARD_DECK_IMAGE_X = 50
    val FULL_CARD_DECK_IMAGE_Y = 115
    val FULL_CARD_DECK_IMAGE_HEIGHT = 80
    val FULL_CARD_DECK_IMAGE_WIDTH = 290

    open val BASE_CARD_HEIGHT = 0
    open val BASE_CARD_WIDTH = 0

    // -- Screens --

    open val SCREEN_MAIN_HEIGHT = 0
    open val SCREEN_MAIN_WIDTH = 0
    open val SCREEN_MAIN_X = 0
    open val SCREEN_MAIN_Y = 0

    open val SCREEN_MAIN_MODE_HEIGHT = 0
    open val SCREEN_MAIN_MODE_WIDTH = 0
    open val SCREEN_MAIN_MODE_X = 0
    open val SCREEN_MAIN_MODE_Y = 0

    open val SCREEN_DECK_BUILDER_X = 0
    open val SCREEN_DECK_BUILDER_Y = 0
    open val SCREEN_DECK_BUILDER_HEIGHT = 0
    open val SCREEN_DECK_BUILDER_WIDTH = 0

    open val SCREEN_DECK_BUILDER_EMPTY_X = 0
    open val SCREEN_DECK_BUILDER_EMPTY_Y = 0
    open val SCREEN_DECK_BUILDER_EMPTY_HEIGHT = 0
    open val SCREEN_DECK_BUILDER_EMPTY_WIDTH = 0

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

    open val GAME_CARD_DRAW_X = 0
    open val GAME_CARD_DRAW_Y = 0

    open val GAME_CARD_DRAW_PROPHECY_HEIGHT = 0
    open val GAME_CARD_DRAW_PROPHECY_WIDTH = 0
    open val GAME_CARD_DRAW_PROPHECY_X = 0
    open val GAME_CARD_DRAW_PROPHECY_Y = 0

    open val GAME_CARD_GENERATED_HEIGHT = 0
    open val GAME_CARD_GENERATED_WIDTH = 0
    open val GAME_CARD_GENERATED_X = 0
    open val GAME_CARD_GENERATED_Y = 0

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

    open val GAME_PLAYER_RANK_HEIGHT = 0
    open val GAME_PLAYER_RANK_WIDTH = 0
    open val GAME_PLAYER_RANK_X = 0
    open val GAME_PLAYER_RANK_Y = 0

    open val GAME_OPPONENT_RANK_HEIGHT = 0
    open val GAME_OPPONENT_RANK_WIDTH = 0
    open val GAME_OPPONENT_RANK_X = 0
    open val GAME_OPPONENT_RANK_Y = 0

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

    open val ARENA_PICK_CARD_START_Y = 0
    open val ARENA_PICK_CARD_FIRST_X = 0
    open val ARENA_PICK_CARD_SECOND_X = 0
    open val ARENA_PICK_CARD_THIRD_X = 0

    open val ARENA_PICK_SELECT_HEIGHT = 0
    open val ARENA_PICK_SELECT_WIDTH = 0
    open val ARENA_PICK_SELECT_FIRST_X = 0
    open val ARENA_PICK_SELECT_SECOND_X = 0
    open val ARENA_PICK_SELECT_THIRD_X = 0
    open val ARENA_PICK_SELECT_Y = 0

    // -- Deck --

    open val DECK_BUILDER_CLEAR_FILTER_MAGICKA_0_X = 0
    open val DECK_BUILDER_CLEAR_FILTER_MAGICKA_0_Y = 0
    open val DECK_BUILDER_CLEAR_FILTER_MAGICKA_1_X = 0
    open val DECK_BUILDER_CLEAR_FILTER_MAGICKA_1_Y = 0
    open val DECK_BUILDER_CLEAR_FILTER_ATTRIBUTES_X = 0
    open val DECK_BUILDER_CLEAR_FILTER_ATTRIBUTES_Y = 0
    open val DECK_BUILDER_CLEAR_FILTER_RARITY_X = 0
    open val DECK_BUILDER_CLEAR_FILTER_RARITY_Y = 0
    open val DECK_BUILDER_CLEAR_FILTER_RARITY_ALL_X = 0
    open val DECK_BUILDER_CLEAR_FILTER_RARITY_ALL_Y = 0
    open val DECK_BUILDER_CLEAR_FILTER_SET_X = 0
    open val DECK_BUILDER_CLEAR_FILTER_SET_Y = 0
    open val DECK_BUILDER_CLEAR_FILTER_SET_ALL_X = 0
    open val DECK_BUILDER_CLEAR_FILTER_SET_ALL_Y = 0
    open val DECK_BUILDER_CLEAR_CARDS_FIRST_CARD_X = 0
    open val DECK_BUILDER_CLEAR_CARDS_FIRST_CARD_Y = 0

    open val DECK_BUILDER_SEARCH_FIELD_X = 0
    open val DECK_BUILDER_SEARCH_FIELD_Y = 0
    open val DECK_BUILDER_FIRST_LINE_CLICK_CARD_X = 0
    open val DECK_BUILDER_FIRST_LINE_CLICK_CARD_DISTANCE_X = 0
    open val DECK_BUILDER_FIRST_LINE_CLICK_CARD_Y = 0

    open val DECK_BUILDER_DECK_COVER_X = 0
    open val DECK_BUILDER_DECK_COVER_Y = 0
    open val DECK_BUILDER_DECK_COVER_CONFIRM_X = 0
    open val DECK_BUILDER_DECK_COVER_CONFIRM_Y = 0

    open val DECK_BUILDER_CARD_HEIGHT = 0
    open val DECK_BUILDER_CARD_WIDTH = 0
    open val DECK_BUILDER_CARD_1_X = 0
    open val DECK_BUILDER_CARD_2_X = 0
    open val DECK_BUILDER_CARD_3_X = 0
    open val DECK_BUILDER_CARD_4_X = 0
    open val DECK_BUILDER_CARD_5_X = 0
    open val DECK_BUILDER_FIRST_LINE_CARD_Y = 0
    open val DECK_BUILDER_SECOND_LINE_CARD_Y = 0

    open val DECK_BUILDER_NONE_LEFT_HEIGHT = 0
    open val DECK_BUILDER_NONE_LEFT_WIDTH = 0
    open val DECK_BUILDER_NONE_LEFT_LINE_CARD_1_X = 0
    open val DECK_BUILDER_NONE_LEFT_LINE_CARD_2_X = 0
    open val DECK_BUILDER_NONE_LEFT_LINE_CARD_3_X = 0
    open val DECK_BUILDER_NONE_LEFT_LINE_CARD_4_X = 0
    open val DECK_BUILDER_NONE_LEFT_LINE_CARD_5_X = 0
    open val DECK_BUILDER_NONE_LEFT_LINE_CARD_Y = 0

}