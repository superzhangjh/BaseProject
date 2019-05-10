package com.wyt.lib_focushandler.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * 闪光View：https://www.jianshu.com/p/0dda922897cf
 * @author 修改：张坚鸿
 */
public class LightningView extends View {
    private Shader mGradient;
    private Matrix mGradientMatrix;
    private Paint mPaint;
    private int mViewWidth = 0, mViewHeight = 0;
    private float mTranslateX = 0, mTranslateY = 0;
    private boolean mAnimating = false;
    private RectF rectF;
    private ValueAnimator valueAnimator;

    /* 自定义属性 */
    private boolean autoRun; //是否自动运行动画
    private int offsetLeft;
    private int offsetRight;
    private int offsetTop;
    private int offsetBottom;
    private float radius;
    private int lightningColor;
    private float duration;

    private LightningView(Context context) {
        super(context);
    }

    private LightningView(Context context, Builder builder) {
        super(context);
        autoRun = builder.autoRun;
        offsetLeft = builder.offsetLeft;
        offsetRight = builder.offsetRight;
        offsetTop = builder.offsetTop;
        offsetBottom = builder.offsetBottom;
        radius = builder.radius;
        lightningColor = builder.lightningColor;
        duration = builder.duration;
        init();
    }


    private void init() {
        rectF = new RectF();
        mPaint = new Paint();
        initGradientAnimator();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            rectF.set(
                    offsetLeft,
                    offsetTop,
                    right - left - offsetRight,
                    bottom - top - offsetBottom
            );
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getWidth();
            mViewHeight = getHeight();
            if (mViewWidth > 0) {
                //亮光闪过
                mGradient = new LinearGradient(0, 0, mViewWidth / 2f, mViewHeight,
                        new int[]{0x00FFFFFF, 0x1AFFFFFF, lightningColor, 0x1AFFFFFF, 0x00FFFFFF},
                        new float[]{0, 0.25f*duration, 0.5f*duration, 0.75f*duration, 1*duration},
                        Shader.TileMode.CLAMP);
                mPaint.setShader(mGradient);
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
                mGradientMatrix = new Matrix();
                mGradientMatrix.setTranslate(-2 * mViewWidth, mViewHeight);
                mGradient.setLocalMatrix(mGradientMatrix);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAnimating && mGradientMatrix != null) {
            canvas.drawRoundRect(rectF, radius, radius, mPaint);
        }
    }

    private void initGradientAnimator() {
        valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(5000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (Float) animation.getAnimatedValue();
                //❶ 改变每次动画的平移x、y值，范围是[-2mViewWidth, 2mViewWidth]
                mTranslateX = 4 * mViewWidth * v - mViewWidth * 2;
                mTranslateY = mViewHeight * v;
                //❷ 平移matrix, 设置平移量
                if (mGradientMatrix != null) {
                    mGradientMatrix.setTranslate(mTranslateX, mTranslateY);
                }
                //❸ 设置线性变化的matrix
                if (mGradient != null) {
                    mGradient.setLocalMatrix(mGradientMatrix);
                }
                //❹ 重绘
                invalidate();
            }
        });
        if (autoRun) {
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    mAnimating = true;
                    if (valueAnimator != null) {
                        valueAnimator.start();
                    }
                }
            });
        }
    }

    //停止动画
    public void stopAnimation() {
        if (mAnimating && valueAnimator != null) {
            mAnimating = false;
            valueAnimator.cancel();
            invalidate();
        }
    }

    //开始动画
    public void startAnimation() {
        stopAnimation();
        if (!mAnimating && valueAnimator != null) {
            mAnimating = true;
            valueAnimator.start();
        }
    }

    public static class Builder {
        private boolean autoRun = true;
        private int offsetLeft;
        private int offsetRight;
        private int offsetTop;
        private int offsetBottom;
        private float radius;
        private int lightningColor = 0x66FFFFFF;
        private float duration = 1f;

        public LightningView build(Context context) {
            return new LightningView(context, this);
        }

        public Builder setAutoRun(boolean autoRun) {
            this.autoRun = autoRun;
            return this;
        }

        public Builder setoffsetTop(int offsetTop) {
            this.offsetTop = offsetTop;
            return this;
        }

        public Builder setoffsetRight(int offsetRight) {
            this.offsetRight = offsetRight;
            return this;
        }

        public Builder setoffsetLeft(int offsetLeft) {
            this.offsetLeft = offsetLeft;
            return this;
        }

        public Builder setoffsetBottom(int offsetBottom) {
            this.offsetBottom = offsetBottom;
            return this;
        }

        public Builder setRadius(float radius) {
            this.radius = radius;
            return this;
        }

        public Builder setLightningColor(int lightningColor) {
            this.lightningColor = lightningColor;
            return this;
        }

        public Builder setDuration(float duration) {
            this.duration = duration;
            return this;
        }
    }

    public void setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
    }

    public void setOffsetLeft(int offsetLeft) {
        this.offsetLeft = offsetLeft;
    }

    public void setOffsetRight(int offsetRight) {
        this.offsetRight = offsetRight;
    }

    public void setOffsetTop(int offsetTop) {
        this.offsetTop = offsetTop;
    }

    public void setOffsetBottom(int offsetBottom) {
        this.offsetBottom = offsetBottom;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setLightningColor(int lightningColor) {
        this.lightningColor = lightningColor;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public boolean isAutoRun() {
        return autoRun;
    }

    public int getOffsetLeft() {
        return offsetLeft;
    }

    public int getOffsetRight() {
        return offsetRight;
    }

    public int getOffsetTop() {
        return offsetTop;
    }

    public int getOffsetBottom() {
        return offsetBottom;
    }

    public float getRadius() {
        return radius;
    }

    public int getLightningColor() {
        return lightningColor;
    }

    public float getDuration() {
        return duration;
    }
}