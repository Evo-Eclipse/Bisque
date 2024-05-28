package com.example.bisque.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class BlurUtil {

    public static Bitmap blur(Context context, Bitmap image, float radius) {
        Bitmap outputBitmap = Bitmap.createBitmap(image);
        RenderScript renderScript = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(renderScript, image);
        Allocation output = Allocation.createFromBitmap(renderScript, outputBitmap);
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setRadius(radius);
        scriptIntrinsicBlur.setInput(input);
        scriptIntrinsicBlur.forEach(output);
        output.copyTo(outputBitmap);
        return outputBitmap;
    }

    public static Bitmap applyOverlay(Context context, Bitmap bitmap, int color, int alpha) {
        Bitmap overlayBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(overlayBitmap);
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAlpha(alpha);
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        return overlayBitmap;
    }
}

