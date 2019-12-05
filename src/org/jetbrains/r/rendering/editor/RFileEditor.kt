/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.rendering.editor

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import java.awt.BorderLayout

class RFileEditor(project: Project, textEditor: TextEditor, virtualFile: VirtualFile)
  : AdvancedTextEditor(project, textEditor, virtualFile) {
  init {
    mainComponent.add(createREditorToolbar().component, BorderLayout.NORTH)
  }

  override fun getName() = "R Editor"

  private fun createREditorToolbar(): ActionToolbar =
    ActionManager.getInstance().createActionToolbar(ActionPlaces.EDITOR_TOOLBAR, createActionGroup(), true).also {
      it.setTargetComponent(textEditor.editor.contentComponent)
    }

  private fun createActionGroup(): ActionGroup = DefaultActionGroup(
    ToolbarAction("org.jetbrains.r.actions.RRunAction"),
    ToolbarAction("org.jetbrains.r.actions.RDebugAction"),
    Separator(),
    ToolbarAction("org.jetbrains.r.actions.RunSelection"),
    ToolbarAction("org.jetbrains.r.actions.DebugSelection"))

  private inner class ToolbarAction(actionId: String) : AnAction() {
    private val action = ActionManager.getInstance().getAction(actionId).also { copyFrom(it) }

    override fun actionPerformed(e: AnActionEvent) {
      action.actionPerformed(createEvent(e))
    }

    override fun update(e: AnActionEvent) {
      action.update(createEvent(e))
    }

    private fun createEvent(e: AnActionEvent): AnActionEvent {
      val file = FileDocumentManager.getInstance().getFile(textEditor.editor.document)
      return AnActionEvent.createFromInputEvent(
        e.inputEvent, "", e.presentation,
        SimpleDataContext.getSimpleContext(
          mapOf(CommonDataKeys.EDITOR.name to textEditor.editor,
                CommonDataKeys.VIRTUAL_FILE.name to file,
                CommonDataKeys.PSI_FILE.name to file?.let { PsiManager.getInstance(project).findFile(it) }),
          e.dataContext))
    }
  }
}
