package org.jetbrains.plugins.notebooks.editor

import com.intellij.openapi.editor.impl.EditorImpl
import org.assertj.core.api.Assertions.assertThat

class CodeCellLinesChecker(private val description: String,
                           private val editorGetter: () -> EditorImpl) : (CodeCellLinesChecker.() -> Unit) -> Unit {
  private var markers: MutableList<NotebookCellLines.Marker>? = null
  private var intervals: MutableList<NotebookCellLines.Interval>? = null
  private var markersStartOffset: Int = 0
  private var markersStartOrdinal: Int = 0
  private var intervalsStartLine: Int = 0
  private val expectedIntervalListenerCalls = mutableListOf<Pair<List<NotebookCellLines.Interval>, List<NotebookCellLines.Interval>>>()

  inner class MarkersSetter {
    init {
      markers = markers ?: mutableListOf()
    }

    fun marker(cellType: NotebookCellLines.CellType, offset: Int, length: Int) {
      markers!!.add(
        NotebookCellLines.Marker(ordinal = markers!!.size + markersStartOrdinal, type = cellType, offset = offset, length = length))
    }
  }

  fun markers(startOffset: Int = 0, startOrdinal: Int = 0, handler: MarkersSetter.() -> Unit) {
    markersStartOffset = startOffset
    markersStartOrdinal = startOrdinal
    MarkersSetter().handler()
  }

  class IntervalsSetter(private val list: MutableList<NotebookCellLines.Interval>, private val startOrdinal: Int) {
    fun interval(cellType: NotebookCellLines.CellType, lines: IntRange) {
      list += NotebookCellLines.Interval(list.size + startOrdinal, cellType, lines)
    }
  }

  fun intervals(startLine: Int = 0, startOrdinal: Int = 0, handler: IntervalsSetter.() -> Unit) {
    intervalsStartLine = startLine
    (intervals ?: mutableListOf()).let {
      intervals = it
      IntervalsSetter(it, startOrdinal).handler()
    }
  }

  class IntervalListenerCalls(
    private val startOrdinal: Int,
    private val before: MutableList<NotebookCellLines.Interval>,
    private val after: MutableList<NotebookCellLines.Interval>
  ) {
    fun before(handler: IntervalsSetter.() -> Unit) {
      IntervalsSetter(before, startOrdinal).handler()
    }

    fun after(handler: IntervalsSetter.() -> Unit) {
      IntervalsSetter(after, startOrdinal).handler()
    }
  }

  fun intervalListenerCall(startOrdinal: Int, handler: IntervalListenerCalls.() -> Unit) {
    val before = mutableListOf<NotebookCellLines.Interval>()
    val after = mutableListOf<NotebookCellLines.Interval>()
    IntervalListenerCalls(startOrdinal, before, after).handler()
    expectedIntervalListenerCalls += before to after
  }

  override fun invoke(handler: CodeCellLinesChecker.() -> Unit) {
    val actualIntervalListenerCalls = mutableListOf<Pair<List<NotebookCellLines.Interval>, List<NotebookCellLines.Interval>>>()
    val intervalListener = object : NotebookCellLines.IntervalListener {
      override fun segmentChanged(oldIntervals: List<NotebookCellLines.Interval>, newIntervals: List<NotebookCellLines.Interval>) {
        actualIntervalListenerCalls += oldIntervals to newIntervals
      }
    }
    val codeCellLines = NotebookCellLines.get(editorGetter())
    codeCellLines.intervalListeners.addListener(intervalListener)
    val prettyDocumentTextBefore = editorGetter().prettyText

    try {
      handler()
    }
    catch (err: Throwable) {
      try {
        err.addSuppressed(Throwable("Document is: ${editorGetter().prettyText}"))
      }
      catch (ignored: Throwable) {
        // Nothing.
      }
      throw err
    }
    finally {
      codeCellLines.intervalListeners.removeListener(intervalListener)
    }

    val prettyDocumentTextAfter = editorGetter().prettyText

    for (attempt in 0..1) {
      val descr = """
        $description${if (attempt > 0) " (repeat to check idempotence)" else ""}
        Document before: $prettyDocumentTextBefore
        Document after: $prettyDocumentTextAfter
        """.trimIndent()
      markers.let { markers ->
        assertThat(codeCellLines.markersIterator(markersStartOffset).asSequence().toList())
          .describedAs(descr)
          .isEqualTo(markers)
      }
      intervals?.let { intervals ->
        assertThat(codeCellLines.intervalsIterator(intervalsStartLine).asSequence().toList())
          .describedAs(descr)
          .isEqualTo(intervals)
      }
    }

    fun List<Pair<List<NotebookCellLines.Interval>, List<NotebookCellLines.Interval>>>.prettyListeners() =
      withIndex().joinToString("\n\n") { (idx, pair) ->
        """
        Call #$idx
          Before:
        ${pair.first.joinToString { "    $it" }}
          After:
        ${pair.second.joinToString { "    $it" }}
        """.trimIndent()
      }

    assertThat(actualIntervalListenerCalls.prettyListeners())
      .describedAs("""
        $description (calls of IntervalListener)
        Document before: $prettyDocumentTextBefore
        Document after: $prettyDocumentTextAfter
        """.trimIndent())
      .isEqualTo(expectedIntervalListenerCalls.prettyListeners())
  }
}