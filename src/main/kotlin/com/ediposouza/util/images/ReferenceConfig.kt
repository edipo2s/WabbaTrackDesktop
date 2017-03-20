package com.ediposouza.util.images

/**
 * Created by ediposouza on 16/03/17.
 */
abstract class ReferenceConfig {

    open val SCREEN_REFERENCE_WIDTH = 0
    open val SCREEN_REFERENCE_HEIGHT = 0

    open val ARENA_PICKS_REMAINING_HEIGHT = 0
    open val ARENA_PICKS_REMAINING_WIDTH = 0
    open val ARENA_PICKS_REMAINING_X = 0
    open val ARENA_PICKS_REMAINING_Y = 0

    open val ARENA_CLASS_SELECTED_HEIGHT = 0
    open val ARENA_CLASS_SELECTED_WIDTH = 0
    open val ARENA_CLASS_SELECTED_X = 0
    open val ARENA_CLASS_SELECTED_Y = 0

    open val ARENA_PICK_CARD_HEIGHT = 0
    open val ARENA_PICK_CARD_WIDTH = 0
    open val ARENA_PICK_CARD_START_Y = 0
    open val ARENA_PICK_CARD_FIRST_X = 0
    open val ARENA_PICK_CARD_SECOND_X = 0
    open val ARENA_PICK_CARD_THIRD_X = 0

    open val ARENA_PICK_VALUE_HEIGHT = 0
    open val ARENA_PICK_VALUE_WIDTH = 0
    open val ARENA_PICK_VALUE_FIRST_X = 0
    open val ARENA_PICK_VALUE_SECOND_X = 0
    open val ARENA_PICK_VALUE_THIRD_X = 0
    open val ARENA_PICK_VALUE_Y = 0

    open val FULL_CARD_HEIGHT = 0
    open val FULL_CARD_WIDTH = 0
    open val FULL_CARD_X = 0
    open val FULL_CARD_Y = 0

    val SCREEN_REFERENCE by lazy { "$SCREEN_REFERENCE_WIDTH x $SCREEN_REFERENCE_HEIGHT" }
    val SCREEN_REFERENCE_RATIO by lazy { SCREEN_REFERENCE_WIDTH.toDouble() / SCREEN_REFERENCE_HEIGHT.toDouble() }

}