package com.ediposouza.extensions

import com.ediposouza.ui.MainWidget
import java.awt.Menu
import java.awt.MenuItem
import java.awt.PopupMenu

fun String.toIntSafely() = this.toIntOrNull() ?: 0

fun String?.equalsOrNull(other: String): Boolean? = if (this == other) true else null

fun PopupMenu.addMenu(label: String, menuOp: Menu.() -> Unit) {
    MainWidget.contextMenu.items.add(javafx.scene.control.Menu(label))
    add(Menu(label).apply {
        menuOp()
    })
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

fun PopupMenu.addMenu(label: String): List<Any> {
    val menu = Menu(label)
    add(menu)
    val contextMenuItem = javafx.scene.control.Menu(label)
    MainWidget.contextMenu.items.add(contextMenuItem)
    return listOf(menu, contextMenuItem)
}

fun Menu.addMenuItem(menuItemLabel: String, onClick: () -> Unit) {
    add(MenuItem(menuItemLabel).apply {
        addActionListener { onClick() }
    })
    val mainContextMenu = MainWidget.contextMenu.items.find { it.text == label } as javafx.scene.control.Menu
    mainContextMenu.items.add(javafx.scene.control.MenuItem(menuItemLabel).apply {
        setOnAction { onClick() }
    })
}
