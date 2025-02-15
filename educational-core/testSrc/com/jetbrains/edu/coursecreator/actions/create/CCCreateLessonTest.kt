package com.jetbrains.edu.coursecreator.actions.create

import com.intellij.testFramework.LightPlatformTestCase
import com.jetbrains.edu.coursecreator.actions.studyItem.CCCreateLesson
import com.jetbrains.edu.coursecreator.ui.withMockCreateStudyItemUi
import com.jetbrains.edu.learning.EduActionTestCase
import com.jetbrains.edu.learning.courseFormat.CourseMode
import com.jetbrains.edu.learning.testAction

class CCCreateLessonTest : EduActionTestCase() {

  fun `test create lesson in course`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson {
        eduTask {
          taskFile("taskFile1.txt")
        }
      }
    }
    withMockCreateStudyItemUi(MockNewStudyItemUi("lesson2")) {
      testAction(CCCreateLesson.ACTION_ID, dataContext(LightPlatformTestCase.getSourceRoot()))
    }
    assertEquals(2, course.lessons.size)
  }

  fun `test create lesson in section`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section {
        lesson {
          eduTask {
            taskFile("taskFile1.txt")
          }
        }
      }
    }
    val sectionName = "section1"
    val sectionFile = findFile(sectionName)
    withMockCreateStudyItemUi(MockNewStudyItemUi("lesson2")) {
      testAction(CCCreateLesson.ACTION_ID, dataContext(sectionFile))
    }
    assertEquals(2, course.getSection(sectionName)!!.lessons.size)
  }

  fun `test create lesson between lessons in course`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson {
        eduTask {
          taskFile("taskFile1.txt")
        }
      }
      lesson(name = "lesson3") {
        eduTask {
          taskFile("taskFile2.txt")
        }
      }
    }
    val lessonFile = findFile("lesson1")
    withMockCreateStudyItemUi(MockNewStudyItemUi("lesson2", 2)) {
      testAction(CCCreateLesson.ACTION_ID, dataContext(lessonFile))
    }
    assertEquals(3, course.lessons.size)
    assertEquals(1, course.getLesson("lesson1")!!.index)
    assertEquals(2, course.getLesson("lesson2")!!.index)
    assertEquals(3, course.getLesson("lesson3")!!.index)
  }

  fun `test create lesson before lesson in course`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson {
        eduTask {
          taskFile("taskFile1.txt")
        }
      }
      lesson {
        eduTask {
          taskFile("taskFile2.txt")
        }
      }
    }
    val lessonFile = findFile("lesson1")
    withMockCreateStudyItemUi(MockNewStudyItemUi("lesson12", 2)) {
      testAction(CCCreateLesson.ACTION_ID, dataContext(lessonFile))
    }
    assertEquals(3, course.items.size)
    assertEquals(1, course.getLesson("lesson1")!!.index)
    assertEquals(2, course.getLesson("lesson12")!!.index)
    assertEquals(3, course.getLesson("lesson2")!!.index)
  }

  fun `test create lesson after lesson in course`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson {
        eduTask {
          taskFile("taskFile1.txt")
        }
      }
      lesson {
        eduTask {
          taskFile("taskFile2.txt")
        }
      }
    }
    val lessonFile = findFile("lesson2")
    withMockCreateStudyItemUi(MockNewStudyItemUi("lesson11", 2)) {
      testAction(CCCreateLesson.ACTION_ID, dataContext(lessonFile))
    }
    assertEquals(3, course.items.size)
    assertEquals(1, course.getLesson("lesson1")!!.index)
    assertEquals(2, course.getLesson("lesson11")!!.index)
    assertEquals(3, course.getLesson("lesson2")!!.index)
  }

  fun `test create lesson between lessons in section`() {
    val course = courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      section {
        lesson {
          eduTask {
            taskFile("taskFile1.txt")
          }
        }
        lesson(name = "lesson3") {
          eduTask {
            taskFile("taskFile2.txt")
          }
        }
      }
    }
    val sectionName = "section1"
    val lessonFile = findFile("$sectionName/lesson1")
    withMockCreateStudyItemUi(MockNewStudyItemUi("lesson2", 2)) {
      testAction(CCCreateLesson.ACTION_ID, dataContext(lessonFile))
    }
    val section = course.getSection(sectionName)
    assertEquals(3, section!!.items.size)
    assertEquals(1, section.getLesson("lesson1")!!.index)
    assertEquals(2, section.getLesson("lesson2")!!.index)
    assertEquals(3, section.getLesson("lesson3")!!.index)
  }

  fun `test create lesson not available inside lesson`() {
    courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson {
        eduTask {
          taskFile("taskFile1.txt")
        }
      }
    }
    val sourceVFile = findFile("lesson1/task1")
    testAction(CCCreateLesson.ACTION_ID, dataContext(sourceVFile), shouldBeEnabled = false)
  }

  fun `test create lesson not available on top level with section on top level`() {
    courseWithFiles(courseMode = CourseMode.EDUCATOR) {
      lesson {
        eduTask {
          taskFile("taskFile1.txt")
        }
      }
      section {
        lesson(name = "lesson3") {
          eduTask {
            taskFile("taskFile2.txt")
          }
        }
      }
    }
    val sourceVFile = findFile("lesson1/task1")
    testAction(CCCreateLesson.ACTION_ID, dataContext(sourceVFile), shouldBeEnabled = false)
  }
}
