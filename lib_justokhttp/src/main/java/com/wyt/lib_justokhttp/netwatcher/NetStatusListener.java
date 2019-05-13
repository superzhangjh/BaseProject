package com.wyt.lib_justokhttp.netwatcher;

/**
 * Created by 张坚鸿 on 2019/4/10 14:38
 */
public interface NetStatusListener {

    /**
     * 网络状态发生改变
     * @param status
     */
    void onNetStatusChange(NetStatus status);

}
