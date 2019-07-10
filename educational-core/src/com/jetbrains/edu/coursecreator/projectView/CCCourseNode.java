package com.jetbrains.edu.coursecreator.projectView;

import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.edu.learning.configuration.EduConfigurator;
import com.jetbrains.edu.learning.courseFormat.Course;
import com.jetbrains.edu.learning.courseFormat.Lesson;
import com.jetbrains.edu.learning.courseFormat.Section;
import com.jetbrains.edu.learning.courseFormat.ext.CourseExt;
import com.jetbrains.edu.learning.projectView.CourseNode;
import com.jetbrains.edu.learning.projectView.LessonNode;
import com.jetbrains.edu.learning.projectView.SectionNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.jetbrains.edu.learning.gradle.GradleConstants.*;

public class CCCourseNode extends CourseNode {
  private static final Collection<String> NAMES_TO_IGNORE = ContainerUtil.newHashSet(
    SETTINGS_GRADLE, LOCAL_PROPERTIES, GRADLE_WRAPPER_UNIX, GRADLE_WRAPPER_WIN);

  public CCCourseNode(@NotNull Project project,
                      PsiDirectory value,
                      ViewSettings viewSettings,
                      @NotNull Course course) {
    super(project, value, viewSettings, course);
  }

  @Nullable
  @Override
  public AbstractTreeNode modifyChildNode(@NotNull AbstractTreeNode childNode) {
    final AbstractTreeNode node = super.modifyChildNode(childNode);
    if (node != null) return node;
    if (childNode instanceof PsiFileNode) {
      VirtualFile virtualFile = ((PsiFileNode)childNode).getVirtualFile();
      if (virtualFile == null) {
        return null;
      }
      if (NAMES_TO_IGNORE.contains(virtualFile.getName())) {
        return null;
      }
      if (FileUtilRt.getExtension(virtualFile.getName()).equals("iml")) {
        return null;
      }
      return new CCStudentInvisibleFileNode(myProject, ((PsiFileNode)childNode).getValue(), getSettings());
    }
    EduConfigurator<?> configurator = CourseExt.getConfigurator(getItem());
    if (configurator == null) {
      return null;
    }
    if (childNode instanceof PsiDirectoryNode) {
      PsiDirectory psiDirectory = ((PsiDirectoryNode)childNode).getValue();
      if (!configurator.excludeFromArchive(myProject, psiDirectory.getVirtualFile())) {
        return new CCNode(myProject, psiDirectory, myViewSettings, null);
      }
    }
    return null;
  }

  @NotNull
  @Override
  protected LessonNode createLessonNode(@NotNull PsiDirectory directory, @NotNull Lesson lesson) {
    return new CCLessonNode(myProject, directory, getSettings(), lesson);
  }

  @NotNull
  @Override
  protected SectionNode createSectionNode(@NotNull PsiDirectory directory, @NotNull Section section) {
    return new CCSectionNode(myProject, getSettings(), section, directory);
  }
}
