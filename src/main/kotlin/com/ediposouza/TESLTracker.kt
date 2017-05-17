package com.ediposouza

import com.ediposouza.data.PHash
import com.ediposouza.data.TESLTrackerAuth
import com.ediposouza.data.TESLTrackerData
import com.ediposouza.executor.DeckBuildExecutor
import com.ediposouza.executor.DeckImportExecutor
import com.ediposouza.executor.ScreenExecutor
import com.ediposouza.extensions.addMenu
import com.ediposouza.extensions.addMenuItem
import com.ediposouza.extensions.getScreenDeckBuilderCrop
import com.ediposouza.model.*
import com.ediposouza.resolution.ReferenceConfig
import com.ediposouza.resolution.ReferenceConfig1366x768
import com.ediposouza.resolution.ReferenceConfig1920x1080
import com.ediposouza.state.ArenaState
import com.ediposouza.state.GameState
import com.ediposouza.state.StateHandler
import com.ediposouza.ui.LoggerController
import com.ediposouza.ui.LoggerView
import com.ediposouza.ui.MainStageView
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
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
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
import java.util.prefs.Preferences
import javax.swing.KeyStroke
import javax.swing.SwingUtilities

/**
 * Created by ediposouza on 06/03/17.
 */
class TESLTracker : App(MainStageView::class) {

