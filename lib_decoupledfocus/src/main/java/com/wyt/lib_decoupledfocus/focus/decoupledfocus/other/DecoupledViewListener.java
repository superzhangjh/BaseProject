package com.wyt.lib_decoupledfocus.focus.decoupledfocus.other;

import android.support.annotation.NonNull;
import android.view.View;

public interface DecoupledViewListener {

    /**
     * 更新位置的时候触发，可以单独对每个View做处理
     * @param defaultParam 默认的焦点设置参数
     * @param focusView 获得焦点的view
     * @param oldFocusView 失去焦点的view
     * @return 返回修改过的默认参数
     */
    @NonNull
    FocusParam onUpdateViewLocation(FocusParam defaultParam, View focusView, View oldFocusView);
}
