package com.wyt.lib_justokhttp.call;

/**
 * Created by 张坚鸿 on 2019/4/12 15:04
 */
public interface Callback<T> {

    void onSuccess(T t);

    void onError(Throwable throwable);

}
