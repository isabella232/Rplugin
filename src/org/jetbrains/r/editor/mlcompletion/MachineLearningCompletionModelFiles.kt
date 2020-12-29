package org.jetbrains.r.editor.mlcompletion

import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.io.FileUtil
import com.intellij.platform.templates.github.ZipUtil
import com.intellij.util.io.exists
import com.intellij.util.io.isDirectory
import com.intellij.util.io.isFile
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.version.Version
import org.jetbrains.idea.maven.aether.ArtifactRepositoryManager
import org.jetbrains.r.RPluginUtil
import org.jetbrains.r.editor.mlcompletion.model.updater.MachineLearningCompletionDependencyCoordinates
import java.io.File
import java.nio.file.Paths

class MachineLearningCompletionModelFiles {

  companion object {
    private fun resolveWithNullable(first: String?, vararg more: String): String? =
      first?.let {
        return Paths.get(first, *more).toString()
      }

    private fun getArtifactVersion(versionFile: String): Version? = File(versionFile).takeIf { it.exists() }
      ?.run {
        ArtifactRepositoryManager.asVersion(readText().trim())
      }

    private fun validateFile(file: String?): Boolean =
      file != null && Paths.get(file).run { exists() && isFile() }

    private fun validateDirectory(directory: String?): Boolean =
      directory != null && Paths.get(directory).run { exists() && isDirectory() }

    private fun File.clearDirectory() {
      if (exists()) {
        FileUtil.delete(toPath())
      }
      mkdir()
    }
  }

  val localServerDirectory = resolveWithNullable(RPluginUtil.helperPathOrNull, "python_server")
    ?.apply { File(this).mkdir() }
  val localServerModelDirectory = resolveWithNullable(localServerDirectory, "model")
  val localServerAppDirectory = resolveWithNullable(localServerDirectory, "app")

  val localServerAppExecutableFile = resolveWithNullable(localServerAppDirectory,
                                                         when {
                                                           SystemInfo.isWindows -> "run_demo.exe"
                                                           else -> "run_demo"
                                                         })
  val localServerConfigFile = resolveWithNullable(localServerAppDirectory, "config.yml")

  private val modelVersionFilePath = resolveWithNullable(localServerModelDirectory, "version.txt")
  private val applicationVersionFilePath = resolveWithNullable(localServerAppDirectory, "version.txt")
  val modelVersion: Version?
    get() = modelVersionFilePath?.let { getArtifactVersion(it) }
  val applicationVersion: Version?
    get() = applicationVersionFilePath?.let { getArtifactVersion(it) }

  fun updateArtifacts(artifacts: Collection<Artifact>) = artifacts.forEach { updateArtifactFromArchive(it) }

  private fun updateArtifactFromArchive(artifact: Artifact) : Boolean {
    val dstDir = File(when (artifact.artifactId) {
      MachineLearningCompletionDependencyCoordinates.MODEL_ARTIFACT_ID -> localServerModelDirectory
      MachineLearningCompletionDependencyCoordinates.APP_ARTIFACT_ID -> localServerAppDirectory
      else -> null
    } ?: return false)

    // TODO: shutdown running app prior to this
    dstDir.clearDirectory()

    ZipUtil.unzip(null, dstDir, artifact.file, null, null, true)
    return true
  }

  fun available(): Boolean = modelAvailable() && applicationAvailable()

  private fun modelAvailable(): Boolean =
    validateDirectory(localServerModelDirectory)

  private fun applicationAvailable(): Boolean =
    validateDirectory(localServerAppDirectory) &&
    validateFile(localServerConfigFile) &&
    validateFile(localServerAppExecutableFile)
}