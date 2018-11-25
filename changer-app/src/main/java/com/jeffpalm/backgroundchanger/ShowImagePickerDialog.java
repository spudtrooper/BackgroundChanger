package com.jeffpalm.backgroundchanger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.jeffpalm.backgroundchanger.util.BitmapProvider;
import com.jeffpalm.backgroundchanger.util.Consumer;

final class ShowImagePickerDialog implements BitmapProvider {

  private final Activity context;
  private final int pickImageRequestId;
  private Consumer<Bitmap> consumer;

  ShowImagePickerDialog(Activity context, int pickImageRequestId) {
    this.context = context;
    this.pickImageRequestId = pickImageRequestId;
  }

  @Override
  public void provide(final Consumer<Bitmap> consumer) {
    this.consumer = consumer;
    Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    pickImageIntent.setType("image/*");
    pickImageIntent.putExtra("aspectX", 1);
    pickImageIntent.putExtra("aspectY", 1);
    pickImageIntent.putExtra("scale", true);
    pickImageIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
    context.startActivityForResult(pickImageIntent, pickImageRequestId);
  }

  public Consumer<Bitmap> getConsumer() {
    return consumer;
  }
}
