package com.wyt.lib_focushandler.animate.size;

import android.animation.TypeEvaluator;

/**
 * 缩放估值器：将宽高根据fraction值不断变化
 * Created by 张坚鸿 on 2019/4/19 10:16
 */
public class SizeEvaluator implements TypeEvaluator<int[]> {

    private int[] evaluates;

    public SizeEvaluator() {
        evaluates = new int[2];
    }

    @Override
    public int[] evaluate(float fraction, int[] startValue, int[] endValue) {
        evaluates[0] = startValue[0] + (int)((endValue[0] - startValue[0]) * fraction);
        evaluates[1] = startValue[1] + (int)((endValue[1] - startValue[1]) * fraction);
        return evaluates;
    }
}

//public class SizeEvaluator implements TypeEvaluator<Rect> {
//
//    private Rect rect;
//
//    public SizeEvaluator() {
//        rect = new Rect();
//    }
//
//    @Override
//    public Rect evaluate(float fraction, Rect startValue, Rect endValue) {
//        rect.set(
//                rect.left,
//                rect.top,
//                (int)(rect.width() + (endValue.width() - startValue.width()) * fraction),
//                (int)(rect.height() + (endValue.height() - startValue.height()) * fraction)
//        );
//        return null;
//    }
//}
