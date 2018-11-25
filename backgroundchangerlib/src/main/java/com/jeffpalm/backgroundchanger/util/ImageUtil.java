package com.jeffpalm.backgroundchanger.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * https://stackoverflow.com/questions/9224056/android-bitmap-to-base64-string
 */
public final class ImageUtil {
  private ImageUtil() {}

  public static Bitmap convert(String base64Str) throws IllegalArgumentException {
    byte[] decodedBytes = Base64.decode(base64Str, Base64.URL_SAFE);
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
  }

  public static String convert(Bitmap bitmap) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

    return Base64.encodeToString(outputStream.toByteArray(), Base64.URL_SAFE);
  }

  public static Bitmap resize(Bitmap bitmap) {
    int newHeight = 400;
    int newWidth = bitmap.getWidth() * newHeight / bitmap.getHeight();
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
  }
}
