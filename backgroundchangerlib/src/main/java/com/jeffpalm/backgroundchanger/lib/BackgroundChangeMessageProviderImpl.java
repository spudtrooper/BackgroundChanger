package com.jeffpalm.backgroundchanger.lib;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.AcknowledgeRequest;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;
import com.jeffpalm.backgroundchanger.util.Consumer;
import com.jeffpalm.backgroundchanger.util.ImageUtil;
import com.jeffpalm.backgroundchanger.util.Log;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

final class BackgroundChangeMessageProviderImpl implements BackgroundChangeMessageProvider {
  private final static class MessageData {
    final String url;
    final String msg;
    final Boolean beep;

    private MessageData(String url, String msg, Boolean beep) {
      this.url = url;
      this.msg = msg;
      this.beep = beep;
    }

    public boolean isEmpty() {
      return beep == null && isNullOrEmpty(url) && isNullOrEmpty(msg);
    }
  }

  private final static class ResponseImpl implements Response {
    private final Bitmap bitmap;
    private final Boolean beep;

    ResponseImpl(Bitmap bitmap, Boolean beep) {
      this.bitmap = bitmap;
      this.beep = beep;
    }

    @Override
    public Bitmap getBitmap() {
      return bitmap;
    }

    @Override
    public Boolean getBeep() {
      return beep;
    }
  }

  private final static class ChangeBackgroundRunnable implements Runnable {
    private final Log log = new Log(this);

    private final Consumer<Response> consumer;
    private final MessageData data;

    ChangeBackgroundRunnable(Consumer<Response> consumer, MessageData data) {
      this.consumer = consumer;
      this.data = data;
    }

    @Override
    public void run() {
      String msg = data.msg;
      String url = data.url;
      Boolean beep = data.beep;

      if (isNullOrEmpty(msg) && isNullOrEmpty(url)) {
        consumer.accept(new ResponseImpl(null, Boolean.TRUE == beep));
        return;
      }

      if (!isNullOrEmpty(msg)) {
        consumer.accept(new ResponseImpl(ImageUtil.convert(msg), beep));
        return;
      }

      Picasso.get().
          load(url).
          into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
              consumer.accept(new ResponseImpl(bitmap, beep));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
              log.e("Failed to load bitmap for " + url, e);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
              log.d("Preparing to load bitmap for: %s", url);
            }
          });
    }
  }

  private final Log log = new Log(this);
  private final String projectId;
  private final String subscriptionId;
  private final CredentialsProvider credentialsProvider;

  BackgroundChangeMessageProviderImpl(String projectId,
                                      String subscriptionId,
                                      CredentialsProvider credentialsProvider) {
    this.projectId = projectId;
    this.subscriptionId = subscriptionId;
    this.credentialsProvider = credentialsProvider;
  }

  private static boolean isNullOrEmpty(String s) {
    return s == null || s.length() == 0;
  }

  @Override
  public void checkForUpdates(Consumer<Response> consumer) {
    doCheckForUpdates(consumer);
  }

  private @Nullable
  String getAttribute(PubsubMessage message, String key) {
    try {
      String res = message.getAttributesOrThrow(key);
      log.d("Have u%srl: %s", key, res);
      return res;
    } catch (Throwable t) {
      log.d("Trying to get url", t);
    }
    return null;
  }

  private Boolean fromString(String s) {
    if ("true".equals(s)) {
      return true;
    }
    if ("false".equals(s)) {
      return false;
    }
    return null;
  }

  private MessageData createUrlToShow(PubsubMessage message) {
    String url = getAttribute(message, "url");
    Boolean beep = fromString(getAttribute(message, "beep"));
    return new MessageData(url, new String(message.getData().toByteArray()), beep);
  }

  private void doCheckForUpdates(Consumer<Response> consumer) {
    log.d("doCheckForUpdates");
    try {
      SubscriberStubSettings subscriberStubSettings = SubscriberStubSettings.newBuilder()
          .setCredentialsProvider(
          credentialsProvider).
          build();
      SubscriberStub subscriber = GrpcSubscriberStub.create(subscriberStubSettings);
      String subscriptionName = ProjectSubscriptionName.format(projectId, subscriptionId);
      PullRequest pullRequest = PullRequest.newBuilder().setMaxMessages(1).setReturnImmediately
          (true).setSubscription(
          subscriptionName).build();
      PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);

      List<String> ackIds = new ArrayList<>();
      MessageData urlToShow = null;
      log.d("Have %d message(s)", pullResponse.getReceivedMessagesCount());
      for (ReceivedMessage message : pullResponse.getReceivedMessagesList()) {
        log.d("message:%s", message);
        urlToShow = createUrlToShow(message.getMessage());
        ackIds.add(message.getAckId());
      }
      log.d("ackIds:%s", ackIds);

      if (!ackIds.isEmpty()) {
        subscriber.acknowledgeCallable().call(AcknowledgeRequest.newBuilder().setSubscription(
            subscriptionName).addAllAckIds(ackIds).build());
        log.d("getReceivedMessagesList:%s", pullResponse.getReceivedMessagesList());
      }

      if (urlToShow != null && !urlToShow.isEmpty()) {
        new Handler(Looper.getMainLooper()).post(new ChangeBackgroundRunnable(consumer, urlToShow));
      }

      subscriber.shutdownNow();

    } catch (IOException e) {
      log.e("Trying to get messages", e);
    }
  }
}
