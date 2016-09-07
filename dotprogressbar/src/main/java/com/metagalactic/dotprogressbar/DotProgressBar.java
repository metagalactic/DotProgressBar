package com.metagalactic.dotprogressbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

public class DotProgressBar extends View {

    private static final int DEFAULT_ANIMATION_DURATION = 250;
    private static final float ANIMATION_DELAY_FACTOR = 0.3f;
    private List<Drawable> mDots;
    private AnimatorSet mAnimatorSet;

    @ColorInt
    private int mDotColor;
    private int mNoOfDots;
    private float mDotRadius;
    private float mDotSpacing;
    private float mGrowFactor;
    private int mAnimationDuration;

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
            mAnimationDuration = typedArray.getInt(R.styleable.DotProgressBar_dpb_animationDuration, DEFAULT_ANIMATION_DURATION);
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
        float viewWidth = (dotSize * mNoOfDots)
                + (mDotSpacing * (mNoOfDots - 1))
                + (mDotRadius * (mGrowFactor - 1) * 2);
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAnimatorSet.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnimatorSet.cancel();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return mDots.contains(who) || super.verifyDrawable(who);
    }

    private void adjustDotBounds() {
        float dotSize = mDotRadius * 2;
        float maxDotSize = dotSize * mGrowFactor;
        float dotSizeWithSpace = dotSize + mDotSpacing;

        int left = (int) (mDotRadius * (mGrowFactor - 1));
        int top = 0;
        int right = left + (int) dotSize;
        int bottom = (int) maxDotSize;

        for (int i = 0; i < mDots.size(); i++) {
            Drawable dotDrawable = mDots.get(i);
            dotDrawable.setBounds(left, top, right, bottom);

            left += dotSizeWithSpace;
            right += dotSizeWithSpace;
        }
    }

    private void setupDots() {
        mAnimatorSet = new AnimatorSet();
        mDots.clear();

        List<Animator> animators = new ArrayList<>(mNoOfDots);
        float growRadius = mDotRadius * mGrowFactor;

        for (int i = 0; i < mNoOfDots; i++) {
            DotDrawable dotDrawable = new DotDrawable(mDotColor, mDotRadius);
            dotDrawable.setCallback(this);
            mDots.add(dotDrawable);

            ValueAnimator growAnimator = ObjectAnimator.ofFloat(dotDrawable,
                    "radius", mDotRadius, growRadius, mDotRadius);
            growAnimator.setDuration(mAnimationDuration);
            growAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            growAnimator.setStartDelay((long) (i * mAnimationDuration * ANIMATION_DELAY_FACTOR));
            growAnimator.addUpdateListener(mAnimationListener);
            animators.add(growAnimator);
        }

        mAnimatorSet.playTogether(animators);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimatorSet.start();
            }
        });
    }

    private ValueAnimator.AnimatorUpdateListener mAnimationListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            invalidate();
        }
    };
}
