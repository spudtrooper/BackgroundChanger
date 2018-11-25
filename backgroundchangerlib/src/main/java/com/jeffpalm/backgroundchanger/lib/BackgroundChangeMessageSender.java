package com.jeffpalm.backgroundchanger.lib;

import android.graphics.Bitmap;

public interface BackgroundChangeMessageSender {
  interface ChangedBackgroundResponse {
    String getId();
  }

  ChangedBackgroundResponse changeBackground(Bitmap bitmap);

  ChangedBackgroundResponse changeBackground(String url);

  ChangedBackgroundResponse beep();
}
