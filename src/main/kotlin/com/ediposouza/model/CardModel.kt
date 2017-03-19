package com.ediposouza.model

import tornadofx.JsonModel
import tornadofx.getProperty
import tornadofx.property
import tornadofx.string
import javax.json.JsonObject

/**
 * Created by Edipo on 19/03/2017.
 */
class CardModel(val shortname: String, var attribute: String) : JsonModel {

    var arenaTier by property<String>()
    fun arenaTierProperty() = getProperty(CardModel::arenaTier)

    override fun updateModel(json: JsonObject) {
        with(json) {
            arenaTier = string("arenaTier")
        }
    }

    override fun toString(): String {
        return "CardModel(shortname='$shortname', " +
                "attribute='$attribute', " +
                "arenaTier='$arenaTier')"
    }


}