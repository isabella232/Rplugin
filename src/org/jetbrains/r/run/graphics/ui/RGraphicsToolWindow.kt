// Copyright (c) 2017, Holger Brandl, Ekaterina Tuzova
/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.run.graphics.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.fileChooser.ex.FileSaverDialogImpl
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VfsUtil
import org.intellij.datavis.inlays.components.GraphicsPanel
import icons.org.jetbrains.r.RBundle
import icons.org.jetbrains.r.notifications.RNotificationUtil
import java.awt.Desktop
import org.jetbrains.r.run.graphics.RGraphicsRepository
import org.jetbrains.r.run.graphics.RGraphicsUtils
import org.jetbrains.r.settings.RGraphicsSettings
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class RGraphicsToolWindow(project: Project) : SimpleToolWindowPanel(true, true) {
  private var lastSnapshots = listOf<File>()
  private var lastIndex = -1

  private val panel = GraphicsPanel(project)

  init {
    setContent(panel.component)
    val groups = createActionHolderGroups(project)
    toolbar = RGraphicsToolbar(groups).component

    RGraphicsRepository.getInstance(project).addSnapshotListener { snapshots ->
      ApplicationManager.getApplication().invokeLater {
        refresh(snapshots)
      }
    }
  }

  private fun refresh(snapshots: List<File>) {
    lastSnapshots = snapshots
    if (snapshots.isNotEmpty()) {
      lastIndex = lastSnapshots.lastIndex
      showCurrent()
    } else {
      reset()
    }
  }

  private fun reset() {
    lastSnapshots = listOf()
    lastIndex = -1
    panel.reset()
  }

  private fun showCurrent() {
    panel.refresh(lastSnapshots[lastIndex])
  }

  private fun createActionHolderGroups(project: Project): List<RGraphicsToolbar.ActionHolderGroup> {
    class PreviousGraphicsActionHolder : RGraphicsToolbar.ActionHolder {
      override val title: String? = null
      override val description = PREVIOUS_GRAPHICS_ACTION_DESCRIPTION
      override val icon = PREVIOUS_GRAPHICS_ACTION_ICON

      override val canClick: Boolean
        get() = lastIndex > 0

      override fun onClick() {
        lastIndex--
        showCurrent()
      }
    }

    class NextGraphicsActionHolder : RGraphicsToolbar.ActionHolder {
      override val title: String? = null
      override val description = NEXT_GRAPHICS_ACTION_DESCRIPTION
      override val icon = NEXT_GRAPHICS_ACTION_ICON

      override val canClick: Boolean
        get() = lastIndex < lastSnapshots.lastIndex

      override fun onClick() {
        lastIndex++
        showCurrent()
      }
    }

    class ExportGraphicsActionHolder : RGraphicsToolbar.ActionHolder {
      override val title = EXPORT_GRAPHICS_ACTION_TITLE
      override val description = EXPORT_GRAPHICS_ACTION_DESCRIPTION
      override val icon = EXPORT_GRAPHICS_ACTION_ICON

      override val canClick: Boolean
        get() = lastSnapshots.isNotEmpty()

      override fun onClick() {
        val title = RBundle.message("graphics.panel.file.saver.title")
        val description = RBundle.message("graphics.panel.file.saver.description")
        val descriptor = FileSaverDescriptor(title, description, "png")
        val baseFile = VfsUtil.findFile(Paths.get(project.basePath!!), false)
        val wrapper = FileSaverDialogImpl(descriptor, project).save(baseFile, "Snapshot")
        if (wrapper != null) {
          try {
            val destination = wrapper.file
            createDestinationFile(destination)
            val source = lastSnapshots[lastIndex]
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
          } catch (e: Exception) {
            val details = e.message?.let { ".\n$it" } ?: ""
            val header = RBundle.message("graphics.panel.file.saver.failure.header")
            RNotificationUtil.notifyGraphicsError(project, "$header: ${wrapper.file}$details")
          }
        }
      }
    }

    class ZoomGraphicsActionHolder : RGraphicsToolbar.ActionHolder {
      override val title = ZOOM_GRAPHICS_ACTION_TITLE
      override val description = ZOOM_GRAPHICS_ACTION_DESCRIPTION
      override val icon = ZOOM_GRAPHICS_ACTION_ICON

      override val canClick: Boolean
        get() = lastSnapshots.isNotEmpty()

      override fun onClick() {
        val snapshot = lastSnapshots[lastIndex]
        Desktop.getDesktop().open(snapshot)
      }
    }

    class ClearGraphicsActionHolder : RGraphicsToolbar.ActionHolder {
      override val title = CLEAR_GRAPHICS_ACTION_TITLE
      override val description = CLEAR_GRAPHICS_ACTION_DESCRIPTION
      override val icon = CLEAR_GRAPHICS_ACTION_ICON

      override val canClick: Boolean
        get() = lastSnapshots.isNotEmpty()

      override fun onClick() {
        RGraphicsRepository.getInstance(project).clearSnapshot(lastIndex)
      }
    }

    class ClearAllGraphicsActionHolder : RGraphicsToolbar.ActionHolder {
      override val title = CLEAR_ALL_GRAPHICS_ACTION_TITLE
      override val description = CLEAR_ALL_GRAPHICS_ACTION_DESCRIPTION
      override val icon = CLEAR_ALL_GRAPHICS_ACTION_ICON

      override val canClick: Boolean
        get() = lastSnapshots.isNotEmpty()

      override fun onClick() {
        RGraphicsRepository.getInstance(project).clearAllSnapshots()
      }
    }

    class TuneGraphicsDeviceActionHolder : RGraphicsToolbar.ActionHolder {
      private val repository = RGraphicsRepository.getInstance(project)

      override val title = TUNE_GRAPHICS_DEVICE_ACTION_TITLE
      override val description = TUNE_GRAPHICS_DEVICE_ACTION_DESCRIPTION
      override val icon = TUNE_GRAPHICS_DEVICE_ACTION_ICON

      override val canClick: Boolean
        get() = true

      override fun onClick() {
        fun getInitialParameters(): RGraphicsUtils.ScreenParameters {
          return repository.getScreenParameters() ?: RGraphicsUtils.getDefaultScreenParameters()
        }

        RGraphicsSettingsDialog(getInitialParameters()) { parameters ->
          RGraphicsSettings.setScreenParameters(project, parameters)
          repository.setScreenParameters(parameters)
        }.show()
      }
    }

    return listOf(
      RGraphicsToolbar.groupOf(
        PreviousGraphicsActionHolder(),
        NextGraphicsActionHolder()
      ),
      RGraphicsToolbar.groupOf(
        ExportGraphicsActionHolder()
      ),
      RGraphicsToolbar.groupOf(
        ZoomGraphicsActionHolder()
      ),
      RGraphicsToolbar.groupOf(
        ClearGraphicsActionHolder()
      ),
      RGraphicsToolbar.groupOf(
        ClearAllGraphicsActionHolder()
      ),
      RGraphicsToolbar.groupOf(
        TuneGraphicsDeviceActionHolder()
      )
    )
  }

  companion object {
    const val TOOL_WINDOW_ID = "R Graphics"

    private val EXPORT_GRAPHICS_ACTION_TITLE = RBundle.message("graphics.panel.action.export.title")
    private val ZOOM_GRAPHICS_ACTION_TITLE = RBundle.message("graphics.panel.action.zoom.title")
    private val CLEAR_GRAPHICS_ACTION_TITLE = RBundle.message("graphics.panel.action.clear.title")
    private val CLEAR_ALL_GRAPHICS_ACTION_TITLE = RBundle.message("graphics.panel.action.clear.all.title")
    private val TUNE_GRAPHICS_DEVICE_ACTION_TITLE = RBundle.message("graphics.panel.action.tune.device.title")

    private val PREVIOUS_GRAPHICS_ACTION_DESCRIPTION = RBundle.message("graphics.panel.action.previous.description")
    private val NEXT_GRAPHICS_ACTION_DESCRIPTION = RBundle.message("graphics.panel.action.next.description")
    private val EXPORT_GRAPHICS_ACTION_DESCRIPTION = RBundle.message("graphics.panel.action.export.description")
    private val ZOOM_GRAPHICS_ACTION_DESCRIPTION = RBundle.message("graphics.panel.action.zoom.description")
    private val CLEAR_GRAPHICS_ACTION_DESCRIPTION = RBundle.message("graphics.panel.action.clear.description")
    private val CLEAR_ALL_GRAPHICS_ACTION_DESCRIPTION = RBundle.message("graphics.panel.action.clear.all.description")
    private val TUNE_GRAPHICS_DEVICE_ACTION_DESCRIPTION = RBundle.message("graphics.panel.action.tune.device.description")

    private val PREVIOUS_GRAPHICS_ACTION_ICON = AllIcons.Actions.Back
    private val NEXT_GRAPHICS_ACTION_ICON = AllIcons.Actions.Forward
    private val EXPORT_GRAPHICS_ACTION_ICON = AllIcons.Actions.Menu_saveall
    private val ZOOM_GRAPHICS_ACTION_ICON = AllIcons.Actions.Search
    private val CLEAR_GRAPHICS_ACTION_ICON = AllIcons.Actions.Close  // TODO [mine]: adequate icons, please
    private val CLEAR_ALL_GRAPHICS_ACTION_ICON = AllIcons.Actions.Cancel
    private val TUNE_GRAPHICS_DEVICE_ACTION_ICON = AllIcons.General.GearPlain

    private fun createDestinationFile(file: File) {
      if (!file.exists() && !file.createNewFile()) {
        throw RuntimeException(RBundle.message("graphics.panel.file.saver.failure.details"))
      }
    }
  }
}