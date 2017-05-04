package com.ediposouza

import com.ediposouza.data.TESLTrackerAuth
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.extensions.addMenu
import com.ediposouza.extensions.addMenuItem
import com.ediposouza.handler.ScreenHandler
import com.ediposouza.handler.StateHandler
import com.ediposouza.model.*
import com.ediposouza.state.ArenaState
import com.ediposouza.state.GameState
import com.ediposouza.ui.LoggerView
import com.ediposouza.ui.MainWidget
import com.ediposouza.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tulskiy.keymaster.common.Provider
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.ProgressIndicator
import javafx.scene.control.TextInputDialog
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.javafx.JavaFx
import tornadofx.App
import tornadofx.FX
import tornadofx.alert
import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStream
import java.net.URI
import java.net.URLDecoder
import java.util.concurrent.CompletableFuture
import javax.swing.KeyStroke
import javax.swing.SwingUtilities

/**
 * Created by ediposouza on 06/03/17.
 */
class TESLTracker : App(LoggerView::class) {

    companion object {

        val APP_NAME = "WabbaTrack"
        val APP_VERSION = "0.1"
        val FILE_NAME = "WabbaTrack.exe"
        val WABBATRACK_URL = "https://edipo2s.github.io/WabbaTrack/"
        val SHOW_TEST_MENU = false

        val keyProvider: Provider by lazy { Provider.getCurrentProvider(true) }
        var referenceConfig: ReferenceConfig = ReferenceConfig1366x768()
        val screenSize: Dimension by lazy { Toolkit.getDefaultToolkit().screenSize }

        val iconName: String = "/ic_legend.png".takeIf { com.sun.jna.Platform.isWindows() } ?: "/ic_legend_osx.png"
        val jarPath: String = URLDecoder.decode(TESLTracker::class.java.protectionDomain.codeSource.location.file, "UTF-8")
        val legendsIcon: Image by lazy { Image(iconName) }
        val loading by lazy {
            Stage().apply {
                initStyle(StageStyle.TRANSPARENT)
                isResizable = false
                isAlwaysOnTop = true
                initModality(Modality.APPLICATION_MODAL)
            }.apply {
                scene = Scene(ProgressIndicator().apply {
                    background = Background.EMPTY
                }).apply {
                    fill = Color.TRANSPARENT
                }
            }
        }

        private var lastScreenshotDHash = ""
        private var loginMenuItems: List<Any>? = null

        private var hasUpdateReady: Boolean = false
            set(value) {
                field = value
                if (value) {
                    loginMenuItems?.forEach {
                        if (it is MenuItem) {
                            it.label = "Restart and Update App"
                        }
                        if (it is javafx.scene.control.MenuItem) {
                            Platform.runLater {
                                it.text = "Restart and Update App"
                            }
                        }
                    }
                }
            }

        fun showRestartToUpdateNow() {
            hasUpdateReady = true
        }

        fun doExit() {
            try {
                val currentTESLState = StateHandler.currentTESLState
                if (currentTESLState is ArenaState) {
                    currentTESLState.saveArenaPicks()
                }
                keyProvider.stop()
            } catch (e: Exception) {
                Logger.e(e)
            } finally {
                Platform.exit()
                System.exit(0)
            }
        }

    }

    val DELAY_WINDOW_DETECTION = 5_000L
    val ELDER_SCROLL_SPS = 2f    //Screenshot Per Second
    val ELDER_SCROLL_LEGENDS_WINDOW_TITLE = "The Elder Scrolls: Legends"

    private lateinit var trayIcon: TrayIcon
    private lateinit var trayPopupMenu: PopupMenu
    private lateinit var menuMyDecks: List<Any>
    private lateinit var menuImportedDecks: List<Any>
    private val legendsIconStream: InputStream by lazy { TESLTracker::class.java.getResourceAsStream(iconName) }
    private val mainWidget by lazy { MainWidget() }
    private val importDecksFromLegendsDecksLabel = "-- Import from Legends-Decks --"
    private val saveDecksImportedLock = "lock"
    private var decksImported = mutableListOf<Deck>()
    private val decksImportedFile by lazy {
        File(File(jarPath).parentFile, "data").let {
            if (!it.exists()) {
                it.mkdirs()
            }
            File(it, "decksImported.json")
        }
    }

