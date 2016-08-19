package com.cleveroad.testrecycler.ui.fragments.full_info_fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.models.SportCardModel;


public class FullInfoTabFragment extends Fragment {

    private static final String SRORT_CARD_MODEL_ARGS = "sportCardModelArg";
    private static final String SRORT_CARD_MODEL_EXTRA = "sportCardModelExtra";

    private SportCardModel sportCardModel;

    private AppCompatImageView ivPhoto;


    public static FullInfoTabFragment newInstance(SportCardModel sportCardModel) {
        FullInfoTabFragment fragment = new FullInfoTabFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_full_info, container, false);

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbarFragmentInfo);
        TabLayout tabs = (TabLayout) root.findViewById(R.id.tabMainGroups);
        ViewPager pager = (ViewPager) root.findViewById(R.id.vpContent);

        toolbar.setTitle(sportCardModel.getSportTitle());
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setBackgroundColor(ContextCompat.getColor(getContext(), sportCardModel.getBackgroundColorResId()));
        tabs.setBackgroundColor(ContextCompat.getColor(getContext(), sportCardModel.getBackgroundColorResId()));

        ivPhoto = (AppCompatImageView) root.findViewById(R.id.ivPhoto);
        ivPhoto.setImageResource(sportCardModel.getImageResId());

        pager.setAdapter(new CardsInfoAdapter(getChildFragmentManager(), sportCardModel));
        tabs.setupWithViewPager(pager, true);

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SRORT_CARD_MODEL_EXTRA, sportCardModel);
        super.onSaveInstanceState(outState);
    }

}
