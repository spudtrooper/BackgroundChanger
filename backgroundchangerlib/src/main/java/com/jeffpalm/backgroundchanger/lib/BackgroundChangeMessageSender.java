package com.jeffpalm.backgroundchanger.lib;

import android.graphics.Bitmap;

public interface BackgroundChangeMessageSender {

  ChangedBackgroundResponse changeBackground(Bitmap bitmap);

  ChangedBackgroundResponse changeBackground(String url);

  ChangedBackgroundResponse beep();

  interface ChangedBackgroundResponse {
    String getId();
  }
}
