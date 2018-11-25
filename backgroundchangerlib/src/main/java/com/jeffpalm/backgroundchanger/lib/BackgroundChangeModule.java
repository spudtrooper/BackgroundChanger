package com.jeffpalm.backgroundchanger.lib;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.ByteArrayInputStream;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class BackgroundChangeModule {
  @Provides
  CredentialsProvider provideCredentialsProvider(@Named(
      "backgroundChangerGoogleApplicationCredentialsJson") String credentialsJson) {
    return () -> GoogleCredentials.fromStream(new ByteArrayInputStream(credentialsJson.getBytes(
        "UTF-8")));
  }

  @Provides
  BackgroundChangeMessageProvider provideBackgroundChangeMessageProvider(@Named(
      "backgroundChangerPubSubProjectId") String projectId,
                                                                         @Named(
                                                                             "backgroundChangerPubSubSubscriptionId") String subscriptionId,
                                                                         CredentialsProvider
                                                                             credentialsProvider) {
    return new BackgroundChangeMessageProviderImpl(projectId, subscriptionId, credentialsProvider);
  }

  @Provides
  BackgroundChangeMessageSender provideBackgroundChangeMessageSender(@Named(
      "backgroundChangerPubSubProjectId") String projectId,
                                                                     @Named(
                                                                         "backgroundChangerPubSubTopicId") String topicId,
                                                                     CredentialsProvider
                                                                         credentialsProvider) {
    return new BackgroundChangeMessageSenderImpl(new PubSubPublisher(credentialsProvider,
        projectId,
        topicId));
  }
}
