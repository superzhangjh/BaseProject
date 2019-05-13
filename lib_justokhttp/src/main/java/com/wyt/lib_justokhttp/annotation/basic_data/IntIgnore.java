package com.wyt.lib_justokhttp.annotation.basic_data;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当参数值为设置值时，不添加到网络请求参数
 * Created by 张坚鸿 on 2019/4/11 16:26
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntIgnore {
    int value() default 0;
}