    var logging: Boolean = false
        set(value) {
            field = value
            loginMenuItems?.forEach {
                if (it is MenuItem) {
                    it.isEnabled = !value
                    if (!hasUpdateReady) {
                        it.label = "Logging..".takeIf { value } ?: "Login"
                    }
                }
                if (it is javafx.scene.control.MenuItem) {
                    Platform.runLater {
                        it.isDisable = value
                        if (!hasUpdateReady) {
                            it.text = "Logging..".takeIf { value } ?: "Login"
                        }
                    }
                }
            }
        }

    var waitingScreenshotChangeWasLogged = false

    init {
        if (decksImportedFile.exists()) {
            val listDeckType = object : TypeToken<ArrayList<Deck>>() {}.type
            decksImported = Gson().fromJson(FileReader(decksImportedFile).readText(), listDeckType)
        }
    }

    override fun start(stage: Stage) {
        super.start(stage.apply {
            initStyle(StageStyle.UTILITY)
            isAlwaysOnTop = true
            height = 300.0
            width = 200.0
            x = 0.0
            y = screenSize.height - height
        })

        stage.close()
        referenceConfig = when {
            screenSize.width == 1366 && screenSize.height == 768 -> ReferenceConfig1366x768()
            else -> ReferenceConfig1920x1080()
        }

        configureSystemTrayIcon()
        if (TESLTrackerAuth.hasLoginCredentialsSaved()) {
            Logger.d("Starting auto-login")
            doLogin()
        } else {
            Mixpanel.trackUser()
        }
        CompletableFuture.runAsync {
            startElderScrollDetection()
        }
        CompletableFuture.runAsync {
            Thread.sleep(500)
            keyProvider.register(KeyStroke.getKeyStroke("control shift W")) {
                mainWidget.isVisible = !mainWidget.isVisible
            }
            Platform.runLater {
                updateMenuDecksImported()
            }
            TESLTrackerData.checkForUpdate()
        }
    }

