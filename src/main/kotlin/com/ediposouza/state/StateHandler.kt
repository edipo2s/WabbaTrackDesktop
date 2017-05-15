package com.ediposouza.state

/**
 * Created by ediposouza on 24/03/17.
 */
object StateHandler {

    var currentTESLState: TESLState? = null
        set(value) {
            if (field != value) {
                field?.onPause()
                field = value
                field?.onResume()
            }
        }

    interface TESLState {

        fun onResume()
        fun onPause()
        fun hasValidState(): Boolean = true
        fun resetState() {}

    }

}
