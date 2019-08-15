package com.gade.zaraproductcheckerapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import android.util.Base64;
import android.view.ViewGroup;
import android.widget.Toast;

final public class UIUtil {

    public static void showShortToast(@NonNull Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showMessageSnackbar(@NonNull final CoordinatorLayout coordinatorLayout, final String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    public static Bitmap base64StringToBitmap(String base64String) {
        Bitmap bmp = null;
        if (base64String != null && !base64String.isEmpty()) {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }

        return bmp;
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        Bitmap output;
        Rect srcRect, dstRect;
        float r;
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        if (width > height) {
            output = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
            int left = (width - height) / 2;
            int right = left + height;
            srcRect = new Rect(left, 0, right, height);
            dstRect = new Rect(0, 0, height, height);
            r = height / 2;
        } else {
            output = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            int top = (height - width)/2;
            int bottom = top + width;
            srcRect = new Rect(0, top, width, bottom);
            dstRect = new Rect(0, 0, width, width);
            r = width / 2;
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        bitmap.recycle();

        return output;
    }

    public static void animateViewToZero(@NonNull final ViewGroup viewGroup) {
        viewGroup.animate()
                .setDuration(500)
                .translationY(0);
    }

    public static void animateViewSlideDown(@NonNull Context context, @NonNull final ViewGroup viewGroup, @DimenRes int id) {
        viewGroup.animate()
                .setDuration(500)
                .translationY(context.getResources().getDimensionPixelSize(id) - 1f);
    }
}
