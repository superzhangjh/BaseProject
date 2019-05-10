package com.wyt.lib_focushandler.holder;

import android.view.View;

import com.wyt.lib_focushandler.border.FocusBorder;

/**
 * Created by 张坚鸿 on 2019/5/9 16:29
 */
public interface FocusCallback {

    /**
     * 当焦点事件变化时
     * @param oldFocus 旧焦点
     * @param newFocus 新焦点
     * @param focusBorder 边框
     * @param isFocusBelongCurrentFragment 焦点是否当前Fragment(Activity：false, Fragment：true/false)
     */
    void onFocusChange(View oldFocus, View newFocus, FocusBorder focusBorder,
                       boolean isFocusBelongCurrentFragment);
}
