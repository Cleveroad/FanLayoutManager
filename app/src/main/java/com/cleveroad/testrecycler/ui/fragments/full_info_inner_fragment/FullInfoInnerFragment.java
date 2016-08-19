package com.cleveroad.testrecycler.ui.fragments.full_info_inner_fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.models.SportCardModel;


public class FullInfoInnerFragment extends Fragment {

    private static final String SRORT_CARD_MODEL_ARGS = "sportCardModelArg";
    private static final String SRORT_CARD_MODEL_EXTRA = "sportCardModelExtra";

    private SportCardModel sportCardModel;


    private TabLayout tabs;
    private ViewPager pager;


    public static FullInfoInnerFragment newInstance(SportCardModel sportCardModel) {
        FullInfoInnerFragment fragment = new FullInfoInnerFragment();
        Bundle args = new Bundle();
        args.putParcelable(SRORT_CARD_MODEL_ARGS, sportCardModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sportCardModel = getArguments().getParcelable(SRORT_CARD_MODEL_ARGS);
        }
        if(savedInstanceState != null) {
            sportCardModel = savedInstanceState.getParcelable(SRORT_CARD_MODEL_EXTRA);
        }
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_full_info_inner, container, false);

        tabs = (TabLayout) root.findViewById(R.id.tabsInner);
        pager = (ViewPager) root.findViewById(R.id.vpLists);

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SRORT_CARD_MODEL_EXTRA, sportCardModel);
        super.onSaveInstanceState(outState);
    }

}
