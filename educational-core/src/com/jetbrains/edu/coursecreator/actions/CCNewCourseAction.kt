package com.jetbrains.edu.coursecreator.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.jetbrains.edu.coursecreator.ui.CCNewCourseDialog

class CCNewCourseAction(title: String = "Create New Course") : DumbAwareAction(title, "Create new educational course", null) {

  override fun actionPerformed(e: AnActionEvent) {
    CCNewCourseDialog("Create Course", "Create").show()
  }

  override fun update(e: AnActionEvent) {
    e.presentation.isEnabledAndVisible = CCPluginToggleAction.isCourseCreatorFeaturesEnabled
  }
}
