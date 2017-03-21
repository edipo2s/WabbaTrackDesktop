package com.ediposouza.extensions

import tornadofx.getLong
import tornadofx.isNotNullOrNULL
import javax.json.JsonObject

fun String.toIntSafely() = this.toIntOrNull() ?: 0

fun JsonObject.jsonString(key: String) = if (isNotNullOrNULL(key)) getString(key) else ""
fun JsonObject.jsonLong(key: String) = if (isNotNullOrNULL(key)) getLong(key) else 0L
fun JsonObject.jsonBool(key: String) = if (isNotNullOrNULL(key)) getBoolean(key) else false