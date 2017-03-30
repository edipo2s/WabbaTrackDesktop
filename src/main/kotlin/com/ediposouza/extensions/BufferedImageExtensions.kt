package com.ediposouza.extensions

import com.ediposouza.TESLTracker
import com.ediposouza.util.ImageFuncs
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by Edipo on 19/03/2017.
 */

val SAVE_CROP_IMAGES = false

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
        return getSubimage(FULL_CARD_X, FULL_CARD_Y, FULL_CARD_WIDTH, FULL_CARD_HEIGHT).apply { if (SAVE_CROP_IMAGES) saveCroppedImage() }
    }
}

fun BufferedImage.getCardForSlotCrop(): BufferedImage? {
    with(TESLTracker.referenceConfig) {
        return getSubimage(FULL_CARD_DECK_IMAGE_X, FULL_CARD_DECK_IMAGE_Y,
                FULL_CARD_DECK_IMAGE_WIDTH, FULL_CARD_DECK_IMAGE_HEIGHT).apply { if (SAVE_CROP_IMAGES) saveCroppedImage() }
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

fun BufferedImage.getGameWinCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(GAME_WIN_X, GAME_WIN_Y, GAME_WIN_WIDTH, GAME_WIN_HEIGHT)
    }
}

fun BufferedImage.getGameWin2Crop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(GAME_WIN2_X, GAME_WIN2_Y, GAME_WIN2_WIDTH, GAME_WIN2_HEIGHT)
    }
}

fun BufferedImage.getGameLossCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(GAME_LOSS_X, GAME_LOSS_Y, GAME_LOSS_WIDTH, GAME_LOSS_HEIGHT)
    }
}

fun BufferedImage.getGameLoss2Crop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(GAME_LOSS2_X, GAME_LOSS2_Y, GAME_LOSS2_WIDTH, GAME_LOSS2_HEIGHT)
    }
}

fun BufferedImage.getGameYourTurnCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(GAME_YOUR_TURN_X, GAME_YOUR_TURN_Y, GAME_YOUR_TURN_WIDTH, GAME_YOUR_TURN_HEIGHT)
    }
}

fun BufferedImage.getGameCardDrawCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(GAME_CARD_DRAW_X, GAME_CARD_DRAW_Y, GAME_CARD_DRAW_WIDTH, GAME_CARD_DRAW_HEIGHT)
    }
}

fun BufferedImage.getGamePlayerFirstCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(GAME_PLAYER_FIRST_X, GAME_PLAYER_FIRST_Y, GAME_PLAYER_FIRST_WIDTH, GAME_PLAYER_FIRST_HEIGHT)
    }
}

fun BufferedImage.getGamePlayerSecondCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(GAME_PLAYER_SECOND_X, GAME_PLAYER_SECOND_Y, GAME_PLAYER_SECOND_WIDTH, GAME_PLAYER_SECOND_HEIGHT)
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

fun BufferedImage.getArenaPickClassCrop(): BufferedImage {
    with(TESLTracker.referenceConfig) {
        return crop(ARENA_PICK_CLASS_X, ARENA_PICK_CLASS_Y, ARENA_PICK_CLASS_WIDTH, ARENA_PICK_CLASS_HEIGHT)
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
    return getSubimage(pickPosition.x, pickPosition.y, pickSize.width, pickSize.height).apply { if (SAVE_CROP_IMAGES) saveCroppedImage() }
}
