package com.luciferldy.someviews.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.luciferldy.someviews.R;
import com.luciferldy.someviews.ui.view.SearchView;

/**
 * Created by Lucifer on 2016/12/14.
 */

public class SearchFragment extends BaseFragment {

    public static final String TAG = SearchView.class.getSimpleName();
    private static final String LOG_TAG = SearchFragment.class.getSimpleName();
    private static final String START = "开始";
    private static final String STOP = "停止";

    private SearchView searchView;
    private Button control;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = (SearchView) root.findViewById(R.id.search_view);
        control = (Button) root.findViewById(R.id.btn_control);
        control.setText(START);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (control.getText().equals(START)) {
                    control.setText(STOP);
                    searchView.startSearch();
                } else {
                    control.setText(START);
                    searchView.stopSearch();
                }
            }
        });
    }
}
