package com.example.mainmusicplayer.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {
    public static Bitmap getScaledBitmap(byte[] path, int destWidth, int destHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(path, 0, path.length, options);
//        BitmapFactory.decodeFile(path, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = Math.min(srcWidth / destWidth, srcHeight / destHeight);

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeByteArray(path ,0 , path.length, options);
    }

    public static Bitmap getScaledBitmap(byte[] path, Activity activity) {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return getScaledBitmap(path, point.x/24, point.y/24);
    }

}
