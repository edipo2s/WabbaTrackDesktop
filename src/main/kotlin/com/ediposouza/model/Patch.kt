package com.ediposouza.teslesgendstracker.data

import java.time.LocalDate

/**
 * Created by ediposouza on 25/01/17.
 */
enum class PatchType {

    BALANCE,
    REWARD,
    SET,
    UNKNOWN;

    companion object {

        fun of(value: String): PatchType {
            val name = value.trim().toUpperCase().replace(" ", "_")
            return if (values().map { it.name }.contains(name)) valueOf(name) else UNKNOWN
        }

    }

}

class PatchChange(

        val attr: String,
        val set: String,
        val shortName: String,
        val change: String

)

data class Patch(

        val uuidDate: String,
        val date: LocalDate,
        val desc: String,
        val legendsDeck: String,
        val type: PatchType,
        val changes: List<PatchChange>

)