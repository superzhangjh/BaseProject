package com.wyt.lib_decoupledfocus.focus.decoupledfocus.other;

/**
 * 配置参数, 需要在Application中使用，如果备份界面需要做单独处理，则可再设置一次
 */
public class FocusParam implements Cloneable {
    //当前View是否允许改进行焦点框
    public boolean enable = true;
    //默认的贴纸
    public int drawableResource;
    //间距(不同贴纸的边距会有差异，可以设置padding进行微调)
    public int leftPadding; //左间距
    public int rightPadding;//右间距
    public int topPadding;//上间距
    public int bottomPadding;//下间距
    //动画执行时间
    public long duration = 300L;
    //view的缩放延迟
    public long offsetDuration;
    //默认缩放倍数
    public float zoom = 1.0f;
    //有焦点，但是不显示tag，只进行缩放的tag
    public String scaleTag;
    //有焦点但是不显示/不缩放的tag
    public String ignoreTag;
    //有焦点显示焦点框，不缩放的tag
    public String focusNoScaleTag;
    //是否使用滑动跟随(当View滑动时，焦点框跟随移动)
    public boolean isScrollFollowing;
    //是否显示焦点框
    public boolean visiable = true;

    public FocusParam copy(){
        FocusParam param = new FocusParam();
        param.enable = true;
        param.drawableResource = drawableResource;
        param.leftPadding = leftPadding;
        param.rightPadding = rightPadding;
        param.topPadding = topPadding;
        param.bottomPadding = bottomPadding;
        param.duration = duration;
        param.offsetDuration = offsetDuration;
        param.zoom = zoom;
        param.ignoreTag = ignoreTag;
        param.isScrollFollowing = isScrollFollowing;
        param.focusNoScaleTag = focusNoScaleTag;
        param.scaleTag = scaleTag;
        return param;
    }
}
