package com.jetbrains.edu.java;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.jetbrains.edu.learning.StudyBasePluginConfigurator;
import com.jetbrains.edu.learning.StudyPluginConfigurator;
import com.jetbrains.edu.learning.courseFormat.Course;
import com.jetbrains.edu.learning.StudyBaseToolWindowConfigurator;
import com.jetbrains.edu.learning.StudyTaskManager;
import com.jetbrains.edu.learning.settings.ModifiableSettingsPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EduJavaStudyToolWindowConfigurator extends StudyBasePluginConfigurator {
    @NotNull
    @Override
    public DefaultActionGroup getActionGroup(Project project) {
        DefaultActionGroup baseGroup = super.getActionGroup(project);
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(new EduJavaCheckAction());
        group.addAll(baseGroup);

        return group;
    }

    @NotNull
    @Override
    public String getDefaultHighlightingMode() {
        return "text/x-java";
    }

    @Override
    public boolean accept(@NotNull Project project) {
        StudyTaskManager instance = StudyTaskManager.getInstance(project);
        if (instance == null) return false;
        Course course = instance.getCourse();
        return course != null && "PyCharm".equals(course.getCourseType()) && "JAVA".equals(course.getLanguage());
    }

    @NotNull
    @Override
    public String getLanguageScriptUrl() {
        return getClass().getResource("/code_mirror/clike.js").toExternalForm();
    }

    @Nullable
    @Override
    public ModifiableSettingsPanel getSettingsPanel() {
        return null;
    }
}
