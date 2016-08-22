package com.cleveroad.testrecycler.ui.fragments.full_info_fragment;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.models.SportCardModel;


public class FullInfoTabFragment extends Fragment {

    private static final String SRORT_CARD_MODEL_ARGS = "sportCardModelArg";
    private static final String SRORT_CARD_MODEL_EXTRA = "sportCardModelExtra";

    private SportCardModel sportCardModel;

    private AppCompatImageView ivSportPreview;
    private TextView tvTime;
    private TextView tvDayPart;

    private RelativeLayout rlTitleContainer;

    private LinearLayout splashLayout;
    private LinearLayout contentLayout;


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

        ivSportPreview = (AppCompatImageView) root.findViewById(R.id.ivSportPreview);
        tvTime = (TextView) root.findViewById(R.id.tvTime);
        tvDayPart = (TextView) root.findViewById(R.id.tvDayPart);
        splashLayout = (LinearLayout) root.findViewById(R.id.splashLay);
        rlTitleContainer = (RelativeLayout) root.findViewById(R.id.rlTitleContainer);

        ivSportPreview.setImageResource(sportCardModel.getImageResId());
        tvTime.setText(sportCardModel.getTime());
        tvDayPart.setText(sportCardModel.getDayPart());

//        splashLayout.setBackgroundColor(ContextCompat.getColor(getContext(), sportCardModel.getBackgroundColorResId()));
        rlTitleContainer.setBackgroundColor(ContextCompat.getColor(getContext(), sportCardModel.getBackgroundColorResId()));

        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbarFragmentInfo);
        TabLayout tabs = (TabLayout) root.findViewById(R.id.tabMainGroups);
        ViewPager pager = (ViewPager) root.findViewById(R.id.vpContent);
        contentLayout = (LinearLayout) root.findViewById(R.id.contentLay);

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

        AppCompatImageView ivPhoto = (AppCompatImageView) root.findViewById(R.id.ivPhoto);
        ivPhoto.setImageResource(sportCardModel.getImageResId());

        pager.setAdapter(new CardsInfoAdapter(getChildFragmentManager(), sportCardModel));
        tabs.setupWithViewPager(pager, true);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        startSwitchAnimation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SRORT_CARD_MODEL_EXTRA, sportCardModel);
        super.onSaveInstanceState(outState);
    }

    private void startSwitchAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(contentLayout, "alpha", 1F);
        fadeIn.setDuration(700);
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator fadeout = ObjectAnimator.ofFloat(splashLayout, "alpha", 0F);
        fadeout.setDuration(700);
        fadeout.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(fadeIn, fadeout);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                splashLayout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

}
