package com.wyt.lib_decoupledfocus.focus.decoupledfocus;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.wyt.lib_decoupledfocus.focus.decoupledfocus.other.DecoupledViewListener;
import com.wyt.lib_decoupledfocus.focus.decoupledfocus.other.FocusParam;
import com.wyt.lib_decoupledfocus.focus.decoupledfocus.view.FocusParent;

/**
 * 解耦布局、无侵入的飞框处理工具
 * (1)功能介绍：处理Tv中的飞框，支持缩放、设置动画时长、调节间距、设置贴图文件，支持所有View、ViewGroup(不用自定义View)，可调节焦点框的间距，
 * 不需要写入布局文件中，通过自定义tag改变飞框的表现(是否显示焦点框、是否缩放)
 * (2)使用方法：
 *    (A)在Activity中调用bind放法，每次绘制前会调用DecoupledViewListener的onUpdateViewLocation方法，
 *    所以可以做判断实现不同view不同的参数设置。
 *    (B)使用前需要前需要前在Application中调用init方法，配置默认参数。
 * (3)已知bug：linerLayout会出现布局位置错乱，使用BringToFrontLinearLayout替换LinearLayout即可
 * (4)已知bug：duration设置成0会出现布局偏差，需要请设置成1
 * (5)已知bug：在dialog主题下的activity使用会出现较大偏差
 * (6)已知bug：Recyclerview焦点丢失问题处理，使用QuickTvAdapter、FocusRecyclerView
 * Created by 张坚鸿
 *
 * @TODO:RecyclerView垂直居中
 * https://download.csdn.net/download/shayubuhuifei/10578796
 */
public final class DecoupledFocusUtils {

    private static DecoupledFocusUtils utils;
    private FocusParam defaultParam;
    private SparseArray<FocusParent> parentSparseArray;

    private DecoupledFocusUtils(){
        parentSparseArray = new SparseArray<>();
        defaultParam = new FocusParam();
    }

    /**
     * 初始化(在Application中使用)
     */
    public void init(FocusParam param){
        this.defaultParam = param;
    }

    /**
     * 获取单例
     */
    public static DecoupledFocusUtils getInstance(){
        if (utils == null){
            utils = new DecoupledFocusUtils();
        }
        return utils;
    }

    /**
     * 绑定Activity
     */
    public void bind(final Activity activity){
        bind(activity,null);
    }

    /**
     * 绑定Activity，在setContentView之后调用
     */
    public void bind(final Activity activity, final DecoupledViewListener viewListener){
        if (defaultParam!= null && defaultParam.enable && parentSparseArray.get(activity.hashCode())==null){
            //创建管理类
            FocusParent parent = new FocusParent(activity, getParam(), viewListener);
            parentSparseArray.put(getActivityCode(activity), parent);
        }
    }

    /**
     * 取消Activty绑定
     */
    public void unbind(Activity activity){
        int hashcode = getActivityCode(activity);
        if (parentSparseArray.get(hashcode) != null){
            parentSparseArray.get(hashcode).release();
            parentSparseArray.delete(hashcode);
        }
    }

    /**
     * 在fragment中注册
     */
    public void bind(Fragment fragment){
        parentSparseArray.get(getActivityCode(fragment.getActivity())).registerFragment(fragment, null);
    }

    /**
     * 在fragment中注册
     * (1)注意：在fragment中绑定需要先在其activity中绑定
     * (2)注意：在fragment中调用该方法，如果view是在fragment中，只会回调到fragment中，如果framgnet没有注册，则回调到对应的activity中
     */
    public void bind(Fragment fragment, final DecoupledViewListener viewListener){
        parentSparseArray.get(getActivityCode(fragment.getActivity())).registerFragment(fragment, viewListener);
    }

    /**
     * 在fragment中解绑
     * @param fragment
     */
    public void unbind(Fragment fragment){
        if (fragment.getActivity() == null){
            return;
        }
        FocusParent parent = parentSparseArray.get(getActivityCode(fragment.getActivity()));
        if (parent != null){
            parent.removeFragment(fragment);
        }
    }

    private int getActivityCode(Activity activity){
        return activity.hashCode();
    }

    public FocusParam getParam() {
        return defaultParam.copy();
    }
}
