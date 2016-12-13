package com.luciferldy.someviews.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.luciferldy.someviews.R;
import com.luciferldy.someviews.ui.fragment.ContactsFragment;
import com.luciferldy.someviews.ui.fragment.ItemTouchFragment;
import com.luciferldy.someviews.ui.fragment.PolygonFragment;
import com.luciferldy.someviews.ui.fragment.RoundedImageFragment;
import com.luciferldy.someviews.ui.fragment.SlideTrackFragment;
import com.luciferldy.someviews.ui.view.DragLayout;
import com.luciferldy.someviews.ui.view.FoldingLayout;
import com.luciferldy.someviews.ui.view.LetterIndexView;
import com.luciferldy.someviews.ui.view.PolygonView;
import com.luciferldy.someviews.ui.view.RoundedImageView;
import com.luciferldy.someviews.ui.view.SearchView;
import com.luciferldy.someviews.ui.view.SlideTrackView;
import com.luciferldy.someviews.ui.view.SpiderWebView;
import com.luciferldy.someviews.ui.view.TouchFoldingLayout;
import com.luciferldy.someviews.ui.view.YRotateBallView;

import java.util.ArrayList;

/**
 * Created by Lucifer on 2016/12/5.
 */

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    ArrayList<String> labelList = new ArrayList<>();
    FragmentManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        init();
        rv.setAdapter(new RvAdapter(labelList));
    }

    /**
     * 初始化一些数据
     */
    private void init() {
        labelList.add(ItemTouchFragment.class.getSimpleName());
        labelList.add(DragLayout.class.getSimpleName());
        labelList.add(FoldingLayout.class.getSimpleName());
        labelList.add(LetterIndexView.class.getSimpleName());
        labelList.add(PolygonView.class.getSimpleName());
        labelList.add(RoundedImageView.class.getSimpleName());
        labelList.add(SearchView.class.getSimpleName());
        labelList.add(SlideTrackView.class.getSimpleName());
        labelList.add(SpiderWebView.class.getSimpleName());
        labelList.add(TouchFoldingLayout.class.getSimpleName());
        labelList.add(YRotateBallView.class.getSimpleName());

        manager = getSupportFragmentManager();
    }

    class RvAdapter extends RecyclerView.Adapter<RvHolder> {

        private ArrayList<String> labels = new ArrayList<>();

        public RvAdapter(ArrayList<String> labels) {
            this.labels.addAll(labels);
        }

        @Override
        public RvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(getBaseContext()).inflate(R.layout.rv_item, parent, false);
            RvHolder holder = new RvHolder(root);
            return holder;
        }

        @Override
        public void onBindViewHolder(RvHolder holder, int position) {
            holder.setLabel(labels.get(position));
        }

        @Override
        public int getItemCount() {
            return labels.size();
        }
    }

    class RvHolder extends RecyclerView.ViewHolder {

        String label;
        View root;
        TextView tvLabel;

        public RvHolder(View itemView) {
            super(itemView);
            this.root = itemView;
            tvLabel = (TextView) this.root.findViewById(R.id.label);
            this.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newFragment(label);
                }
            });
        }

        public void setLabel(String label) {
            this.label = label;
            tvLabel.setText(label);
        }
    }

    /**
     * 新建一个 Fragment
     * @param label 标签
     */
    private void newFragment(String label) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment;
        if (ItemTouchFragment.TAG.contains(label)) {
            fragment = new ItemTouchFragment();
        } else if (ContactsFragment.TAG.contains(label)) {
            fragment = new ContactsFragment();
        } else if (PolygonFragment.TAG.contains(label)) {
            fragment = new PolygonFragment();
        } else if (RoundedImageFragment.TAG.contains(label)) {
            fragment = new RoundedImageFragment();
        } else if (SlideTrackFragment.TAG.contains(label)) {
            fragment = new SlideTrackFragment();
        } else {
            Toast.makeText(getBaseContext(), "No property fragment.", Toast.LENGTH_SHORT).show();
            return;
        }

        transaction.add(R.id.content, fragment, label);
        transaction.addToBackStack(label);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (manager.getBackStackEntryCount() > 0 ) {
            manager.popBackStack();
            return;
        }
        super.onBackPressed();
    }
}
