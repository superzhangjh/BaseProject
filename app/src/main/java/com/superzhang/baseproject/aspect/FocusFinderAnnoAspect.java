package com.superzhang.baseproject.aspect;

import android.util.Log;

import com.superzhang.baseproject.FocusActivity;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;

import static com.superzhang.baseproject.MainActivity.TAG;

/**
 * Created by 张坚鸿 on 2019/3/15 19:14
 */
@Aspect
public class FocusFinderAnnoAspect {

    @Pointcut("execution(* com.superzhang.baseproject.FocusLinerLayout.dispatchFocusKeyEvent(..))")
    public void dispatchKeyEvent(){}

    @Pointcut("execution(* com.superzhang.baseproject.FocusLinerLayout.doRun(..))")
    public void doRun(){}

    @Pointcut("execution(* com.superzhang.baseproject.FocusActivity.logD(..))")
    public void logD(){}

    @Around("dispatchKeyEvent()")
    public void aroundDispatchKeyEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Log.d(FocusActivity.TAG, "@Around ->> 开始 " + signature.getName()
                + " ->> " + Arrays.toString(signature.getParameterNames()));
//        joinPoint.proceed();
        Log.d(FocusActivity.TAG, "@Around ->> 结束 " + signature.getName());
    }

    @Around("logD()")
    public void aroundDoRun(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.d(FocusActivity.TAG, "@Around ->> 开始 logD()");
        joinPoint.proceed();
        Log.d(FocusActivity.TAG, "@Around ->> 结束 logD()");
    }
}
