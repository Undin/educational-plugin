package com.jetbrains.edu.learning.compatibility

import com.intellij.util.PlatformUtils
import com.jetbrains.edu.learning.plugins.PluginInfo

class ScalaGradleCourseCompatibilityProvider : CourseCompatibilityProvider {
  override fun requiredPlugins(): List<PluginInfo>? {
    if (!PlatformUtils.isIntelliJ()) return null
    return listOf(
      PluginInfo.SCALA,
      PluginInfo.JAVA,
      PluginInfo.GRADLE,
      PluginInfo.JUNIT
    )
  }

  override val technologyName: String get() = "Scala"
}
