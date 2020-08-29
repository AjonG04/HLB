package com.example.leafpiction;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class RectShape extends View {
    private Paint paint = new Paint();

    public RectShape(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) { // Override the onDraw() Method
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(8);

        //center
        int x0 = canvas.getWidth();
        int y0 = canvas.getHeight() / 2;

        int dx = 150;
        int dy = 150;

        //draw guide box
        canvas.drawRect(dx, dy, x0 - dx, y0 - dy, paint);
    }

}