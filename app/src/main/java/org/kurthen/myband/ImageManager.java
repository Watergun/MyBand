package org.kurthen.myband;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by Leonhard on 05.10.2016.
 */
public class ImageManager {
    private static ImageManager ourInstance = new ImageManager();

    private Context applicationContext;

    public static ImageManager getInstance() {
        return ourInstance;
    }
    private ImageManager() {}

    public void setContext(Context newContext){
        applicationContext = newContext;
    }

    public int calcSampleSize(int reqWidth, int reqHeight, int width, int height){
        int factor = 1;
        while((width/factor) > reqWidth || (height/factor) > reqHeight){
            factor *= 2;
        }
        return factor;
    }


    public Drawable createResourceFromBase64(String base64Image, int reqWidth, int reqHeight){
        if(applicationContext == null){
            Log.d("ERROR", "No context given to ImageManager! Cannot create drawable");
            return null;
        }
        byte[] picture = Base64.decode(base64Image.getBytes(), Base64.DEFAULT);

        int pxWidth = dpToPx(reqWidth);
        int pxHeight = dpToPx(reqHeight);

        // Get picture bounds
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(picture, 0, picture.length, options);
        String imagetype = options.outMimeType;
        int width = options.outWidth;
        int height = options.outHeight;

        options.inJustDecodeBounds = false;
        options.inSampleSize = calcSampleSize(pxWidth, pxHeight, width, height);

        Bitmap bmp = BitmapFactory.decodeByteArray(picture, 0, picture.length, options);

        return RoundedBitmapDrawableFactory.create(Resources.getSystem(), bmp);
    }

    public String createBase64FromResource(Drawable image){

        return "";
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = applicationContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public Image createResourceFromBytes(byte[] fullPicture){

        return null;
    }


}
