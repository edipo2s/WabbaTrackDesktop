package com.ediposouza.extensions

import com.ediposouza.App
import com.ediposouza.util.images.ImageFuncs
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Created by Edipo on 19/03/2017.
 */

fun BufferedImage.saveCroppedImage() {
    val tmpFileName = "recognize_${System.currentTimeMillis()}.png"
    File("src/main/resources/Test/Tmp").apply {
        if (!exists()) {
            mkdir()
        }
    }
    ImageIO.write(this, "png", File("src/main/resources/Test/Tmp/$tmpFileName"))
}

fun BufferedImage.getCardCroppedImage(): BufferedImage? {
    with(App.referenceConfig) {
        return getSubimage(FULL_CARD_X, FULL_CARD_Y, FULL_CARD_WIDTH, FULL_CARD_HEIGHT)
    }
}

// -- Arena Crops --

fun BufferedImage.getArenaPicksRemainingCroppedImage(): BufferedImage {
    with(App.referenceConfig) {
        val pickPosition = ImageFuncs.getScaledPosition(width, height, ARENA_PICKS_REMAINING_X, ARENA_PICKS_REMAINING_Y)
        val pickSize = ImageFuncs.getScaledSize(height, ARENA_PICKS_REMAINING_WIDTH, ARENA_PICKS_REMAINING_HEIGHT)
        return getSubimage(pickPosition.first, pickPosition.second, pickSize.first, pickSize.second)
    }
}

fun BufferedImage.getArenaClassSelectedCroppedImage(): BufferedImage {
    with(App.referenceConfig) {
        val pickPosition = ImageFuncs.getScaledPosition(width, height, ARENA_CLASS_SELECTED_X, ARENA_CLASS_SELECTED_Y)
        val pickSize = ImageFuncs.getScaledSize(height, ARENA_CLASS_SELECTED_WIDTH, ARENA_CLASS_SELECTED_HEIGHT)
        return getSubimage(pickPosition.first, pickPosition.second, pickSize.first, pickSize.second)
    }
}

fun BufferedImage.getArenaCardCropped(pickPosition: Int): BufferedImage {
    with(App.referenceConfig) {
        val cardPositionPickStartX = when (pickPosition) {
            1 -> ARENA_PICK_CARD_FIRST_X
            2 -> ARENA_PICK_CARD_SECOND_X
            else -> ARENA_PICK_CARD_THIRD_X
        }
        val cardPosition = ImageFuncs.getScaledPosition(width, height, cardPositionPickStartX, ARENA_PICK_CARD_START_Y)
        val cardSize = ImageFuncs.getScaledSize(height, ARENA_PICK_CARD_WIDTH, ARENA_PICK_CARD_HEIGHT)
        return getSubimage(cardPosition.first, cardPosition.second, cardSize.first, cardSize.second)
    }
}
