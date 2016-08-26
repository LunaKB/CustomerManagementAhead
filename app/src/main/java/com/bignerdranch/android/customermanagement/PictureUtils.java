package com.bignerdranch.android.customermanagement;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;

/**
 * Created by Chaz-Rae on 8/24/2016.
 */
public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return rotateBitmap(getScaledBitmap(path, size.x, size.y));
    }

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight){
        // Read in dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1;
        if(srcHeight > destHeight || srcWidth > destWidth){
            if(srcWidth > srcHeight){
                inSampleSize = Math.round(srcHeight/destHeight);
            }
            else{
                inSampleSize = Math.round(srcWidth/destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    private static Bitmap rotateBitmap(Bitmap source){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        return Bitmap.createBitmap(
                source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
