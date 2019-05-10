package com.wyt.lib_decoupledfocus.focus.decoupledfocus.view;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.wyt.lib_decoupledfocus.focus.decoupledfocus.observer.FocusObservable;
import com.wyt.lib_decoupledfocus.focus.decoupledfocus.observer.FocusObserver;
import com.wyt.lib_decoupledfocus.focus.decoupledfocus.other.DecoupledViewListener;
import com.wyt.lib_decoupledfocus.focus.decoupledfocus.other.FocusParam;
import com.wyt.lib_decoupledfocus.focus.decoupledfocus.other.FocusTag;

import java.util.ArrayList;
import java.util.List;

/**
 * 焦点处理父类 - 对应于Activity
 */
public class FocusParent extends FocusObservable {

    private static final int CODE_UNREGISTER = -1;


    //xml布局中的第一个控件
    private View parent;
    //默认配置参数
    private FocusParam param;
    //焦点框
    private FocusView focusView;
    //旧的焦点view
    private View oldFocusView;
    //新的焦点view
    private View newFocusView;
    //Fragment的ViewHashCodes
    private List<Integer> fragmentCodes;

    public FocusParent(Activity activity, FocusParam param, DecoupledViewListener listener){
        fragmentCodes = new ArrayList<>();
        this.param = param;
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        parent = contentView.getChildAt(0);
        //创建焦点View
        focusView = new FocusView(contentView.getContext());
        focusView.setFocusable(false);
        focusView.setFocusableInTouchMode(false);
        //解决一开始无焦点时焦点框在全屏显示不好看的问题
        focusView.setAlpha(0);
        //添加到android.R.id.content中
        contentView.addView(focusView);
        initListener();

        //将Activity添加到观察者队列中
        register(getCode(parent), listener);
    }

    /**
     * 为Fragment注册焦点监听
     * @param fragment
     * @param listener
     */
    public void registerFragment(Fragment fragment, DecoupledViewListener listener){
        int code = getCode(fragment.getView());
        if (!fragmentCodes.contains(code)){
            fragmentCodes.add(code);
            register(code, listener);
        }
    }

    /**
     * 移除Fragment的焦点监听
     * @param fragment
     */
    public void removeFragment(Fragment fragment){
        Integer code = getCode(fragment.getView());
        fragmentCodes.remove(code);
        removeObserver(code);
    }

    /**
     * 注册观察者，并对应回调
     * @param code
     * @param listener
     */
    private void register(int code, final DecoupledViewListener listener){
        registerObserver(code, new FocusObserver() {
            @Override
            public void onFocusChange(FocusParam param, View focusView, View oldView) {
                updateFocusView(
                        listener == null? param : listener.onUpdateViewLocation(param, focusView, oldView),
                        focusView,
                        oldView
                );
            }
        });
    }

    /**
     * 获取对象的hashCode
     */
    private int getCode(Object o){
        if (o == null){
            return CODE_UNREGISTER;
        }
        return o.hashCode();
    }

    /**
     * 初始化监听事件
     */
    private void initListener() {
        //设置全局焦点监听，当获得焦点时，更改焦点框的位置
        parent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                if (newFocus == null){
                    return;
                }
                //找到焦点View对应的布局view， 并回调给该布局
                int animCode = getCode(parent);
                for (int code : fragmentCodes){
                    if (newFocus.hashCode() == code){
                        animCode = code;
                    } else {
                        if (isCont(newFocus, code)){
                            animCode = code;
                        }
                    }
                }
                notifyObserver(animCode, param, newFocus, oldFocus);
            }
        });

        //目标view滑动时，焦点框跟随,这里针对RecyclerView选中居中的设置
        parent.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (newFocusView != null && focusView != null){
                    updateScrolledChanged();
                }
            }
        });

        //TODO:可以通过这个方法来post循环监听首次加载完成之后，实现首个View获取焦点的功能
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

            }
        });
    }

    /**
     * 判断view是否在Fragment中
     * @param view 目标view
     * @param pCode fragment的code
     * @return
     */
    private boolean isCont(View view, int pCode){
        if (view != null && view.getParent() != null && view.getParent() instanceof View){
            if ((view.getParent()).hashCode() == pCode){
                return true;
            } else {
                return isCont((View) view.getParent(), pCode);
            }
        } else {
            return false;
        }
    }

    /**
     * 暴露给外部调用的方法：用户获取到焦点时做的处理
     * @param oldFocus
     * @param newFocus
     */
    private void updateFocusView(FocusParam newParam, View newFocus, View oldFocus){
        if (newFocus == null){
            return;
        }
        this.param = newParam;
        oldFocusView = oldFocus;
        newFocusView = newFocus;
        //这里使用post的方式，避免该view的绘制还没完成就进行焦点处理，导致焦点框大小异常
        newFocusView.post(new Runnable() {
            @Override
            public void run() {
                if (focusView!=null){
                    focusView.upDateLocation(
                            param,
                            newFocusView,
                            getLocation(newFocusView),
                            getFcousTag(newFocusView),
                            false);
                }
                upDateViews(oldFocusView,newFocusView, getFcousTag(newFocusView).isScale()? param.zoom: 1.0f);
            }
        });
    }

    /**
     * 焦点View滚动时候的处理
     */
    private void updateScrolledChanged(){
        if (focusView != null){
            ViewGroup group = (ViewGroup) newFocusView.getParent();
            if (group instanceof ScrollingView
                    || group instanceof ScrollView
                    || group instanceof HorizontalScrollView
                    || group instanceof NestedScrollingParent2
                    || group instanceof NestedScrollingChild2){
                if (focusView.isAnimating()){
                    newFocusView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateScrolledChanged();
                        }
                    }, 30L);
                } else {
                    focusView.cancelAnimation();
                    focusView.upDateLocation(
                            param,
                            newFocusView,
                            getLocation(newFocusView),
                            getFcousTag(newFocusView),
                            true);
                }
            }
        }
    }

    /**
     * 获取View在布局中的位置
     * @param view
     * @return location
     */
    private int[] getLocation(View view){
        if (view == null){
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
    private int[] getLocation(View view, int[] location){
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

    /**
     * 更新View的缩放状态
     * @param animView 焦点框的目标View
     * @param oldView 上次更新的view
     */
    private void upDateViews(View oldView, View animView, float zoom){
        //如果不可见，不进行缩放
        if (!param.visiable){
            zoom = 1.0f;
        }

        //更新目标View
        if (animView!=null && zoom != animView.getScaleX()){
            animView.bringToFront();
            ViewCompat.animate(animView)
                    .scaleX(zoom)
                    .scaleY(zoom)
                    .setDuration(param.duration)
                    .start();
        }
        //缩放旧View到原大小
        if (oldView != null){
            ViewCompat.animate(oldView)
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(param.duration)
                    .start();
        }
    }

    /**
     * 判断该View是否是不进行焦点处理的View
     * 忽略的view可获取焦点，但是不参与缩放与焦点框，仅仅是动画效果
     * @param view
     */
    private FocusTag getFcousTag(View view){
        if (view != null && view.getTag() instanceof String){
            String tag = (String) view.getTag();
            if (tag.equals(param.scaleTag)){
                return FocusTag.FOCUS_HIDE_SCALE;
            } else if (tag.equals(param.focusNoScaleTag)){
                return FocusTag.FOCUS_SHOW_NOSCALE;
            } else if (tag.equals(param.ignoreTag)){
                return FocusTag.FCOUS_HIDE_NOSCALE;
            }
        }
        return FocusTag.FOCUS_SHOW_SCALE;
    }

    public void release(){
        parent = null;
        focusView = null;
    }
}
