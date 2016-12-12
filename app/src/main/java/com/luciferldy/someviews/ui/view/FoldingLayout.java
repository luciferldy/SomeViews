package com.luciferldy.someviews.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Lucifer on 2016/11/12.
 */

public class FoldingLayout extends ViewGroup {

    private static final String LOG_TAG = FoldingLayout.class.getSimpleName();
    private static final int NUM_OF_POINT = 8;

    // 图片折叠后的宽度
    private int mFoldedWith;
    // 折叠后总宽度与原图宽度的比例
    private float mFoldedRatio = 0.4f;
    // 折叠的个数
    private int mNumOfFold = 8;
    private Matrix[] mMatrices = new Matrix[NUM_OF_POINT];

    private Bitmap mBitmap;

    private Paint mSolidPaint;
    private Paint mShadowPaint;
    private Matrix mShadowMatrix;
    private LinearGradient mShadowGradient;

    // 折叠前每块的宽度
    private int mSrcWidth;
    // 折叠后每块的宽度
    private int mDstWidth;

    private Canvas mCanvas = new Canvas();
    private boolean isReady;

    public FoldingLayout(Context context) {
        super(context, null);
    }

    public FoldingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        this.setWillNotDraw(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View child = getChildAt(0);
        measureChild(child, widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(child.getMeasuredWidth(), child.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(LOG_TAG, "onLayout");
        View child = getChildAt(0);
        child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBitmap);
        updateFold();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Log.d(LOG_TAG, "dispatchDraw");
        if (mFoldedRatio == 0)
            return;
        if (mFoldedRatio == 1) {
            super.dispatchDraw(canvas);
            return;
        }
        for (int i = 0; i < mNumOfFold; i++) {
            canvas.save();

            canvas.concat(mMatrices[i]);
            canvas.clipRect(mSrcWidth * i, 0, mSrcWidth * i + mSrcWidth, getHeight());
            if (isReady) {
                canvas.drawBitmap(mBitmap, 0, 0, null);
            } else {
                super.dispatchDraw(mCanvas);
                canvas.drawBitmap(mBitmap, 0, 0, null);
                isReady = true;
            }
            canvas.translate(mSrcWidth * i, 0);
            if (i % 2 == 0) {
                canvas.drawRect(0, 0, mSrcWidth, getHeight(), mSolidPaint);
            } else {
                canvas.drawRect(0, 0, mSrcWidth, getHeight(), mShadowPaint);
            }
            canvas.restore();
        }
    }

    private void init() {
        // init bitmap
//        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lolita);
        // 折叠后的总宽度
//        mFoldedWith = (int) (mBitmap.getWidth() * mFoldedRatio);
        // 原图每块的宽度
//        mSrcWidth = mBitmap.getWidth() / mNumOfFold;
//        mDstWidth = mFoldedWith / mNumOfFold;

        // init matrices
        for (int i = 0; i < mNumOfFold; i++) {
            mMatrices[i] = new Matrix();
        }

        mSolidPaint = new Paint();

        mShadowPaint = new Paint();
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowGradient = new LinearGradient(0, 0, 0.5f, 0,
                Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP);
        mShadowPaint.setShader(mShadowGradient);
        mShadowMatrix = new Matrix();
    }

    private void updateFold() {
        int w = getMeasuredWidth();
        int h = getMeasuredHeight();

        mFoldedWith = (int) (w * mFoldedRatio);
        mSrcWidth = w / mNumOfFold;
        mDstWidth = mFoldedWith / mNumOfFold;

        int alpha = (int) ( 255 * (1 - mFoldedRatio));
        mSolidPaint.setColor(Color.argb((int) (alpha * 0.8f), 0, 0, 0));

        mShadowMatrix.setScale(mSrcWidth, 1);
        mShadowGradient.setLocalMatrix(mShadowMatrix);
        mShadowPaint.setAlpha(alpha);

        // 计算纵轴减小的高度
        int height = (int) Math.sqrt(mSrcWidth * mSrcWidth - mDstWidth * mDstWidth);

        // 转换点
        float[] src = new float[NUM_OF_POINT];
        float[] dst = new float[NUM_OF_POINT];

        // 计算点的位置
        for (int i = 0; i < mNumOfFold; i++) {
            mMatrices[i].reset();
            // 代表矩形的4个顶点
            // src 选取的原图形的4个顶点对应的切图
            src[0] = i * mSrcWidth;
            src[1] = 0;
            src[2] = src[0] + mSrcWidth;
            src[3] = 0;
            src[4] = src[2];
            src[5] = h;
            src[6] = src[0];
            src[7] = src[5];

            boolean isEven = i % 2 == 0;
            // 代表多边形的4个顶点
            dst[0] = i * mDstWidth;
            dst[1] = isEven ? 0 : height;
            dst[2] = dst[0] + mDstWidth;
            dst[3] = isEven ? height : 0;
            dst[4] = dst[2];
            dst[5] = isEven ? h - height : h;
            dst[6] = dst[0];
            dst[7] = isEven ? h : h - height;

//            for (int j = 0; j < 8; j++) {
//                dst[j] = Math.round(dst[j]); // 四舍五入
//            }

            mMatrices[i].setPolyToPoly(src, 0, dst, 0, src.length >> 1);
        }
    }

    public void setFoldRatio(float ratio) {
        if (ratio >= 0 && ratio <= 1) {
            Log.d(LOG_TAG, "setFoldRatio " + ratio);
            this.mFoldedRatio = ratio;
            updateFold();
            invalidate();
        }
    }

    public float getFoldRatio() {
        return this.mFoldedRatio;
    }
}
