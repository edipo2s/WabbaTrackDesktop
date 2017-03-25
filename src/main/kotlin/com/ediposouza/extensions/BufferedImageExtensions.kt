package com.ediposouza.extensions

import com.ediposouza.TESLTracker
import com.ediposouza.util.ImageFuncs
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by Edipo on 19/03/2017.
 */

fun BufferedImage.saveCroppedImage() {
    val tmpFileName = "recognize_${System.currentTimeMillis()}.png"
    File("src/main/resources/Crops/Tmp").apply {
        if (!exists()) {
            mkdir()
        }
    }
    ImageIO.write(this, "png", File("src/main/resources/Crops/Tmp/$tmpFileName"))
}

fun BufferedImage.getCardCrop(): BufferedImage? {
    with(TESLTracker.referenceConfig) {
        return getSubimage(FULL_CARD_X, FULL_CARD_Y, FULL_CARD_WIDTH, FULL_CARD_HEIGHT)
    }
}

// -- Screen Crops --

fun BufferedImage.getScreenMainCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(SCREEN_MAIN_X, SCREEN_MAIN_Y, SCREEN_MAIN_WIDTH, SCREEN_MAIN_HEIGHT)
    }
}

fun BufferedImage.getScreenGameCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(SCREEN_GAME_X, SCREEN_GAME_Y, SCREEN_GAME_WIDTH, SCREEN_GAME_HEIGHT)
    }
}

fun BufferedImage.getScreenArenaClassesCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(SCREEN_ARENA_CLASSES_X, SCREEN_ARENA_CLASSES_Y, SCREEN_ARENA_CLASSES_WIDTH, SCREEN_ARENA_CLASSES_HEIGHT)
    }
}

fun BufferedImage.getScreenArenaClassSelectCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(SCREEN_ARENA_CLASS_SELECT_X, SCREEN_ARENA_CLASS_SELECT_Y,
                SCREEN_ARENA_CLASS_SELECT_WIDTH, SCREEN_ARENA_CLASS_SELECT_HEIGHT)
    }
}

fun BufferedImage.getScreenArenaPickCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(SCREEN_ARENA_PICK_X, SCREEN_ARENA_PICK_Y, SCREEN_ARENA_PICK_WIDTH, SCREEN_ARENA_PICK_HEIGHT)
    }
}

fun BufferedImage.getScreenArenaDashboardCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(SCREEN_ARENA_DASHBOARD_X, SCREEN_ARENA_DASHBOARD_Y, SCREEN_ARENA_DASHBOARD_WIDTH, SCREEN_ARENA_DASHBOARD_HEIGHT)
    }
}

// -- Game Crops --

fun BufferedImage.getGamePlayerRingCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(GAME_PLAYER_RING_X, GAME_PLAYER_RING_Y, GAME_PLAYER_RING_WIDTH, GAME_PLAYER_RING_HEIGHT)
    }
}

fun BufferedImage.getGamePlayerClassCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(GAME_PLAYER_CLASS_X, GAME_PLAYER_CLASS_Y, GAME_PLAYER_CLASS_WIDTH, GAME_PLAYER_CLASS_HEIGHT)
    }
}

fun BufferedImage.getGameOpponentClassCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(GAME_OPPONENT_CLASS_X, GAME_OPPONENT_CLASS_Y, GAME_OPPONENT_CLASS_WIDTH, GAME_OPPONENT_CLASS_HEIGHT)
    }
}

// -- Arena Crops --

fun BufferedImage.getArenaClassSelectCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(ARENA_CLASS_SELECT_X, ARENA_CLASS_SELECT_Y, ARENA_CLASS_SELECT_WIDTH, ARENA_CLASS_SELECT_HEIGHT)
    }
}

fun BufferedImage.getArenaCardCrop(pickPosition: Int): BufferedImage {
    with(TESLTracker.referenceConfig) {
        val cardPositionPickStartX = when (pickPosition) {
            1 -> ARENA_PICK_CARD_FIRST_X
            2 -> ARENA_PICK_CARD_SECOND_X
            else -> ARENA_PICK_CARD_THIRD_X
        }
        return crop(cardPositionPickStartX, ARENA_PICK_CARD_START_Y, ARENA_PICK_CARD_WIDTH, ARENA_PICK_CARD_HEIGHT)
    }
}


private fun BufferedImage.crop(cropX: Int, cropY: Int, cropWidth: Int, cropHeight: Int): BufferedImage {
    val pickPosition = ImageFuncs.getScaledPosition(width, height, cropX, cropY)
    val pickSize = ImageFuncs.getScaledSize(height, cropWidth, cropHeight)
    return getSubimage(pickPosition.x, pickPosition.y, pickSize.width, pickSize.height)
}
