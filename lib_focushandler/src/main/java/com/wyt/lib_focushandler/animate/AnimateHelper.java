package com.wyt.lib_focushandler.animate;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

/**
 * View相关位置和变化的帮助类, 实现了一些简单的计算与封装
 * Created by 张坚鸿 on 2019/4/18 14:50
 */
public final class AnimateHelper {

    private AnimateHelper() {
    }

    /**
     * 获取View自身位置
     */
    public static int[] getFromLocation(View view) {
        int[] fromLocation = new int[2];
        if (view != null) {
            view.getLocationOnScreen(fromLocation);
        }
        return fromLocation;
    }

    /**
     * 获取View在布局中的位置
     */
    public static int[] getAnimLocation(View view) {
        if (view == null) {
            return null;
        }
        return getLocation(view, null);
    }

    /**
     * 获取View在布局中的位置
     * @param view
     * @param location 坐标点 location[0]:left location[1]:top
     * @return location
     */
    private static int[] getLocation(View view, int[] location){
        if (location == null){
            location = new int[2];
        }
        location[0] += view.getLeft();
        location[1] += view.getTop();

        //适配ViewPager的item间距, 如果你的Viewpager自定义成垂直的，要单独处理。。
        if (view instanceof ViewPager){
            ViewPager viewPager = (ViewPager) view;
            if (viewPager.getCurrentItem() > 0){
                location[0] -= viewPager.getCurrentItem() * viewPager.getWidth();
            }
        }
        //适配HorizontalScrollView
        if (view instanceof HorizontalScrollView || view instanceof ScrollView){
            location[0] -= view.getScrollX();
            location[1] -= view.getScrollY();
        }
        //如果父布局不存在， 或者父布局是FrameContentLayout，则返回坐标结果
        return view.getParent()!=null &&  !(((View)view.getParent()).getId() == android.R.id.content)?
                getLocation((View) view.getParent(), location): location;
    }
}
