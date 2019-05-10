package com.wyt.lib_decoupledfocus.focus.decoupledfocus.observer;

import android.util.SparseArray;
import android.view.View;

import com.wyt.lib_decoupledfocus.focus.decoupledfocus.other.FocusParam;

/**
 * 焦点被观察者
 * Created by 张坚鸿 on 2019/2/21 09:05
 */
public class FocusObservable {

    //焦点观察者列表
    protected SparseArray<FocusObserver> observerList;

    public FocusObservable(){
        observerList = new SparseArray<>();
    }

    /**
     * 注册观察者
     * @param observer
     */
    public void registerObserver(int code, FocusObserver observer) {
        if (observerList.get(code) == null){
            observerList.put(code, observer);
        }
    }

    /**
     * 移除观察者
     */
    public void removeObserver(int code) {
        if (observerList.get(code) != null){
            observerList.delete(code);
        }
    }

    /**
     * 通知更新
     * @param focusView 新的焦点
     * @param oldView 旧的焦点
     */
    public void notifyObserver(int code, FocusParam param, View focusView, View oldView) {
        if (observerList.get(code) != null){
            observerList.get(code).onFocusChange(param, focusView, oldView);
        }
    }
}
