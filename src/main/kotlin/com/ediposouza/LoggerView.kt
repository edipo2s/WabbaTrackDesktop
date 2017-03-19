package com.ediposouza

import javafx.application.Platform
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import tornadofx.FX
import tornadofx.View
import tornadofx.singleAssign

/**
 * Created by Edipo on 18/03/2017.
 */
class LoggerView : View("TES Legends Tracker Log") {

    val TRAY_ICON = "ic_legend.png"

    var textArea: TextArea by singleAssign()

    private val loggerController by inject<LoggerController>()

    override val root = BorderPane()

    init {
        FX.primaryStage.icons += Image(javaClass.getResourceAsStream("/$TRAY_ICON"))
        textArea = TextArea().apply {
            isEditable = false
        }
        with(root) {
            center = textArea
        }
        Platform.runLater {
            loggerController.logText("Logger Initialized")
        }
    }
}