    private fun configureSystemTrayIcon() {
        if (!SystemTray.isSupported()) {
            Logger.d("Tray Icon not supported")
        }
        trayicon(legendsIconStream, APP_NAME, false, true) {
            trayIcon = this
            trayPopupMenu = PopupMenu().apply {
                loginMenuItems = addMenuItem("Login") {
                    if (hasUpdateReady) {
                        TESLTrackerData.restartAppToUpdate()
                    } else if (TESLTrackerAuth.isUserLogged()) {
                        Desktop.getDesktop().browse(URI("$WABBATRACK_URL?id=${TESLTrackerAuth.userUuid}"))
                    } else {
                        doLogin()
                    }
                }
                logging = logging
                menuMyDecks = addMenu("My Decks")
                menuMyDecks.forEach {
                    if (it is Menu) {
                        it.isEnabled = false
                    }
                    if (it is javafx.scene.control.Menu) {
                        it.isDisable = true
                    }
                }
                menuImportedDecks = addMenu("Imported Decks") {
                    addMenuItem(importDecksFromLegendsDecksLabel) {
                        importDeckFromLegendsClick()
                    }
                }
                addMenuItem("Show/Hide Deck Tracker") {
                    Platform.runLater {
                        val isShowed = GameState.deckTracker.isVisible
                        GameState.deckTracker.isVisible = !isShowed
                        GameState.shouldShowDeckTracker = !isShowed
                        if (isShowed) {
                            Mixpanel.postEventHideDeckTracker()
                        }
                    }
                }
                addMenuItem("Show/Hide Floating Icon  (Crtl+Shift+W)") {
                    mainWidget.isVisible = !mainWidget.isVisible
                }
                addMenuItem("About") {
                    Platform.runLater {
                        alert(Alert.AlertType.INFORMATION, "About", "$APP_NAME $APP_VERSION \nby Edipo2s")
                    }
                }
                addMenuItem("Exit") {
                    doExit()
                }
                if (SHOW_TEST_MENU) {
                    addMenu("Test") {
                        addMenuItem("Show Log") {
                            Platform.runLater {
                                FX.primaryStage.show()
                            }
                        }
                        addMenuItem("Show Arena Tier Test") {
                            Platform.runLater {
                                ArenaState.setTierPicks(Triple(CardPick(Card.DUMMY, 20, listOf()),
                                        CardPick(Card.DUMMY, 20, listOf()), CardPick(Card.DUMMY, 20, listOf())))
                            }
                        }
                        addMenuItem("Show Arena Tier with Synergy Test") {
                            Platform.runLater {
                                ArenaState.pickNumber = 2
                                ArenaState.setTierPicks(Triple(CardPick(Card.DUMMY, 20, listOf(Card.DUMMY, Card.DUMMY)),
                                        CardPick(Card.DUMMY, 20, listOf(Card.DUMMY)), CardPick(Card.DUMMY, 20, listOf(Card.DUMMY))))
                            }
                        }
                        addMenuItem("Show Deck Test") {
                            Platform.runLater {
                                val cardsSlot = listOf(TESLTrackerData.getCard("ashservant") ?: Card.DUMMY,
                                        TESLTrackerData.getCard("deathlessdraugr") ?: Card.DUMMY,
                                        TESLTrackerData.getCard("firebolt") ?: Card.DUMMY,
                                        TESLTrackerData.getCard("firebolt") ?: Card.DUMMY,
                                        TESLTrackerData.getCard("lightningbolt") ?: Card.DUMMY,
                                        TESLTrackerData.getCard("firebolt") ?: Card.DUMMY,
                                        TESLTrackerData.getCard("lightningbolt") ?: Card.DUMMY,
                                        TESLTrackerData.getCard("windkeepspellsword") ?: Card.DUMMY,
                                        TESLTrackerData.getCard("windkeepspellsword") ?: Card.DUMMY)
                                        .groupBy(Card::shortName)
                                        .map { CardSlot(it.value.first(), it.value.size) }
                                StateHandler.currentTESLState = GameState.apply {
                                    setDeckCardsSlot(cardsSlot)
                                }
                            }
                        }
                        addMenuItem("Draw Test") {
                            GameState.deckTracker.trackCardDraw(TESLTrackerData.getCard("firebolt") ?: Card.DUMMY)
                            kotlinx.coroutines.experimental.launch(JavaFx) {
                                delay(2000L)
                                GameState.deckTracker.trackCardDraw(TESLTrackerData.getCard("windkeepspellsword") ?: Card.DUMMY)
                                delay(2000L)
                                GameState.deckTracker.trackCardDraw(TESLTrackerData.getCard("lightningbolt") ?: Card.DUMMY)
                            }
                        }
                        addMenuItem("Save Match Test") {
                            Platform.runLater {
                                GameState.apply {
                                    playerGoFirst = true
                                    playerDeckClass = DeckClass.BATTLEMAGE
                                    opponentDeckClass = DeckClass.MAGE
                                    matchMode = MatchMode.CASUAL
                                    saveMatch(true)
                                }
                            }
                        }
                    }
                }
            }
            popupMenu = trayPopupMenu
            if (!logging) {
                SwingUtilities.invokeLater {
                    displayMessage(APP_NAME, "$APP_NAME started.", TrayIcon.MessageType.NONE)
                }
            }
        }
    }

