package com.luciferldy.someviews.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.luciferldy.someviews.R;
import com.luciferldy.someviews.ui.view.ZoomImageView;

/**
 * Created by Lucifer on 2016/12/24.
 */

public class ZoomImageFragment extends BaseFragment {

    private ViewPager mVp;
    private int[] mImgs = new int[]{
            R.drawable.doctor_strange,
            R.drawable.biubiubiu,
            R.drawable.biabiabia
    };
    private ImageView[] mIvs = new ImageView[mImgs.length];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_zoom_iv, container, false);
        mVp = (ViewPager) root.findViewById(R.id.view_pager);
        mVp.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ZoomImageView iv = new ZoomImageView(getContext());
                iv.setImageResource(mImgs[position]);
                iv.setScaleType(ImageView.ScaleType.MATRIX);
                container.addView(iv);
                mIvs[position] = iv;
                return iv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mIvs[position]);
            }

            @Override
            public int getCount() {
                return mImgs.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        return root;
    }
}
