package com.ediposouza.ui

import tornadofx.Controller

/**
 * Created by Edipo on 18/03/2017.
 */
class ArenaTierController : Controller() {

    private val card1ArenaTierStage by lazy { ArenaTierWidget(1) }
    private val card2ArenaTierStage by lazy { ArenaTierWidget(2) }
    private val card3ArenaTierStage by lazy { ArenaTierWidget(3) }

    init {
        subscribe<ShowArenaTierEvent> {
            card1ArenaTierStage.setPickValue(it.pick1ArenaTier)
            card2ArenaTierStage.setPickValue(it.pick2ArenaTier)
            card3ArenaTierStage.setPickValue(it.pick3ArenaTier)
        }
        subscribe<HideArenaTierEvent> {
            card1ArenaTierStage.isVisible = false
            card2ArenaTierStage.isVisible = false
            card3ArenaTierStage.isVisible = false
        }
    }

}