/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r

import com.intellij.codeInsight.lookup.Lookup
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.application.TransactionGuard
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.DumbServiceImpl
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.PlatformTestUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl
import com.intellij.testFramework.registerServiceInstance
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexImpl
import com.intellij.util.indexing.UnindexedFilesUpdater
import com.intellij.util.io.exists
import junit.framework.TestCase
import org.jetbrains.concurrency.Promise
import org.jetbrains.concurrency.isPending
import org.jetbrains.r.console.RConsoleView
import org.jetbrains.r.interpreter.RInterpreterManager
import org.jetbrains.r.interpreter.RInterpreterUtil
import org.jetbrains.r.interpreter.RInterpreterUtil.DEFAULT_TIMEOUT
import org.jetbrains.r.interpreter.RLocalInterpreterImpl
import org.jetbrains.r.interpreter.RLocalInterpreterLocation
import org.jetbrains.r.mock.MockInterpreterManager
import org.jetbrains.r.mock.MockRepoProvider
import org.jetbrains.r.packages.RPackage
import org.jetbrains.r.packages.RSkeletonUtil
import org.jetbrains.r.packages.remote.RepoProvider
import org.jetbrains.r.skeleton.RSkeletonFileType
import java.io.File
import java.io.IOException
import java.nio.file.Paths
import java.util.*

abstract class RUsefulTestCase : BasePlatformTestCase() {

  private var mockInterpreterManagerSet = false
  private var isLibraryAdded = false

  override fun getTestDataPath(): String {
    return TEST_DATA_PATH
  }

  public override fun setUp() {
    super.setUp()
    File(project.basePath!!).mkdirs()
    myFixture.testDataPath = testDataPath
  }

  public override fun tearDown() {
    isLibraryAdded = false
    super.tearDown()
  }

  fun addLibraries() {
    prepareTestSkeletons(project)
    setupMockInterpreterManager()
    val dumbService = DumbServiceImpl.getInstance(project)
    if (FileBasedIndex.getInstance() is FileBasedIndexImpl) {
      dumbService.queueTask(UnindexedFilesUpdater(project))
    }
    setupMockRepoProvider()
  }

  protected fun doExprTest(expressionList: String, checkWeakWarnings: Boolean = false): CodeInsightTestFixture {
    myFixture.configureByText("a.R", expressionList)
    configureFixture(myFixture)
    myFixture.testHighlighting(true, false, checkWeakWarnings)

    return myFixture
  }

  fun doApplyCompletionTest(text: String,
                            elementName: String,
                            expected: String,
                            fileIsRConsole: Boolean = false,
                            fileExtension: String = "R") {
    myFixture.configureByText("foo.$fileExtension", text)
    if (fileIsRConsole) {
      myFixture.file.putUserData(RConsoleView.IS_R_CONSOLE_KEY, true)
    }
    val result = myFixture.completeBasic()
    TestCase.assertNotNull(result)
    val element = result.first { it.lookupString == elementName }
    myFixture.lookup.currentItem = element
    myFixture.finishLookup(Lookup.NORMAL_SELECT_CHAR)
    val caretPosition = myFixture.caretOffset
    val newText = myFixture.file.text
    TestCase.assertEquals(expected, "${newText.substring(0, caretPosition)}<caret>${newText.substring(caretPosition, newText.length)}")
  }


  protected open fun configureFixture(myFixture: CodeInsightTestFixture) {}

  private fun createFixture(): CodeInsightTestFixture {
    val factory = IdeaTestFixtureFactory.getFixtureFactory()
    val fixtureBuilder = factory.createLightFixtureBuilder()
    val fixture = fixtureBuilder.fixture
    return IdeaTestFixtureFactory.getFixtureFactory().createCodeInsightFixture(fixture, LightTempDirTestFixtureImpl(true))
  }

  protected fun createAnActionEvent(): AnActionEvent =
    AnActionEvent.createFromDataContext(ActionPlaces.EDITOR_POPUP, null, createDataContext())

  protected open fun createDataContext(): DataContext {
    return DataContext {
      when (it) {
        CommonDataKeys.PROJECT.name -> myFixture.project
        CommonDataKeys.EDITOR.name -> myFixture.editor
        CommonDataKeys.PSI_FILE.name -> myFixture.file
        CommonDataKeys.VIRTUAL_FILE.name -> myFixture.file.virtualFile
        else -> null
      }
    }
  }

  protected fun resolve(): Array<ResolveResult> {
    val reference = myFixture.file.findReferenceAt(myFixture.caretOffset) ?: return emptyArray()
    return if (reference is PsiPolyVariantReference) {
      reference.multiResolve(false)
    }
    else {
      val result = reference.resolve() ?: return emptyArray()
      arrayOf(PsiElementResolveResult(result))
    }
  }

