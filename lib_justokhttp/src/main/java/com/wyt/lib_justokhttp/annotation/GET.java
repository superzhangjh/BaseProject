package com.wyt.lib_justokhttp.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by zhang on 2019/4/9 17:33
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface GET {
    String value() default "";
}
