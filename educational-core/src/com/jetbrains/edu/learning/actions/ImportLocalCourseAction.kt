package com.jetbrains.edu.learning.actions

import com.intellij.ide.impl.ProjectUtil
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.edu.coursecreator.ui.CCCreateCoursePreviewDialog
import com.jetbrains.edu.learning.EduUtils
import com.jetbrains.edu.learning.courseFormat.Course
import com.jetbrains.edu.learning.messages.EduCoreBundle
import com.jetbrains.edu.learning.newproject.LocalCourseFileChooser
import com.jetbrains.edu.learning.newproject.coursesStorage.CoursesStorage
import com.jetbrains.edu.learning.newproject.ui.JoinCourseDialog
import com.jetbrains.edu.learning.statistics.EduCounterUsageCollector
import org.jetbrains.annotations.NonNls
import java.util.function.Supplier

open class ImportLocalCourseAction(
  text: Supplier<String> = EduCoreBundle.lazyMessage("course.dialog.open.course.from.disk"),
) : DumbAwareAction(text) {
  override fun actionPerformed(e: AnActionEvent) {
    FileChooser.chooseFile(LocalCourseFileChooser, null, importLocation()) { file ->
      val fileName = file.path
      var course = EduUtils.getLocalCourse(fileName)
      if (course == null) {
        showInvalidCourseDialog()
      }
      else {
        saveLastImportLocation(file)
        course = initCourse(course)

        val courseMetaInfo = CoursesStorage.getInstance().getCourseMetaInfo(course)
        if (courseMetaInfo != null) {
          invokeLater {
            val result = Messages.showOkCancelDialog(
              null,
              EduCoreBundle.message("action.import.local.course.dialog.text"),
              EduCoreBundle.message("action.import.local.course.dialog"),
              EduCoreBundle.message("action.import.local.course.dialog.cancel.text"),
              EduCoreBundle.message("action.import.local.course.dialog.ok.text"),
              Messages.getErrorIcon()
            )
            if (result == Messages.CANCEL) {
              EduCounterUsageCollector.importCourseArchive()
              course.putUserData(CCCreateCoursePreviewDialog.IS_LOCAL_COURSE_KEY, true)
              JoinCourseDialog(course).show()
            }
            else if (result == Messages.OK) {
              val project = ProjectUtil.openProject(courseMetaInfo.location, null, true)
              ProjectUtil.focusProjectWindow(project, true)
            }
          }
          return@chooseFile
        }
        EduCounterUsageCollector.importCourseArchive()
        JoinCourseDialog(course).show()
      }
    }
  }

  protected open fun initCourse(course: Course): Course {
    return course
  }

  companion object {
    @NonNls
    private const val LAST_IMPORT_LOCATION = "Edu.LastImportLocation"

    @JvmStatic
    fun importLocation(): VirtualFile? {
      val defaultDir = VfsUtil.getUserHomeDir()
      val lastImportLocation = PropertiesComponent.getInstance().getValue(LAST_IMPORT_LOCATION) ?: return defaultDir
      return LocalFileSystem.getInstance().findFileByPath(lastImportLocation) ?: defaultDir
    }

    @JvmStatic
    fun saveLastImportLocation(file: VirtualFile) {
      val location = if (!file.isDirectory) file.parent ?: return else file
      PropertiesComponent.getInstance().setValue(LAST_IMPORT_LOCATION, location.path)
    }

    @JvmStatic
    fun showInvalidCourseDialog() {
      Messages.showErrorDialog(
        EduCoreBundle.message("dialog.message.no.course.in.archive"),
        EduCoreBundle.message("dialog.title.failed.to.add.local.course")
      )
    }
  }
}
