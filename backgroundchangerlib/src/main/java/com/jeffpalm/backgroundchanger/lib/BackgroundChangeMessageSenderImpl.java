package com.jeffpalm.backgroundchanger.lib;

import android.graphics.Bitmap;

import com.jeffpalm.backgroundchanger.util.ImageUtil;
import com.jeffpalm.backgroundchanger.util.Log;

import java.util.HashMap;
import java.util.Map;

final class BackgroundChangeMessageSenderImpl implements BackgroundChangeMessageSender {
  private final static class ChangedBackgroundResponseImpl implements ChangedBackgroundResponse {
    private final String id;

    private ChangedBackgroundResponseImpl(String id) {this.id = id;}

    @Override
    public String getId() {
      return id;
    }
  }

  private final Log log = new Log(this);
  private final PubSubPublisher publisher;

  BackgroundChangeMessageSenderImpl(PubSubPublisher publisher) {this.publisher = publisher;}

  @Override
  public ChangedBackgroundResponse changeBackground(Bitmap bitmap) {
    Map<String, String> attrs = new HashMap<>();
    String imageBytes = ImageUtil.convert(ImageUtil.resize(bitmap));
    log.d("have imageBytes.length=%d", imageBytes.length());
    String id = publisher.sendMessage(imageBytes, attrs);
    return new ChangedBackgroundResponseImpl(id);
  }

  @Override
  public ChangedBackgroundResponse changeBackground(String url) {
    Map<String, String> attrs = new HashMap<>();
    attrs.put("url", url);
    String id = publisher.sendMessage("", attrs);
    return new ChangedBackgroundResponseImpl(id);
  }

  @Override
  public ChangedBackgroundResponse beep() {
    Map<String, String> attrs = new HashMap<>();
    attrs.put("beep", "true");
    String id = publisher.sendMessage("", attrs);
    return new ChangedBackgroundResponseImpl(id);
  }
}
