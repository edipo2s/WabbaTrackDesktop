package com.ediposouza.util.images

/**
 * Created by ediposouza on 16/03/17.
 */
abstract class ReferenceConfig {

    abstract val SCREEN_REFERENCE_WIDTH: Int
    abstract val SCREEN_REFERENCE_HEIGHT: Int

    abstract val ARENA_PICKS_REMAINING_HEIGHT: Int
    abstract val ARENA_PICKS_REMAINING_WIDTH: Int
    abstract val ARENA_PICKS_REMAINING_START_X: Int
    abstract val ARENA_PICKS_REMAINING_START_Y: Int

    abstract val ARENA_CLASS_SELECTED_HEIGHT: Int
    abstract val ARENA_CLASS_SELECTED_WIDTH: Int
    abstract val ARENA_CLASS_SELECTED_START_X: Int
    abstract val ARENA_CLASS_SELECTED_START_Y: Int

    abstract val ARENA_PICK_CARD_HEIGHT: Int
    abstract val ARENA_PICK_CARD_WIDTH: Int
    abstract val ARENA_PICK_CARD_START_Y: Int
    abstract val ARENA_PICK_CARD_FIRST_X: Int
    abstract val ARENA_PICK_CARD_SECOND_X: Int
    abstract val ARENA_PICK_CARD_THIRD_X: Int

    abstract val FULL_CARD_HEIGHT: Int
    abstract val FULL_CARD_WIDTH: Int
    abstract val FULL_CARD_START_X: Int
    abstract val FULL_CARD_START_Y: Int

    val SCREEN_REFERENCE by lazy { "$SCREEN_REFERENCE_WIDTH x $SCREEN_REFERENCE_HEIGHT" }
    val SCREEN_REFERENCE_RATIO by lazy { SCREEN_REFERENCE_WIDTH.toDouble() / SCREEN_REFERENCE_HEIGHT.toDouble() }

}