package com.wyt.lib_justokhttp.netwatcher;

/**
 * 网络状态
 * Created by 张坚鸿 on 2019/4/10 14:29
 */
public enum NetStatus {

    /**
     * 4G网络
     */
    STATE_4G(true),
    /**
     * wifi
     */
    STATE_WIFI(true),
    /**
     * 3g
     */
    STATE_3G(true),
    /**
     * 无网络
     */
    STATE_NULL(true);

    NetStatus(boolean available){
        this.available = available;
    }

    //是否可联网
    private boolean available;

    public boolean isNetworkAvailable(){
        return available;
    }
}
