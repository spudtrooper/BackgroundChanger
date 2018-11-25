package com.jeffpalm.backgroundchanger;

import android.content.Context;

import com.jeffpalm.backgroundchanger.lib.BackgroundChangeMessageProvider;

final class UpdateChecker {
  private final Context context;
  private final WallpaperChanger wallpaperChanger = new WallpaperChanger();
  private final SoundBeeper soundBeeper = new SoundBeeper();
  private final BackgroundChangeMessageProvider messageProvider;

  public UpdateChecker(Context context, BackgroundChangeMessageProvider messageProvider) {
    this.context = context;
    this.messageProvider = messageProvider;
  }

  public void checkForUpdates() {
    messageProvider.checkForUpdates(resp -> {
      if (resp.getBitmap() != null) {
        wallpaperChanger.loadWallpaper(context, resp.getBitmap());
      }
      if (resp.getBeep() != null && resp.getBeep()) {
        soundBeeper.beep();
      }
    });
  }
}
