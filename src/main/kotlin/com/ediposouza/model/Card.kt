package com.ediposouza.model

import com.ediposouza.extensions.jsonBool
import com.ediposouza.extensions.jsonString
import com.ediposouza.extensions.toIntSafely
import com.ediposouza.util.Logger
import javax.json.JsonObject

/**
 * Created by Edipo on 19/03/2017.
 */
enum class CardSet(val db: String) {

    CORE("core"),
    MADHOUSE("madhouse"),
    UNKNOWN("unknown");

    var unknownSetName = ""

    override fun toString(): String {
        return name.takeIf { this != UNKNOWN } ?: unknownSetName
    }

    companion object {

        fun of(value: String): CardSet {
            val name = value.trim().toUpperCase().replace(" ", "_")
            return if (values().map { it.name }.contains(name)) valueOf(name) else UNKNOWN
        }

    }

}

enum class CardAttribute(val isBasic: Boolean = true) {

    STRENGTH(),
    INTELLIGENCE(),
    WILLPOWER(),
    AGILITY(),
    ENDURANCE(),
    NEUTRAL(false),
    DUAL(false);

    companion object {

        fun of(value: String): CardAttribute {
            val name = value.trim().toUpperCase().replace(" ", "_")
            return if (CardAttribute.values().map { it.name }.contains(name)) CardAttribute.valueOf(name) else DUAL
        }

    }

}

enum class CardRarity(val soulCost: Int) {

    COMMON(50),
    RARE(100),
    EPIC(400),
    LEGENDARY(1200),
    UNKNOWN(0);

    companion object {

        fun of(value: String): CardRarity {
            val name = value.trim().toUpperCase().replace(" ", "_")
            return if (values().map { it.name }.contains(name)) valueOf(name) else UNKNOWN
        }

    }

}

enum class CardType {

    ACTION,
    CREATURE,
    ITEM,
    SUPPORT,
    UNKNOWN;

    companion object {

        fun of(value: String): CardType {
            val name = value.trim().toUpperCase().replace(" ", "_")
            return if (values().map { it.name }.contains(name)) valueOf(name) else UNKNOWN
        }

    }

}

enum class CardRace(val desc: String) {

    ARGONIAN("The Argonians of Black Marsh are possessed of a cool intellect and are well-versed in " +
            "stealth and the use of blades. They often act as the scouts and skirmishers."),
    BRETON("Flamboyant, intelligent, and resourceful, the Bretons of High Rock are renowned craftsmen, " +
            "shrewd merchants, gallant cavaliers, and inventive wizards."),
    DARK_ELF("The Dark Elf homeland of Morrowind has been invaded many times over the millennia. " +
            "This history of conflict has transformed the Dunmer into hardened warriors."),
    HIGH_ELF("The Altmer are one of the longest-lived and most-intelligent races in Tamriel, " +
            "which grants them a natural affinity for spells and magic."),
    IMPERIAL("The Imperial natives of Cyrodiil have proven to be shrewd tacticians and diplomats."),
    KHAJIIT("Khajiit are quick and agile, making them some of the most adept thieves in Tamriel."),
    NORD("Hailing from the inhospitable mountains of Skyrim, Nords are fearsome and hardy warriors."),
    ORC("The Orcs of the Wrothgarian and Dragontail Mountains are renowed as both craftsmen and berserkers."),
    REDGUARD("The Redguard of Hammerfell are Tamriel's most talented and resourceful warriors."),
    WOOD_ELF("The clanfolk of the Valenwood are the finest archers in Tamriel. Wood Elves excel at " +
            "hunting and dispatching the unwary."),
    ASH_CREATURE(""),
    BEAST(""),
    CENTAUR(""),
    CHAURUS(""),
    DAEDRA(""),
    DEFENSE(""),
    DRAGON(""),
    DREUGH(""),
    DWEMER(""),
    FISH(""),
    GIANT(""),
    GOBLIN(""),
    HARPY(""),
    IMP(""),
    KWAMA(""),
    LURCHER(""),
    LYCANTHROPE(""),
    MAMMOTH(""),
    MANTIKORA(""),
    MINOTAUR(""),
    MUDCRAB(""),
    MUMMY(""),
    NEREID(""),
    OGRE(""),
    REPTILE(""),
    REACHMAN(""),
    SKELETON(""),
    SPIRIT(""),
    SPIDER(""),
    SPRIGGAN(""),
    TROLL(""),
    VAMPIRE(""),
    WOLF(""),
    WAMASU(""),
    WRAITH(""),
    UNKNOWN(""),
    NONE("");

    companion object {

        fun of(value: String): CardRace {
            val name = value.trim().toUpperCase().replace(" ", "_")
            return if (value.trim().isEmpty()) CardRace.NONE else
                if (values().map { it.name }.contains(name)) valueOf(name) else UNKNOWN
        }

    }
}

enum class CardKeyword {

    ACTIVATE,
    BREAKTHROUGH,
    CHARGE,
    COVER,
    DRAIN,
    EVOLVES,
    GUARD,
    LAST_GASP,
    LETHAL,
    PILFER,
    PROPHECY,
    REGENERATE,
    SHACKLE,
    SILENCE,
    SUMMON,
    WARD,
    UNKNOWN;

    companion object {

        fun of(value: String): CardKeyword {
            val name = value.trim().toUpperCase().replace(" ", "_")
            return if (values().map { it.name }.contains(name)) valueOf(name) else UNKNOWN
        }

    }
}

enum class CardArenaTier(val value: Int = 0) {

    TERRIBLE(10),
    POOR(20),
    AVERAGE(30),
    GOOD(50),
    EXCELLENT(70),
    INSANE(90),
    UNKNOWN(),
    NONE();

