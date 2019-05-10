package com.wyt.lib_focushandler.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wyt.lib_focushandler.R;
import com.wyt.lib_focushandler.animate.AnimateHelper;
import com.wyt.lib_focushandler.animate.AnimatorType;
import com.wyt.lib_focushandler.animate.FocusAnimationLisenter;
import com.wyt.lib_focushandler.animate.size.SizeEvaluator;
import com.wyt.lib_focushandler.animate.size.SizeProperty;
import com.wyt.lib_focushandler.border.FocusBorder;
import com.wyt.lib_focushandler.widget.FocusHorizontalScrollView;

/**
 * 基类焦点适配：如果有其他需求，可重写FocusAdapter自行实现动画效果
 * Created by 张坚鸿 on 2019/4/30 16:37
 */
public abstract class BaseFocusAdapter implements FocusAdapter {

    public static final int TAG_OFFSET = R.id.focushandler_tag_offset;

    private final SizeProperty sizeProperty;
    private final SizeEvaluator sizeEvaluator;
    private final LinearInterpolator interpolator;

    public BaseFocusAdapter() {
        sizeProperty = new SizeProperty();
        sizeEvaluator = new SizeEvaluator();
        interpolator = new LinearInterpolator();
    }

    @Override
    public FocusAnimationLisenter getAnimationLisenter() {
        return this;
    }

    @Override
    public FocusAnimationLisenter.Option createFocusOption(FocusBorder focusBolder) {
        return new Option(focusBolder);
    }

    @Override
    public Animator onCreateAnimation(AnimatorType type, FocusAnimationLisenter.Option option) {
        switch (type) {
            case BORDER:
                return createBolderAnimator(option);
            case NEW_FOCUS:
                return createFocusedAnimator(option);
            case OLD_FOCUS:
                return createOldFocusAnimator(option);
            default:
                return null;
        }
    }

    /**
     * 创建焦点框动画
     */
    private Animator createBolderAnimator(FocusAnimationLisenter.Option option) {
        //位移动画
        Rect toRect = option.getToRect();
        ObjectAnimator translationX = ObjectAnimator.ofFloat(option.getBolder(),
                "translationX", toRect.left);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(option.getBolder(),
                "translationY", toRect.top);

        //缩放动画
        float scale = option.getScale(option.getNewFocus());
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(option.getBolder(), "scaleX", scale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(option.getBolder(), "scaleY", scale);

        //大小动画
        int[] fromSize = {option.getFromRect().width(), option.getFromRect().height()};
        int[] toSize = {option.getToRect().width(), option.getToRect().height()};
        ObjectAnimator layout = ObjectAnimator.ofObject(option.getBolder(),
                sizeProperty, sizeEvaluator, fromSize, toSize);

        //透明度动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(option.getBolder(), "alpha", option.getAlpha());

        //组合动画
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationX, translationY, scaleX, scaleY, layout, alpha);
        animatorSet.setDuration(option.isFirstFocus() ? 0 : option.getBolderDuration());
        animatorSet.setInterpolator(interpolator);
        return animatorSet;
    }

    /**
     * 创建新焦点动画
     */
    private Animator createFocusedAnimator(FocusAnimationLisenter.Option option) {
        if (option.getNewFocus() != null) {
            //将焦点View置顶
            option.getNewFocus().bringToFront();

            //缩放动画
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(option.getNewFocus(), "scaleX",
                    option.getScale(option.getNewFocus()));
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(option.getNewFocus(), "scaleY",
                    option.getScale(option.getNewFocus()));

            //组合动画
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.setDuration(option.getFocusedDuration());
            animatorSet.setInterpolator(interpolator);
            return animatorSet;
        }
        return null;
    }

    /**
     * 创建旧焦点动画
     */
    private Animator createOldFocusAnimator(FocusAnimationLisenter.Option option) {
        if (option.getOldFocus() != null) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(option.getOldFocus(), "scaleX", 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(option.getOldFocus(), "scaleY", 1);
            AnimatorSet animator = new AnimatorSet();
            animator.playTogether(scaleX, scaleY);
            animator.setDuration(option.getOldFocusDuration());
            animator.setInterpolator(interpolator);
            return animator;
        }
        return null;
    }

    @Override
    public void startAnimation(AnimatorType type, Animator animation, FocusAnimationLisenter.Option option) {
        if (animation != null) {
            animation.start();
        }
    }

    @Override
    public void cancelAnimation(AnimatorType type, Animator animation, FocusAnimationLisenter.Option option) {
        if (type != AnimatorType.OLD_FOCUS && animation != null) {
            animation.cancel();
        }
    }

    /**
     * 运动参数
     */
    private class Option extends FocusAnimationLisenter.Option {

        private View newFocus;

        protected Option(FocusBorder focusBolder) {
            super(focusBolder);
            newFocus = focusBolder.getNewFocus();
        }

        @Override
        public Rect getFromRect() {
            Rect rect = new Rect();
            rect.set(
                    newFocus.getLeft(),
                    newFocus.getTop(),
                    newFocus.getLeft() + focusBolder.getBorder().getWidth(),
                    newFocus.getTop() + focusBolder.getBorder().getHeight()
            );
            return rect;
        }

        @Override
        public Rect getToRect() {
            if (newFocus != null) {
                Rect rect = new Rect();

                //目标位置
//                int[] location = AnimateHelper.getAnimLocation(newFocus);
//                location[0] -= (focusBolder.getPaddingLeft() + focusBolder.getPaddingRight())/2;
//                location[1] -= (focusBolder.getPaddingTop() + focusBolder.getPaddingBottom())/2;
//
//                //计算偏差
//                cacurlateOffset((View) newFocus.getParent(), location);
//
//                //目标大小
//                int width = newFocus.getWidth() + focusBolder.getPaddingLeft() + focusBolder.getPaddingRight();
//                int height = newFocus.getHeight() + focusBolder.getPaddingTop() + focusBolder.getPaddingBottom();
//
//                //设置边框
//                rect.set(location[0], location[1], location[0] + width, location[1] + height);

                //目标位置
                int[] location = AnimateHelper.getAnimLocation(newFocus);
                //计算偏差
                cacurlateOffset((View) newFocus.getParent(), location);
                //加入padding
                location[0] -= focusBolder.getPaddingLeft();
                location[1] -= focusBolder.getPaddingTop();
                //设置范围
                rect.set(
                        location[0],
                        location[1],
                        location[0] + newFocus.getWidth() + focusBolder.getPaddingRight() + (focusBolder.getPaddingLeft() + focusBolder.getPaddingRight())/2,
                        location[1] + newFocus.getHeight() + focusBolder.getPaddingBottom() + (focusBolder.getPaddingTop() + focusBolder.getPaddingBottom())/2
                );
                return rect;
            }
            return null;
        }
    }

    /**
     * 计算FocusWidget控件的位移偏差:
     * 使用了offset量的控件： FocusRecyclerView、FocusHorizontalScrollView
     * @param location 新焦点的位置
     * @param parent 新焦点的parent
     */
    private void cacurlateOffset(View parent, int[] location) {
        if (parent != null) {
            Object tag = parent.getTag(TAG_OFFSET);
            if (tag instanceof Point) {
                Point offset = (Point) tag;
                location[0] -= offset.x;
                location[1] -= offset.y;
            } else if (parent.getParent() instanceof FocusHorizontalScrollView){
                cacurlateOffset((View) parent.getParent(), location);
            }
        }
    }
}
