package com.wyt.lib_focushandler.border;
import android.view.View;

import java.lang.ref.WeakReference;


/**
 * 焦点框
 * Created by 张坚鸿 on 2019/4/30 15:51
 */
public class FocusBorder {

    private WeakReference<View> mBolder;
    private String tagOnlyScale;
    private String tagOnlyVisible;
    private String tagIgnore;
    private float scale;
    private long bolderDuration;
    private long focusedDuration;
    private long oldFocusDuration;
    private int paddingLeft;
    private int paddingRight;
    private int paddingTop;
    private int paddingBottom;

    //不参与Builder的参数
    private WeakReference<View> newFocus;
    private WeakReference<View> oldFocus;
    private BorderParamsLister paramsLister;

    private FocusBorder(Builder builder) {
        setBorder(builder.bolder);
        setTagOnlyScale(builder.tagOnlyScale);
        setTagOnlyVisible(builder.tagOnlyVisible);
        setTagIgnore(builder.tagIgnore);
        setScale(builder.scale);
        setBolderDuration(builder.bolderDuration);
        setFocusedDuration(builder.focusedDuration);
        setOldFocusDuration(builder.oldFocusDuration);
        setPaddingLeft(builder.paddingLeft);
        setPaddingRight(builder.paddingRight);
        setPaddingTop(builder.paddingTop);
        setPaddingBottom(builder.paddingBottom);
    }

    public View getNewFocus() {
        return newFocus.get();
    }

    public View getOldFocus() {
        return oldFocus.get();
    }

    public void setParamsLister(BorderParamsLister paramsLister) {
        this.paramsLister = paramsLister;
        onPaddingChange(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private void onPaddingChange(int left, int top, int right, int bottom) {
        if (paramsLister != null) {
            paramsLister.onAdapterLightningView(left, top, right, bottom);
        }
    }

    /**
     * 重新赋值焦点
     * @param oldFocus
     * @param newFocus
     */
    public void resetFocus(View oldFocus, View newFocus) {
        this.oldFocus = new WeakReference<>(oldFocus);
        this.newFocus = new WeakReference<>(newFocus);
    }

    public void setBorder(View view) {
        mBolder = new WeakReference<>(view);
    }

    public View getBorder() {
        return mBolder.get();
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        onPaddingChange(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
        onPaddingChange(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        onPaddingChange(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        onPaddingChange(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setBolderDuration(long bolderDuration) {
        this.bolderDuration = bolderDuration;
    }

    public long getBolderDuration() {
        return bolderDuration;
    }

    public void setFocusedDuration(long focusedDuration) {
        this.focusedDuration = focusedDuration;
    }

    public long getFocusedDuration() {
        return focusedDuration;
    }

    public void setOldFocusDuration(long oldFocusDuration) {
        this.oldFocusDuration = oldFocusDuration;
    }

    public long getOldFocusDuration() {
        return oldFocusDuration;
    }

    public void setTagOnlyScale(String tag) {
        this.tagOnlyScale = tag;
    }

    public String getTagOnlyScale() {
        return tagOnlyScale;
    }

    public void setTagOnlyVisible(String tag) {
        this.tagOnlyVisible = tag;
    }

    public String getTagOnlyVisible() {
        return tagOnlyVisible;
    }

    public void setTagIgnore(String tag) {
        this.tagIgnore = tag;
    }

    public String getTagIgnore() {
        return tagIgnore;
    }

    public static class Builder {

        private View bolder;
        private String tagOnlyScale;
        private String tagOnlyVisible;
        private String tagIgnore;
        private float scale;
        private long bolderDuration;
        private long focusedDuration;
        private long oldFocusDuration;
        private int paddingLeft;
        private int paddingRight;
        private int paddingTop;
        private int paddingBottom;

        public FocusBorder build(){
            return new FocusBorder(this);
        }

        /**
         * 设置焦点框的view
         * @param bolderView
         */
        public Builder setBorder(View bolderView) {
            this.bolder = bolderView;
            return this;
        }

        public Builder setTagIgnore(String tagIgnore) {
            this.tagIgnore = tagIgnore;
            return this;
        }

        public Builder setTagOnlyScale(String tagOnlyScale) {
            this.tagOnlyScale = tagOnlyScale;
            return this;
        }

        public Builder setTagOnlyVisible(String tagOnlyVisible) {
            this.tagOnlyVisible = tagOnlyVisible;
            return this;
        }

        public Builder setScale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder setPaddingBottom(int paddingBottom) {
            this.paddingBottom = paddingBottom;
            return this;
        }

        public Builder setPaddingLeft(int paddingLeft) {
            this.paddingLeft = paddingLeft;
            return this;
        }

        public Builder setPaddingRight(int paddingRight) {
            this.paddingRight = paddingRight;
            return this;
        }

        public Builder setPaddingTop(int paddingTop) {
            this.paddingTop = paddingTop;
            return this;
        }

        public Builder setBolderDuration(long bolderDuration) {
            this.bolderDuration = bolderDuration;
            return this;
        }

        public Builder setFocusedDuration(long focusedDuration) {
            this.focusedDuration = focusedDuration;
            return this;
        }

        public Builder setOldFocusDuration(long oldFocusDuration) {
            this.oldFocusDuration = oldFocusDuration;
            return this;
        }
    }
}
