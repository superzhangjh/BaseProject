package com.wyt.lib_focushandler.widget;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wyt.lib_focushandler.widget.layoutmanager.FocusLayoutManager;

import static com.wyt.lib_focushandler.adapter.BaseFocusAdapter.TAG_OFFSET;

/**
 * 焦点RecyclerView
 * 处理问题：
 * (1) RecyclerView child 的 bringToFont() 方法无效, 需要在焦点变化时调用 invalidate() 方法;
 * (2) 参考 androidtvwidget 项目，实现 RecyclerView 的选中居中
 * Created by 张坚鸿 on 2019/4/24 10:48
 */
public class FocusRecyclerView extends RecyclerView {
    private static final String TAG = "FocusRecyclerView";

    private boolean mSelectedItemCentered = true;
    private final Rect mTempRect = new Rect();
    private int mSelectedItemOffsetStart;
    private int mSelectedItemOffsetEnd;
    private ScrollInterruptListner scrollInterruptListner;

    public FocusRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public FocusRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setChildrenDrawingOrderEnabled(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        addOnScrollListener(new FocusScrollListener());
    }

    /**
     * 设置选中的item是否居中
     * @param selectedItemCentered
     */
    public void setSelectedItemCentered(boolean selectedItemCentered) {
        this.mSelectedItemCentered = selectedItemCentered;
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (getLayoutManager() != null) {
            View view = getLayoutManager().getFocusedChild();
            if (view == null) {
                return super.getChildDrawingOrder(childCount, i);
            }
            int position = indexOfChild(view);
            if (position < 0) {
                return super.getChildDrawingOrder(childCount, i);
            }
            if (i == childCount - 1) {
                return position;
            }
            if (i == position) {
                return childCount - 1;
            }
        }
        return super.getChildDrawingOrder(childCount, i);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
    }

    @Override
    public void smoothScrollBy(int dx, int dy) {
        setScrollValue(dx, dy);
        super.smoothScrollBy(dx, dy);
    }

    private void setScrollValue(int x, int y) {
        if(x != 0 || y != 0) {
            Point point = new Point(x, y);
            setTag(TAG_OFFSET, point);
        } else {
            setTag(TAG_OFFSET, null);
        }
    }

    @Override
    public void setLayoutManager(@Nullable LayoutManager layout) {
        if (layout != null && !(layout instanceof FocusLayoutManager)) {
            throw new IllegalArgumentException(layout.getClass().getSimpleName() + " is not implement FocusLayoutManager.");
        }
        super.setLayoutManager(layout);
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rect, boolean immediate) {
        if(null == child || null == getLayoutManager()) {
            return false;
        }

        if(mSelectedItemCentered) {
            getDecoratedBoundsWithMargins(child, mTempRect);
            mSelectedItemOffsetStart = (getLayoutManager().canScrollHorizontally() ? (getFreeWidth() - mTempRect.width())
                    : (getFreeHeight() - mTempRect.height())) / 2;
            mSelectedItemOffsetEnd = mSelectedItemOffsetStart;
        }

        int[] scrollAmount = getChildRectangleOnScreenScrollAmount2(child, rect, mSelectedItemOffsetStart, mSelectedItemOffsetEnd);
        int dx = scrollAmount[0];
        int dy = scrollAmount[1];

        Log.d(TAG,"requestChildRectangle dx=" +dx + " dy=" + dy);

        smoothScrollBy(dx, dy);

        if (dx != 0 || dy != 0) {
            return true;
        }

        // 重绘是为了选中item置顶，具体请参考getChildDrawingOrder方法
        postInvalidate();

        return false;
    }

    private int getFreeHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private int getFreeWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int[] getChildRectangleOnScreenScrollAmount2(View focusView, Rect rect, int offsetStart, int offsetEnd) {
        //横向滚动
        int dx = 0;
        int dy = 0;

        if(null != getLayoutManager()) {

            getDecoratedBoundsWithMargins(focusView, mTempRect);


            if (getLayoutManager().canScrollHorizontally()) {
                final int right =
                        mTempRect.right
                                + getPaddingRight()
                                - getWidth();
                final int left =
                        mTempRect.left
                                - getPaddingLeft();
                dx = computeScrollOffset(left, right, offsetStart, offsetEnd);
            }

            //竖向滚动
            if (getLayoutManager().canScrollVertically()) {
                final int bottom =
                        mTempRect.bottom
                                + getPaddingBottom()
                                - getHeight();
                final int top =
                        mTempRect.top
                                - getPaddingTop();
                dy = computeScrollOffset(top, bottom, offsetStart, offsetEnd);
            }

        }
        return new int[]{dx, dy};
    }

    private int computeScrollOffset(int start, int end, int offsetStart, int offsetEnd) {
        // focusView超出下/右边界
        if (end > 0) {
            if(getLastVisiblePosition() != (getItemCount() - 1)) {
                return end + offsetEnd;
            } else {
                return end;
            }
        }
        // focusView超出上/左边界
        else if (start < 0) {
            if(getFirstVisiblePosition() != 0) {
                return start - offsetStart;
            } else {
                return start;
            }
        }
        // focusView未超出上/左边界，但边距小于指定offset
        else if(start > 0 && start < offsetStart
                && (canScrollHorizontally( -1) || canScrollVertically( -1))) {
            return start - offsetStart;
        }
        // focusView未超出下/右边界，但边距小于指定offset
        else if(Math.abs(end) > 0 && Math.abs(end) < offsetEnd
                && (canScrollHorizontally( 1) || canScrollVertically( 1))) {
            return offsetEnd - Math.abs(end);
        }

        return 0;
    }

    public int getLastVisiblePosition() {
        final int childCount = getChildCount();
        if(childCount == 0) {
            return 0;
        } else {
            if(getLayoutManager() instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
            }
            return getChildAdapterPosition(getChildAt(childCount - 1));
        }
    }

    public int getItemCount() {
        if(null != getAdapter()) {
            return getAdapter().getItemCount();
        }
        return 0;
    }

    private int getFirstVisiblePosition() {
        if(getChildCount() == 0) {
            return 0;
        } else {
            if(getLayoutManager() instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            }
            return getChildAdapterPosition(getChildAt(0));
        }
    }

    /**
     * 滑动监听，用于修正滑动值大于RecyclerView的消费阈值时的bug(计算出来的requestChildRectangleOnScreen的值大于实际值)，
     * 当滑动结束的时候，重新请求一次边框绘制
     * （因为动画时间 > 滑动时间时，才能达到这种修正效果）
     */
    private class FocusScrollListener extends RecyclerView.OnScrollListener {
        private int mScrollX = 0;
        private int mScrollY = 0;

        private FocusScrollListener() { }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            mScrollX = Math.abs(dx) == 1? 0: dx;
            mScrollY = Math.abs(dy) == 1? 0 : dy;
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                View focused = recyclerView.getFocusedChild();
                if (!(focused instanceof RecyclerView) && (mScrollX != 0 || mScrollY !=0)) {
                    recyclerView.setTag(TAG_OFFSET, null);
                    if (scrollInterruptListner != null) {
                        scrollInterruptListner.onScrollInterrupt(focused);
                    }
                }
                mScrollX = mScrollY = 0;
            }
        }
    }

    public void setScrollInterruptListner(ScrollInterruptListner listner) {
        this.scrollInterruptListner = listner;
    }

    public interface ScrollInterruptListner {
        void onScrollInterrupt(View focused);
    }
}
