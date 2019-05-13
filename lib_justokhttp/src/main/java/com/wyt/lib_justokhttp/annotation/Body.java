package com.wyt.lib_justokhttp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by 张坚鸿 on 2019/4/12 09:27
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Body {
}
