package com.luciferldy.someviews.ui.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by Lucifer on 2016/12/24.
 */

public class ZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener{

    private static final String LOG_TAG = ZoomImageView.class.getSimpleName();

    // ------------------------放大或者缩小
    private boolean isInit = true;
    private float mInitScale;
    private float mMidScale;
    private float mMaxScale;
    private Matrix mScaleMatrix;
    // 捕获用户多点触控时缩放的比例
    private ScaleGestureDetector mScaleGestureDetector;

    // ----------------------自由移动
    // 记录上一次多点触控的数量
    private int mLastPointerCount;

    private float mLastX;
    private float mLastY;

    private int mTouchSlop;

    private boolean isCanDrag;

    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    // -----------------------双击放大与缩小
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;

    private AutoScaleRunnable mAutoScaleRunnable;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleMatrix = new Matrix();
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.i(LOG_TAG, "onDoubleTap");
                if (isAutoScale)
                    return true;
                float x = e.getX();
                float y = e.getY();

                if (mAutoScaleRunnable == null) {
                    mAutoScaleRunnable = new AutoScaleRunnable();
                }

                if (getScale() < mMidScale) {
//                    mScaleMatrix.postScale(mMidScale / getScale(), mMidScale / getScale(), x, y);
//                    setImageMatrix(mScaleMatrix);
                    mAutoScaleRunnable.config(mMidScale, x, y);
                    postDelayed(mAutoScaleRunnable, 16);
                    isAutoScale = true;
                } else {
//                    mScaleMatrix.postScale(mInitScale / getScale(), mInitScale / getScale(), x, y);
//                    setImageMatrix(mScaleMatrix);
                    mAutoScaleRunnable.config(mInitScale, x, y);
                    postDelayed(mAutoScaleRunnable, 16);
                    isAutoScale = true;
                }
                return true;
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (isInit) {
            int width = getWidth();
            int height = getHeight();

            Drawable d = getDrawable();
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            float scale = 1.0f;
            if (dw > width && dh < height) {
                scale = width * 1.0f / dw;
            }
            if (dh > height && dw < width) {
                scale = height / dh;
            }
            if (dh > height && dw > width) {
                scale = Math.min(height * 1.0f / dh, width * 1.0f / dw);
            }
            if (dh < height && dw < width) {
                scale = Math.min(height * 1.0f / dh, width * 1.0f / dw);
            }

            mInitScale = scale;
            mMidScale = mInitScale * 2;
            mMaxScale = mInitScale * 4;

            int dx = width / 2 - dw / 2;
            int dy = height / 2 - dh / 2;
            mScaleMatrix.postTranslate(dx, dy);
            mScaleMatrix.postScale(mInitScale, mInitScale, width * 0.5f, height * 0.5f);
            setImageMatrix(mScaleMatrix);
            isInit = false;
        }
    }

    /**
     * 获取当前图片的缩放值
     * @return
     */
    public float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * 缩放的区间 {@link #mInitScale} {@link #mMaxScale}
     * @param detector
     * @return
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        if (getDrawable() == null)
            return true;
        // 缩放范围的控制
        if ((scale < mMaxScale && scaleFactor > 1.0f)
                || (scale > mInitScale && scaleFactor < 1.0f)) {
            // 当缩放后的图片小于最小值时，将其设置为最小值
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale;
            }
            // 当图片放大后大于最大值时，将其设置为最大值
            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }

            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());

            checkBorderAndCenterWhenScale();

            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(LOG_TAG, "onTouch");
        if (mGestureDetector.onTouchEvent(event))
            return true;

        if (mScaleGestureDetector.onTouchEvent(event))
            return true;

        float x = 0;
        float y = 0;
        // 多点触控的数量
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }

        x /= pointerCount;
        y /= pointerCount;

        if (mLastPointerCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        mLastPointerCount = pointerCount;
        RectF rectF = getMatrixRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (rectF.width() > getWidth() + 0.01f
                        || rectF.height() > getHeight() + 0.01f) {
                    if (getParent() instanceof ViewPager)
                        getParent().requestDisallowInterceptTouchEvent(true);
                }
                 break;
            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > getWidth() + 0.01f
                        || rectF.height() > getHeight() + 0.01f) {
                    if (getParent() instanceof ViewPager)
                        getParent().requestDisallowInterceptTouchEvent(true);
                }
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }

                if (isCanDrag) {
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        // 如果宽度小于控件宽度，不允许横向移动
                        if (rectF.width() < getWidth()) {
                            dx = 0;
                        }
                        // 如果高度小于控件高度，不允许纵向移动
                        if (rectF.height() < getHeight()) {
                            dy = 0;
                        }
                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 在缩放的时候进行边界控制
     */
    private void checkBorderAndCenterWhenScale() {
        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 水平方向的控制
        if (rectF.width() >= width) {
            if (rectF.left > 0) {
                deltaX = -rectF.left;
            }

            if (rectF.right < width) {
                deltaX = width - rectF.right;
            }
        }
        // 竖直方向的控制
        if (rectF.height() >= height) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            }

            if (rectF.bottom < height) {
                deltaY = height - rectF.bottom;
            }
        }

        // 如果宽度或者高度小于控件的宽高，则让其居中
        if (rectF.width() < width) {
            deltaX = width / 2 - rectF.right + rectF.width() / 2;
        }

        if (rectF.height() < height) {
            deltaY = height / 2 - rectF.bottom + rectF.height() / 2;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 当移动时控制边界
     */
    public void checkBorderWhenTranslate() {
        RectF rectF = getMatrixRectF();

        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height= getHeight();

        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltaY = -rectF.top;
        }
        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltaY = height - rectF.bottom;
        }
        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = - rectF.left;
        }
        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 获得图片方法或缩小以后的宽和高，以及 left, top, right, bottom
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rectF = new RectF();

        Drawable d = getDrawable();
        if (d != null) {
            // 都是基于原图的一种转换，使用当前矩阵的缩放和平移的量对原图进行修改，得到现在的图的 RectF
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//            Log.i(LOG_TAG, "rectF before, " + rectF.toString());
            matrix.mapRect(rectF);
//            Log.i(LOG_TAG, "rectF after, " + rectF.toString());
        }

        return rectF;
    }

    /**
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }

    /**
     * 缩放类
     */
    class AutoScaleRunnable implements Runnable {

        // 缩放的目标值
        private float mTargetScale;
        // 缩放的中心点
        private float x;
        private float y;

        private final float BIGGER = 1.07f;
        private final float SMALLER = 0.93f;

        private float tmpScale;
        private float currentScale;

        public AutoScaleRunnable() {

        }

        public void config(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;

            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            }
            if (getScale() > mTargetScale) {
                tmpScale = SMALLER;
            }
        }

        @Override
        public void run() {
            Log.i(LOG_TAG, "AutoScaleRunnable run");
            // 进行缩放
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);

            currentScale = getScale();
            if ((tmpScale > 1.0f && currentScale < mTargetScale)
                    || (tmpScale < 1.0f && currentScale > mTargetScale)) {
                postDelayed(this, 16);
            } else {
                // 设置为我们的坐标值
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);

                isAutoScale = false;
            }
        }
    }
}
