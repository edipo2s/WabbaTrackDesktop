package com.ediposouza.util

/**
 * Created by ediposouza on 06/03/17.
 */
object Logger {

    fun d(text: String) {
        println(text)
    }

    fun e(text: String) {
        println(text)
    }

    fun e(exception: Exception) {
        println(exception.message)
    }

}