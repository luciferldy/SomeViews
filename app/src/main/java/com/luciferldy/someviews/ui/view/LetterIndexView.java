package com.luciferldy.someviews.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.luciferldy.someviews.R;

/**
 * Created by Lucifer on 2016/11/6.
 * lian_dy@foxmail.com
 */

public class LetterIndexView extends View implements View.OnTouchListener, View.OnClickListener{

    private static final String LOG_TAG = LetterIndexView.class.getSimpleName();
    private static final String[] mNumbers = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private int width;
    private int height;
    private int distance;
    private boolean isActionDown = false;

    private int mTextSize = 36;
    private int mLastPos = 0;
    private int mCurrentPos = 0;

    private Canvas mCanvas;
    private ClickArea mClickArea;

    public LetterIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        distance = h / mNumbers.length;
        Log.d(LOG_TAG, "width = " + width + ", height = " + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
    }

    private int mIndex;
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d(LOG_TAG, "onTouch distance = " + distance + ", " + motionEvent.getY());
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isActionDown = true;
                invalidate();
                mClickArea.show();
                break;
            case MotionEvent.ACTION_MOVE:

                if (motionEvent.getY() > height) {
                    mIndex = height;
                } else if (motionEvent.getY() < 0) {
                    mIndex = 0;
                } else {
                    mIndex = (int) motionEvent.getY();
                }
                mIndex = mIndex / distance;
                if (mIndex == mNumbers.length)
                    mIndex--;
                mClickArea.clickPosition(mIndex, mNumbers[mIndex]);
                break;
            case MotionEvent.ACTION_UP:
                isActionDown = false;
                invalidate();
                mClickArea.hide();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
    }

    /**
     * 绘制文字
     * @param canvas 画布
     */
    private void drawText(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(getResources().getColor(android.R.color.black));
        textPaint.setAntiAlias(true); // 开启抗锯齿
        textPaint.setTextSize(mTextSize);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float bh, bw;
        for (int i = 0; i < mNumbers.length; i++) {
            // 通过设置 baseline 让文字水平和垂直居中
            // drawText 绘制出来的 text 由 baseline, descent, ascent, top and bottom 共同控制
            // 可以使用 bottom - top 也可以使用 descent - ascent 来获得文字的高度
            // 文字的宽度使用 paint.measureText() 计算
            bw = width / 2 - textPaint.measureText(mNumbers[i]) / 2;
            bh = distance / 2 + (fm.descent - fm.ascent) / 2 + distance * i;
            canvas.drawText(mNumbers[i], bw, bh, textPaint);
        }

        if (isActionDown) {
            Paint coverPaint = new Paint();
            coverPaint.setColor(getResources().getColor(R.color.black_trans));
            canvas.drawRect(0, 0, width, height, coverPaint);
        }
    }

    public void moveTo(int position) {
    }

    public void setClickArea(ClickArea area) {
        this.mClickArea = area;
    }

    /**
     * 当 click 或者 touch 事件触发时，返回位置
     */
    public interface ClickArea {
        void clickPosition(int position, String value);
        void show();
        void hide();
    }
}
