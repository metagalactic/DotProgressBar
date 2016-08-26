package com.metagalactic.dotprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DotProgressBar extends View {

    private List<DotDrawable> mDots;

    @ColorInt
    private int mDotColor;
    private int mNoOfDots;
    private float mDotRadius;
    private float mDotSpacing;
    private float mGrowFactor;

    public DotProgressBar(Context context) {
        this(context, null);
    }

    public DotProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        Context context = getContext();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DotProgressBar);

        try {
            mNoOfDots = typedArray.getInt(R.styleable.DotProgressBar_dpb_noOfDots, 4);
            mDotColor = typedArray.getColor(R.styleable.DotProgressBar_dpb_dotColor, Color.GRAY);
            mDotRadius = typedArray.getDimension(R.styleable.DotProgressBar_dpb_dotRadius, 18);
            mGrowFactor = typedArray.getFloat(R.styleable.DotProgressBar_dpb_growFactor, 1.5f);
            mDotSpacing = typedArray.getDimension(R.styleable.DotProgressBar_dpb_dotSpacing, 8);
        } finally {
            typedArray.recycle();
        }

        mDots = new ArrayList<>(mNoOfDots);

        setupDots();
        adjustDotBounds();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float dotSize = mDotRadius * 2;
        float viewWidth = (dotSize * mNoOfDots) + (mDotSpacing * (mNoOfDots - 1));
        setMeasuredDimension((int) viewWidth, (int) (dotSize * mGrowFactor));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mDots.size(); i++) {
            mDots.get(i).draw(canvas);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        adjustDotBounds();
    }

    private void adjustDotBounds() {
        float dotSize = mDotRadius * 2;
        float maxDotSize = dotSize * mGrowFactor;
        float dotSizeWithSpace = dotSize + mDotSpacing;

        int left = (int) (mDotRadius * (1 - mGrowFactor));
        int right = left + (int) maxDotSize;
        int top = 0;
        int bottom = (int) maxDotSize;

        for (int i = 0; i < mDots.size(); i++) {
            DotDrawable dotDrawable = mDots.get(i);
            dotDrawable.setBounds(left, top, right, bottom);

            left += dotSizeWithSpace;
            right += dotSizeWithSpace;
        }
    }

    private void setupDots() {
        mDots.clear();

        for (int i = 0; i < mNoOfDots; i++) {
            DotDrawable dotDrawable = new DotDrawable(mDotColor, mDotRadius);
            dotDrawable.setCallback(this);
            mDots.add(dotDrawable);
        }
    }
}
