package com.jetbrains.edu.learning.courseFormat.tasks

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.edu.EducationalCoreIcons
import com.jetbrains.edu.coursecreator.StudyItemType
import com.jetbrains.edu.coursecreator.presentableName
import com.jetbrains.edu.learning.EduUtils
import com.jetbrains.edu.learning.actions.CheckAction
import com.jetbrains.edu.learning.courseDir
import com.jetbrains.edu.learning.courseFormat.*
import com.jetbrains.edu.learning.courseFormat.ext.findDir
import com.jetbrains.edu.learning.courseFormat.ext.project
import com.jetbrains.edu.learning.messages.EduCoreBundle.message
import com.jetbrains.edu.learning.stepik.hyperskill.courseFormat.HyperskillCourse
import com.jetbrains.edu.learning.submissions.SubmissionsManager.Companion.getInstance
import com.jetbrains.edu.learning.yaml.YamlDeserializer.deserializeTask
import java.io.IOException
import java.util.*
import javax.swing.Icon

/**
 * Implementation of task which contains task files, tests, input file for tests
 * Update [and StepikChangeRetriever#taskInfoChanged][com.jetbrains.edu.coursecreator.stepik.StepikChangeRetriever.taskFilesChanged] if you added new property that has to be compared
 * To implement new task there are 5 steps to be done:
 * - Extend [Task] class
 * - Update [com.jetbrains.edu.learning.stepik.api.StepikJacksonDeserializersKt.doDeserializeTask] to handle json serialization
 * - Update [com.jetbrains.edu.learning.checker.TaskCheckerProvider.getTaskChecker] and provide default checker for new task
 * - Update [com.jetbrains.edu.learning.stepik.StepikTaskBuilder.pluginTaskTypes] for the tasks we do not have separately on stepik
 *   and [com.jetbrains.edu.learning.stepik.StepikTaskBuilder.StepikTaskType] otherwise
 * - Handle yaml deserialization:
 * - add type in [com.jetbrains.edu.learning.yaml.YamlDeserializer.deserializeTask]
 * - add yaml mixins for course creator and student fields [com.jetbrains.edu.learning.yaml.format]
 */
abstract class Task : StudyItem {
  private var _taskFiles: MutableMap<String, TaskFile> = LinkedHashMap()
  var taskFiles: Map<String, TaskFile>
    get() = _taskFiles
    set(value) {
      require(value is LinkedHashMap<String, TaskFile>) // taskFiles is supposed to be ordered
      _taskFiles = value
    }

  var feedback: CheckFeedback? = null
  var descriptionText: String = ""
  var descriptionFormat: DescriptionFormat = EduUtils.getDefaultTaskDescriptionFormat()
  var feedbackLink: String? = null

  /**
   * null means that behaviour for this particular Task hasn't been configured by a user and [Course.solutionsHidden] should be used instead
   */
  var solutionHidden: Boolean? = null
  var record: Int = -1
  var isUpToDate: Boolean = true

  // Used for marketplace courses. We need to store a meta-entity id (corresponding to list of submissions) to correctly process submissions
  // storage on grazie platform
  var submissionsId: String? = null

  protected var checkStatus: CheckStatus = CheckStatus.Unchecked

  open var status: CheckStatus
    get() = checkStatus
    set(status) {
      for (taskFile in _taskFiles.values) {
        for (placeholder in taskFile.answerPlaceholders) {
          placeholder.status = status
        }
      }
      if (checkStatus !== status) {
        feedback = null
      }
      checkStatus = status
    }

  val lesson: Lesson
    get() = parent as? Lesson ?: error("Lesson is null for task $name")

  open val checkAction: CheckAction
    get() = course.checkAction
  open val isPluginTaskType: Boolean
    get() = true
  open val supportSubmissions: Boolean
    get() = false
  open val isToSubmitToRemote: Boolean
    get() = false
  open val isChangedOnFailed: Boolean // For retry button: true means task description changes after failing
    get() = false

  override val course: Course
    get() = lesson.course

