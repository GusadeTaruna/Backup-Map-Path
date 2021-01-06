package com.example.tesanimasi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class Line extends View {

    public int height;
    private Path mPath;
    Context context;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;
    int color;

    public Line(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        mPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(20);
        mPaint.setAlpha(125);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }

    public void startTouch(float x, float y, int warna) {
        mPath.moveTo(x, y);
        mPaint.setColor(warna);
        mX = x;
        mY = y;
        color = warna;
    }

    public void moveTouch(float x, float y, int warna) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        System.out.println("tes :"+mX);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) /2 , (y + mY)/2);
            mPaint.setColor(warna);
            mX = x;
            mY = y;
            color = warna;
        }
    }

//    public void getColor(int signal){
//        color = signal;
//        System.out.println("warna : "+color);
//    }



}