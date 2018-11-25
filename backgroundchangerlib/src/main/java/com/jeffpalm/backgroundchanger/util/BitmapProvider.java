package com.jeffpalm.backgroundchanger.util;

import android.graphics.Bitmap;

public interface BitmapProvider {
  void provide(Consumer<Bitmap> consumer);
}
