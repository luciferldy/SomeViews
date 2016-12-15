package com.luciferldy.someviews.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.luciferldy.someviews.R;

/**
 * Created by Lucifer on 2016/12/8.
 */

public class RoundedImageFragment extends BaseFragment {

    public static final String TAG = RoundedImageFragment.class.getSimpleName();

    private ImageView mIvClip;
    private ImageView mIvXfermode;
    private ImageView mIvRoundedBmp;
    private int mImageWidth;
    private int mRadius;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_rounded_image, container, false);
        mIvClip = (ImageView) root.findViewById(R.id.iv_clip);
        mIvXfermode = (ImageView) root.findViewById(R.id.iv_xfermode);
        mIvRoundedBmp = (ImageView) root.findViewById(R.id.iv_rounded_bitmap);

        mImageWidth = getResources().getDimensionPixelOffset(R.dimen.image_width);
        mRadius = getResources().getDimensionPixelOffset(R.dimen.image_radius);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        new XfermodeAsyncTask().execute();
        new ClipAsyncTask().execute();
        new RoundedBmpAsyncTask().execute();
    }

    class XfermodeAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mIvXfermode.setImageBitmap(bitmap);
        }


        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap result = Bitmap.createBitmap(mImageWidth, mImageWidth, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.doctor_strange);
            final RectF rectF = new RectF(0, 0, mImageWidth, mImageWidth);

            // 此处借鉴 ImageView 设置 scaleType 为 CenterCrop 的属性时对图片的裁剪
            float dwidth = bitmap.getWidth();
            float dheight = bitmap.getHeight();
            float vwidth = mImageWidth;
            float vheight = mImageWidth;

            float scale = 0;
            int dx = 0, dy = 0; // 表示画布的偏移距离

            Rect rect;
            if (dwidth * vheight > vwidth * dheight) {
                // bitmap 的宽高比比 view 的大
                scale = vwidth / vheight * dheight / dwidth;
                dx = (int) ((dwidth - dwidth * scale) * 0.5f);
                rect = new Rect(dx, 0, (int) (dwidth - dx), (int) dheight);
            } else {
                // bitmap 的宽高比比 view 的小
                scale = vheight / vwidth * dwidth / dheight;
                dy = (int) ((dheight - dheight * scale) * 0.5f);
                rect = new Rect(0, dy, (int) dwidth, (int) (dheight - dy));
            }

            Paint paint = new Paint();
            paint.setAntiAlias(true);
//            paint.setFilterBitmap(true);

            paint.setXfermode(null);
            canvas.drawRoundRect(rectF, mRadius, mRadius, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            // if the rect is not null, is will draw subset of the bitmap
            canvas.drawBitmap(bitmap, rect, rectF, paint);
            return result;
        }
    }

    class ClipAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mIvClip.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap result = Bitmap.createBitmap(mImageWidth, mImageWidth, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.doctor_strange);

            final RectF rectF = new RectF(0, 0, mImageWidth, mImageWidth);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
//            paint.setFilterBitmap(true);


            // 此处借鉴 ImageView 设置 scaleType 为 CenterCrop 的属性时对图片的裁剪
            float dwidth = bitmap.getWidth();
            float dheight = bitmap.getHeight();
            float vwidth = mImageWidth;
            float vheight = mImageWidth;

            float scale = 0;
            int dx = 0, dy = 0; // 表示画布的偏移距离

            Rect rect;
            if (dwidth * vheight > vwidth * dheight) {
                // bitmap 的宽高比比 view 的大
                scale = vwidth / vheight * dheight / dwidth;
                dx = (int) ((dwidth - dwidth * scale) * 0.5f);
                rect = new Rect(dx, 0, (int) (dwidth - dx), (int) dheight);
            } else {
                // bitmap 的宽高比比 view 的小
                scale = vheight / vwidth * dwidth / dheight;
                dy = (int) ((dheight - dheight * scale) * 0.5f);
                rect = new Rect(0, dy, (int) dwidth, (int) (dheight - dy));
            }

            Path path = new Path();
            path.addRoundRect(rectF, mRadius, mRadius, Path.Direction.CW);
            canvas.clipPath(path, Region.Op.INTERSECT);

            // 此时的 Bitmap 会被压缩
            canvas.drawBitmap(bitmap, rect, rectF, paint);
            return result;
        }
    }


    class RoundedBmpAsyncTask extends AsyncTask<Void, Void, Drawable> {
        @Override
        protected void onPostExecute(Drawable drawable) {
            mIvRoundedBmp.setImageDrawable(drawable);
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.doctor_strange));
            drawable.setCornerRadius(12);
            return drawable;
        }
    }
}
