package com.ediposouza.model

/**
 * Created by Edipo on 29/03/2017.
 */
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

        fun getClass(attr1: CardAttribute, attr2: CardAttribute): DeckClass {
            return getClasses(listOf(attr1, attr2)).first()
        }

        fun getClasses(attr: List<CardAttribute>): List<DeckClass> {
            return values().filter { attr.contains(it.attr1) && attr.contains(it.attr2) }
        }

    }

}