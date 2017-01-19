package com.oraro.mbroadcast.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import com.oraro.mbroadcast.R;

/**
 * Created by admin on 2016/9/28
 *
 * @author zmy
 */
public class EditRefreshLayout extends SwipeRefreshLayout implements AbsListView.OnScrollListener {
    /**
     * 滑动到最下面时的上拉操作
     */

    private int mTouchSlop;
    /**
     * listview实例
     */
    private ListView mListView;

    /**
     * 上拉监听器, 到了最底部的上拉加载操作
     */
    private OnLoadListener mOnLoadListener;

    /**
     * ListView的加载中footer
     */
    private View mListViewFooter;

    /**
     * 按下时的y坐标
     */
    private int mYDown;
    /**
     * 抬起时的y坐标, 与mYDown一起用于滑动到底部时判断是上拉还是下拉
     */
    private int mLastY;
    /**
     * 是否在加载中 ( 上拉加载更多 )
     */
    private boolean isLoading = false;
    private boolean scrollFlag = false;// 标记是否滑动
    private int lastVisibleItemPosition;// 标记上次滑动位置
    private int measuredHeight;

    /**
     * @param context
     */
    public EditRefreshLayout(Context context) {
        this(context, null);
    }

    public EditRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        Log.d("dy", "mTouchSlop  =  " + mTouchSlop);
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null,
                false);
        mListViewFooter.measure(0, 0);
        measuredHeight = mListViewFooter.getMeasuredHeight();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 初始化ListView对象
        if (mListView == null) {
            getListView();
            // TODO: 2016/9/28  
//        mListView.addFooterView(mListViewFooter);
        }
    }

    /**
     * 获取ListView对象
     */
    private void getListView() {
        int childs = getChildCount();
        if (childs > 0) {
            View childView = getChildAt(0);
            if (childView instanceof ListView) {
                mListView = (ListView) childView;
                // 设置滚动监听器给ListView, 使得滚动的情况下也可以自动加载
                mListView.setOnScrollListener(this);
                Log.d(VIEW_LOG_TAG, "### 找到listview");
            }
        }
    }


    /**
     * 是否可以加载更多, 条件是到了最底部, listview不在加载中, 且为上拉操作.
     *
     * @return
     */
    private boolean canLoad() {
        return isBottom() && !isLoading && isPullUp();
    }

    /**
     * 判断是否到了最底部
     */
    private boolean isBottom() {

        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }
        return false;
    }

    /*
         * (non-Javadoc)
         * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
         */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                mYDown = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                // 移动
                mLastY = (int) event.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                // 抬起
                if (canLoad()) {
                    loadData();
                    Log.e("dy", "抬起");
                }
                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

//

    /**
     * 是否是上拉操作
     *
     * @return
     */
    private boolean isPullUp() {
        return (mYDown - mLastY) >= (mTouchSlop + 10);
    }

    /**
     * 如果到了最底部,而且是上拉操作.那么执行onLoad方法
     */
    private void loadData() {
        if (isLoading) return;

        if (mOnLoadListener != null) {
            // 设置状态
            setLoading(true);
            //
            mOnLoadListener.onLoad();
        }
    }

    /**
     * @param loading
     */
    public void setLoading(boolean loading) {
        isLoading = loading;
        if (isLoading) {
            mListView.addFooterView(mListViewFooter);
//            mListViewFooter.setPadding(0,0,0,0);
        } else {
            mListView.removeFooterView(mListViewFooter);
//            mListViewFooter.setPadding(0,-measuredHeight,0,0);
            mYDown = 0;
            mLastY = 0;
        }
    }

    /**
     * @param loadListener
     */
    public void setOnLoadListener(OnLoadListener loadListener) {
        mOnLoadListener = loadListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
// TODO Auto-generated method stub
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            scrollFlag = true;
        } else {
            scrollFlag = false;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        if (true) {
            if (firstVisibleItem > lastVisibleItemPosition) {
//                Log.e("dy", "上滑");
                // 滚动时到了最底部也可以加载更多
                if (canLoad()) {
                    loadData();
                }
            }
            if (firstVisibleItem < lastVisibleItemPosition) {
                Log.e("dy", "下滑");
            }
            if (firstVisibleItem == lastVisibleItemPosition) {
                return;
            }
            lastVisibleItemPosition = firstVisibleItem;
        }
    }


    /**
     * 加载更多的监听器
     *
     * @author mrsimple
     */
    public interface OnLoadListener {
        void onLoad();
    }
}