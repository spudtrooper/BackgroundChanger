package com.jeffpalm.backgroundchanger;

import com.jeffpalm.backgroundchanger.lib.BackgroundChangeModule;
import com.jeffpalm.backgroundchanger.lib.BuildConfigModule;

final class ComponentHelper {
  private ComponentHelper() {}

  public static BackgroundChangeeComponent newComponent() {
    return DaggerBackgroundChangeeComponent.builder().backgroundChangeModule(
        new BackgroundChangeModule()).buildConfigModule(new BuildConfigModule()).build();
  }
}
