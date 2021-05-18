/*
 * Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.inspections.s4class

import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.r.RBundle
import org.jetbrains.r.classes.s4.context.RS4ContextProvider
import org.jetbrains.r.inspections.RInspection
import org.jetbrains.r.psi.api.RStringLiteralExpression
import org.jetbrains.r.psi.api.RVisitor
import org.jetbrains.r.psi.references.RSearchScopeUtil
import org.jetbrains.r.psi.stubs.RS4ClassNameIndex

class UnknownS4ClassNameInspection : RInspection() {
  override fun getDisplayName() = RBundle.message("inspection.unknown.s4.class.name.name")

  override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession): PsiElementVisitor {
    return Visitor(holder)
  }

  private class Visitor(private val myProblemHolder: ProblemsHolder) : RVisitor() {
    override fun visitStringLiteralExpression(str: RStringLiteralExpression) {
      val name = str.name.takeIf { it?.isNotEmpty() == true } ?: return
      RS4ContextProvider.getS4Context(str, *RS4ContextProvider.S4_CLASS_USAGE_CONTEXTS) ?: return
      if (RS4ClassNameIndex.findClassDefinitions(name, str.project, RSearchScopeUtil.getScope(str)).isEmpty()) {
        myProblemHolder.registerProblem(str,
                                        RBundle.message("inspection.unknown.s4.class.name.description", name),
                                        ProblemHighlightType.GENERIC_ERROR,
                                        ElementManipulators.getValueTextRange(str))
      }
    }
  }
}

