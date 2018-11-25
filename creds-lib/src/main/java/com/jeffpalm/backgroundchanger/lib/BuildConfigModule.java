package com.jeffpalm.backgroundchanger.lib;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import jeffpalm.com.creds_lib.BuildConfig;

@Module
public class BuildConfigModule {
  @Provides
  @Named("backgroundChangerGoogleApplicationCredentialsJson")
  String provideBackgroundChangerGoogleApplicationCredentialsJson() {
    return BuildConfig.BackgroundChangerGoogleApplicationCredentialsJson;
  }

  @Provides
  @Named("backgroundChangerPubSubProjectId")
  String provideBackgroundChangerPubSubProjectId() {
    return BuildConfig.BackgroundChangerPubSubProjectId;
  }

  @Provides
  @Named("backgroundChangerPubSubSubscriptionId")
  String provideBackgroundChangerPubSubSubscriptionId() {
    return BuildConfig.BackgroundChangerPubSubSubscriptionId;
  }

  @Provides
  @Named("backgroundChangerPubSubTopicId")
  String provideBackgroundChangerPubSubTopicId() {
    return BuildConfig.BackgroundChangerPubSubTopicId;
  }
}
