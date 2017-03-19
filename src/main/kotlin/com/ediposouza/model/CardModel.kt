package com.ediposouza.model

import tornadofx.JsonModel
import tornadofx.getProperty
import tornadofx.property
import tornadofx.string
import javax.json.JsonObject

/**
 * Created by Edipo on 19/03/2017.
 */
class CardModel(val shortname: String, var attribute: String, var set: String) : JsonModel {

    var name by property<String>()
    fun nameProperty() = getProperty(CardModel::name)

    var arenaTier by property<String>()
    fun arenaTierProperty() = getProperty(CardModel::arenaTier)

    override fun updateModel(json: JsonObject) {
        with(json) {
            name = string("name")
            arenaTier = string("arenaTier")
        }
    }

    override fun toString(): String {
        return "CardModel(shortname='$shortname', " +
                "attribute='$attribute', " +
                "set='$set', " +
                "name='$name', " +
                "arenaTier='$arenaTier')"
    }


}