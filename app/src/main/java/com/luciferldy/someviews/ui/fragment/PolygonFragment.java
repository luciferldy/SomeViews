package com.luciferldy.someviews.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.luciferldy.someviews.R;
import com.luciferldy.someviews.ui.view.PolygonView;

/**
 * Created by Lucifer on 2016/12/8.
 * 可拖拽的多边形
 */

public class PolygonFragment extends BaseFragment{

    private static final String LOG_TAG = PolygonFragment.class.getSimpleName();
    public static final String TAG = PolygonView.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_polygon, container, false);
        final PolygonView poly = (PolygonView) root.findViewById(R.id.poly);
        RadioGroup group = (RadioGroup) root.findViewById(R.id.group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.point0:
                        poly.setTestPoint(0);
                        break;
                    case R.id.point1:
                        poly.setTestPoint(1);
                        break;
                    case R.id.point2:
                        poly.setTestPoint(2);
                        break;
                    case R.id.point3:
                        poly.setTestPoint(4);
                        break;
                    default:
                        Log.d(LOG_TAG, "me?");
                }
            }
        });
        return root;
    }
}
