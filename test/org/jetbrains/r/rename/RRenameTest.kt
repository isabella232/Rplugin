// Copyright (c) 2017, Holger Brandl, Ekaterina Tuzova
/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.rename

import com.intellij.codeInsight.TargetElementUtil
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.refactoring.BaseRefactoringProcessor
import com.intellij.testFramework.exceptionCases.AbstractExceptionCase
import com.intellij.testFramework.fixtures.CodeInsightTestUtil
import icons.org.jetbrains.r.RBundle
import org.jetbrains.r.RFileType.DOT_R_EXTENSION
import org.jetbrains.r.RLightCodeInsightFixtureTestCase
import org.jetbrains.r.refactoring.rename.RMemberInplaceRenameHandler
import org.jetbrains.r.refactoring.rename.RVariableInplaceRenameHandler

class RRenameTest : RLightCodeInsightFixtureTestCase() {

  fun testRenameFunction() = doTestWithProject("test_function1")

  fun testRenameFunctionUsage() = doTestWithProject("test_function1")

  fun testRenameParameter() = doTestWithProject("x1")

  fun testRenameParameterUsage() = doTestWithProject("x1")

  fun testRenameLocalVariable() = doTestWithProject("ttt1")

  fun testRenameLocalVariableUsage() = doTestWithProject("ttt1")

  fun testRenameLocalVariableClosure() = doTestWithProject("ttt1")

  fun testRenameLocalVariableClosureUsage() = doTestWithProject("ttt1")

  fun testRenameForLoopTarget() = doTestWithProject("k")

  fun testRenameForLoopTargetUsage() = doTestWithProject("l")

  fun testRenameQuotedVariable() = doTestWithProject("New value")

  fun testRenameQuotedUnquotedVariable() = doTestWithProject("var")

  fun testRenameNeedQuote() = doTestWithProject("New val")

  fun testRenameLibraryFunction() = doTestWithProject("printt", false)

  fun testRenameRedeclarationGlobalInFunction() = doTestWithProject("global")

  fun testRenameVariableInFileCollisions() = doExceptionTestWithProject("was", false)

  fun testRenameVariableInFunctionCollisions() = doExceptionTestWithProject("was", false, "scopeFun")

  fun testRenameFunctionInFileCollisions() = doExceptionTestWithProject("was", true)

  fun testRenameFunctionInFunctionCollisions() = doExceptionTestWithProject("was", true, "scopeFun")

  fun testRenameRFile() {
    val psiFile = myFixture.configureByText("foo.R", "print('Hello world')")
    myFixture.renameElement(psiFile, "bar.R")
    assertEquals(psiFile.name, "bar.R")
  }

  private fun doTestWithProject(newName: String, isInlineAvailable: Boolean = true) {
    myFixture.configureByFile("rename/" + getTestName(true) + DOT_R_EXTENSION)
    val variableHandler = RVariableInplaceRenameHandler()
    val memberHandler = RMemberInplaceRenameHandler()

    val element = TargetElementUtil.findTargetElement(myFixture.editor, TargetElementUtil.getInstance().getAllAccepted())

    assertNotNull(element)
    val dataContext = SimpleDataContext.getSimpleContext(CommonDataKeys.PSI_ELEMENT.name, element!!, createDataContext())
    val handler = when {
      memberHandler.isRenaming(dataContext) -> memberHandler
      variableHandler.isRenaming(dataContext) -> variableHandler
      else -> {
        assertFalse("In-place rename not allowed for $element", isInlineAvailable)
        return
      }
    }

    CodeInsightTestUtil.doInlineRename(handler, newName, myFixture)
    myFixture.checkResultByFile("rename/" + getTestName(true) + ".after.R", true)
  }

  private fun doExceptionTestWithProject(newName: String,
                                         isFunctionCollision: Boolean,
                                         functionScope: String? = null,
                                         isInlineAvailable: Boolean = true) {
    myFixture.configureByFile("rename/" + getTestName(true) + DOT_R_EXTENSION)

    val scopeString =
      if (functionScope != null) RBundle.message("rename.processor.function.scope", functionScope)
      else RBundle.message("rename.processor.file.scope", myFixture.file.name)
    val message =
      if (isFunctionCollision) RBundle.message("rename.processor.collision.function.description", newName, scopeString)
      else RBundle.message("rename.processor.collision.variable.description", newName, scopeString)

    assertException(object : CollisionErrorCase() {
      override fun tryClosure() {
        doTestWithProject(newName, isInlineAvailable)
      }
    }, message)
  }

  private abstract inner class CollisionErrorCase : AbstractExceptionCase<BaseRefactoringProcessor.ConflictsInTestsException>() {
    override fun getExpectedExceptionClass(): Class<BaseRefactoringProcessor.ConflictsInTestsException>? {
      return BaseRefactoringProcessor.ConflictsInTestsException::class.java
    }
  }
}
