package com.jeffpalm.backgroundchanger.lib;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.jeffpalm.backgroundchanger.util.Log;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

final class PubSubPublisher {
  private final Log log = Log.of(this);

  private final CredentialsProvider credentialsProvider;
  private final String projectId;
  private final String topicId;

  PubSubPublisher(CredentialsProvider credentialsProvider, String projectId, String topicId) {
    this.credentialsProvider = credentialsProvider;
    this.projectId = projectId;
    this.topicId = topicId;
  }

  public String sendMessage(String dataStr, Map<String, String> attributes) {
    log.d("sendMessage projectId=%s topicId=%s dataStr=%s attributes=%s",
        projectId,
        topicId,
        dataStr,
        attributes);

    ProjectTopicName topicName = ProjectTopicName.of(projectId, topicId);
    Publisher publisher = null;
    ApiFuture<String> future = null;
    try {
      // Create a publisher instance with default settings bound to the topic
      publisher = Publisher.newBuilder(topicName).setCredentialsProvider(credentialsProvider)
          .build();

      ByteString data = ByteString.copyFromUtf8(dataStr);
      PubsubMessage.Builder pubsubMessageBuilder = PubsubMessage.newBuilder().setData(data);
      for (Map.Entry<String, String> e : attributes.entrySet()) {
        pubsubMessageBuilder.getMutableAttributes().put(e.getKey(), e.getValue());
      }
      PubsubMessage pubsubMessage = pubsubMessageBuilder.build();

      // Schedule a message to be published. Messages are automatically batched.
      future = publisher.publish(pubsubMessage);
    } catch (IOException e) {
      log.e("Creating publisher", e);
    } finally {
      // Wait on any pending requests
      String id = null;
      try {
        id = future.get();
      } catch (InterruptedException e) {
        log.e("InterruptedException", e);
      } catch (ExecutionException e) {
        log.e("ExecutionException", e);
      }

      if (publisher != null) {
        try {
          publisher.shutdown();
        } catch (Exception e) {
          log.e("Shutting down publisher", e);
        }
      }

      return id;
    }
  }
}
