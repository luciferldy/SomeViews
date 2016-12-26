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

import com.luciferldy.someviews.R;
import com.luciferldy.someviews.ui.fragment.ContactsFragment;
import com.luciferldy.someviews.ui.fragment.DragLayoutFragment;
import com.luciferldy.someviews.ui.fragment.FlipBallFragment;
import com.luciferldy.someviews.ui.fragment.FoldingLayoutFragment;
import com.luciferldy.someviews.ui.fragment.ItemTouchFragment;
import com.luciferldy.someviews.ui.fragment.RadarViewFragment;
import com.luciferldy.someviews.ui.fragment.RoundedImageFragment;
import com.luciferldy.someviews.ui.fragment.RvRefreshFragment;
import com.luciferldy.someviews.ui.fragment.SearchFragment;
import com.luciferldy.someviews.ui.fragment.SlideTrackFragment;
import com.luciferldy.someviews.ui.fragment.ZoomImageFragment;

import java.util.ArrayList;

/**
 * Created by Lucifer on 2016/12/5.
 */

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private FragmentManager mManager;
    private RvAdapter mRvAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRvAdapter = new RvAdapter();
        rv.setAdapter(mRvAdapter);

        mRvAdapter.add(new SampleInfo("ZoomImage", ZoomImageFragment.class.getName()));
        mRvAdapter.add(new SampleInfo("ItemTouchHelper", ItemTouchFragment.class.getName()));
        mRvAdapter.add(new SampleInfo("DragLayout", DragLayoutFragment.class.getName()));
        mRvAdapter.add(new SampleInfo("FoldingLayout", FoldingLayoutFragment.class.getName()));
        mRvAdapter.add(new SampleInfo("Contacts", ContactsFragment.class.getName()));
        mRvAdapter.add(new SampleInfo("RoundedImageView", RoundedImageFragment.class.getName()));
        mRvAdapter.add(new SampleInfo("SearchView", SearchFragment.class.getName()));
        mRvAdapter.add(new SampleInfo("SlideTrackView", SlideTrackFragment.class.getName()));
        mRvAdapter.add(new SampleInfo("RadarView", RadarViewFragment.class.getName()));
        mRvAdapter.add(new SampleInfo("FlipBallView", FlipBallFragment.class.getName()));
        mRvAdapter.add(new SampleInfo("RefreshRecyclerView", RvRefreshFragment.class.getName()));

        mManager = getSupportFragmentManager();

        newFragment(ZoomImageFragment.class.getName());
    }

    class RvAdapter extends RecyclerView.Adapter<RvHolder> {

        private ArrayList<SampleInfo> lists = new ArrayList<>();

        public RvAdapter() {
        }

        @Override
        public RvHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_mainrv, parent, false);
            RvHolder holder = new RvHolder(root);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RvHolder holder, final int position) {
            holder.setLabel(lists.get(position).title);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newFragment(lists.get(holder.getAdapterPosition()).fragmentName);
                }
            });
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

        public boolean add(SampleInfo object) {
            int lastIndex = lists.size();
            if (lists.add(object)) {
                notifyItemInserted(lastIndex);
                return true;
            } else {
                return false;
            }
        }
    }

    class RvHolder extends RecyclerView.ViewHolder {

        View root;
        TextView tvLabel;

        public RvHolder(View itemView) {
            super(itemView);
            this.root = itemView;
            tvLabel = (TextView) this.root.findViewById(R.id.label);
        }

        public void setLabel(String label) {
            tvLabel.setText(label);
        }
    }

    /**
     * 新建一个 Fragment
     * @param fragmentName 标签
     */
    private void newFragment(String fragmentName) {
        Fragment fragment = Fragment.instantiate(getBaseContext(), fragmentName);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        transaction.add(R.id.content, fragment, fragmentName);
        transaction.addToBackStack(fragmentName);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mManager.getBackStackEntryCount() > 0 ) {
            mManager.popBackStack();
            return;
        }
        super.onBackPressed();
    }

    class SampleInfo {
        public String title;
        public String fragmentName;

        public SampleInfo(String title, String fragmentName) {
            this.title = title;
            this.fragmentName = fragmentName;
        }
    }
}
