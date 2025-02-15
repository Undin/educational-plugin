package com.jetbrains.edu.coursecreator.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "CCSettings", storages = @Storage("other.xml"))
public class CCSettings implements PersistentStateComponent<CCSettings.State> {
  private CCSettings.State myState = new CCSettings.State();

  public static class State {
    public boolean isHtmlDefault = false;
    public boolean showSplitEditor = false;
    public boolean copyTestsInFrameworkLessons = false;
  }

  @Nullable
  @Override
  public State getState() {
    return myState;
  }

  @Override
  public void loadState(@NotNull State state) {
    myState = state;
  }

  public boolean useHtmlAsDefaultTaskFormat() {
    return myState.isHtmlDefault;
  }

  public void setUseHtmlAsDefaultTaskFormat(final boolean useHtml) {
    myState.isHtmlDefault = useHtml;
  }

  public boolean showSplitEditor() {
    return myState.showSplitEditor;
  }

  public void setShowSplitEditor(boolean value) {
    myState.showSplitEditor = value;
  }

  public boolean copyTestsInFrameworkLessons() {
    return myState.copyTestsInFrameworkLessons;
  }

  public void setCopyTestsInFrameworkLessons(boolean value) {
    myState.copyTestsInFrameworkLessons = value;
  }

  public static CCSettings getInstance() {
    return ServiceManager.getService(CCSettings.class);
  }
}
