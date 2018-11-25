package com.jeffpalm.backgroundchanger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jeffpalm.backgroundchanger.lib.BackgroundChangeMessageProvider;
import com.jeffpalm.backgroundchanger.util.Log;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
  private final Log log = new Log(this);

  private final SoundBeeper soundBeeper = new SoundBeeper();

  private UpdateChecker updateChecker;

  @Inject BackgroundChangeMessageProvider messageProvider;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ComponentHelper.newComponent().inject(this);
    updateChecker = new UpdateChecker(this, messageProvider);

    setContentView(R.layout.activity_main);
    setSupportActionBar(findViewById(R.id.toolbar));

    findViewById(R.id.updateButton).setOnClickListener(view -> updateWallpaper());
    findViewById(R.id.beepButton).setOnClickListener(view -> beep());

    startService(new Intent(this, UpdaterService.class));
  }

  private void beep() {
    log.d("beep");
    soundBeeper.beep();
  }

  private void updateWallpaper() {
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... voids) {
        updateChecker.checkForUpdates();
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        Toast.makeText(MainActivity.this, "Updated wallpaper", Toast.LENGTH_SHORT).show();
      }
    }.execute();
  }
}
