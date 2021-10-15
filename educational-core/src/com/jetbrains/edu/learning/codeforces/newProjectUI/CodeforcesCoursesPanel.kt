package com.jetbrains.edu.learning.codeforces.newProjectUI

import com.intellij.ide.DataManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.util.ui.JBUI
import com.jetbrains.edu.learning.codeforces.CodeforcesNames
import com.jetbrains.edu.learning.codeforces.CodeforcesSettings
import com.jetbrains.edu.learning.codeforces.actions.CodeforcesLoginAction
import com.jetbrains.edu.learning.codeforces.actions.StartCodeforcesContestAction
import com.jetbrains.edu.learning.courseFormat.Course
import com.jetbrains.edu.learning.messages.EduCoreBundle
import com.jetbrains.edu.learning.newproject.coursesStorage.CoursesStorage
import com.jetbrains.edu.learning.newproject.ui.CourseCardComponent
import com.jetbrains.edu.learning.newproject.ui.CoursesPanel
import com.jetbrains.edu.learning.newproject.ui.CoursesPlatformProvider
import com.jetbrains.edu.learning.newproject.ui.LoginPanel
import com.jetbrains.edu.learning.newproject.ui.coursePanel.CoursePanel
import com.jetbrains.edu.learning.newproject.ui.coursePanel.MAIN_BG_COLOR
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.annotations.NonNls
import java.awt.BorderLayout
import javax.swing.JButton

class CodeforcesCoursesPanel(
  platformProvider: CoursesPlatformProvider,
  scope: CoroutineScope,
  disposable: Disposable
) : CoursesPanel(platformProvider, scope, disposable) {

  init {
    coursesSearchComponent.hideFilters()
    coursesSearchComponent.add(createOpenContestButtonPanel(), BorderLayout.LINE_END)
  }

  override fun createCoursePanel(disposable: Disposable): CoursePanel {
    return CodeforcesCoursePanel(disposable)
  }

  private fun createOpenContestButtonPanel(): NonOpaquePanel {
    val button = JButton(EduCoreBundle.message("codeforces.open.contest.by.link")).apply {
      background = MAIN_BG_COLOR
      isOpaque = false
      addActionListener(ActionUtil.createActionListener(StartCodeforcesContestAction.ACTION_ID, this, PLACE))
    }

    return NonOpaquePanel().apply {
      border = JBUI.Borders.empty(0, 0, 0, 12)
      add(button)
    }
  }

  override fun getEmptySearchText(): String {
    return EduCoreBundle.message("codeforces.search.placeholder")
  }

  override fun tabDescription(): String {
    val linkText = """<a href="${CodeforcesNames.CODEFORCES_URL}">${CodeforcesNames.CODEFORCES_TITLE}</a>"""
    return EduCoreBundle.message("codeforces.courses.description", linkText)
  }

  override fun createCoursesListPanel(): CoursesListWithResetFilters {
    return CodeforcesCoursesListPanel()
  }

  override fun getLoginComponent(): LoginPanel {
    return CodeforcesLoginPanel()
  }

  private inner class CodeforcesLoginPanel : LoginPanel(isLoginNeeded(),
                                                        CodeforcesNames.CODEFORCES_TITLE,
                                                        EduCoreBundle.message("course.dialog.to.sync.contest.progress"),
                                                        { handleLogin() })

  private fun handleLogin() {
    val loginAction = ActionManager.getInstance().getAction(CodeforcesLoginAction.ACTION_ID)
    val actionEvent = AnActionEvent.createFromAnAction(loginAction,
                                                       null,
                                                       PLACE,
                                                       DataManager.getInstance().getDataContext())
    loginAction.actionPerformed(actionEvent).let {
      if (CodeforcesSettings.getInstance().isLoggedIn()) {
        hideLoginPanel()
      }
    }


  }

  override fun isLoginNeeded(): Boolean = !CodeforcesSettings.getInstance().isLoggedIn()

  private inner class CodeforcesCoursesListPanel : CoursesListWithResetFilters() {
    override fun createCourseCard(course: Course): CourseCardComponent {
      val courseMetaInfo = CoursesStorage.getInstance().getCourseMetaInfoForAnyLanguage(course)
      if (courseMetaInfo != null) {
        course.language = courseMetaInfo.language
      }
      return CodeforcesCardComponent(course)
    }
  }

  companion object {
    @NonNls
    const val PLACE = "Codeforces Courses Panel"
  }
}
