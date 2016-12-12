package com.luciferldy.someviews.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.luciferldy.someviews.R;

/**
 * Created by Lucifer on 2016/11/10.
 */

public class PolygonView extends View{
    private static final String LOG_TAG = PolygonView.class.getSimpleName();

    private int testPoint = 0;
    private int triggerRadius = 180;    // 触发半径为180px

    private Bitmap mBitmap;             // 要绘制的图片
    private Matrix mPolyMatrix;         // 测试setPolyToPoly用的Matrix

    private float[] src = new float[8];
    private float[] dst = new float[8];

    private Paint pointPaint;

    public PolygonView(Context context) {
        this(context, null);
    }

    public PolygonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PolygonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBitmapAndMatrix();
    }

    private void initBitmapAndMatrix() {
        mBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.doctor_strange);

        float[] temp = {0, 0,                                    // 左上
                mBitmap.getWidth(), 0,                          // 右上
                mBitmap.getWidth(), mBitmap.getHeight(),        // 右下
                0, mBitmap.getHeight()};                        // 左下
        src = temp.clone();
        dst = temp.clone();

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStrokeWidth(50);
        pointPaint.setColor(0xffd19165);
        pointPaint.setStrokeCap(Paint.Cap.ROUND);

        mPolyMatrix = new Matrix();
        mPolyMatrix.setPolyToPoly(src, 0, src, 0, 4);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(LOG_TAG, "onTouchEvent");

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                float tempX = event.getX();
                float tempY = event.getY();

                // 根据触控位置改变dst
                for (int i=0; i<testPoint*2; i+=2 ) {
                    if (Math.abs(tempX - dst[i]) <= triggerRadius && Math.abs(tempY - dst[i+1]) <= triggerRadius){
                        Log.d(LOG_TAG, "trigger.");
                        dst[i]   = tempX-100;
                        dst[i+1] = tempY-100;
                        break;  // 防止两个点的位置重合
                    }
                }

                resetPolyMatrix(testPoint);
                invalidate();
                break;
        }

        return true;
    }

    public void resetPolyMatrix(int pointCount){
        mPolyMatrix.reset();
        // 核心要点
        mPolyMatrix.setPolyToPoly(src, 0, dst, 0, pointCount);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(LOG_TAG, "onDraw");
        canvas.translate(100,100);

//        // 绘制坐标系
//        CanvasAidUtils.set2DAxisLength(900, 0, 1200, 0);
//        CanvasAidUtils.draw2DCoordinateSpace(canvas);

        // 根据Matrix绘制一个变换后的图片
        canvas.drawBitmap(mBitmap, mPolyMatrix, null);

        float[] dst = new float[8];
        mPolyMatrix.mapPoints(dst,src);

        // 绘制触控点
        Log.d(LOG_TAG, "testPoint = " + testPoint);
        for (int i = 0; i < testPoint * 2; i += 2 ) {
            Log.d(LOG_TAG, "dst[i] = " + dst[i] + ", dst[i+1] = " + dst[i+1]);
            canvas.drawPoint(dst[i], dst[i+1],pointPaint);
        }
    }

    public void setTestPoint(int testPoint) {
        Log.d(LOG_TAG, "setTestPoint " + testPoint);
        this.testPoint = testPoint > 4 || testPoint < 0 ? 4 : testPoint;
        dst = src.clone();
        resetPolyMatrix(this.testPoint);
        invalidate();
    }
}