  constructor() // used for deserialization
  constructor(name: String) : super(name)
  constructor(name: String, id: Int, position: Int, updateDate: Date, status: CheckStatus) : super(name) {
    this.id = id
    this.index = position
    this.updateDate = updateDate
    checkStatus = status
  }

  override fun init(parentItem: ItemContainer, isRestarted: Boolean) {
    parent = parentItem
    for (taskFile in _taskFiles.values) {
      taskFile.initTaskFile(this, isRestarted)
    }
  }

  fun getTaskFile(name: String): TaskFile? {
    return _taskFiles[name]
  }

  @JvmOverloads
  fun addTaskFile(name: String, isVisible: Boolean = true): TaskFile {
    val taskFile = TaskFile()
    taskFile.task = this
    taskFile.name = name
    taskFile.isVisible = isVisible
    _taskFiles[name] = taskFile
    return taskFile
  }

  fun addTaskFile(taskFile: TaskFile) {
    taskFile.task = this
    _taskFiles[taskFile.name] = taskFile
  }

  fun addTaskFile(taskFile: TaskFile, position: Int) {
    taskFile.task = this
    if (position < 0 || position > _taskFiles.size) {
      throw IndexOutOfBoundsException()
    }
    val newTaskFileMap = LinkedHashMap<String, TaskFile>(_taskFiles.size + 1)
    var currentIndex = 0
    for ((key, value) in _taskFiles) {
      if (currentIndex == position) {
        newTaskFileMap[taskFile.name] = taskFile
      }
      newTaskFileMap[key] = value
      currentIndex++
    }
    if (currentIndex == position) {
      newTaskFileMap[taskFile.name] = taskFile
    }
    _taskFiles = newTaskFileMap
  }

  //used for yaml deserialization
  private fun setTaskFileValues(taskFiles: List<TaskFile>) {
    _taskFiles.clear()
    for (taskFile in taskFiles) {
      _taskFiles[taskFile.name] = taskFile
    }
  }

  fun getTaskFileValues(): Collection<TaskFile> {
    return _taskFiles.values
  }

  fun removeTaskFile(taskFile: String): TaskFile? {
    return _taskFiles.remove(taskFile)
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null || javaClass != other.javaClass) return false
    val task = other as Task
    if (index != task.index) return false
    if (name != task.name) return false
    if (_taskFiles != task._taskFiles) return false
    if (descriptionText != task.descriptionText) return false
    return descriptionFormat == task.descriptionFormat
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + index
    result = 31 * result + _taskFiles.hashCode()
    result = 31 * result + descriptionText.hashCode()
    result = 31 * result + descriptionFormat.hashCode()
    return result
  }

  fun copy(): Task {
    return copyAs(javaClass)
  }

  fun isValid(project: Project): Boolean {
    val taskDir = getDir(project.courseDir) ?: return false
    for (taskFile in _taskFiles.values) {
      val file = EduUtils.findTaskFileInDir(taskFile, taskDir) ?: continue
      try {
        val text = VfsUtilCore.loadText(file)
        if (!taskFile.isValid(text)) return false
      }
      catch (e: IOException) {
        return false
      }
    }
    return true
  }

  open fun getIcon(): Icon {
    if (checkStatus === CheckStatus.Unchecked) {
      return EducationalCoreIcons.Task
    }
    val project = course.project
    if (project != null && getInstance(project).containsCorrectSubmission(id)) {
      return EducationalCoreIcons.TaskSolved
    }
    return if (checkStatus === CheckStatus.Solved) EducationalCoreIcons.TaskSolved else EducationalCoreIcons.TaskFailed
  }

  override fun getDir(baseDir: VirtualFile): VirtualFile? {
    val lessonDir = lesson.getDir(baseDir)
    return findDir(lessonDir)
  }

  fun getUIName(): String = if (course is HyperskillCourse) {
    if (this is CodeTask) message("item.task.challenge") else message("item.task.stage")
  }
  else {
    StudyItemType.TASK_TYPE.presentableName
  }

  companion object {
    @JvmStatic
    protected val LOG = Logger.getInstance(Task::class.java)
  }
}