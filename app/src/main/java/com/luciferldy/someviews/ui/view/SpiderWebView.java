package com.luciferldy.someviews.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.luciferldy.someviews.R;

/**
 * Created by lian_ on 2016/10/17.
 */

public class SpiderWebView extends View {

    private static final String LOG_TAG = SpiderWebView.class.getSimpleName();

    private int count = 6;
    private float angle = (float) (Math.PI * 2 / count);
    private float radius; // 网格的最大半径
    private int centerX;
    private int centerY;
    private String[] titles = {"a", "b", "c", "d", "e", "f"};
    private double[] data = {100, 60, 60, 60, 100, 50, 10, 20};
    private float maxValue = 100; // 数据最大值
    private Paint mainPaint; // 蛛网区画笔
    private Paint valuePaint; // 数据区画笔
    private Paint textPaint; // 文本画笔

    public SpiderWebView(Context context) {
        super(context);
        Log.i(LOG_TAG, "constructor with one argument.");
    }

    public SpiderWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(LOG_TAG, "constructor with two arguments.");
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpiderWebView, 0, 0);
        try {
            maxValue = a.getInt(R.styleable.SpiderWebView_maxValue, 100);
            int mainColor = a.getColor(R.styleable.SpiderWebView_mainPaintColor, getResources().getColor(android.R.color.darker_gray));
            mainPaint = new Paint();
            mainPaint.setColor(mainColor);
            int valueColor = a.getColor(R.styleable.SpiderWebView_valuePaintColor, getResources().getColor(android.R.color.holo_blue_dark));
            valuePaint = new Paint();
            valuePaint.setColor(valueColor);
            int textColor = a.getColor(R.styleable.SpiderWebView_textPaintColor, getResources().getColor(android.R.color.black));
            textPaint = new Paint();
            textPaint.setColor(textColor);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.i(LOG_TAG, "onSizeChanged w=" + w + ", h=" + h);
        radius = Math.min(h, w) / 2 * 0.9f;
        centerX = w / 2;
        centerY = h / 2;

        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(LOG_TAG, "onDraw");
        super.onDraw(canvas);
        if (centerX > 0 && centerY > 0) {
            drawPolygon(canvas);
            drawLines(canvas);
            drawTexts(canvas);
            drawRegion(canvas);
        }
    }

    /**
     * 绘制多边形
     * @param canvas 画布
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        mainPaint.setStyle(Paint.Style.STROKE);
        mainPaint.setStrokeWidth(3);
        float r = radius / (count - 1); // 蛛网之间的间距
        for (int i = 1; i < count; i++) {
            float curR = r * i; // 当前半径
            path.reset();
            for (int j = 0; j < count; j++) {
                if (j == 0) {
                    path.moveTo(centerX + curR, centerY);
                } else {
                    //
                    float x = (float) (centerX + curR * Math.cos(angle * j));
                    float y = (float) (centerY + curR * Math.sin(angle * j));
                    path.lineTo(x, y);
                }
            }
            path.close(); // 封闭
            canvas.drawPath(path, mainPaint);
        }
    }

    /**
     * 绘制直线
     * @param canvas
     */
    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < count; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            float x = (float) (centerX + radius * Math.cos(angle * i));
            float y = (float) (centerX + radius * Math.sin(angle * i));
            path.lineTo(x, y);
            canvas.drawPath(path, mainPaint);
        }
    }

    /**
     * 绘制文本
     * @param canvas
     */
    private void drawTexts(Canvas canvas) {
        textPaint.setTextSize(36);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        for (int i = 0; i < count; i++) {
            float x = (float) (centerX + (radius + fontHeight / 2) * Math.cos(angle * i));
            float y = (float) (centerY + (radius + fontHeight / 2) * Math.sin(angle * i));
            float curAngle = angle * i;
            if (curAngle >= 0 && curAngle < Math.PI / 2) {
                canvas.drawText(titles[i], x, y, textPaint);
            } else if (curAngle >= Math.PI * 3 / 2 && curAngle <= Math.PI * 2) {
                canvas.drawText(titles[i], x, y, textPaint);
            } else if (curAngle >= Math.PI / 2 && curAngle < Math.PI) {
                float width = textPaint.measureText(titles[i]);
                canvas.drawText(titles[i], x - width, y, textPaint);
            } else if (curAngle >= Math.PI && curAngle < Math.PI * 3 / 2) {
                float width = textPaint.measureText(titles[i]);
                canvas.drawText(titles[i], x - width, y, textPaint);
            } else {
                Log.e(LOG_TAG, "drawTexts occur error.");
            }
        }
    }

    /**
     * 使 Path 被包围区域被填充
     * @param canvas
     */
    private void drawRegion(Canvas canvas) {
        Path path = new Path();
        valuePaint.setAlpha(255);
        for (int i = 0; i < count; i++) {
            double percent = data[i] / maxValue;
            float x = (float) (centerX + radius * Math.cos(angle * i) * percent);
            float y = (float) (centerY + radius * Math.sin(angle * i) * percent);
            if (i == 0) {
                path.moveTo(x, centerY);
            } else {
                path.lineTo(x, y);
            }
            // 绘制小圆点
            canvas.drawCircle(x, y, 5, valuePaint);
        }
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, valuePaint);
        valuePaint.setAlpha(127);
        // 绘制填充区域
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);

    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public void setData(double[] data) {
        this.data = data;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public void setMainPaintColor(@ColorInt int color) {
        mainPaint.setColor(color);
    }

    public void setTextPainColor(@ColorInt int color) {
        textPaint.setColor(color);
    }

    public void setValuePaintColor(@ColorInt int color) {
        valuePaint.setColor(color);
    }
}
