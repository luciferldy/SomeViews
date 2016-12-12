package com.luciferldy.someviews.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Lucifer on 2016/11/13.
 */

public class SlideTrackView extends View {

    private static final String LOG_TAG = SlideTrackView.class.getSimpleName();
    private Paint mPaint;
    private Path mPath;

    public SlideTrackView(Context context) {
        this(context, null);
    }

    public SlideTrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(15);
        mPaint.setColor(Color.RED);
        mPath = new Path();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }

    private void drawPoint() {

    }
}
