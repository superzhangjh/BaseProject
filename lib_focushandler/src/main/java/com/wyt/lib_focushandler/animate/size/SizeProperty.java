package com.wyt.lib_focushandler.animate.size;

import android.util.Property;
import android.view.View;
import android.view.ViewGroup;

/**
 * View缩放属性
 * Created by 张坚鸿 on 2019/4/19 09:50
 */
public class SizeProperty extends Property<View, int[]> {

    private int[] params;

    /**
     * A constructor that takes an identifying name and {@link #getType() type} for the property.
     */
    public SizeProperty() {
        super(int[].class, "layout");
        params = new int[2];
    }

    @Override
    public int[] get(View object) {
        return params;
    }

    @Override
    public void set(View object, int[] value) {
        params[0] = value[0];
        params[1] = value[1];
        ViewGroup.LayoutParams layoutParams = object.getLayoutParams();
        layoutParams.width = params[0];
        layoutParams.height = params[1];
        object.setLayoutParams(layoutParams);
    }
}

//public class SizeProperty extends Property<View, Rect> {
//
//    private Rect rect;
//
//
//    /**
//     * A constructor that takes an identifying name and {@link #getType() type} for the property.
//     *
//     */
//    public SizeProperty() {
//        super(Rect.class, "layout");
//    }
//
//    @Override
//    public Rect get(View object) {
//        return rect;
//    }
//
//    @Override
//    public void set(View object, Rect value) {
//        if (value != null) {
//            this.rect = value;
//            ViewGroup.LayoutParams layoutParams = object.getLayoutParams();
//            layoutParams.width = value.width();
//            layoutParams.height = value.height();
//            object.setLayoutParams(layoutParams);
//        }
//    }
//}
