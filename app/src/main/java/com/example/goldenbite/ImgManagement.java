package com.example.goldenbite;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;

import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Response;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImgManagement {
    // Transform image into text & transform from text to an image.

    public static String ImgBBUrl(String json) {
        String result = null;
        try {
            JSONObject root = new JSONObject(json);
            JSONObject data = root.optJSONObject("data");
            if (data != null && data.has("url")) {
                result = data.optString("url", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Bitmap rotateImage(Context context, Bitmap imag, Uri selectedImage) {
        Bitmap output = imag;
        InputStream inputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(selectedImage);
            ExifInterface eii = (android.os.Build.VERSION.SDK_INT > 23)
                    ? new ExifInterface(inputStream)
                    : new ExifInterface(selectedImage.getPath());

            int orientation = eii.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
            );

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                output = rBitmap(imag, 90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                output = rBitmap(imag, 180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                output = rBitmap(imag, 270);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception ignored) {}
        }

        return output;
    }

    private static Bitmap rBitmap(Bitmap bitmap, float degrees) {
        Bitmap rotatedBitmap;
        Matrix matrix = new Matrix();

        matrix.setRotate(degrees,
                bitmap.getWidth() / 2f,
                bitmap.getHeight() / 2f);

        rotatedBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
        );

        return rotatedBitmap;
    }

    public static Bitmap DtoB(Context context, int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableRes);
        if (drawable == null) return null;

        int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 1;
        int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        return bitmap;
    }










}
