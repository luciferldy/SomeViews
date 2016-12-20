package com.luciferldy.someviews.ui.view;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Lucifer on 2016/12/1.
 */

public class DragLayout extends ViewGroup {

    private ViewDragHelper mDragHelper;
    private GestureDetectorCompat mGestureDetector;

    private View page_one, page_two;
    private int viewHeight;
    private static final int SPEED_THRESHOLD = 100;  // 滑动速度的阈值
    private static final int DISTANCE_THRESHOLD = 100;  //
    private int distanceTop;
    private ShowNextPageListener mNextPageListener;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        page_one.layout(i, 0, i2, i3 - i1);
        page_two.layout(i, 0, i2, i3 - i1);

        viewHeight = page_one.getMeasuredHeight();
        page_two.offsetTopAndBottom(viewHeight);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        page_one = getChildAt(0);
        page_two = getChildAt(1);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {

        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (page_one.getBottom() > 0 && page_one.getTop() < 0) {
            // 当 page one 在执行动画中的时候，不处理 touch 事件
            return false;
        }
        boolean yScroll = mGestureDetector.onTouchEvent(ev);
        boolean shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);
        int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            //
            mDragHelper.processTouchEvent(ev);
            distanceTop = page_one.getTop();
        }

        return shouldIntercept && yScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 统一交给 mDragHelper 处理，由 ViewDragHelper 实现拖动效果
        mDragHelper.processTouchEvent(event);
        return true;
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 10f, new ViewDragHelper.Callback() {

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                int childIndex = 1;
                if (changedView == page_two) {
                    childIndex = 2;
                }

                // 一个 view 的位置改变，另一个 view 的位置也要跟进
                onViewPosChanged(childIndex, top);
            }

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                // 这个用来控制拖拽过程中松手后，自动滑行的速度，暂时给一个随意的数值
                return 1;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                // 滑动松开后，需要向上或者向下粘到特定的位置
                animTopOrBottom(releasedChild, yvel);
            }

            /**
             * 滑动时 view 位置改变的协调处理
             * @param viewIndex
             * @param posTop
             */
            private void onViewPosChanged(int viewIndex, int posTop) {
                int offsetTopBottom;
                if (viewIndex == 1) {
                    offsetTopBottom = viewHeight + page_one.getTop() - page_two.getTop();
                    page_two.offsetTopAndBottom(offsetTopBottom);
                } else if (viewIndex == 2) {
                    offsetTopBottom = page_two.getTop() - viewHeight - page_one.getTop();
                    page_one.offsetTopAndBottom(offsetTopBottom);
                }

                invalidate();
            }

            private void animTopOrBottom(View releaseChild, float yvel) {
                int finalTop = 0;
                if (releaseChild == page_one) {
                    // 拖动第一个 view 松手
                    if (yvel < -SPEED_THRESHOLD || (distanceTop == 0 && page_one.getTop() < - DISTANCE_THRESHOLD)) {
                        finalTop = -viewHeight;

                        // 下一页可以进行初始化
                        if (null != mNextPageListener) {
                            mNextPageListener.onDragNext();
                        }
                    }
                } else {
                        //  拖动第二个 view 松手
                        if (yvel > SPEED_THRESHOLD || (distanceTop == -viewHeight && releaseChild.getTop() > DISTANCE_THRESHOLD)) {
                            // 保持不动
                            finalTop = viewHeight;
                        }
                    }

                if (mDragHelper.smoothSlideViewTo(releaseChild, 0, finalTop)) {
                    ViewCompat.postInvalidateOnAnimation(DragLayout.this);
                }
            }


        });
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM);
        mGestureDetector = new GestureDetectorCompat(getContext(), new YScrollDetector());
    }

    /**
     * view 的一个方法，不支持 android 2.2, 2.3
     * @param size
     * @param measureSpec
     * @param childMeasuredState
     * @return
     */
    public static int resolveSizeAndState(int size, int measureSpec, int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result | (childMeasuredState & MEASURED_STATE_MASK);
    }

    public interface ShowNextPageListener {
        public void onDragNext();
    }

    public void setNextPageListener(ShowNextPageListener listener) {
        this.mNextPageListener = listener;
    }

    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return Math.abs(distanceY) > Math.abs(distanceX);
        }
    }
}
