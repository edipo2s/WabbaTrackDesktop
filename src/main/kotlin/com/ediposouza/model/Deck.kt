package com.ediposouza.model

import com.ediposouza.TESLTracker
import com.ediposouza.extensions.toFXImage
import javafx.scene.image.Image
import java.awt.image.BufferedImage
import java.time.LocalDateTime
import javax.imageio.ImageIO

/**
 * Created by Edipo on 29/03/2017.
 */
enum class DeckType {

    AGGRO,
    ARENA,
    COMBO,
    CONTROL,
    MIDRANGE,
    OTHER;

    companion object {

        fun of(value: String): DeckType {
            val name = value.toUpperCase()
            return OTHER.takeUnless { values().map { it.name }.contains(name) } ?: valueOf(name)
        }

    }
}

data class DeckUpdate(

        val date: LocalDateTime,
        val changes: Map<String, Int>

)

enum class DeckClass(val attr1: CardAttribute, val attr2: CardAttribute = CardAttribute.NEUTRAL) {

    ARCHER(CardAttribute.STRENGTH, CardAttribute.AGILITY),
    ASSASSIN(CardAttribute.INTELLIGENCE, CardAttribute.AGILITY),
    BATTLEMAGE(CardAttribute.STRENGTH, CardAttribute.INTELLIGENCE),
    CRUSADER(CardAttribute.STRENGTH, CardAttribute.WILLPOWER),
    MAGE(CardAttribute.INTELLIGENCE, CardAttribute.WILLPOWER),
    MONK(CardAttribute.WILLPOWER, CardAttribute.AGILITY),
    SCOUT(CardAttribute.AGILITY, CardAttribute.ENDURANCE),
    SORCERER(CardAttribute.INTELLIGENCE, CardAttribute.ENDURANCE),
    SPELLSWORD(CardAttribute.WILLPOWER, CardAttribute.ENDURANCE),
    WARRIOR(CardAttribute.STRENGTH, CardAttribute.ENDURANCE),
    STRENGTH(CardAttribute.STRENGTH),
    INTELLIGENCE(CardAttribute.INTELLIGENCE),
    AGILITY(CardAttribute.AGILITY),
    WILLPOWER(CardAttribute.WILLPOWER),
    ENDURANCE(CardAttribute.ENDURANCE),
    NEUTRAL(CardAttribute.NEUTRAL);

    companion object {

        fun of(value: String?): DeckClass {
            val name = value?.trim()?.toUpperCase()?.replace(" ", "_") ?: ""
            return if (values().map { it.name }.contains(name)) valueOf(name) else NEUTRAL
        }

        fun getClass(attr1: CardAttribute, attr2: CardAttribute): DeckClass? {
            return getClasses(listOf(attr1, attr2)).firstOrNull()
        }

        fun getClasses(attr: List<CardAttribute>): List<DeckClass> {
            return values().filter { attr.contains(it.attr1) && attr.contains(it.attr2) }
        }

    }

}

data class DeckComment(

        val uuid: String,
        val owner: String,
        val comment: String,
        val date: LocalDateTime

)

data class Deck(

        val uuid: String,
        val name: String,
        val owner: String,
        val private: Boolean,
        val type: DeckType,
        val cls: DeckClass,
        val cost: Int,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
        val patch: String,
        val likes: List<String>,
        val views: Int,
        val cards: Map<String, Int>,
        val updates: List<DeckUpdate>,
        val comments: List<DeckComment>

) {

    companion object {

        val DUMMY = Deck("", "", "", false, DeckType.OTHER, DeckClass.NEUTRAL, 0, LocalDateTime.now(),
                LocalDateTime.now(), "", listOf(), 0, mapOf(), listOf(), listOf())
    }

    fun update(deckName: String, deckPrivate: Boolean, deckTypeSelected: DeckType, deckCls: DeckClass,
               deckSoulCost: Int, deckPatchUuid: String, deckCards: Map<String, Int>): Deck {
        return Deck(uuid, deckName, owner, deckPrivate, deckTypeSelected, deckCls, deckSoulCost,
                createdAt, LocalDateTime.now(), deckPatchUuid, likes, views, deckCards, updates, comments)
    }

    fun getClassIcon(): Image {
        val attr1Name = cls.attr1.name.toLowerCase().capitalize()
        val attr2Name = cls.attr2.name.toLowerCase().capitalize()
        val attr1Img = ImageIO.read(TESLTracker::class.java.getResourceAsStream("/UI/Attribute/$attr1Name.png"))
        val attr2Img = ImageIO.read(TESLTracker::class.java.getResourceAsStream("/UI/Attribute/$attr2Name.png"))
        val distance = 10
        val clsImg = BufferedImage(attr1Img.width * 2 + distance, attr1Img.height, BufferedImage.TYPE_INT_ARGB).apply {
            graphics.drawImage(attr1Img, 0, 0, null)
            graphics.drawImage(attr2Img, attr1Img.width + distance, 0, null)
        }
        return clsImg.toFXImage()
    }

    override fun toString(): String {
        return "Deck(id='$uuid', name='$name', owner='$owner', private=$private, type=$type, cls=$cls, cost=$cost, " +
                "createdAt=$createdAt, updatedAt=$updatedAt, patch='$patch', likes=$likes, views=$views, cards=$cards, " +
                "updates=$updates, comments=$comments)"
    }

}