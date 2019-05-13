package com.wyt.lib_justokhttp;

/**
 * Created by 张坚鸿 on 2019/4/12 10:57
 */
public interface Observable<T> {

    void subscribe(Observer<T> observer);
}
