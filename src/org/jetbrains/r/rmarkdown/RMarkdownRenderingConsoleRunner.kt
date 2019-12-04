/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.rmarkdown

import com.intellij.execution.ExecutionManager
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessOutputTypes.STDOUT
import com.intellij.execution.runners.ConsoleTitleGen
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VirtualFile
import icons.org.jetbrains.r.RBundle
import org.jetbrains.r.interpreter.RInterpreterManager
import org.jetbrains.r.packages.*
import org.jetbrains.r.rendering.settings.RMarkdownSettings
import java.awt.BorderLayout
import java.io.File
import java.nio.file.Paths
import javax.swing.JPanel

class RMarkdownRenderingConsoleRunner(private val project : Project,
                                      private val consoleTitle: String = "RMarkdown Console") {
  private lateinit var title : String
  private val knitOutputListener = KnitrOutputListener()
  private var currentRenderFilePath: String = ""
  private var isInterrupted = false
  @Volatile
  private lateinit var processHandler: OSProcessHandler
  @Volatile
  private lateinit var consoleView: ConsoleViewImpl

  fun getResultPath() = knitOutputListener.getResultFileName()


  fun interruptRendering(){
    isInterrupted = true
    consoleView.print("\nInterrupted\n", ConsoleViewContentType.ERROR_OUTPUT)
    processHandler.process.destroy()
  }

  fun render(project: Project, file: VirtualFile, onFinished: (() -> Unit)? = null) {
    RMarkdownUtil.checkOrInstallPackages(project, RBundle.message("rmarkdown.processor.notification.utility.name"))
      .onSuccess { doRender(project, file, onFinished) }
      .onError { onFinished?.invoke() }
  }

  private fun createConsoleView(processHandler: OSProcessHandler): ConsoleViewImpl {
    consoleView = ConsoleViewImpl(project, false)
    val rMarkdownConsolePanel = JPanel(BorderLayout())
    rMarkdownConsolePanel.add(consoleView.component, BorderLayout.CENTER)
    title = ConsoleTitleGen(project, consoleTitle, false).makeTitle()
    val contentDescriptor = RunContentDescriptor(consoleView, processHandler, rMarkdownConsolePanel, title)
    contentDescriptor.setFocusComputable { consoleView.preferredFocusableComponent }
    contentDescriptor.isAutoFocusContent = true
    val contentManager = ExecutionManager.getInstance(project).contentManager
    val executor = DefaultRunExecutor.getRunExecutorInstance()
    contentManager.allDescriptors.filter { it.displayName == title }.forEach { contentManager.removeRunContent(executor, it) }
    contentManager.showRunContent(executor, contentDescriptor)
    return consoleView
  }

  private fun runRender(commands: ArrayList<String>, renderDirectory: String, path: String, onFinished: (() -> Unit)?) {
    knitOutputListener.onKnitrEnds = onFinished
    currentRenderFilePath = path
    val commandLine = GeneralCommandLine(commands)
    commandLine.setWorkDirectory(renderDirectory)
    processHandler = OSProcessHandler(commandLine)
    processHandler.addProcessListener(knitOutputListener)
    runInEdt {
      val consoleView = createConsoleView(processHandler)
      consoleView.attachToProcess(processHandler)
      consoleView.scrollToEnd()
      processHandler.startNotify()
    }
  }

  private fun doRender(project: Project,
                       rMarkdownFile: VirtualFile,
                       onFinished: (() -> Unit)?) {
    fun getRScriptPath(interpreterPath: String): String {
      return if (SystemInfo.isWindows) {
        val withoutExe = interpreterPath.substring(0, interpreterPath.length - 4)
        withoutExe + "script.exe"
      } else {
        interpreterPath + "script"
      }
    }

    fun getPandocLibraryPath(): String {
      return Paths.get(PathManager.getPluginsPath(), "rplugin", "pandoc").toString().also {
        File(it).mkdir()
      }
    }

    val interpreter = RInterpreterManager.getInterpreter(project) ?: return
    val pathToRscript = getRScriptPath(interpreter.interpreterPath)
    val scriptPath = StringUtil.escapeBackSlashes(R_MARKDOWN_HELPER.absolutePath)
    val filePath = StringUtil.escapeBackSlashes(rMarkdownFile.path)
    val knitRootDirectory = StringUtil.escapeBackSlashes(
      RMarkdownSettings.getInstance(project).state.getKnitRootDirectory(rMarkdownFile.path))
    val libraryPath = StringUtil.escapeBackSlashes(getPandocLibraryPath())
    val script = arrayListOf<String>(pathToRscript, scriptPath, libraryPath, filePath, knitRootDirectory)
    runRender(script, knitRootDirectory, rMarkdownFile.path, onFinished)
  }

  private fun renderingErrorNotification(errorMessage : String) {
    val notification = Notification("RMarkdownRenderError", "Rendering status",
                                    errorMessage, NotificationType.ERROR, null)
    notification.notify(project)
  }

  private inner class KnitrOutputListener : ProcessAdapter() {
    private var outputFile : String? = null
    private var exitCode = -1
    private val OUTPUT_KEY = " --output "
    var onKnitrEnds: (()->Unit)? = null

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
      super.onTextAvailable(event, outputType)
      if (outputType == STDOUT && event.text.isNotBlank() && event.text.split(OUTPUT_KEY).size == 2) {
        outputFile = event.text.split(OUTPUT_KEY).lastOrNull()?.substringBefore(" ")?.trim()
      }
    }

    override fun processTerminated(event: ProcessEvent) {
      super.processTerminated(event)
      exitCode = event.exitCode
      if (exitCode != 0 && !isInterrupted) {
        renderingErrorNotification("Error occurred during rendering")
      } else if (!isInterrupted){
        RMarkdownSettings.getInstance(project).state.setProfileLastOutput(currentRenderFilePath,  outputFile.orEmpty())
      } else {
        isInterrupted = false
      }
      onKnitrEnds?.invoke()
    }

    fun getResultFileName() : String = outputFile.takeIf { exitCode == 0 } ?: ""
  }

  companion object {
    private val R_MARKDOWN_HELPER = RHelpersUtil.findFileInRHelpers("R/render_markdown.R")
  }
}