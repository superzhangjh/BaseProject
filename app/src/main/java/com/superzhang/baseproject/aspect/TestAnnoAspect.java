package com.superzhang.baseproject.aspect;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import static com.superzhang.baseproject.MainActivity.TAG;

/**
 * Created by 张坚鸿 on 2019/3/15 16:14
 */
@Aspect
public class TestAnnoAspect {

    @Pointcut("execution(* com.superzhang.baseproject.MainActivity.onCreate(..))")
    public void pointcut(){}

    @Before("pointcut()")
    public void before(JoinPoint point){
        Log.d(TAG, "@Before");
    }
}
