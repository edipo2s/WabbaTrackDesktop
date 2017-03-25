package com.ediposouza.handler

/**
 * Created by ediposouza on 24/03/17.
 */
object StateHandler {

    var currentTESLState: TESLState? = null
        set(value) {
            field?.onPause()
            field = value
            field?.onResume()
        }

    interface TESLState {

        fun onResume()
        fun onPause()

    }

}
