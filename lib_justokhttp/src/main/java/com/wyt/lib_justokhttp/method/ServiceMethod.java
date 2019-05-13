package com.wyt.lib_justokhttp.method;

import com.wyt.lib_justokhttp.call.Call;
import com.wyt.lib_justokhttp.call.OkHttpCall;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by 张坚鸿 on 2019/4/12 14:41
 */
public class ServiceMethod<T> {

    public Type[] types;
    public Object[] args;

    private ServiceMethod(Method method, Object[] args) {
        this.types = method.getParameterTypes();
        this.args = args;
    }

    public Object adapt(Call<T> call) {
        return null;
    }

    public static class Builder {

        private Method method;
        private Object[] args;

        public Builder(Method method, Object[] args) {
            this.method = method;
            this.args = args;
        }

        public ServiceMethod build(){
            return new ServiceMethod(method, args);
        }

    }
}
