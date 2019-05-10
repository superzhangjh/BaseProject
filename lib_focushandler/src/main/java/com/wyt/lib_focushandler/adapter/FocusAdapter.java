package com.wyt.lib_focushandler.adapter;

import android.content.Context;

import com.wyt.lib_focushandler.animate.FocusAnimationLisenter;
import com.wyt.lib_focushandler.border.FocusBorder;

/**
 * 核心类：用于控制动画的类
 */
public interface FocusAdapter extends FocusAnimationLisenter {

    /**
     * 设置默认焦点框
     */
    FocusBorder getFocusBolder(Context context);

    FocusAnimationLisenter getAnimationLisenter();
}
