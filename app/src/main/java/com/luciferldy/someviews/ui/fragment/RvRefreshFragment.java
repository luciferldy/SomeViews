package com.luciferldy.someviews.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luciferldy.someviews.R;

import java.util.ArrayList;

/**
 * Created by Lucifer on 2016/12/18.
 */

public class RvRefreshFragment extends BaseFragment {

    public static final String TAG = RvRefreshFragment.class.getSimpleName();
    private String line = "春风沈醉的夜晚，云遮月，月遮云，云又遮月，月又遮云，云急了说，你有完没完，月也急了说，X你大爷，风来了，天亮了，他们都死了";
    private RecyclerView mRv;
    private RvAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private View header;
    private View footer;
    private ImageView ivArrow;
    private TextView tvDes;
    private ProgressBar progressBar;
    private LinearLayout.LayoutParams headerParams;
    private LinearLayout.LayoutParams footerParams;
    private ValueAnimator mHideAnimator;
    private ValueAnimator mRefreshAnimator;
    private ObjectAnimator mArrowDown;
    private ObjectAnimator mArrowUp;
    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;

    private float downPosition;
    private float upPosition;
    private float movePosition;

    private boolean isRefreshing = false;

    private int mRotateAniTime = 100;

    private int REFRESH_HEIGHT;
    private int ARROW_ROTATE_THRESHOLD;

    RefreshCallback callback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rv_refresh, container, false);
        header = root.findViewById(R.id.header);
        headerParams = (LinearLayout.LayoutParams) header.getLayoutParams();
        footer = root.findViewById(R.id.footer);
        footerParams = (LinearLayout.LayoutParams) footer.getLayoutParams();
        ivArrow = (ImageView) root.findViewById(R.id.arrow);
        tvDes = (TextView) root.findViewById(R.id.des);
        progressBar = (ProgressBar) root.findViewById(R.id.progress_bar);

        mRv = (RecyclerView) root.findViewById(R.id.rv_refresh);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRv.setLayoutManager(mLayoutManager);
        mAdapter = new RvAdapter();
        mRv.setAdapter(mAdapter);
        mRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mRv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 正在刷新的时候不响应
                if (isRefreshing)
                    return true;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downPosition = event.getRawY();
                        Log.i(TAG, "action down " + downPosition);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        movePosition = event.getRawY();
                        Log.i(TAG, "action move " + movePosition);
                        if (movePosition <= downPosition)
                            return false;
                        if (mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                            if (movePosition - downPosition <= ARROW_ROTATE_THRESHOLD) {
                                if (ivArrow.getRotation() % 360 == 180) {
                                    rotateArrowDown(); // 箭头旋转向上
                                }
                            } else {
                                if (ivArrow.getRotation() % 360 == 0) {
                                    rotateArrowUp(); // 箭头旋转向下
                                }
                            }
                            if (movePosition - downPosition <= REFRESH_HEIGHT) {
                                headerParams.topMargin = (int) (-REFRESH_HEIGHT + movePosition - downPosition);
                                header.setLayoutParams(headerParams);
                                return true;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        upPosition = event.getRawY();
                        if (upPosition <= downPosition)
                            return false;
                        Log.i(TAG, "action up " + upPosition);
                        if (mLayoutManager.findFirstCompletelyVisibleItemPosition() != 0)
                            return false;
                        if (upPosition - downPosition >= ARROW_ROTATE_THRESHOLD) {
                            smoothToRefresh(headerParams.topMargin);
                        } else {
                            smoothToHide(headerParams.topMargin);
                        }
                        return true;
                }
                return false;
            }
        });

//        buildAnimation();
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        REFRESH_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.refresh_height);
        ARROW_ROTATE_THRESHOLD = REFRESH_HEIGHT / 2;
        String[] lines = line.split("，");
        for (String item: lines) {
            mAdapter.add(item);
        }

        final Handler handler = new Handler();

        callback = new RefreshCallback() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.add("New Item");
                        stopRefresh();
                    }
                }, 2000);
            }
        };
    }

    class RvAdapter extends RecyclerView.Adapter<RvViewHolder> {

        ArrayList<String> items = new ArrayList<>();

        @Override
        public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_refresh, parent, false);
            RvViewHolder holder = new RvViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RvViewHolder holder, int position) {
            holder.setText(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void add(String item) {
            items.add(item);
        }
    }

    class RvViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        public RvViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.text);
        }

        public void setText(String text) {
            if (tv != null)
                tv.setText(text);
        }
    }

    private synchronized void smoothToHide(int start) {
        mHideAnimator = ValueAnimator.ofInt(start, -REFRESH_HEIGHT).setDuration(200);
        mHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int value;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                value = (int) animation.getAnimatedValue();
                headerParams.topMargin = value;
                header.setLayoutParams(headerParams);
                header.invalidate();
            }
        });
        mHideAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRefreshing = false;
                ivArrow.setRotation(0);
                ivArrow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                tvDes.setText(getResources().getText(R.string.pull_to_refresh));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mHideAnimator.start();
    }

    private synchronized void smoothToRefresh(int start) {
        isRefreshing = true;
        mRefreshAnimator = ValueAnimator.ofInt(start, -ARROW_ROTATE_THRESHOLD).setDuration(200);
        mRefreshAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int value;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                value = (int) animation.getAnimatedValue();
                headerParams.topMargin = value;
                header.setLayoutParams(headerParams);
                header.invalidate();
            }
        });
        mRefreshAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tvDes.setText(getResources().getText(R.string.refreshing));
                ivArrow.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                callback.onRefresh();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mRefreshAnimator.start();
    }

//    private void buildAnimation() {
//        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//        mFlipAnimation.setInterpolator(new LinearInterpolator());
//        mFlipAnimation.setDuration(mRotateAniTime);
//        mFlipAnimation.setFillAfter(true);
//
//        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
//        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
//        mReverseFlipAnimation.setDuration(mRotateAniTime);
//        mReverseFlipAnimation.setFillAfter(true);
//    }

    private synchronized void rotateArrowUp() {
        if (mArrowDown != null) {
            mArrowDown.start();
            return;
        }
        mArrowDown = ObjectAnimator.ofFloat(ivArrow, "rotation", 0, 180f).setDuration(mRotateAniTime);
        mArrowDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tvDes.setText(getResources().getText(R.string.release_to_update));
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mArrowDown.start();
    }

    private synchronized void rotateArrowDown() {
        if (mArrowUp != null) {
            mArrowUp.start();
            return;
        }
        mArrowUp = ObjectAnimator.ofFloat(ivArrow, "rotation", 180f, 360f).setDuration(mRotateAniTime);
        mArrowUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tvDes.setText(getResources().getText(R.string.pull_to_refresh));
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mArrowUp.start();
    }

    private void stopRefresh() {
        ivArrow.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvDes.setText(getResources().getText(R.string.load_done));
        smoothToHide(headerParams.topMargin);
    }

    interface RefreshCallback {
        void onRefresh();
    }
}
