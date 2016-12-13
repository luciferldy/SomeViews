package com.luciferldy.someviews.ui.fragment;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luciferldy.someviews.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Lucifer on 2016/12/12.
 * E-mail: lian_dy@foxmail.com
 * Most code from iPaulPro https://github.com/iPaulPro/Android-ItemTouchHelper-Demo
 */

public class ItemTouchFragment extends BaseFragment {

    public static final String TAG = ItemTouchFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_rv_itemtouch, container, false);
        RecyclerView rv = (RecyclerView) root.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        RvAdapter adapter = new RvAdapter();
        rv.setAdapter(adapter);
        ItemTouchHelperCallback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rv);
        return root;
    }

    class RvAdapter extends RecyclerView.Adapter<RvViewHolder> implements ItemTouchHelperAdapter {

        private ArrayList<String> labels = new ArrayList<>();

        public RvAdapter() {
            for (int i = 0; i < 20; i++) {
                labels.add("item " + i);
            }
        }

        @Override
        public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(getContext()).inflate(R.layout.itemtouch_item, parent, false);
            RvViewHolder holder = new RvViewHolder(root);
            // 下面的代码执行后没有结果
//            TextView tv = new TextView(getContext());
//            tv.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//            tv.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//            RvViewHolder holder = new RvViewHolder(tv);
            return holder;
        }

        @Override
        public void onBindViewHolder(RvViewHolder holder, int position) {
            holder.setText(labels.get(position));
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "getItemCount " + labels.size());
            return labels.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(labels, i, i+1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(labels, i, i-1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onItemDismiss(int position) {
            labels.remove(position);
            notifyItemRemoved(position);
        }
    }

    class RvViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{
        private TextView tv;
        public RvViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv_label);
//            if (itemView instanceof TextView)
//                tv = (TextView) itemView;
        }

        public void setText(String str) {
            if (tv != null)
                tv.setText(str);
        }

        @Override
        public void onItemSelected() {

        }

        @Override
        public void onItemClear() {

        }
    }

    /**
     * Interface to listen for a move or dismissal event from
     * {@link ItemTouchHelper.Callback} .
     */
    interface ItemTouchHelperAdapter {
        /**
         * Called when an item has been dragged enough to trigger a move. This is called every time
         * an item is shifted, and not at the end of "drop" event.
         * @param fromPosition the start position of the moved item.
         * @param toPosition the resolved position of the moved item.
         */
        void onItemMove(int fromPosition, int toPosition);

        /**
         * Called when an item has been dismissed by a swipe.
         * @param position the position of the item dismissed.
         */
        void onItemDismiss(int position);
    }

    /**
     * Notifies a view holder of relevant callbacks from
     * {@link ItemTouchHelper.Callback}
     */
    interface ItemTouchHelperViewHolder {
        /**
         * Called when the {@link ItemTouchHelper} first registers an
         * item as being moved or swiped.
         * Implementations should update the item view to indicate it's active state
         */
        void onItemSelected();

        /**
         * Called when the {@link ItemTouchHelper} has completed the
         * move or swipe, and the active item state should be cleared.
         */
        void onItemClear();
    }

    class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

        private static final float ALPHA_FULL = 1.0f;

        private ItemTouchHelperAdapter adapter;

        public ItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            super();
            this.adapter = adapter;
        }

        /**
         * 开启长按拖动
         * @return
         */
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        /**
         * item 任意位置触发操作
         * @return
         */
        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        /**
         * 指定拖动和滑动的方向，drag 为上下拖动，swipe 为左右滑动
         * @param recyclerView
         * @param viewHolder
         * @return
         */
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager ||
                    recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                        | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                final int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            } else {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            if (viewHolder.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            // notify the adapter to move
            adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        /**
         * 删除操作
         * @param viewHolder
         * @param direction
         */
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // Notify the adapter to dismiss
            adapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // 自定义滑动动画
                final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder instanceof ItemTouchHelperViewHolder) {
                    // Let the view holder know that this item is being moved or dragged.
                    ((ItemTouchHelperViewHolder) viewHolder).onItemSelected();
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(ALPHA_FULL);
            if (viewHolder instanceof ItemTouchHelperViewHolder) {
                // 清除状态，未选中状态回调
                ((ItemTouchHelperViewHolder) viewHolder).onItemClear();
            }
        }
    }
}
