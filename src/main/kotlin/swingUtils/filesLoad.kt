package swingUtils

import java.awt.Component
import java.io.File
import javax.swing.JFileChooser


fun openFileChooser(window: Component, title: String): List<File> = JFileChooser().apply {
    dialogTitle = title
    dialogType = JFileChooser.OPEN_DIALOG
    isMultiSelectionEnabled = true
    fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
    isVisible = true
    showOpenDialog(window)
}.selectedFiles.distinct()
