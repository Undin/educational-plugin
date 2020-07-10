package com.jetbrains.edu.coursecreator.actions.sections;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Function;
import com.jetbrains.edu.coursecreator.CCUtils;
import com.jetbrains.edu.coursecreator.SectionType;
import com.jetbrains.edu.coursecreator.actions.CCCreateStudyItemActionBase;
import com.jetbrains.edu.coursecreator.actions.NewStudyItemInfo;
import com.jetbrains.edu.coursecreator.actions.StudyItemVariant;
import com.jetbrains.edu.learning.OpenApiExtKt;
import com.jetbrains.edu.learning.courseFormat.Course;
import com.jetbrains.edu.learning.courseFormat.Section;
import com.jetbrains.edu.learning.courseFormat.StudyItem;
import com.jetbrains.edu.learning.messages.EduCoreStudyItemBundle;
import icons.EducationalCoreIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CCCreateSection extends CCCreateStudyItemActionBase<Section> {

  public CCCreateSection() {
    super(SectionType.INSTANCE, EducationalCoreIcons.Section);
  }

  @Override
  protected void addItem(@NotNull Course course, @NotNull Section section) {
    course.addSection(section);
  }

  @NotNull
  @Override
  protected Function<VirtualFile, ? extends StudyItem> getStudyOrderable(@NotNull final StudyItem item,
                                                                         @NotNull Course course) {
    return file -> course.getItem(file.getName());
  }

  @Override
  @Nullable
  protected VirtualFile createItemDir(@NotNull final Project project, @NotNull final Course course, @NotNull final Section section,
                                      @NotNull final VirtualFile parentDirectory) {
    return CCUtils.createSectionDir(project, section.getName());
  }

  @Override
  protected int getSiblingsSize(@NotNull Course course, @Nullable StudyItem parentItem) {
    return course.getItems().size();
  }

  @Nullable
  @Override
  protected StudyItem getParentItem(@NotNull Project project, @NotNull Course course, @NotNull VirtualFile directory) {
    return course;
  }

  @Nullable
  @Override
  protected StudyItem getThresholdItem(@NotNull Project project, @NotNull final Course course, @NotNull final VirtualFile sourceDirectory) {
    return course.getItem(sourceDirectory.getName());
  }

  @Override
  protected boolean isAddedAsLast(@NotNull Project project, @NotNull Course course, @NotNull VirtualFile sourceDirectory) {
    return sourceDirectory.equals(OpenApiExtKt.getCourseDir(project));
  }

  @Override
  protected void sortSiblings(@NotNull Course course, @Nullable StudyItem parentItem) {
    course.sortItems();
  }

  @Override
  protected void initItem(@NotNull Project project,
                          @NotNull Course course,
                          @Nullable StudyItem parentItem,
                          @NotNull Section item,
                          @NotNull NewStudyItemInfo info) {
    item.setCourse(course);
  }

  @NotNull
  @Override
  protected List<StudyItemVariant> getStudyItemVariants() {
    return Collections.singletonList(
      new StudyItemVariant(EduCoreStudyItemBundle.message("item.section.title"), "", EducationalCoreIcons.Section, Section::new)
    );
  }
}
