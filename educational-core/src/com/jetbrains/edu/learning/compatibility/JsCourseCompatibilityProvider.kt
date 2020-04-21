package com.jetbrains.edu.learning.compatibility

import com.intellij.util.PlatformUtils.*
import com.jetbrains.edu.learning.plugins.PluginInfo

class JsCourseCompatibilityProvider : CourseCompatibilityProvider {
  override fun requiredPlugins(): List<PluginInfo>? {
    return if (isIdeaUltimate() || isWebStorm() || isPyCharmPro() || isGoIde()) {
      listOf(
        PluginInfo.JAVA_SCRIPT,
        PluginInfo.JAVA_SCRIPT_DEBUGGER,
        PluginInfo.NODE_JS
      )
    } else {
      null
    }
  }

  override val technologyName: String get() = "JavaScript"
}