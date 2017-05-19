package com.ediposouza.tests

import org.junit.Before

/**
 * Created by ediposouza on 06/03/17.
 */
class Tests1360x768 : com.ediposouza.util.BaseResolutionTests() {

    override val arenaPickClass = "Assassin"
    override val initialCardsDraw1 = "blacksapprotector"
    override val initialCardsDraw2 = "execute"
    override val initialCardsDraw3 = "cloudrestillusionist"
    override val gameCardDraw = "eclipsebaroness"
    override val gameCardDrawProphecy = "brotherhoodslayer"
    override val gameOpponentClass_CardDraw = "Assassin"
    override val gameOpponentClass_PlayFirst = "Assassin"
    override val gamePlayerClass_CardDraw = "Monk"
    override val gamePlayerClass_PlayFirst = "Monk"
    override val gamePlayerRank_Player = "3"
    override val gamePlayerRank_Opponent = "2"

    override val arenaPicks1 = "moonlightwerebat"
    override val arenaPicks2 = "dragonstarrider"
    override val arenaPicks3 = "desperateconjuring"

    @Before
    fun setUp() {
        com.ediposouza.TESLTracker.Companion.referenceConfig = com.ediposouza.resolution.ReferenceConfig1360x768()
    }

}