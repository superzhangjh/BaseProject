package com.wyt.lib_decoupledfocus.focus.decoupledfocus.other;

/**
 * Created by 张坚鸿 on 2019/1/11 16:20
 */
public enum  FocusTag {
    /**
     * 焦点框：有   缩放：是
     */
    FOCUS_SHOW_SCALE(true, true),
    /**
     * 焦点框：无   缩放：是
     */
    FOCUS_HIDE_SCALE(false, true),
    /**
     * 焦点框：显示   缩放：否
     */
    FOCUS_SHOW_NOSCALE(true, false),
    /**
     * 焦点框：无   缩放：否
     */
    FCOUS_HIDE_NOSCALE(false, false);

    private boolean isShow;

    private boolean isScale;

    FocusTag(boolean isShow, boolean isScale){
        this.isShow = isShow;
        this.isScale = isScale;
    }

    public boolean isScale() {
        return isScale;
    }

    public boolean isShow() {
        return isShow;
    }
}
