package com.jeffpalm.backgroundchanger.lib;

import android.graphics.Bitmap;

import com.jeffpalm.backgroundchanger.util.Consumer;

public interface BackgroundChangeMessageProvider {
  void checkForUpdates(Consumer<Response> consumer);

  interface Response {
    Bitmap getBitmap();

    Boolean getBeep();
  }
}
