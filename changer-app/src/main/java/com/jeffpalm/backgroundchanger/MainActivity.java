package com.jeffpalm.backgroundchanger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.frosquivel.magicaltakephoto.MagicalTakePhoto;
import com.jeffpalm.backgroundchanger.lib.BackgroundChangeMessageSender;
import com.jeffpalm.backgroundchanger.lib.BackgroundChangeModule;
import com.jeffpalm.backgroundchanger.lib.BuildConfigModule;
import com.jeffpalm.backgroundchanger.util.Log;

import java.io.IOException;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
  private final Log log = Log.of(this);
  private final ShowImagePickerDialog showImagePickerDialog = new ShowImagePickerDialog(this, 1);

  MagicalTakePhoto magicalTakePhoto;

  @Inject BackgroundChangeMessageSender messageSender;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    DaggerBackgroundChangerComponent.builder().backgroundChangeModule(new BackgroundChangeModule
        ()).buildConfigModule(
        new BuildConfigModule()).build().inject(this);

    setContentView(R.layout.activity_main);
    setSupportActionBar(findViewById(R.id.toolbar));

    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().build());

    findViewById(R.id.takeScreenshotButton).setOnClickListener(view -> takeScreenshot());
    findViewById(R.id.uploadImageButton).setOnClickListener(view -> uploadImage());
    findViewById(R.id.beepButton).setOnClickListener(view -> beep());

    TextView urlText = findViewById(R.id.urlText);
    findViewById(R.id.uploadUrlButton).setOnClickListener(view -> uploadUrl(urlText.getText()));
  }

  private void beep() {
    log.d("beep");
    new AsyncTask<Void, Void, String>() {
      @Override
      protected String doInBackground(Void... voids) {
        return messageSender.beep().getId();
      }

      @Override
      protected void onPostExecute(String messageId) {
        Toast.makeText(MainActivity.this,
            "Published beep id=" + messageId,
            Toast.LENGTH_SHORT).show();
      }
    }.execute();
  }

  private void uploadUrl(CharSequence url) {
    new AsyncTask<String, Void, String>() {
      @Override
      protected String doInBackground(String... urls) {
        return doUploadUrl(urls[0]);
      }

      @Override
      protected void onPostExecute(String messageId) {
        Toast.makeText(MainActivity.this,
            "Published url: " + url + " id=" + messageId,
            Toast.LENGTH_SHORT).show();
      }
    }.execute(url.toString());
  }

  private String doUploadUrl(String url) {
    log.d("Trying to publish url: %s", url);
    return messageSender.changeBackground(url).getId();
  }

  private void shareBitmapFromUri(Uri imageUri) {
    log.d("Trying to share from %s", imageUri);
    try {
      Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
      publishBitmap(bitmap);
    } catch (IOException e) {
      log.d("Creating a bitmap from %s", imageUri);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode != 1 || resultCode != Activity.RESULT_OK || data == null || data.getData() ==
        null) {
      return;
    }
    try {
      Uri filePath = data.getData();
      Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
      showImagePickerDialog.getConsumer().accept(bitmap);
    } catch (IOException e) {
      log.e(e, "converting image");
    }
  }

  private String doPublishBitmap(Bitmap bitmap) {
    return messageSender.changeBackground(bitmap).getId();
  }

  private void publishBitmap(Bitmap bitmap) {
    log.d("bitmap: %s", bitmap);
    new AsyncTask<Bitmap, Void, String>() {
      @Override
      protected String doInBackground(Bitmap... bitmaps) {
        return doPublishBitmap(bitmap);
      }

      @Override
      protected void onPostExecute(String messageId) {
        Toast.makeText(MainActivity.this,
            String.format("Published: %s", messageId),
            Toast.LENGTH_SHORT).show();
      }
    }.execute(bitmap);
  }

  private void uploadImage() {
    log.d("uploadImage");
    showImagePickerDialog.provide(bitmap -> publishBitmap(bitmap));
  }

  private void takeScreenshot() {
    magicalTakePhoto.takePhoto("my_photo_name");
  }
}
