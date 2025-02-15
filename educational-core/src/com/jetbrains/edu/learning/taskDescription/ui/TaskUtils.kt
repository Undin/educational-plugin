@file:JvmName("TaskUtils")
package com.jetbrains.edu.learning.taskDescription.ui

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.project.Project
import com.jetbrains.edu.learning.EduUtils
import com.jetbrains.edu.learning.courseDir
import com.jetbrains.edu.learning.taskDescription.IMG_TAG
import com.jetbrains.edu.learning.taskDescription.SCRIPT_TAG
import com.jetbrains.edu.learning.taskDescription.SRC_ATTRIBUTE
import com.jetbrains.edu.learning.taskDescription.ui.styleManagers.StyleManager
import org.apache.commons.lang.text.StrSubstitutor
import org.jsoup.Jsoup
import java.io.File

fun htmlWithResources(project: Project, content: String): String {
  val templateText = loadText("/style/template.html.ft")
  val textWithResources = StrSubstitutor(StyleManager.resources(content)).replace(templateText) ?: "Cannot load task text"
  return absolutizePaths(project, textWithResources)
}

fun loadText(filePath: String): String? {
  val stream = try {
    object {}.javaClass.getResourceAsStream(filePath)
  } catch (e: NullPointerException) {
    return null
  }

  return stream.use {
    it.bufferedReader().readText()
  }
}

private fun absolutizePaths(project: Project, content: String): String {
  val task = EduUtils.getCurrentTask(project)
  if (task == null) {
    return content
  }

  val taskDir = task.getDir(project.courseDir)
  if (taskDir == null) {
    return content
  }

  val document = Jsoup.parse(content)
  val imageElements = document.getElementsByTag(IMG_TAG)
  val scriptElements = document.getElementsByTag(SCRIPT_TAG)
  for (element in scriptElements + imageElements) {
    val src = element.attr(SRC_ATTRIBUTE)
    if (src.isNotEmpty() && !BrowserUtil.isAbsoluteURL(src)) {
      val file = File(src)
      val absolutePath = File(taskDir.path, file.path).toURI().toString()
      element.attr(SRC_ATTRIBUTE, absolutePath)
    }
  }
  return document.outerHtml()
}
