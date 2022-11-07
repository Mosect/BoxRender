package com.mosect.app.boxrender;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ScaleLayout extends FrameLayout {

    private float mScale = 1f;

    public ScaleLayout(Context context) {
        super(context);
    }

    public ScaleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected FrameLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        super.measureChild(child, parentWidthMeasureSpec, parentHeightMeasureSpec);
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int paddingWidth = widthUsed + lp.leftMargin + lp.rightMargin + getPaddingLeft() + getPaddingRight();
        int paddingHeight = heightUsed + lp.topMargin + lp.bottomMargin + getPaddingTop() + getPaddingBottom();
        int width = getSafeSize(lp.width, parentWidthMeasureSpec, paddingWidth);
        int height = getSafeSize(lp.height, parentHeightMeasureSpec, paddingHeight);
        int maxWidth = getMaxSize(parentWidthMeasureSpec);
        int maxHeight = getMaxSize(parentHeightMeasureSpec);
        int childWidth, childHeight;
        if (width < 0 && height < 0) {
            if (maxWidth > 0 && maxHeight > 0) {
                float parentScale = maxWidth / (float) maxHeight;
                if (parentScale > mScale) {
                    childHeight = maxHeight;
                    childWidth = (int) (childHeight * mScale);
                } else {
                    childWidth = maxWidth;
                    childHeight = (int) (childWidth / mScale);
                }
            } else if (maxWidth > 0) {
                childWidth = maxWidth;
                childHeight = (int) (childWidth / mScale);
            } else if (maxHeight > 0) {
                childHeight = maxHeight;
                childWidth = (int) (childHeight * mScale);
            } else {
                childWidth = 0;
                childHeight = 0;
            }
        } else if (width < 0) {
            childHeight = height;
            childWidth = (int) (childHeight * mScale);
        } else if (height < 0) {
            childWidth = width;
            childHeight = (int) (childWidth / mScale);
        } else {
            childWidth = width;
            childHeight = height;
        }
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    private int getMaxSize(int measureSpec) {
        int msMode = MeasureSpec.getMode(measureSpec);
        if (msMode == MeasureSpec.UNSPECIFIED) return 0;
        return MeasureSpec.getSize(measureSpec);
    }

    private int getSafeSize(int size, int measureSpec, int padding) {
        int safePadding = Math.max(0, padding);
        int msSize = MeasureSpec.getSize(measureSpec);
        int msMode = MeasureSpec.getMode(measureSpec);
        if (size == LayoutParams.WRAP_CONTENT) {
            return -1;
        } else if (size == LayoutParams.MATCH_PARENT) {
            if (msMode == MeasureSpec.UNSPECIFIED) {
                return -1;
            }
            return Math.max(0, msSize - safePadding);
        } else {
            return msSize;
        }
    }

    public void setScale(float scale) {
        if (scale <= 0) {
            throw new IllegalArgumentException("Invalid scale: " + scale);
        }
        this.mScale = scale;
        requestLayout();
    }

    public float getScale() {
        return mScale;
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(FrameLayout.LayoutParams source) {
            super(source);
        }
    }
}
