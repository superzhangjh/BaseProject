package com.wyt.lib_justokhttp;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求工具类
 * Created by 张坚鸿 on 2019/4/10 14:18
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * 参考地址！！代理方式写法：https://www.jianshu.com/p/8550146203d6
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
public class JustOK {

    private final static int DEFAULT_TIMEOUT = 10;

    @SuppressLint("StaticFieldLeak")
    private static JustOK mClient;
    private boolean isNetworkAvailable = true;
    @SuppressLint("StaticFieldLeak")
    private static Context mApplicationContext;
    public OkHttpClient mOkHttpClient;
    //缓冲大小
    private int cacheSize = 10 * 1024 * 1024; // 10 MiB
//    private final Map<Method, ServiceMethod<?,?>> serviceMethodCache;

    /**
     * 初始化Context， 这里需要传入Application的Context， 不能传入Activity的
     */
    public static void init(Context applicationContext){
        mApplicationContext = applicationContext;
    }

    public static JustOK getInstance(){
        if (mClient == null) {
            synchronized (JustOK.class) {
                if (mClient == null) {
                    mClient = new JustOK();
                }
            }
        }
        return mClient;
    }

    private JustOK(){
//        serviceMethodCache = new HashMap<>();

        //有网时候的缓存
        Interceptor netCacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                int onlineCacheTime = 30;//在线的时候的缓存过期时间，如果想要不缓存，直接时间设置为0
                return response.newBuilder()
                        .header("Cache-Control", "public, max-age="+onlineCacheTime)
                        .removeHeader("Pragma")
                        .build();
            }
        };

        //没有网时候的缓存
        Interceptor offlineCacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!isNetworkAvailable) {
                    int offlineCacheTime = 60;//离线的时候的缓存的过期时间
                    request = request.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + offlineCacheTime)
                            .build();
                }
                return chain.proceed(request);
            }
        };

        //创建缓存
        File httpCacheDirectory = new File(mApplicationContext.getCacheDir(), "okhttpCache");
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        //创建OkhttpClient
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS)
                .addNetworkInterceptor(netCacheInterceptor)
                .addInterceptor(offlineCacheInterceptor)
                .cache(cache)
                .build();

    }

    /**
     * 使用代理模式，生成传入的service类
     * @param service
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T createService(final Class<T> service){
        Utils.validateServiceInterface(service);
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
//                        return new BaseObservable<Object>(args) {};
                        return null;
                    }
                });
    }



    /**
     * 获取一个服务方法, 如果该方法已声明，则直接调用
     * @return
     */
//    private ServiceMethod<?, ?> loadServiceMethod(Method method, Object[] args) {
//        ServiceMethod<?, ?> result = serviceMethodCache.get(method);
//        if (result != null) {
//            return result;
//        }
//
//        synchronized (serviceMethodCache) {
//            result = serviceMethodCache.get(method);
//            if (result == null) {
//                result = new ServiceMethod.Builder<>(this, method, args).build();
//                serviceMethodCache.put(method, result);
//            }
//        }
//        return result;
//    }

    public void get(String url, com.wyt.lib_justokhttp.call.Callback callback){
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}
