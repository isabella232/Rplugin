/*
 * Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.jetbrains.r.rinterop

import junit.framework.TestCase
import org.jetbrains.r.run.RProcessHandlerBaseTestCase

class RInteropUnicodeTest : RProcessHandlerBaseTestCase() {
  fun testUnicode() {
    val s = "abc\u0448\u79c1 \u2203 x"
    val name = "\u79c2"
    rInterop.executeCode("`$name` <- '$s'")
    TestCase.assertEquals("[1] \"$s\"", rInterop.executeCode("`$name`").stdout.trim())
    TestCase.assertEquals(s, rInterop.executeCode("cat(`$name`)").stdout.trim())
    TestCase.assertTrue(s in (RRef.expressionRef("`$name`", rInterop).getValueInfo() as RValueSimple).text)
    TestCase.assertTrue(rInterop.globalEnvLoader.variables.any { it.name == name })
  }

  fun testUnicodeEscape() {
    val s = "abc\u0448\u79c1 \u2203 x"
    val escaped = "abc\\u0448\\u79c1 \\u2203 x"
    val name = "\u79c2"
    rInterop.executeCode("`$name` <- '$escaped'")
    TestCase.assertEquals("[1] \"$s\"", rInterop.executeCode("`$name`").stdout.trim())
    TestCase.assertEquals(s, rInterop.executeCode("cat(`$name`)").stdout.trim())
    TestCase.assertTrue(s in (RRef.expressionRef("`$name`", rInterop).getValueInfo() as RValueSimple).text)
    TestCase.assertTrue(rInterop.globalEnvLoader.variables.any { it.name == name })
  }

  fun testDataTable() {
    addLibraries()
    val s1 = "column \u79c1"
    val s2 = "value \u79c2"
    val table = rInterop.dataFrameGetViewer(RRef.expressionRef("dplyr::tibble(`$s1` = '$s2')", rInterop)).blockingGet(DEFAULT_TIMEOUT)!!
    TestCase.assertEquals(s1, table.getColumnName(1))
    TestCase.assertEquals(s2, table.getValueAt(0, 1))
  }
}