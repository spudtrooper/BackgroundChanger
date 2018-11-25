package com.jeffpalm.backgroundchanger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;

import com.jeffpalm.backgroundchanger.lib.BackgroundChangeMessageProvider;
import com.jeffpalm.backgroundchanger.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class UpdaterService extends Service {

  private Timer mTimer;
  @Inject BackgroundChangeMessageProvider messageProvider;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onCreate() {
    if (mTimer != null) {
      mTimer.cancel();
    } else {
      mTimer = new Timer();
    }
    ComponentHelper.newComponent().inject(this);

    mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(this),
        0,
        BuildConfig.BackgroundChangerUpdatePeriodSecs * 1000);
  }

  private final Handler mHandler = new Handler();

  private final class TimeDisplayTimerTask extends TimerTask {
    private final Log log = new Log(this);
    private final UpdateChecker updateChecker;

    private TimeDisplayTimerTask(Context context) {
      this.updateChecker = new UpdateChecker(context, messageProvider);
    }

    @Override
    public void run() {
      log.d("Running");
      final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... voids) {
          updateChecker.checkForUpdates();
          return null;
        }
      };
      mHandler.post(() -> asyncTask.execute());

    }
  }

}
