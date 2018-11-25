package com.jeffpalm.backgroundchanger;

import com.jeffpalm.backgroundchanger.lib.BackgroundChangeModule;
import com.jeffpalm.backgroundchanger.lib.BuildConfigModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {BackgroundChangeModule.class, BuildConfigModule.class})
interface BackgroundChangerComponent {
  void inject(MainActivity mainActivity);
}
