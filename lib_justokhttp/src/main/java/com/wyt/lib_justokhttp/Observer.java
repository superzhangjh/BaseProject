package com.wyt.lib_justokhttp;

/**
 * Created by 张坚鸿 on 2019/4/11 19:37
 */
public interface Observer<T> {

    void onSubscribe();

    void onNext(T t);

    void onError(Throwable e);

    void onComplete();

}