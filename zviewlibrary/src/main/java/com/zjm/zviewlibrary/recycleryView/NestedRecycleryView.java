package com.zjm.zviewlibrary.recycleryView;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * When I wrote this, only God and I understood what I was doing
 * Now, God only knows
 * <p>
 * Created by zjm on 2018/12/3.
 * <p>
 * 允许嵌套滑动的 RecycleryView
 */
public class NestedRecycleryView extends RecyclerView {


    private boolean mIsIntercept;
    private float mCurrentY;
    private float mLastY;

    /**
     * 是否永远不允许父控件的拦截事件
     *
     * @param isIntercept
     */
    public void setInterceptTouchEvent(boolean isIntercept) {
        mIsIntercept = isIntercept;
    }

    public NestedRecycleryView(Context context) {
        this(context,null);
    }

    public NestedRecycleryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = e.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsIntercept) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    mCurrentY = e.getY();
                    boolean boundary = isBoundary();
                    getParent().requestDisallowInterceptTouchEvent(!boundary);
                }
                mLastY = mCurrentY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
            default:
        }
        return super.onTouchEvent(e);
    }

    /**
     * @return 是否滑动到边界
     */
    private boolean isBoundary() {
        LayoutManager layoutManager = getLayoutManager();

        int lastCompletelyVisibleItemPosition;
        int firstCompletelyVisibleItemPosition;
        int itemCount = layoutManager.getItemCount();
        int childCount = layoutManager.getChildCount();

        if (layoutManager instanceof GridLayoutManager) {
            lastCompletelyVisibleItemPosition =
                    ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            firstCompletelyVisibleItemPosition =
                    ((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
        } else {
            lastCompletelyVisibleItemPosition =
                    ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            firstCompletelyVisibleItemPosition =
                    ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
        }

        if (childCount > 0) {
            if (mLastY > mCurrentY) {
                //手指向上滑
                Log.e("NestedRecycleryView", "手指上滑");

                //是否滚动到底部
                boolean isScrollBottom = !canScrollVertically(1);
                //是否最后一个
                boolean isLast = lastCompletelyVisibleItemPosition >= itemCount - 1;
                Log.e("NestedRecycleryView", isScrollBottom ? "滑到底部不能再滑了" : "还没滑到底部");
                Log.e("NestedRecycleryView", isLast ? "已经滑到最后一个了" : "还没滑到最后一个了");
                return isScrollBottom && isLast;
            } else {
                //手指向下滑
                Log.e("NestedRecycleryView", "手指下滑");

                boolean isScrollTop = !canScrollVertically(-1);
                boolean isFirst = firstCompletelyVisibleItemPosition == 0;
                Log.e("NestedRecycleryView", isScrollTop ? "滑到顶部不能再滑了" : "还没滑到顶部");
                Log.e("NestedRecycleryView", isFirst ? "已经滑到第一个了" : "还没滑到第一个了");

                return isScrollTop && isFirst;
            }
        }
        return true;
    }
}
