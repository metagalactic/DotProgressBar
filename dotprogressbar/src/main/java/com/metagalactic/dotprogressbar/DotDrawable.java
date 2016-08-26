package com.metagalactic.dotprogressbar;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;

public class DotDrawable extends Drawable {

    private Paint mPaint;
    private float mRadius;

    public DotDrawable(@ColorInt final int color, float radius) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(color);
        mRadius = radius;
    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), mRadius, mPaint);
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setRadius(float radius) {
        mRadius = radius;
        invalidateSelf();
    }
}
