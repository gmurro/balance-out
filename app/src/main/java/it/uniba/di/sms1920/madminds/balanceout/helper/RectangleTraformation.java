package it.uniba.di.sms1920.madminds.balanceout.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

public class RectangleTraformation implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int dim = Math.max(source.getWidth(), source.getHeight());
        Bitmap dstBmp = Bitmap.createBitmap(source.getWidth(), 250, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBmp);
        canvas.drawBitmap(source, (dim - source.getWidth()) / 2, (dim - source.getHeight()) / 2, null);


        return dstBmp;
    }

    @Override
    public String key() {
        return "rectangle";
    }
}