    companion object {

        val APP_NAME = "WabbaTrack"
        val APP_VERSION = "0.2.0"
        val DEBUG_FILE_NAME = "WabbaTrack.debug"
        val WABBATRACK_URL = "https://edipo2s.github.io/WabbaTrack/"

        val preferences: Preferences by lazy { Preferences.userNodeForPackage(TESLTracker::class.java) }
        val keyProvider: Provider by lazy { Provider.getCurrentProvider(true) }
        var usingSupportedResolution = true
        var referenceConfig: ReferenceConfig = ReferenceConfig1366x768()
        lateinit var graphicsDevice: GraphicsDevice
        lateinit var screenSize: Dimension

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

        private var trayIcon: TrayIcon? = null
        private var lastScreenshotPHash = ""
        private var loginMenuItems: List<Any>? = null

        private var hasUpdateReady: Boolean = false
            set(value) {
                field = value
                if (value) {
                    loginMenuItems?.forEach {
                        if (it is MenuItem) {
                            it.label = "Close and Update App"
                        }
                        if (it is javafx.scene.control.MenuItem) {
                            Platform.runLater {
                                it.text = "Close and Update App"
                            }
                        }
                    }
                }
            }

        fun showMessage(msg: String) {
            SwingUtilities.invokeLater {
                trayIcon?.displayMessage(APP_NAME, msg, TrayIcon.MessageType.NONE)
            }
        }

        fun showMessageUnsupportedResolution() {
            showMessage("You are using a unsupported resolution, so app may not work. Please change to 1366x768 or 1920x1080.")
        }

        fun showRestartToUpdateNow() {
            hasUpdateReady = true
            showMessage("Update $APP_VERSION ready to install.")
        }

        fun doExit() {
            try {
                val currentTESLState = StateHandler.currentTESLState
                if (currentTESLState is ArenaState) {
                    currentTESLState.saveArenaPicks()
                }
                Platform.runLater {
                    keyProvider.stop()
                }
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

    private lateinit var trayPopupMenu: PopupMenu
    private lateinit var menuMyDecks: List<Any>
    private lateinit var menuImportedDecks: List<Any>
    private val legendsIconStream: InputStream by lazy { TESLTracker::class.java.getResourceAsStream(iconName) }
    private val mainWidget: MainWidget by lazy { MainWidget() }
    private val loggerController by inject<LoggerController>()
    private val loggerView: LoggerView by inject<LoggerView>()
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
            initStyle(StageStyle.TRANSPARENT)
            isAlwaysOnTop = true
            ScreenFuncs.getGameMonitor {
                graphicsDevice = it
                screenSize = Dimension(it.displayMode.width, it.displayMode.height)
                referenceConfig = when {
                    screenSize.width == 1366 && screenSize.height == 768 -> ReferenceConfig1366x768()
                    screenSize.width == 1920 && screenSize.height == 1080 -> ReferenceConfig1920x1080()
                    else -> {
                        usingSupportedResolution = false
                        showMessageUnsupportedResolution()
                        ReferenceConfig1920x1080()
                    }
                }
            }
            with(TESLTracker.referenceConfig) {
                val mainSize = ImageFuncs.getScreenScaledSize(APP_MAIN_WIDTH, APP_MAIN_HEIGHT)
                val mainPos = ImageFuncs.getScreenScaledPosition(APP_MAIN_X, APP_MAIN_Y)
                height = mainSize.height.toDouble()
                width = mainSize.width.toDouble()
                x = mainPos.x.toDouble()
                y = mainPos.y.toDouble()
            }
        })

        loggerController.initialize()
        configureSystemTrayIcon()
        stage.close()
        mainWidget.isVisible = true

        if (TESLTrackerAuth.hasLoginCredentialsSaved()) {
            Logger.d("Starting auto-login")
            doLogin()
        } else {
            Mixpanel.trackUser()
        }
        launch(CommonPool) {
            startElderScrollDetection()
        }
        launch(CommonPool) {
            delay(500)
            keyProvider.register(KeyStroke.getKeyStroke("control shift W")) {
                mainWidget.isVisible = !mainWidget.isVisible
            }
            Platform.runLater {
                updateMenuDecksImported()
            }
            checkUpdate()
        }
        FX.primaryStage.scene.fill = Color.TRANSPARENT
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
                        Mixpanel.postEventShowStatistics()
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
                        startImportFromLegendsDeck()
                    }
                }
                addMenuItem("Show/Hide Deck Tracker") {
                    Platform.runLater {
                        val isShowed = GameState.isDeckTrackerVisible()
                        if (isShowed) {
                            GameState.hideDeckTracker()
                        } else {
                            GameState.showDeckTracker(true)
                        }
                        GameState.shouldShowDeckTracker = !isShowed
                        if (isShowed) {
                            Mixpanel.postEventDeckTrackerHide()
                        }
                    }
                }
                addMenuItem("Show/Hide Floating Icon  (Crtl+Shift+W)") {
                    mainWidget.isVisible = !mainWidget.isVisible
                }
                addMenuItem("Android TESLegends Tracker") {
                    val url = "https://play.google.com/store/apps/details?id=com.ediposouza.teslesgendstracker"
                    Desktop.getDesktop().browse(URI(url))
                    Mixpanel.postEventAndroidTESLegendsTracker()
                }
                addMenu("Settings") {
                    addMenuItem("Change default game monitor") {
                        ScreenFuncs.clearGameMonitorPref()
                        showMessage("Default monitor setting clear. Restart $APP_NAME to choose default monitor")
                    }
                }
                addMenuItem("About") {
                    Platform.runLater {
                        alert(Alert.AlertType.INFORMATION, "About", "$APP_NAME $APP_VERSION \n\nSpecial thanks:\n" +
                                "CVH for Arena Tier List\nBethesda and Direwolf for Amazing Game\n\nby Edipo2s")
                    }
                }
                addMenuItem("Exit") {
                    doExit()
                }
                if (File(DEBUG_FILE_NAME).exists()) {
                    addMenu("Test") {
                        addMenuItem("Show Log") {
                            Platform.runLater {
                                Stage().apply {
                                    initStyle(StageStyle.UTILITY)
                                    isAlwaysOnTop = true
                                    scene = Scene(loggerView.root)
                                    title = "$APP_NAME Logs"
                                    height = 200.0
                                    width = 180.0
                                    x = screenSize.width - width - 10.0 + TESLTracker.graphicsDevice.defaultConfiguration.bounds.x
                                    y = screenSize.height - height + TESLTracker.graphicsDevice.defaultConfiguration.bounds.y
                                }.show()
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
                                        TESLTrackerData.getCard("allenabenoch") ?: Card.DUMMY,
                                        TESLTrackerData.getCard("windkeepspellsword") ?: Card.DUMMY)
                                        .groupBy(Card::shortName)
                                        .map { CardSlot(it.value.first(), it.value.size) }
                                StateHandler.currentTESLState = GameState.apply {
                                    setDeckCardsSlot(cardsSlot)
                                }
                            }
                        }
                        addMenuItem("Draw Test") {
                            GameState.trackCardDraw(TESLTrackerData.getCard("firebolt") ?: Card.DUMMY)
                            launch(CommonPool) {
                                delay(2000L)
                                GameState.trackCardDraw(TESLTrackerData.getCard("windkeepspellsword") ?: Card.DUMMY)
                                delay(2000L)
                                GameState.trackCardDraw(TESLTrackerData.getCard("lightningbolt") ?: Card.DUMMY)
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
        launch(CommonPool) {
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
                TESLTrackerData.updateUserDB {
                    updateMenuDecks()
                }
                Platform.runLater {
                    SwingUtilities.invokeLater {
                        trayIcon?.displayMessage(APP_NAME, "Success logged as ${TESLTrackerAuth.userName}", TrayIcon.MessageType.NONE)
                    }
                }
            } else {
                if (retry < 3) {
                    delay(500)
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
                    addMenuItem("Build") {
                        buildDeck(deck)
                    }
                    addMenuItem("Delete") {
                        Platform.runLater {
                            alert(Alert.AlertType.CONFIRMATION, "Are you sure?", "Delete ${deck.name}",
                                    ButtonType.YES, ButtonType.NO) { bt ->
                                if (bt == ButtonType.YES) {
                                    TESLTrackerData.deleteDecks(deck) {
                                        SwingUtilities.invokeLater {
                                            trayIcon?.displayMessage(APP_NAME, "${deck.name} deleted.", TrayIcon.MessageType.NONE)
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
                        startImportFromLegendsDeck()
                    }
                }
            }
            decksImported.forEach { deck ->
                (menuImportedDecks.first() as Menu).addMenu(deck.name, deck.getClassIcon()) {
                    addMenuItem("Load") {
                        showDeckInDeckTracker(deck)
                        Mixpanel.postEventShowDeckTrackerFromImportedDecks(deck.name)
                    }
                    addMenuItem("Build") {
                        buildDeck(deck)
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

    private fun buildDeck(deck: Deck) {
        launch(CommonPool) {
            delay(250)
            ScreenFuncs.takeScreenshot()?.getScreenDeckBuilderCrop()?.let {
                if (Recognizer.recognizeImageInMap(it, PHash.SCREENS_LIST) == PHash.SCREEN_DECK_BUILDER) {
                    GameState.hideDeckTracker()
                    DeckBuildExecutor.buildDeck(deck.name, deck.cards)
                    showDeckInDeckTracker(deck)
                    Mixpanel.postEventBuildDeckFromMenu(deck.name)
                } else {
                    showMessage("To build a deck, please first go to deck builder screen")
                }
            }
        }
    }

    private fun startImportFromLegendsDeck() {
        DeckImportExecutor.importDeckFromLegendsClick { deck ->
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
            GameState.showDeckTracker(true)
        }
    }

    private fun checkUpdate() {
        TESLTrackerData.checkForUpdate { lastVersion ->
            SwingUtilities.invokeLater {
                trayIcon?.displayMessage(APP_NAME, "New version $lastVersion detected, downloading...", TrayIcon.MessageType.NONE)
            }
        }
    }

    suspend private fun startElderScrollDetection() {
        TESLTrackerData.updateCardDB()
        Logger.d("Using ${referenceConfig.SCREEN_REFERENCE} as reference")
        with(screenSize) {
            Logger.d("Image size: ${width}x$height")
        }
        Logger.i("Waiting Elder scroll legends..")
        while (true) {
            if (isTESLegendsScreenActive()) {
                Logger.i("Elder scroll legends detected!")
                StateHandler.currentTESLState?.onResume()
                startElderScrollRecognition()
                Logger.i("Waiting Elder scroll legends..")
                StateHandler.currentTESLState?.onPause()
            }
            delay(DELAY_WINDOW_DETECTION)
        }
    }

    suspend private fun startElderScrollRecognition() {
        Logger.i("Start screenshot game!")
        launch(CommonPool) {
            checkUpdate()
        }
        while (true) {
            if (!analyseScreenshot(ScreenFuncs.takeScreenshot())) {
                ScreenExecutor.lastScreenRecognized = ""
                break
            }
        }
    }

    suspend private fun analyseScreenshot(screenshot: BufferedImage?): Boolean {
        if (screenshot == null) {
            return false
        }
        val screenshotPHash = Recognizer.calcPHash(screenshot)
        if (Recognizer.isScreenshotDifferent(screenshotPHash, lastScreenshotPHash) ||
                !(StateHandler.currentTESLState?.hasValidState() ?: false)) {
            lastScreenshotPHash = screenshotPHash
            waitingScreenshotChangeWasLogged = false
            ScreenExecutor.process(screenshot)
            val sps = ELDER_SCROLL_SPS.takeIf { StateHandler.currentTESLState == null } ?: 0.5f
            delay((1000 / sps).toLong())
            if (!ScreenExecutor.screenRecognized) {
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