    private fun doLogin(retry: Int = 0) {
        logging = true
        CompletableFuture.runAsync {
            if (TESLTrackerAuth.login()) {
                logging = false
                Logger.d("Success logged")
                loginMenuItems?.forEach {
                    if (it is MenuItem) {
                        it.label = "Open Match Statistics - ${TESLTrackerAuth.userName}"
                    }
                    if (it is javafx.scene.control.MenuItem) {
                        Platform.runLater {
                            it.text = "Open Match Statistics - ${TESLTrackerAuth.userName}"
                        }
                    }
                }
                updateMenuDecks()
                Platform.runLater {
                    SwingUtilities.invokeLater {
                        trayIcon.displayMessage(APP_NAME, "Success logged as ${TESLTrackerAuth.userName}", TrayIcon.MessageType.NONE)
                    }
                }
            } else {
                if (retry < 3) {
                    Thread.sleep(500)
                    Logger.e("Error while logging. Retrying...")
                    doLogin(retry + 1)
                } else {
                    logging = false
                    Logger.e("Error while logging")
                }
            }
        }
    }

    private fun updateMenuDecks() {
        Logger.d("Starting updating decks database")
        TESLTrackerData.updateDecksDB {
            menuMyDecks.forEach {
                if (it is Menu) {
                    it.isEnabled = true
                }
                if (it is javafx.scene.control.Menu) {
                    it.isDisable = false
                }
            }
            menuMyDecks.forEach {
                if (it is Menu) {
                    it.removeAll()
                }
                if (it is javafx.scene.control.Menu) {
                    it.items.clear()
                }
            }
            menuMyDecks.forEach {
                if (it is Menu) {
                    it.addMenuItem("-- Update list --") {
                        updateMenuDecks()
                    }
                }
            }
            TESLTrackerData.decks.forEach { deck ->
                (menuMyDecks.first() as Menu).addMenu(deck.name, deck.getClassIcon()) {
                    addMenuItem("Load") {
                        showDeckInDeckTracker(deck)
                        Mixpanel.postEventShowDeckTrackerFromMyDecks(deck.name)
                    }
                    addMenuItem("Delete") {
                        Platform.runLater {
                            alert(Alert.AlertType.CONFIRMATION, "Are you sure?", "Delete ${deck.name}",
                                    ButtonType.YES, ButtonType.NO) { bt ->
                                if (bt == ButtonType.YES) {
                                    TESLTrackerData.deleteDecks(deck) {
                                        SwingUtilities.invokeLater {
                                            trayIcon.displayMessage(APP_NAME, "${deck.name} deleted.", TrayIcon.MessageType.NONE)
                                        }
                                        updateMenuDecks()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateMenuDecksImported() {
        Logger.d("Loading imported decks")
        decksImported.forEach {
            menuImportedDecks.forEach {
                if (it is Menu) {
                    it.removeAll()
                }
                if (it is javafx.scene.control.Menu) {
                    it.items.clear()
                }
            }
            menuImportedDecks.forEach {
                if (it is Menu) {
                    it.addMenuItem(importDecksFromLegendsDecksLabel) {
                        importDeckFromLegendsClick()
                    }
                }
            }
            decksImported.forEach { deck ->
                (menuImportedDecks.first() as Menu).addMenu(deck.name, deck.getClassIcon()) {
                    addMenuItem("Load") {
                        showDeckInDeckTracker(deck)
                        Mixpanel.postEventShowDeckTrackerFromImportedDecks(deck.name)
                    }
                    addMenuItem("Delete") {
                        Platform.runLater {
                            alert(Alert.AlertType.CONFIRMATION, "Are you sure?", "Delete ${deck.name}",
                                    ButtonType.YES, ButtonType.NO) { bt ->
                                if (bt == ButtonType.YES) {
                                    decksImported.remove(deck)
                                    saveDecksImported()
                                    updateMenuDecksImported()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun importDeckFromLegendsClick() {
        Platform.runLater {
            val result = TextInputDialog("").apply {
                title = "$APP_NAME - Importing deck from Legends-Decks"
                contentText = "Url:"
            }.showAndWait()
            if (result.isPresent) {
                val url = result.get()
                loading.show()
                CompletableFuture.runAsync {
                    LegendsDeckImporter.import(url) { deck ->
                        Platform.runLater {
                            loading.close()
                            if (decksImported.find { it.name == deck.name } == null) {
                                decksImported.add(deck)
                                updateMenuDecksImported()
                                saveDecksImported()
                            }
                        }
                        Mixpanel.postEventDeckImported(deck.name)
                        showDeckInDeckTracker(deck)
                        Mixpanel.postEventShowDeckTrackerFromImportedDecks(deck.name)
                    }
                }
            }
        }
    }

    private fun saveDecksImported() {
        synchronized(saveDecksImportedLock) {
            try {
                val picksJson = Gson().toJson(decksImported)
                FileWriter(decksImportedFile).apply {
                    write(picksJson)
                    flush()
                }
            } catch (e: Exception) {
                Logger.e(e)
            }
        }
    }

    private fun showDeckInDeckTracker(deck: Deck) {
        GameState.setDeckCardsSlot(deck.cards.map {
            CardSlot(TESLTrackerData.getCard(it.key) ?: Card.DUMMY, it.value)
        }, deck.name)
        Platform.runLater {
            GameState.deckTracker.isVisible = true
        }
    }

    private fun startElderScrollDetection() {
        mainWidget.isVisible = true
        TESLTrackerData.updateCardDB()
        Logger.d("Using ${referenceConfig.SCREEN_REFERENCE} as reference")
        with(screenSize) {
            Logger.d("Image size: ${width}x$height")
        }
        Logger.i("Waiting Elder scroll legends..")
        while (true) {
            if (isTESLegendsScreenActive()) {
                Logger.i("Elder scroll legends detected!")
                Mixpanel.postEventGameDetected()
                StateHandler.currentTESLState?.onResume()
                startElderScrollRecognition()
                Logger.i("Waiting Elder scroll legends..")
                StateHandler.currentTESLState?.onPause()
            }
            Thread.sleep(DELAY_WINDOW_DETECTION)
        }
    }

    private fun startElderScrollRecognition() {
        Logger.i("Start screenshot game!")
        CompletableFuture.runAsync {
            TESLTrackerData.checkForUpdate()
        }
        while (true) {
            if (!analyseScreenshot(ScreenFuncs.takeScreenshot())) {
                ScreenHandler.lastScreenRecognized = ""
                break
            }
        }
    }

    private fun analyseScreenshot(screenshot: BufferedImage?): Boolean {
        if (screenshot == null) {
            return false
        }
        val screenshotDHash = Recognizer.calcPHash(screenshot)
        if (Recognizer.isScreenshotDifferent(screenshotDHash, lastScreenshotDHash) ||
                !(StateHandler.currentTESLState?.hasValidState() ?: false)) {
            lastScreenshotDHash = screenshotDHash
            waitingScreenshotChangeWasLogged = false
            ScreenHandler.process(screenshot)
            val sps = ELDER_SCROLL_SPS.takeIf { StateHandler.currentTESLState == null } ?: 0.5f
            Thread.sleep((1000 / sps).toLong())
            if (!ScreenHandler.screenRecognized) {
                return isTESLegendsScreenActive() || isTESLegendsTrackerWindow() || isTESLegendsTrackerPopupWindow()
            }
        } else if (!waitingScreenshotChangeWasLogged) {
            Logger.d("Waiting screen change..")
            waitingScreenshotChangeWasLogged = true
        }
        return true
    }

    private fun isTESLegendsScreenActive() = ScreenFuncs.getActiveWindowTitle().contains(ELDER_SCROLL_LEGENDS_WINDOW_TITLE)

    private fun isTESLegendsTrackerWindow() = ScreenFuncs.getActiveWindowTitle().contains(APP_NAME)

    private fun isTESLegendsTrackerPopupWindow() = ScreenFuncs.getActiveWindowTitle().isEmpty()

}