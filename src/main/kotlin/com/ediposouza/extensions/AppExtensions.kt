package com.ediposouza.extensions

fun String.toIntSafely() = this.toIntOrNull() ?: 0

fun String?.equalsOrNull(other: String): Boolean? = if (this == other) true else null

fun Map<String, *>.jsonString(key: String) = get(key) as String
fun Map<String, *>.jsonLong(key: String) = get(key) as Long
fun Map<String, *>.jsonBool(key: String) = get(key) as Boolean