package com.luciferldy.someviews.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by lian_ on 2016/10/20.
 */

public class SearchView extends View {

    private static final String LOG_TAG = SearchView.class.getSimpleName();

    private static final int NONE = 1;
    private static final int STARTING = 2;
    private static final int SEARCHING = 3;
    private static final int ENDING = 4;

    @IntDef({NONE, STARTING, SEARCHING, ENDING})
    public @interface Status{}
    @Status
    private int mCurrentState = NONE;

    private int defaultDuration = 2000;
    private float mAnimatorValue;

    private Paint mMainPaint;

    private Path mSearchPath;
    private Path mCirclePath;
    private PathMeasure mMeasure;

    private int centerX;
    private int centerY;

    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    private ValueAnimator mStartAnimator;
    private ValueAnimator mSearchAnimator;
    private ValueAnimator mEndAnimator;
    private Handler mAnimatorHandler;

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initPaint();
        initPath();
        initListener();
        initAnimator();
        initHandler();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSearch(canvas);
    }

    private void initPaint() {
        mMainPaint = new Paint();
        mMainPaint.setStyle(Paint.Style.STROKE);
        mMainPaint.setStrokeWidth(6);
        mMainPaint.setColor(getResources().getColor(android.R.color.white));
    }

    private void initPath() {
        mSearchPath = new Path();
        mCirclePath = new Path();

        mMeasure = new PathMeasure();

        RectF ovalSearch = new RectF(-50, -50, 50, 50);
        mSearchPath.addArc(ovalSearch, 45, -359.9f);

        RectF ovalCircle = new RectF(-100, -100, 100, 100);
        mCirclePath.addArc(ovalCircle, 45, -359.9f);

        float[] pos = new float[2];
        mMeasure.setPath(mCirclePath, false);
        mMeasure.getPosTan(0, pos, null);

        mSearchPath.lineTo(pos[0], pos[1]);
    }

    private void initAnimator() {
        mStartAnimator = ValueAnimator.ofFloat(0, 1f).setDuration(defaultDuration);
        mSearchAnimator = ValueAnimator.ofFloat(0, 1f).setDuration(defaultDuration);
        mEndAnimator = ValueAnimator.ofFloat(0, 1f).setDuration(defaultDuration);

        mStartAnimator.addUpdateListener(mUpdateListener);
        mSearchAnimator.addUpdateListener(mUpdateListener);
        mEndAnimator.addUpdateListener(mUpdateListener);

        mStartAnimator.addListener(mAnimatorListener);
        mSearchAnimator.addListener(mAnimatorListener);
        mEndAnimator.addListener(mAnimatorListener);
    }

    private boolean isSearchOver = false;
    private void initHandler() {
        mAnimatorHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (mCurrentState) {
                    case STARTING:
                        // 从开始动画转到搜索动画
                        isSearchOver = false;
                        mCurrentState = SEARCHING;
                        mSearchAnimator.start();
                        break;
                    case SEARCHING:
                        if (!isSearchOver) {
                            mSearchAnimator.start();
                            Log.d(LOG_TAG, "re searching.");
                        } else {
                            mCurrentState = ENDING;
                            mEndAnimator.start();
                        }
                        break;
                    case ENDING:
                        // 从结束动画到无
                        mCurrentState = NONE;
                        break;
                }
            }
        };
    }

    /**
     * 绘制 canvas
     * @param canvas
     */
    private void drawSearch(Canvas canvas) {
        canvas.translate(centerX, centerY);
        canvas.drawColor(getResources().getColor(android.R.color.holo_blue_light));

        switch (mCurrentState) {
            case NONE:
                canvas.drawPath(mSearchPath, mMainPaint);
                break;
            case STARTING:
                mMeasure.setPath(mSearchPath, false);
                Path dst = new Path();
                mMeasure.getSegment(mMeasure.getLength() * mAnimatorValue, mMeasure.getLength(), dst, true);
                canvas.drawPath(dst, mMainPaint);
                break;
            case SEARCHING:
                mMeasure.setPath(mCirclePath, false);
                Path dst2 = new Path();
                float stop = mMeasure.getLength() * mAnimatorValue;
                float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * 200f));
                mMeasure.getSegment(start, stop, dst2, true);
                canvas.drawPath(dst2, mMainPaint);
                break;
            case ENDING:
                mMeasure.setPath(mSearchPath, false);
                Path dst3 = new Path();
                mMeasure.getSegment(mMeasure.getLength() * (1 - mAnimatorValue), mMeasure.getLength(), dst3, true);
                canvas.drawPath(dst3, mMainPaint);
                break;
        }

    }

    /**
     * 初始化 listener
     */
    private void initListener() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        };

        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // getHandler 发消息通知状态更新
                mAnimatorHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    /**
     * 开始搜索
     */
    public void startSearch() {
        // 开始进入动画
        mCurrentState = STARTING;
        mStartAnimator.start();
    }

    /**
     * 停止搜索
     */
    public void stopSearch() {
        // 保证下一次结束旋转
        isSearchOver = true;
    }
 }