  protected fun <T : PsiElement> findElementAtCaret(aClass: Class<T>): T? {
    return PsiTreeUtil.getParentOfType(myFixture.file.findElementAt(myFixture.caretOffset), aClass, false)
  }

  fun setupMockInterpreterManager() {
    if (mockInterpreterManagerSet) return
    mockInterpreterManagerSet = true
    project.registerServiceInstance(RInterpreterManager::class.java, MockInterpreterManager(project))
  }

  private fun setupMockRepoProvider() {
    project.registerServiceInstance(RepoProvider::class.java, MockRepoProvider())
  }

  private fun prepareTestSkeletons(project: Project) {
    RSkeletonUtil.checkVersion(SKELETON_LIBRARY_PATH)
    val missingTestSkeletons = missingTestSkeletons()
    if (missingTestSkeletons.isEmpty()) return

    System.err.println("Generate binary summary for: " + missingTestSkeletons)

    val interpreterPath = RInterpreterUtil.suggestHomePath()
    check(
      !(interpreterPath.isBlank() || RInterpreterUtil.getVersionByPath(interpreterPath) == null)) { "No interpreter to build skeletons" }
    val location = RLocalInterpreterLocation(interpreterPath)
    val versionInfo = RInterpreterUtil.loadInterpreterVersionInfo(location)
    val rInterpreter = RLocalInterpreterImpl(location, versionInfo, project)
    rInterpreter.updateState().blockingGet(DEFAULT_TIMEOUT)
    val packagesForTest = missingTestSkeletons.map {
      rInterpreter.getPackageByName(it)?.run { RPackage(name, version) }
      ?: throw IllegalStateException("No package $it found for $interpreterPath")
    }
    RSkeletonUtil.generateSkeletons(Collections.singletonMap(SKELETON_LIBRARY_PATH, packagesForTest), rInterpreter)
  }

  private fun missingTestSkeletons(): Set<String> {
    val skeletonsDirectory = File(SKELETON_LIBRARY_PATH)
    val existedSkeletons = skeletonsDirectory.listFiles { _, name -> name.endsWith(".${RSkeletonFileType.EXTENSION}") }

    if (existedSkeletons == null) {
      if (!skeletonsDirectory.mkdirs()) {
        throw IOException("Can't create $skeletonsDirectory")
      }
      return packageNamesForTests
    }

    val foundSkeletons = existedSkeletons.map { it }.map { nameOfBinSummary(it) }.toSet()

    existedSkeletons.forEach {
      if (!packageNamesForTests.contains(nameOfBinSummary(it))) {
        it.delete()
      }
    }

    return packageNamesForTests.minus(foundSkeletons)
  }

  private fun nameOfBinSummary(file: File): String {
    val fileName = file.nameWithoutExtension
    return fileName.indexOf('-').takeIf { it != -1 }?.let { fileName.substring(0, it) } ?: fileName
  }

  protected fun doActionTest(expected: String, actionId: String) {
    val action = ActionManager.getInstance().getAction(actionId) ?: error("Action $actionId is non found")
    DataManager.getInstance().dataContextFromFocusAsync.onSuccess { dataContext ->
      TransactionGuard.submitTransaction(project, Runnable {
        val event = AnActionEvent.createFromAnAction(action, null, ActionPlaces.UNKNOWN, dataContext)
        ActionUtil.performActionDumbAwareWithCallbacks(action, event, dataContext)
      })
    }
    myFixture.checkResult(expected)
  }

  companion object {
    val TEST_DATA_PATH : String
      get() = Paths.get(File(PathManager.getHomePath(), "/rplugin/testData").path).takeIf { it.exists() }?.toString() ?:
              Paths.get(File("testData").absolutePath.replace(File.pathSeparatorChar, '/')).takeIf { it.exists() }?.toString() ?:
              throw IllegalStateException("Could not find testData path")
    val SKELETON_LIBRARY_PATH = File(TEST_DATA_PATH, "/skeletons").path
    private val packageNamesForTests: Set<String> = """
      base
      datasets
      data.table
      dplyr
      graphics
      grDevices
      magrittr
      methods
      stats
      utils
      roxygen2
      ggplot2
      tibble
      readxl
      readr
    """.trimIndent().split("\n").toSet()
  }
}

fun <T> Promise<T>.blockingGetAndDispatchEvents(timeout: Int, edtTimeout: Int = 300): T? {
  val time = System.currentTimeMillis()
  while (System.currentTimeMillis() - time < timeout && isPending) {
    PlatformTestUtil.dispatchAllEventsInIdeEventQueue()
    var writeActionTime = System.currentTimeMillis()
    runWriteAction {
      writeActionTime = System.currentTimeMillis() - writeActionTime
    }
    // No UI Freezes
    TestCase.assertTrue("Timeout: $writeActionTime > $edtTimeout", writeActionTime < edtTimeout)
    Thread.sleep(5)
  }
  TestCase.assertTrue(isSucceeded)
  return blockingGet(1)
}
