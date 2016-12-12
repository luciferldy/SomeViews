package com.luciferldy.someviews.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luciferldy.someviews.ui.view.SlideTrackView;

/**
 * Created by Lucifer on 2016/12/8.
 */

public class SlideTrackFragment extends BaseFragment {

    private static final String LOG_TAG = SlideTrackFragment.class.getSimpleName();
    public static final String TAG = SlideTrackView.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new SlideTrackView(getContext());
    }
}
