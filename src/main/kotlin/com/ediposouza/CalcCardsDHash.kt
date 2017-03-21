package com.ediposouza

import com.ediposouza.extensions.getCardCroppedImage
import java.awt.image.BufferedImage
import java.io.File

/**
 * Created by ediposouza on 06/03/17.
 */
object CalcCardsDHash : BaseCalcDHash() {

    @JvmStatic fun main(args: Array<String>) {
        val cards = File(javaClass.getResource("/Cards").toURI())
        getDHashFolderFiles(cards, cards.path, "Cards")
    }

    override fun getCroppedImage(fullImage: BufferedImage, type: Int): BufferedImage? {
        return fullImage.getCardCroppedImage()
    }

}