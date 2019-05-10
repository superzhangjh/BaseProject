package com.wyt.lib_focushandler;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.wyt.lib_focushandler.adapter.FocusAdapter;
import com.wyt.lib_focushandler.holder.FocusBorderHolder;
import com.wyt.lib_focushandler.holder.FocusCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张坚鸿 on 2019/4/30 17:25
 */
public class FocusHandler {

    private Map<Object, FocusBorderHolder> holders;
    private Map<ViewGroup, ViewTreeObserver.OnGlobalFocusChangeListener> focusChangeListeners;

    private FocusHandler() {
        holders = new HashMap<>();
        focusChangeListeners = new HashMap<>();
    }

    public static FocusHandler getInstance() {
        return FocusBolderUtilsHolder.INSTANCE;
    }

    private static class FocusBolderUtilsHolder {
        private static FocusHandler INSTANCE = new FocusHandler();
    }

    /**
     * 在当前的Activity中使用
     */
    public FocusHandler bind(@NonNull Activity activity, @NonNull FocusAdapter adapter) {
        if (holders.get(activity) != null) {
            holders.remove(activity);
        }
        holders.put(activity, createBolderHolder(activity, adapter));
        return this;
    }

    /**
     * 添加焦点回调监听
     */
    public void addCallback(Activity activity, FocusCallback callback) {
        View content = getContentView(activity);
        FocusBorderHolder holder = holders.get(activity);
        if (content != null && holder != null) {
            holder.getFocusCallbackHolder().addFocusCallback(content, callback);
        }
    }

    /**
     * 添加焦点回调监听
     */
    public void addCallback(Fragment fragment, FocusCallback callback) {
        View content = fragment.getView();
        if (content != null && fragment.getActivity() != null) {
            FocusBorderHolder holder = holders.get(fragment.getActivity());
            if (holder == null) {
                throw new IllegalStateException("You must bind activity at focushandler first.");
            }
            holder.getFocusCallbackHolder().addFragmentView(content);
            holder.getFocusCallbackHolder().addFocusCallback(content, callback);
        }
    }

    /**
     * 在Activity中解绑
     */
    public void unbind(Activity activity) {
        View parent = getContentView(activity);
        parent.getViewTreeObserver().removeOnGlobalFocusChangeListener(focusChangeListeners.get(parent));
        holders.remove(activity);
    }

    /**
     * 创建FocusBolderHolder
     */
    private FocusBorderHolder createBolderHolder(Activity activity, FocusAdapter adapter) {
        ViewGroup parent = getContentView(activity);
        //创建实例
        final FocusBorderHolder holder = new FocusBorderHolder(parent);
        //绑定默认的焦点框
        holder.bindFocusBolder(adapter.getFocusBolder(parent.getContext()));
        //设置焦点监听
        focusChangeListeners.remove(parent);
        focusChangeListeners.put(parent, new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                holder.onFocusChange(oldFocus, newFocus);
            }
        });
        parent.getViewTreeObserver().addOnGlobalFocusChangeListener(focusChangeListeners.get(parent));
        //设置动画监听
        holder.setAnimationListener(adapter.getAnimationLisenter());
        return holder;
    }

    /**
     * 获取Activity的根布局
     */
    private ViewGroup getContentView(Activity activity) {
        return activity.findViewById(android.R.id.content);
    }
}
