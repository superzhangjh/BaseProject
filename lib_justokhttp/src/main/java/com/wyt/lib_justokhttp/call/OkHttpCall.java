package com.wyt.lib_justokhttp.call;
import com.wyt.lib_justokhttp.method.ServiceMethod;


/**
 * Created by 张坚鸿 on 2019/4/12 14:57
 */
public class OkHttpCall<T> implements Call<T> {

    private ServiceMethod serviceMethod;
    private Callback<T> callback;

    public OkHttpCall(ServiceMethod serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    @Override
    public void enqueue(Callback<T> callback) {
        this.callback = callback;
    }
}
