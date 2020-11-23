package com.jetbrains.edu.scala.codeforces;

import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.jetbrains.edu.jvm.MainFileProvider;
import com.jetbrains.edu.learning.OpenApiExtKt;
import com.jetbrains.edu.learning.codeforces.run.CodeforcesRunConfiguration;
import com.jetbrains.edu.learning.courseFormat.Course;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

import static com.intellij.openapi.module.ModuleUtilCore.findModuleForFile;
import static com.jetbrains.edu.learning.codeforces.run.CodeforcesRunConfigurationType.CONFIGURATION_ID;

public class ScalaCodeforcesRunConfiguration extends ApplicationConfiguration implements CodeforcesRunConfiguration {
  private static final Logger LOG = Logger.getInstance(ScalaCodeforcesRunConfiguration.class);

  public ScalaCodeforcesRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory) {
    super(CONFIGURATION_ID, project, factory);
  }

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    super.checkConfiguration();
  }

  @Nullable
  @Override
  public VirtualFile getRedirectInputFile() {
    String path = getInputRedirectOptions().getRedirectInputPath();
    if (path == null) return null;
    return VfsUtil.findFile(Path.of(path), true);
  }

  @Override
  public void setExecutableFile(@NotNull VirtualFile file) {
    setModule(findModuleForFile(file, getProject()));
    Course course = OpenApiExtKt.getCourse(getProject());
    if (course == null) {
      LOG.error("Unable to find course");
      return;
    }
    Language language = course.getLanguageById();
    if (language == null) {
      LOG.error("Unable to get language for course " + course.getPresentableName());
      return;
    }

    PsiClass mainClass = ((PsiClass)MainFileProvider.Companion.getMainClass(getProject(), file, language));
    if (mainClass == null) {
      LOG.error("Unable to find main class for file " + file.getPath());
      return;
    }

    setMainClass(mainClass);
  }
}
