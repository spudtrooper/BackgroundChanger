package com.jeffpalm.backgroundchanger;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.jeffpalm.backgroundchanger.util.Log;

import java.io.IOException;

final class WallpaperChanger {
  private final Log log = new Log(this);

  public boolean loadWallpaper(Context context, Bitmap bitmap) {
    if (bitmap == null) {
      log.d("bitmap is null");
      return false;
    }
    WallpaperManager wallpaperManager = WallpaperManager.getInstance(context
        .getApplicationContext());
    try {
      wallpaperManager.setBitmap(bitmap);
      return true;
    } catch (IOException e) {
      log.e("setting wallpaper", e);
    }
    return false;
  }
}
