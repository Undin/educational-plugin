package com.jetbrains.edu.python.learning.codeforces

import com.jetbrains.edu.EducationalCoreIcons
import com.jetbrains.edu.learning.EduNames
import com.jetbrains.edu.learning.codeforces.CodeforcesLanguageProvider
import com.jetbrains.edu.learning.configuration.EduConfigurator
import com.jetbrains.edu.python.learning.PyNewConfigurator
import com.jetbrains.python.newProject.PyNewProjectSettings
import javax.swing.Icon

class PyCodeforcesLanguageProvider : CodeforcesLanguageProvider() {
  override val configurator: EduConfigurator<PyNewProjectSettings> = PyNewConfigurator()
  override val languageId: String = EduNames.PYTHON
  override val templateFileName: String = "codeforces.Python main.py"
  override val displayTemplateName: String = "main.py"
  override val languageIcon: Icon = EducationalCoreIcons.PythonLogo

}