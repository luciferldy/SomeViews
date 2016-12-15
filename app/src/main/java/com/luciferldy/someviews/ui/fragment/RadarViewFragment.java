package com.luciferldy.someviews.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luciferldy.someviews.R;
import com.luciferldy.someviews.ui.view.RadarView;

/**
 * Created by Lucifer on 2016/12/14.
 */

public class RadarViewFragment extends BaseFragment {

    public static final String TAG = RadarView.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_radarview, container, false);
    }
}
