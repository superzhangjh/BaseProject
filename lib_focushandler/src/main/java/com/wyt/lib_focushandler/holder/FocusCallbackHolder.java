package com.wyt.lib_focushandler.holder;

import android.view.View;
import android.view.ViewGroup;

import com.wyt.lib_focushandler.border.FocusBorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 张坚鸿 on 2019/5/9 18:33
 */
public class FocusCallbackHolder {

    private FocusBorder focusBorder;
    private List<View> fragmentViews;
    private Map<View, FocusCallback> focusCallbacks;

    public FocusCallbackHolder(FocusBorder border) {
        this.focusBorder = border;
    }

    /**
     * 添加当前的FragmentView
     * @param view
     */
    public void addFragmentView(View view) {
        if (view != null) {
            if (fragmentViews == null) {
                fragmentViews = new ArrayList<>();
            }
            fragmentViews.remove(view);
            fragmentViews.add(view);
        }
    }

    /**
     * 添加焦点回调
     * @param view
     * @param callback
     */
    public void addFocusCallback(View view, FocusCallback callback) {
        if (focusCallbacks == null) {
            focusCallbacks = new HashMap<>();
        }
        focusCallbacks.remove(view);
        focusCallbacks.put(view, callback);
    }

    /**
     * 获取newFocus所属于的FragmentView
     * @param newFocus
     * @return
     */
    private View getFocusBelongFragment(View newFocus) {
        if (fragmentViews != null && newFocus != null) {
            for (View fragment : fragmentViews) {
                View view = isFragmentView(fragment, newFocus);
                if (view != null) {
                    return view;
                }
            }
        }
        return null;
    }

    /**
     * 传入的view是否属于fragment
     * @param fragment
     * @param view
     * @return
     */
    private View isFragmentView(View fragment, View view) {
        if (fragment == view) {
            return fragment;
        }
        return view.getParent() instanceof ViewGroup ? isFragmentView(fragment, (View) view.getParent()): null;
    }

    /**
     * 回调焦点监听
     * @param oldFocus
     * @param newFocus
     */
    public void notifyFocusCallback(View oldFocus, View newFocus) {
        for (Map.Entry<View, FocusCallback> entry : focusCallbacks.entrySet()) {
            View view = getFocusBelongFragment(newFocus);
            boolean isFocusBelongCurrentFragment = view == entry.getKey();
            entry.getValue().onFocusChange(oldFocus, newFocus, focusBorder, isFocusBelongCurrentFragment);
        }
    }
}
