package com.ediposouza.extensions

import com.ediposouza.ui.MainWidget
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import tornadofx.Rest
import java.awt.Menu
import java.awt.MenuItem
import java.awt.PopupMenu
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import javax.imageio.ImageIO

fun String?.toIntSafely() = this?.toIntOrNull() ?: 0

fun String?.equalsOrNull(other: String): Boolean? = if (this == other) true else null

fun PopupMenu.addMenu(label: String, menuOp: Menu.() -> Unit): List<Any> {
    val contextMenuItem = javafx.scene.control.Menu(label)
    MainWidget.contextMenu.items.add(contextMenuItem)
    val menu = Menu(label).apply {
        menuOp()
    }
    add(menu)
    return listOf(menu, contextMenuItem)
}

fun PopupMenu.addMenu(label: String): List<Any> {
    val menu = Menu(label)
    add(menu)
    val contextMenuItem = javafx.scene.control.Menu(label)
    MainWidget.contextMenu.items.add(contextMenuItem)
    return listOf(menu, contextMenuItem)
}

fun Menu.addMenu(label: String, icon: Image? = null, menuOp: Menu.() -> Unit): List<Any> {
    val contextMenuItem = javafx.scene.control.Menu(label).apply {
        icon?.let {
            graphic = ImageView(it).apply {
                fitHeight = 15.0
                fitWidth = 30.0
            }
        }
    }
    val mainContextMenu = MainWidget.contextMenu.items.find { it.text == this.label } as? javafx.scene.control.Menu
    mainContextMenu?.items?.add(contextMenuItem)
    val menu = Menu(label).apply {
        menuOp()
//        icon?.let {
//            setGraphic(it)
//        }
    }
    add(menu)
    return listOf(menu, contextMenuItem)
}

fun PopupMenu.addMenuItem(label: String, onClick: () -> Unit): List<Any> {
    val menuItem = MenuItem(label)
    add(menuItem.apply {
        addActionListener { onClick() }
    })
    val contextMenuItem = javafx.scene.control.MenuItem(label)
    MainWidget.contextMenu.items.add(contextMenuItem.apply {
        setOnAction { onClick() }
    })
    return listOf(menuItem, contextMenuItem)
}

fun Menu.addMenuItem(menuItemLabel: String, onClick: () -> Unit) {
    add(MenuItem(menuItemLabel).apply {
        addActionListener { onClick() }
    })
    val menuItem = javafx.scene.control.MenuItem(menuItemLabel).apply {
        setOnAction { onClick() }
    }
    val mainContextMenu = MainWidget.contextMenu.items.find { it.text == label } as? javafx.scene.control.Menu
    if (mainContextMenu != null) {
        mainContextMenu.items.add(menuItem)
    } else {
        MainWidget.contextMenu.items.filter { it is javafx.scene.control.Menu }.forEach {
            val subMenu = it as javafx.scene.control.Menu
            val mainContextSubMenu = subMenu.items.find { it.text == this.label } as? javafx.scene.control.Menu
            mainContextSubMenu?.items?.add(menuItem)
        }
    }
}

fun InputStream.toFXImage(): Image {
    return SwingFXUtils.toFXImage(ImageIO.read(this), null)
}

fun File.getMD5(): String? {
    val fis = FileInputStream(this)
    try {
        return DigestUtils.md5Hex(IOUtils.toByteArray(fis))
    } catch (e: Exception) {
        return null
    } finally {
        IOUtils.closeQuietly(fis)
    }
}

fun Rest.Response.asJson(): JsonObject {
    val text = text() ?: "{}"
    return JsonParser().parse(text.takeIf { text != "null" } ?: "{}").asJsonObject
}

fun alertAlwaysOnTop(type: Alert.AlertType,
                     header: String,
                     content: String,
                     vararg buttons: ButtonType,
                     actionFn: (Alert.(ButtonType) -> Unit)? = null): Alert {

    val alert = Alert(type, content, *buttons)
    alert.headerText = header
    with(alert.dialogPane) {
        scene.root = Group()
        Stage(StageStyle.UTILITY).apply {
            for (buttonType in buttonTypes) {
                lookupButton(buttonType).setOnMouseClicked {
                    this@with.userData = buttonType
                    close()
                }
            }
            initModality(Modality.APPLICATION_MODAL)
            isAlwaysOnTop = true
            scene = Scene(this@with)
        }.showAndWait()
        val button: Optional<ButtonType> = Optional.ofNullable(userData as? ButtonType)
        button.ifPresent { actionFn?.invoke(alert, button.get()) }
    }
    return alert
}
