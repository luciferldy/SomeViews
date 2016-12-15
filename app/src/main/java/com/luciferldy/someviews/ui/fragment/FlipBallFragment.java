package com.luciferldy.someviews.ui.fragment;


import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;

import com.luciferldy.someviews.R;
import com.luciferldy.someviews.ui.view.FlipBallView;

/**
 * Created by Lucifer on 2016/12/14.
 * Two balls flip animation.
 */

public class FlipBallFragment extends BaseFragment {

    public static final String TAG = FlipBallFragment.class.getSimpleName();
    private static final String START = "开始";
    private static final String STOP = "停止";

    private FlipBallView flipBallView;
    private Button control;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_flipball, container, false);
        flipBallView = (FlipBallView) root.findViewById(R.id.flip_ball);
        control = (Button) root.findViewById(R.id.btn_control);
        control.setText(START);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        control.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use reveal effect of Material Design
            // https://developer.android.com/training/material/animations.html#Reveal
            int cx = (control.getLeft() + control.getRight()) / 2;
            int cy = (control.getTop() + control.getBottom()) / 2;
            int finalRadius = Math.max(control.getWidth(), control.getHeight());
            // Create animator for this view
            Animator animator = ViewAnimationUtils.createCircularReveal(control, cx, cy, 0, finalRadius);
            animator.start();
        }

        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (control.getText().equals(START)) {
                    control.setText(STOP);
                    flipBallView.startAnim();
                } else {
                    control.setText(START);
                    flipBallView.stopAnim();
                }
            }
        });
    }
}
