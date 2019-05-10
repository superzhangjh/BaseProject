package com.wyt.lib_decoupledfocus.focus.decoupledfocus.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.wyt.lib_decoupledfocus.focus.decoupledfocus.other.FocusParam;
import com.wyt.lib_decoupledfocus.focus.decoupledfocus.other.FocusTag;

/**
 * 绘制飞框的View
 */
public class FocusView extends View implements Animator.AnimatorListener {

    //加速器
    private LinearOutSlowInInterpolator outSlowInInterpolator;
    //是否正在动画中
    private boolean isAnimating;
    //宽度
    private int width;
    //高度
    private int height;
    //当前动画
    private Animator animator;

    public FocusView(Context context) {
        super(context);
        init();
    }

    public FocusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        outSlowInInterpolator = new LinearOutSlowInInterpolator();

        //设置锚点
        setPivotX(0.5f);
        setPivotY(0.5f);
    }

    /**
     * 核心方法:更新焦点区域的位置
     * @param view 需要依附的View
     * @param toLacation 要移动到的目标位置
     * @param isScroll 是否是滑动触发的更新，如果是则只进行位置移动
     */
    public void upDateLocation(FocusParam param, View view, int toLacation[] , FocusTag focusTag, boolean isScroll){
        if (view == null || isScroll && !param.isScrollFollowing || !param.visiable){
            //如果是滑动模式触发的，该View不支持滑动跟随移动则不触发
            return;
        }

        //如果正在动画，则取消动画
        if (isAnimating() && animator != null){
            animator.cancel();
        }

        //设置贴图资源
        setBackgroundResource(param.drawableResource);

        //获取自身view屏幕位置
        int[] fromLocation = new int[2];
        getLocationOnScreen(fromLocation);

        //动画集合
        Animator[] animators;

        //如果是忽略的View，则不进行缩放
        float zoom = focusTag.isScale()? param.zoom: 1.0f;

        //目标宽高
        int animWidth = view.getWidth() + param.leftPadding + param.rightPadding;
        int animHeight = view.getHeight() + param.topPadding + param.bottomPadding;
        if (isScroll){
            animators = new Animator[2];
        } else {
            animators = new Animator[7];

            //宽高动画
            final ObjectAnimator widthAnimator = ObjectAnimator.ofInt(this,
                    "scaleWidth", width == 0? getMeasuredWidth(): width, animWidth);
            final ObjectAnimator heightAnimator = ObjectAnimator.ofInt(this,
                    "scaleHeight", height == 0? getMeasuredHeight(): height, animHeight);

            //渐变动画
            final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this,
                    "alpha", getAlpha()
                    , param.visiable? (focusTag.isShow()? 1.0f: 0) : 0);

            //缩放动画
            final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(this,
                    "scaleX", getScaleX(), zoom);
            final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(this,
                    "scaleY", getScaleY(), zoom);

            animators[2] = scaleXAnimator;
            animators[3] = scaleYAnimator;
            animators[4] = widthAnimator;
            animators[5] = heightAnimator;
            animators[6] = alphaAnimator;
        }


        //位置移动动画
        int xOffset = (param.rightPadding - param.leftPadding)/2;
        int yOffset = (param.bottomPadding - param.topPadding)/2;
        //x坐标： 目标x - x间距 - 目标宽度的一半
        float animX = toLacation[0] - (param.leftPadding + param.rightPadding)/2 - animWidth/2 * (zoom - 1.0f) + xOffset;
        //x坐标： 目标y - y间距 - 目标高度的一半
        float animY = toLacation[1] - (param.topPadding + param.bottomPadding)/2 - animHeight/2 * (zoom - 1.0f) + yOffset;
        final ObjectAnimator translateXAnimator = ObjectAnimator.ofFloat(
                this, "translationX", fromLocation[0], animX);
        final ObjectAnimator translateYAnimator = ObjectAnimator.ofFloat(
                this, "translationY", fromLocation[1], animY);



        animators[0] = translateXAnimator;
        animators[1] = translateYAnimator;

        //进行播放
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);

        //设置加速器
        animatorSet.setInterpolator(outSlowInInterpolator);

        //设置时长
        animatorSet.setDuration(param.duration);

        //设置动画监听
        animatorSet.addListener(this);

        //执行动画
        animatorSet.start();
    }

    /**
     * 属性动画 - 更改宽度
     */
    private void setScaleWidth(int width){
        this.width = width;
        updateLayoutParams();
    }

    /**
     * 属性动画 - 更改高度
     */
    private void setScaleHeight(int height){
        this.height = height;
        updateLayoutParams();
    }

    /**
     * 更新view的layoutParams
     */
    private void updateLayoutParams(){
        setMeasuredDimension(width, height);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }

    /**
     * 停止动画
     */
    public void cancelAnimation(){
        if (animator != null){
            animator.cancel();
            Log.d("cancelAnimation", "true");
        }
    }

    /**
     * 是否正在动画
     */
    public boolean isAnimating() {
        return isAnimating;
    }

    /**
     * 获取动画时间
     */
    public long getAnimateTime(){
        return animator == null? 0: animator.getDuration();
    }

    @Override
    public void onAnimationStart(Animator animator) {
        onAnimating(animator);
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        onAnimated();
    }

    @Override
    public void onAnimationCancel(Animator animator) {
        onAnimated();
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
        onAnimating(animator);
    }

    private void onAnimating(Animator animator){
        this.animator = animator;
        isAnimating = true;
    }

    private void onAnimated(){
        this.animator = null;
        isAnimating = false;
    }
}
