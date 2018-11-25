package com.jeffpalm.backgroundchanger;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Looper;

final class SoundBeeper {
  private final static int DURATION = 2000;

  public void beep() {
    final ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    toneGen.startTone(ToneGenerator.TONE_DTMF_S, DURATION);
    new Handler(Looper.getMainLooper()).postDelayed(() -> toneGen.release(), DURATION + 50);
  }
}