    companion object {

        fun of(value: String): CardArenaTier {
            val name = value.trim().toUpperCase().replace(" ", "_")
            return if (values().map { it.name }.contains(name)) valueOf(name) else UNKNOWN
        }

    }

}

data class CardArenaTierPlus(

        val type: CardArenaTierPlusType,
        val operator: CardArenaTierPlusOperator?,
        val value: String

)

enum class CardArenaTierPlusOperator {

    EQUALS,
    GREAT,
    MINOR,
    UNKNOWN;

    companion object {

        fun of(value: String): CardArenaTierPlusOperator {
            return when (value) {
                "=" -> EQUALS
                ">" -> GREAT
                "<" -> MINOR
                else -> UNKNOWN
            }
        }

    }

}

enum class CardArenaTierPlusType(val extraPoints: Int = 5) {

    ATTACK(),
    ATTR(2),
    COST(),
    HEALTH(),
    KEYWORD(),
    RACE(),
    STRATEGY(),
    TEXT(),
    TYPE(),
    UNKNOWN();

    companion object {

        fun of(value: String): CardArenaTierPlusType {
            val name = value.trim().toUpperCase().replace(" ", "_")
            return if (values().map { it.name }.contains(name)) valueOf(name) else UNKNOWN
        }

    }

}

data class CardMissing(

        val shortName: String,
        val rarity: CardRarity,
        val qtd: Int

)

data class CardStatistic(

        val shortName: String,
        val rarity: CardRarity,
        val unique: Boolean

)

data class CardBasicInfo(

        val shortName: String,
        val set: String,
        val attr: String
)

data class CardSlot(

        val card: Card,
        val qtd: Int

)

data class Card(

        val name: String,
        val shortName: String,
        val set: CardSet,
        val attr: CardAttribute,
        val dualAttr1: CardAttribute,
        val dualAttr2: CardAttribute,
        val rarity: CardRarity,
        val unique: Boolean,
        val cost: Int,
        val attack: Int,
        val health: Int,
        val type: CardType,
        val race: CardRace,
        val keywords: List<CardKeyword>,
        val text: String,
        val arenaTier: CardArenaTier,
        val arenaTierPlus: CardArenaTierPlus?,
        val evolves: Boolean,
        val season: String

) {
    companion object {

        const val ARENA_TIER_PLUS_VALUE_DELIMITER = "_"

        val DUMMY = Card("Card", "", CardSet.UNKNOWN, CardAttribute.AGILITY, CardAttribute.AGILITY, CardAttribute.AGILITY,
                CardRarity.COMMON, false, 0, 0, 0, CardType.UNKNOWN, CardRace.ARGONIAN, listOf(), "", CardArenaTier.NONE,
                null, false, "")

        fun fromJson(shortname: String, attr: CardAttribute, set: CardSet, json: JsonObject): Card {
            var clsAttr1 = attr
            var clsAttr2 = attr
            try {
                with(json) {
                    if (attr == CardAttribute.DUAL) {
                        clsAttr1 = CardAttribute.valueOf(jsonString("attr1").trim().toUpperCase())
                        clsAttr2 = CardAttribute.valueOf(jsonString("attr2").trim().toUpperCase())
                    }
                    return Card(jsonString("name"), shortname, set, attr, clsAttr1, clsAttr2, CardRarity.of(jsonString("rarity")),
                            jsonBool("unique"), jsonString("cost").toIntSafely(), jsonString("attack").toIntSafely(),
                            jsonString("health").toIntSafely(), CardType.of(jsonString("type")), CardRace.of(jsonString("race")),
                            jsonString("keyword").split(",")
                                    .filter { it.trim().isNotEmpty() }
                                    .mapTo(arrayListOf<CardKeyword>()) {
                                        CardKeyword.of(it)
                                    },
                            jsonString("text"), CardArenaTier.of(jsonString("arenaTier")),
                            getCardArenaTierPlus(json.getJsonObject("arenaTierPlus")), jsonBool("evolves"), jsonString("season"))
                }
            } catch (e: Exception) {
                Logger.e(e)
                return getDefaultCard()
            }
        }

        fun getDefaultCard(): Card {
            return Card("", "", CardSet.CORE, CardAttribute.STRENGTH, CardAttribute.STRENGTH, CardAttribute.STRENGTH,
                    CardRarity.COMMON, false, 0, 0, 0, CardType.ACTION, CardRace.ARGONIAN, listOf(), "",
                    CardArenaTier.NONE, null, false, "")
        }

        private fun getCardArenaTierPlus(arenaTierPlusJson: JsonObject?): CardArenaTierPlus? {
            val arenaTierPlus: Map<String, String> = arenaTierPlusJson?.mapValues { it.value.toString() } ?: mapOf()
            if (arenaTierPlus.keys.isEmpty()) {
                return null
            }
            val cardArenaTierPlusType = CardArenaTierPlusType.of(arenaTierPlus.keys.first())
            var operator: CardArenaTierPlusOperator? = null
            val value = when (cardArenaTierPlusType) {
                CardArenaTierPlusType.ATTACK,
                CardArenaTierPlusType.COST,
                CardArenaTierPlusType.HEALTH ->
                    with(arenaTierPlus.values.first().split(ARENA_TIER_PLUS_VALUE_DELIMITER)) {
                        operator = CardArenaTierPlusOperator.of(get(0))
                        get(1)
                    }
                else -> arenaTierPlus.values.first()
            }
            return CardArenaTierPlus(cardArenaTierPlusType, operator, value)
        }

    }
}