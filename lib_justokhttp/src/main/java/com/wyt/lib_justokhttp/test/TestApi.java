package com.wyt.lib_justokhttp.test;

import com.wyt.lib_justokhttp.Observable;
import com.wyt.lib_justokhttp.annotation.POST;


/**
 * Created by 张坚鸿 on 2019/4/11 14:00
 */
public interface TestApi {

    Observable<String> getString(String s, int i);

}
