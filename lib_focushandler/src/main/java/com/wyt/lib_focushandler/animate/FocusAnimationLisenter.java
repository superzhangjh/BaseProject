package com.wyt.lib_focushandler.animate;

import android.animation.Animator;
import android.graphics.Rect;
import android.view.View;

import com.wyt.lib_focushandler.border.FocusBorder;

/**
 * 焦点动画监听
 * Created by 张坚鸿 on 2019/4/30 18:04
 */
public interface FocusAnimationLisenter {

    /**
     * 在开始执行动画之前，进行一些计算操作
     */
    FocusAnimationLisenter.Option createFocusOption(FocusBorder focusBolder);

    /**
     * 创建动画
     */
    Animator onCreateAnimation(AnimatorType type, FocusAnimationLisenter.Option option);

    /**
     * 开始动画:启动
     */
    void startAnimation(AnimatorType type, Animator animation, FocusAnimationLisenter.Option option);

    /**
     * 取消动画
     */
    void cancelAnimation(AnimatorType type, Animator animation, FocusAnimationLisenter.Option option);


    abstract class Option {

        protected FocusBorder focusBolder;

        protected Option(FocusBorder focusBolder) {
            this.focusBolder = focusBolder;
        }

        /**
         * 起始位置
         */
        public abstract Rect getFromRect();

        /**
         * 终点位置
         */
        public abstract Rect getToRect();

        /**
         * 可见度
         */
        public float getAlpha() {
            if (focusBolder.getNewFocus().getTag() instanceof String) {
                String tag = (String) focusBolder.getNewFocus().getTag();
                if (tag.equals(focusBolder.getTagIgnore()) || tag.equals(focusBolder.getTagOnlyScale())) {
                    return 0;
                }
            }
            return 1;
        }

        /**
         * 缩放
         */
        public float getScale(View newFocus) {
            if (newFocus.getTag() instanceof String) {
                String tag = (String) newFocus.getTag();
                if (tag.equals(focusBolder.getTagIgnore()) || tag.equals(focusBolder.getTagOnlyVisible())) {
                    return 1;
                }
            }
            return focusBolder.getScale();
        }

        /**
         * 第一次调用
         */
        public boolean isFirstFocus() {
            return getOldFocus() == null;
        }

        /**
         * 焦点框动画时间
         */
        public long getBolderDuration() {
            return focusBolder.getBolderDuration();
        }

        /**
         * 新焦点动画时间
         */
        public long getFocusedDuration() {
            return focusBolder.getFocusedDuration();
        }

        /**
         * 旧焦点动画时间
         */
        public long getOldFocusDuration() {
            return focusBolder.getOldFocusDuration();
        }

        /**
         * 获取焦点框
         */
        public View getBolder() {
            return focusBolder.getBorder();
        }

        /**
         * 获取新焦点
         */
        public View getNewFocus() {
            return focusBolder.getNewFocus();
        }

        /**
         * 获取旧焦点
         */
        public View getOldFocus() {
            return focusBolder.getOldFocus();
        }

    }
}
