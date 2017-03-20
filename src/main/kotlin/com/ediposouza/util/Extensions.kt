package com.ediposouza.util

import tornadofx.getLong
import tornadofx.isNotNullOrNULL
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.json.JsonObject

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

fun String.toIntSafely() = this.toIntOrNull() ?: 0

fun JsonObject.jsonString(key: String) = if (isNotNullOrNULL(key)) getString(key) else ""
fun JsonObject.jsonLong(key: String) = if (isNotNullOrNULL(key)) getLong(key) else 0L
fun JsonObject.jsonBool(key: String) = if (isNotNullOrNULL(key)) getBoolean(key) else false