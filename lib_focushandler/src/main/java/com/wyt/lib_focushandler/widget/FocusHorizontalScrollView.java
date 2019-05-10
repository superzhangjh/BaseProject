package com.wyt.lib_focushandler.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import com.wyt.lib_focushandler.R;

import static com.wyt.lib_focushandler.adapter.BaseFocusAdapter.TAG_OFFSET;

/**
 * Created by 张坚鸿 on 2019/2/27 15:46
 * 注意要将滑动系数mFadingEdge设置成距离边缘最远的距离
 */
public class FocusHorizontalScrollView extends HorizontalScrollView {

    //滑动系数
    private int mFadingEdge;
    //最后一次滑动的时间
    private long lastScrollTimestamp;

    public FocusHorizontalScrollView(Context context) {
        this(context, null);
    }

    public FocusHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.FocusHorizontalScrollView);

        int n = ta.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.FocusHorizontalScrollView_fadingEdge){
                mFadingEdge = ta.getDimensionPixelSize(attr, 50);
            }
        }
        ta.recycle();
    }

    public void setFadingEdge(int fadingEdge) {
        this.mFadingEdge = fadingEdge;
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        if (getChildCount() == 0) {
            return 0;
        }

        int width = getWidth();
        int screenLeft = getScrollX();
        int screenRight = screenLeft + width;

        int fadingEdge = mFadingEdge;

        // leave room for left fading edge as long as rect isn't at very left
        if (rect.left > 0) {
            screenLeft += fadingEdge;
        }

        // leave room for right fading edge as long as rect isn't at very right
        if (rect.right < getChildAt(0).getWidth()) {
            screenRight -= fadingEdge;
        }

        int scrollXDelta = 0;

        if (rect.right > screenRight && rect.left > screenLeft) {
            // need to move right to get it in view: move right just enough so
            // that the entire rectangle is in view (or at least the first
            // screen size chunk).

            if (rect.width() > width) {
                // just enough to get screen size chunk on
                scrollXDelta += (rect.left - screenLeft);
            } else {
                // get entire rect at right of screen
                scrollXDelta += (rect.right - screenRight);
            }

            // make sure we aren't scrolling beyond the end of our content
            int right = getChildAt(0).getRight();
            int distanceToRight = right - screenRight;
            scrollXDelta = Math.min(scrollXDelta, distanceToRight);

        } else if (rect.left < screenLeft && rect.right < screenRight) {
            // need to move right to get it in view: move right just enough so
            // that
            // entire rectangle is in view (or at least the first screen
            // size chunk of it).

            if (rect.width() > width) {
                // screen size chunk
                scrollXDelta -= (screenRight - rect.right);
            } else {
                // entire rect at left
                scrollXDelta -= (screenLeft - rect.left);
            }

            // make sure we aren't scrolling any further than the left our
            // content
            scrollXDelta = Math.max(scrollXDelta, -getScrollX());
        }

        //这里调用平滑滚动
        if (scrollXDelta != 0) {
            long timestamp = System.currentTimeMillis();
            if (timestamp - lastScrollTimestamp > 30L) {
                lastScrollTimestamp = System.currentTimeMillis();
                setOffsetTag(new Point(scrollXDelta, 0));
                smoothScrollBy(scrollXDelta, 0);
            }
        } else {
            setOffsetTag(null);
        }
        //返回0不使用自带的滑动
        return 0;
    }

    private void setOffsetTag(Point offset) {
        setTag(TAG_OFFSET, offset);
    }
}
