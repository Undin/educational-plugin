package com.jetbrains.edu.scala.sbt

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.edu.EducationalCoreIcons
import com.jetbrains.edu.jvm.JdkProjectSettings
import com.jetbrains.edu.jvm.stepik.fileName
import com.jetbrains.edu.learning.EduCourseBuilder
import com.jetbrains.edu.learning.EduNames
import com.jetbrains.edu.learning.EduUtils
import com.jetbrains.edu.learning.checker.TaskCheckerProvider
import com.jetbrains.edu.learning.configuration.EduConfigurator
import com.jetbrains.edu.learning.courseGeneration.GeneratorUtils.getInternalTemplateText
import com.jetbrains.edu.scala.isScalaPluginCompatible
import com.jetbrains.edu.scala.sbt.checker.ScalaSbtTaskCheckerProvider
import org.jetbrains.plugins.scala.ScalaLanguage
import javax.swing.Icon

class ScalaSbtConfigurator : EduConfigurator<JdkProjectSettings> {
  override val courseBuilder: EduCourseBuilder<JdkProjectSettings>
    get() = ScalaSbtCourseBuilder()

  override val testFileName: String
    get() = TEST_SCALA

  override val isEnabled: Boolean
    get() = !EduUtils.isAndroidStudio() && isScalaPluginCompatible

  override val taskCheckerProvider: TaskCheckerProvider
    get() = ScalaSbtTaskCheckerProvider()

  override fun getMockFileName(text: String): String = fileName(ScalaLanguage.INSTANCE, text)

  override val mockTemplate: String
    get() = getInternalTemplateText(MOCK_SCALA)

  override val sourceDir: String
    get() = EduNames.SRC

  override val testDirs: List<String>
    get() = listOf(EduNames.TEST)

  override val logo: Icon
    get() = EducationalCoreIcons.ScalaLogo

  override val defaultPlaceholderText: String
    get() = "/* TODO */"

  override fun excludeFromArchive(project: Project, file: VirtualFile): Boolean {
    return super.excludeFromArchive(project, file) ||
           generateSequence(file, VirtualFile::getParent).any { it.name == "target"}
  }

  companion object {
    const val TEST_SCALA = "TestSpec.scala"
    const val TASK_SCALA = "Task.scala"
    const val MAIN_SCALA = "Main.scala"
    const val MOCK_SCALA = "Mock.scala"
  }
}
