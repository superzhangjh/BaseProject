package com.wyt.lib_focushandler.holder;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;

import com.wyt.lib_focushandler.animate.AnimatorType;
import com.wyt.lib_focushandler.animate.FocusAnimationLisenter;
import com.wyt.lib_focushandler.border.BorderParamsLister;
import com.wyt.lib_focushandler.border.FocusBorder;
import com.wyt.lib_focushandler.widget.FocusRecyclerView;
import com.wyt.lib_focushandler.widget.LightningView;

/**
 * Created by 张坚鸿 on 2019/4/30 16:24
 */
public class FocusBorderHolder implements FocusRecyclerView.ScrollInterruptListner {

    private ViewGroup parent;
    private FocusBorder focusBorder;
    private FocusAnimationLisenter animationLisenter;
    private FocusAnimationLisenter.Option animatorOption;
    private FocusCallbackHolder focusCallbackHolder;

    //动画类型
    private Animator borderAnimator;
    private Animator focusedAnimator;
    private Animator oldFocusAnimator;

    public FocusBorderHolder(ViewGroup contentView) {
        this.parent = contentView;
    }

    public void setAnimationListener(final FocusAnimationLisenter listener) {
        this.animationLisenter = listener;
    }

    /**
     * 绑定焦点框
     * @param focusBorder
     */
    public void bindFocusBolder(FocusBorder focusBorder) {
        this.focusBorder = focusBorder;
        View border = focusBorder.getBorder();
        //将焦点框设置成透明, 防止一开始框全屏很丑的bug
        border.setAlpha(0);
        //将焦点Viewt添加到parent中
        if (border.getParent() instanceof ViewGroup) {
            ((ViewGroup) border.getParent()).removeView(border);
        }
        adapterSomeView();
        parent.addView(border);
    }

    /**
     * 适配部分自定义Views(LightningView)
     */
    private void adapterSomeView() {
        focusBorder.setParamsLister(new BorderParamsLister() {
            @Override
            public void onAdapterLightningView(int left, int top, int right, int bottom) {
                if (focusBorder.getBorder() instanceof LightningView) {
                    LightningView view = (LightningView) focusBorder.getBorder();
                    view.setOffsetLeft(left);
                    view.setOffsetTop(top);
                    view.setOffsetRight(right);
                    view.setOffsetBottom(bottom);
                }
            }
        });
    }

    public void onFocusChange(View oldFocus, View newFocus) {
        if (focusCallbackHolder != null) {
            focusCallbackHolder.notifyFocusCallback(oldFocus, newFocus);
        }

        if (animationLisenter != null) {
            //设置当前的动画参数
            focusBorder.resetFocus(oldFocus, newFocus);
            animatorOption = animationLisenter.createFocusOption(focusBorder);

            if (newFocus != null) {
                //如果是FocusRecyclerView， 则设置回调
                if (newFocus.getParent() instanceof FocusRecyclerView) {
                    FocusRecyclerView focusRecyclerView = (FocusRecyclerView) newFocus.getParent();
                    focusRecyclerView.setScrollInterruptListner(this);
                }

                //焦点框
                borderAnimator = runFocusAnimation(AnimatorType.BORDER, borderAnimator);

                //新焦点
                focusedAnimator = runFocusAnimation(AnimatorType.NEW_FOCUS, focusedAnimator);
            }

            //旧焦点
            oldFocusAnimator = runFocusAnimation(AnimatorType.OLD_FOCUS, oldFocusAnimator);
        }
    }

    /**
     * 修复RecyclerView的焦点框
     * @param focused
     */
    @Override
    public void onScrollInterrupt(View focused) {
        borderAnimator = runFocusAnimation(AnimatorType.BORDER, borderAnimator);
    }

    private Animator runFocusAnimation(AnimatorType type, Animator animation) {
        animationLisenter.cancelAnimation(type, animation, animatorOption);
        animation = animationLisenter.onCreateAnimation(type, animatorOption);
        animationLisenter.startAnimation(type, animation, animatorOption);
        return animation;
    }

    public FocusCallbackHolder getFocusCallbackHolder() {
        if (focusCallbackHolder == null) {
            focusCallbackHolder = new FocusCallbackHolder(focusBorder);
        }
        return focusCallbackHolder;
    }
}
