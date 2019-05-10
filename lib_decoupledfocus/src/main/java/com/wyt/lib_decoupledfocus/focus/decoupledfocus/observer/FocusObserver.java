package com.wyt.lib_decoupledfocus.focus.decoupledfocus.observer;

import android.view.View;

import com.wyt.lib_decoupledfocus.focus.decoupledfocus.other.FocusParam;

/**
 * Created by 张坚鸿 on 2019/2/21 13:54
 */
public interface FocusObserver {

    /**
     * 当焦点发生变化时
     * @param param
     * @param focusView 新的焦点
     * @param oldView 旧的焦点
     */
    void onFocusChange(FocusParam param, View focusView, View oldView);
}
