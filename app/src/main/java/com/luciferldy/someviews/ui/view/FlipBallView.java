package com.luciferldy.someviews.ui.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.luciferldy.someviews.R;

/**
 * Created by Lucifer on 2016/10/28.
 * 尝试制作一个进度条，两个小球围绕中心旋转
 */

public class FlipBallView extends View {

    private static final String LOG_TAG = FlipBallView.class.getSimpleName();
    private static final int IDLE = 1; // 小球空闲
    private static final int ROTATE = 2; // 小球旋转

//    @IntDef({IDLE, ROTATE})
//    private @interface STATUS{}
//    @STATUS
//    private int mStatus = IDLE;
    private boolean isRotateEnd = false;

    private Paint mBluePaint;
    private Paint mOrangePaint;
    private int width;
    private int height;
    private float maxRadius;
    private float minRadius;
    private float distance;
    private float bluePos;
    private float orangePos;
    private float blueRadius;
    private float orangeRadius;
    private ValueAnimator animator;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case IDLE:
//                    mStatus = ROTATE;
//                    isRotateEnd = false;
//                    animator.start();
//                    break;
//                case ROTATE:
//                    if (!isRotateEnd) {
//                        animator.start();
//                    } else {
//                        mStatus = IDLE;
//                        Log.d(TAG, "handle rotate isRotateEnd" + isRotateEnd);
//                    }
//                    break;
//            }
            // 实际上使用一个布尔变量即可控
            if (!isRotateEnd)
                animator.start();
        }
    };

    public FlipBallView(Context context) {
        super(context, null);
    }

    public FlipBallView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FlipBallView, 0, 0);
        try {
            maxRadius = a.getDimensionPixelOffset(R.styleable.FlipBallView_maxRadius, 24);
            minRadius = a.getDimensionPixelOffset(R.styleable.FlipBallView_minRadius, 15);
            distance = a.getDimensionPixelOffset(R.styleable.FlipBallView_distance, 60);
        } finally {
            a.recycle();
        }

        bluePos = -distance;
        orangePos = distance;
        blueRadius = (maxRadius + minRadius) / 2;
        orangeRadius = (maxRadius + minRadius) / 2;

        initPaint();
        initAnimator();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBall(canvas);
    }

    public void startAnim() {
//        mStatus = IDLE;
//        handler.sendEmptyMessage(mStatus);
        isRotateEnd  = false;
        animator.start();
    }

    public void stopAnim() {
        Log.d(LOG_TAG, "stopAnim");
        isRotateEnd = true;
    }

    private void drawBall(Canvas canvas) {
        canvas.drawCircle(width / 2 + bluePos, height / 2, blueRadius, mBluePaint);
        canvas.drawCircle(width / 2 + orangePos, height / 2, orangeRadius, mOrangePaint);
    }

    private void initPaint() {
        mBluePaint = new Paint();
        mBluePaint.setStyle(Paint.Style.FILL);
        mBluePaint.setAntiAlias(true);
        mBluePaint.setColor(getResources().getColor(android.R.color.holo_blue_light));


        mOrangePaint = new Paint();
        mOrangePaint.setStyle(Paint.Style.FILL);
        mOrangePaint.setAntiAlias(true);
        mOrangePaint.setColor(getResources().getColor(android.R.color.holo_orange_light));
    }

    /**
     * instance PropertyAnimation
     */
    private void initAnimator() {
        animator = ValueAnimator.ofFloat(0, (float) ( 2 * Math.PI)).setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float f = (float) valueAnimator.getAnimatedValue();
                blueRadius = (float) ((maxRadius - minRadius) / 2 * Math.sin(f) + (maxRadius + minRadius) / 2);
                bluePos = (float) (-distance * Math.sin(f + Math.PI / 2));
                orangeRadius = (float) ((maxRadius - minRadius) / 2 * Math.sin(f) + (maxRadius + minRadius) / 2);
                orangePos = (float) (distance * Math.sin(f + Math.PI / 2));
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                handler.sendEmptyMessage(mStatus);
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

    }
}
