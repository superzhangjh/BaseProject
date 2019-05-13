package com.wyt.lib_justokhttp.netwatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 张坚鸿 on 2019/4/10 14:27
 */
public class NetStatesWatcher {

    private Map<Object, NetStatusListener> observers;
    private static NetStatesWatcher mUtils;

    private NetStatesWatcher(){
        observers = new HashMap<>();
    }

    public static NetStatesWatcher getInstance(){
        if (mUtils == null) {
            synchronized (NetStatesWatcher.class) {
                if (mUtils == null) {
                    mUtils = new NetStatesWatcher();
                }
            }
        }
        return mUtils;
    }

    public void subscribe(Object o, NetStatusListener listener){
        observers.put(o, listener);
    }

    public void unsubscribe(Object o){
        observers.remove(o);
    }

    /**
     * 通知网络状态改变
     */
    public void notifyNetworkStatusChange(NetStatus status){
        for (Map.Entry<Object, NetStatusListener> entry : observers.entrySet()){
            entry.getValue().onNetStatusChange(status);
        }
    }
}
