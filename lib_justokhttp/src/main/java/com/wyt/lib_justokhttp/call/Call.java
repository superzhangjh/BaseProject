package com.wyt.lib_justokhttp.call;

/**
 * Created by 张坚鸿 on 2019/4/12 15:03
 */
public interface Call<T> {

    void enqueue(Callback<T> callback);
}
