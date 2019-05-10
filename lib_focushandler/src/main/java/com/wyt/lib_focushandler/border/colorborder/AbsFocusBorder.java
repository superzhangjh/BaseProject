package com.wyt.lib_focushandler.border.colorborder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * Created by owen on 2017/7/20.
 */

public abstract class AbsFocusBorder extends FrameLayout {
    private static final long DEFAULT_ANIM_DURATION_TIME = 300;
    private static final long DEFAULT_SHIMMER_DURATION_TIME = 1000;
    private static final long DEFAULT_BREATHING_DURATION_TIME = 3000;

    protected Builder mBuilder;
    protected RectF mFrameRectF = new RectF();
    protected RectF mPaddingRectF = new RectF();
    protected RectF mTempRectF = new RectF();

    private LinearGradient mShimmerLinearGradient;
    private Matrix mShimmerGradientMatrix;
    private Paint mShimmerPaint;
    private float mShimmerTranslate = 0;
    // 闪光动画是否正在执行
    private boolean mShimmerAnimating = false;

    private boolean mIsVisible = false;

    protected AbsFocusBorder(Context context, Builder builder) {
        super(context);
        setWillNotDraw(false);

        mBuilder = builder;

        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        setVisibility(VISIBLE);

        //绘制闪光相关
        mShimmerPaint = new Paint();
        mShimmerGradientMatrix = new Matrix();

    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    /**
     * 绘制闪光
     *
     * @param canvas
     */
    protected void onDrawShimmer(Canvas canvas) {
        if (mShimmerAnimating) {
            canvas.save();
            mTempRectF.set(mFrameRectF);
            mTempRectF.intersect(mBuilder.mPaddingOffsetRectF);
            float shimmerTranslateX = mTempRectF.width() * mShimmerTranslate;
            float shimmerTranslateY = mTempRectF.height() * mShimmerTranslate;
            mShimmerGradientMatrix.setTranslate(shimmerTranslateX, shimmerTranslateY);
            mShimmerLinearGradient.setLocalMatrix(mShimmerGradientMatrix);
            canvas.drawRoundRect(mTempRectF, getRoundRadius(), getRoundRadius(), mShimmerPaint);
            canvas.restore();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            mFrameRectF.set(mPaddingRectF.left, mPaddingRectF.top, w - mPaddingRectF.right, h - mPaddingRectF.bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onDrawShimmer(canvas);
    }

    void setShimmerAnimating(boolean shimmerAnimating) {
        mShimmerAnimating = shimmerAnimating;
        if (mShimmerAnimating) {
            mTempRectF.set(mFrameRectF);
            mTempRectF.left += mBuilder.mPaddingOffsetRectF.left;
            mTempRectF.top += mBuilder.mPaddingOffsetRectF.top;
            mTempRectF.right -= mBuilder.mPaddingOffsetRectF.right;
            mTempRectF.bottom -= mBuilder.mPaddingOffsetRectF.bottom;
            mShimmerLinearGradient = new LinearGradient(
                    0, 0, mTempRectF.width(), mTempRectF.height(),
                    new int[]{0x00FFFFFF, 0x1AFFFFFF, mBuilder.mShimmerColor, 0x1AFFFFFF, 0x00FFFFFF},
                    new float[]{0f, 0.2f, 0.5f, 0.8f, 1f}, Shader.TileMode.CLAMP);
            mShimmerPaint.setShader(mShimmerLinearGradient);
        }
    }

    protected void setShimmerTranslate(float shimmerTranslate) {
        if (mBuilder.mRunShimmerAnim && mShimmerTranslate != shimmerTranslate) {
            mShimmerTranslate = shimmerTranslate;
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    protected float getShimmerTranslate() {
        return mShimmerTranslate;
    }

    protected void setWidth(int width) {
        if (getLayoutParams().width != width) {
            getLayoutParams().width = width;
            requestLayout();
        }
    }

    protected void setHeight(int height) {
        if (getLayoutParams().height != height) {
            getLayoutParams().height = height;
            requestLayout();
        }
    }

    public void setVisible(boolean visible) {
        setVisible(visible, true);
    }

    public void setVisible(boolean visible, boolean anim) {
        if (mIsVisible != visible) {
            mIsVisible = visible;
            if(anim) {
                animate().alpha(visible ? 1f : 0f).setDuration(mBuilder.mAnimDuration).start();
            } else {
                setAlpha(visible ? 1f : 0f);
            }
        }
    }

    public boolean isVisible() {
        return mIsVisible;
    }

    protected Rect findLocationWithView(View view) {
        return findOffsetDescendantRectToMyCoords(view);
    }

    protected Rect findOffsetDescendantRectToMyCoords(View descendant) {
        final ViewGroup root = (ViewGroup) getParent();
        final Rect rect = new Rect();
        if (descendant == root) {
            return rect;
        }

        final View srcDescendant = descendant;

        ViewParent theParent = descendant.getParent();
        // search and offset up to the parent
        while (theParent instanceof View && theParent != root) {

            rect.offset(descendant.getLeft() - descendant.getScrollX(),
                    descendant.getTop() - descendant.getScrollY());

            descendant = (View) theParent;
            theParent = descendant.getParent();
        }

        // now that we are up to this view, need to offset one more time
        // to get into our coordinate space
        if (theParent == root) {
            rect.offset(descendant.getLeft() - descendant.getScrollX(),
                    descendant.getTop() - descendant.getScrollY());
        }

        rect.right = rect.left + srcDescendant.getMeasuredWidth();
        rect.bottom = rect.top + srcDescendant.getMeasuredHeight();

        return rect;
    }

    @NonNull
    public abstract View getBorderView();

    abstract float getRoundRadius();

    public static class Options {
        protected float scaleX = 1f, scaleY = 1f;

        Options() {
        }

        private static class OptionsHolder {
            private static final Options INSTANCE = new Options();
        }

        public static Options get(float scaleX, float scaleY) {
            return get(scaleX, scaleY, null);
        }

        public static Options get(float scaleX, float scaleY, String title) {
            OptionsHolder.INSTANCE.scaleX = scaleX;
            OptionsHolder.INSTANCE.scaleY = scaleY;
            return OptionsHolder.INSTANCE;
        }
    }

    public enum Mode {
        TOGETHER,
        SEQUENTIALLY,
        NOLL
    }

    public static abstract class Builder {
        protected int mShimmerColor = 0x66FFFFFF;
        protected boolean mRunShimmerAnim = true;
        protected boolean mRunBreathingAnim = true;
        protected Mode mAnimMode = Mode.TOGETHER;
        protected long mAnimDuration = AbsFocusBorder.DEFAULT_ANIM_DURATION_TIME;
        protected long mShimmerDuration = AbsFocusBorder.DEFAULT_SHIMMER_DURATION_TIME;
        protected long mBreathingDuration = AbsFocusBorder.DEFAULT_BREATHING_DURATION_TIME;
        protected RectF mPaddingOffsetRectF = new RectF();

        public Builder breathingDuration(long duration) {
            this.mBreathingDuration = duration;
            return this;
        }

        public Builder noBreathing() {
            this.mRunBreathingAnim = false;
            return this;
        }

        public Builder shimmerColor(@ColorInt int color) {
            this.mShimmerColor = color;
            return this;
        }

        public Builder shimmerColorRes(@ColorRes int colorId, Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.shimmerColor(context.getColor(colorId));
            } else {
                this.shimmerColor(context.getResources().getColor(colorId));
            }
            return this;
        }

        public Builder shimmerDuration(long duration) {
            this.mShimmerDuration = duration;
            return this;
        }

        public Builder noShimmer() {
            this.mRunShimmerAnim = false;
            return this;
        }

        public Builder animDuration(long duration) {
            this.mAnimDuration = duration;
            return this;
        }

        public Builder animMode(Mode mode) {
            this.mAnimMode = mode;
            return this;
        }

        public Builder padding(float padding) {
            return padding(padding, padding, padding, padding);
        }

        public Builder padding(float left, float top, float right, float bottom) {
            this.mPaddingOffsetRectF.left = left;
            this.mPaddingOffsetRectF.top = top;
            this.mPaddingOffsetRectF.right = right;
            this.mPaddingOffsetRectF.bottom = bottom;
            return this;
        }

        public abstract ColorFocusBorder build(Context context);
    }
}
