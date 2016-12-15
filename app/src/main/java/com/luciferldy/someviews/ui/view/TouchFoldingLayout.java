package com.luciferldy.someviews.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Lucifer on 2016/11/13.
 * E-mail: lian_dy@foxmail.com
 */

public class TouchFoldingLayout extends FoldingLayout {

    private static final String LOG_TAG = TouchFoldingLayout.class.getSimpleName();

    private GestureDetector mScrollGestureDetector;
    private int mTranslation = -1;

    public TouchFoldingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mTranslation == -1)
            mTranslation = getWidth();
        super.dispatchDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(LOG_TAG, "onTouchEvent event.X = " + event.getX());
        return mScrollGestureDetector.onTouchEvent(event);
    }

    private void init(Context context) {
        mScrollGestureDetector = new GestureDetector(context, new ScrollGestureDetectorListener());
    }

    class ScrollGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(LOG_TAG, "onScroll distanceX = " + distanceX + ", distanceY = " + distanceY);
            mTranslation -= distanceX;
            if (mTranslation < 0)
                mTranslation = 0;
            if (mTranslation > getWidth())
                mTranslation = getWidth();
            float ratio = Math.abs(((float) mTranslation) / ((float) getWidth()) );
            setFoldRatio(ratio);
            return true;
        }
    }
}
