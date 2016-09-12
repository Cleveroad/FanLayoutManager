package com.cleveroad.testrecycler.ui.fragments.full_info_fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.models.SportCardModel;


public class FullInfoTabFragment extends Fragment {

    private static final String SRORT_CARD_MODEL_ARGS = "sportCardModelArg";
    private static final String POSITION_NAME_ARG = "positionNameArg";
    private static final String SRORT_CARD_MODEL_EXTRA = "sportCardModelExtra";

    private SportCardModel sportCardModel;
    String transitionTag;


    public static FullInfoTabFragment newInstance(SportCardModel sportCardModel, @Nullable String transitionTag) {
        FullInfoTabFragment fragment = new FullInfoTabFragment();
        Bundle args = new Bundle();
        args.putParcelable(SRORT_CARD_MODEL_ARGS, sportCardModel);
        args.putString(POSITION_NAME_ARG, transitionTag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sportCardModel = getArguments().getParcelable(SRORT_CARD_MODEL_ARGS);
            transitionTag = getArguments().getString(POSITION_NAME_ARG);
        }
        if(savedInstanceState != null) {
            sportCardModel = savedInstanceState.getParcelable(SRORT_CARD_MODEL_EXTRA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatImageView ivSportPreview = (AppCompatImageView) view.findViewById(R.id.ivSportPreview);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        TextView tvDayPart = (TextView) view.findViewById(R.id.tvDayPart);
        LinearLayout splashLayout = (LinearLayout) view.findViewById(R.id.splashLay);
        RelativeLayout rlTitleContainer = (RelativeLayout) view.findViewById(R.id.rlTitleContainer);
        AppCompatImageView ivPhoto = (AppCompatImageView) view.findViewById(R.id.ivPhoto);
        ivSportPreview.setImageResource(sportCardModel.getImageResId());
        tvTime.setText(sportCardModel.getTime());
        tvDayPart.setText(sportCardModel.getDayPart());

        rlTitleContainer.setBackgroundColor(ContextCompat.getColor(getContext(), sportCardModel.getBackgroundColorResId()));

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbarFragmentInfo);
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabMainGroups);
        ViewPager pager = (ViewPager) view.findViewById(R.id.vpContent);
        LinearLayout contentLayout = (LinearLayout) view.findViewById(R.id.contentLay);



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

        ivPhoto.setImageResource(sportCardModel.getImageResId());

        pager.setAdapter(new CardsInfoAdapter(getChildFragmentManager(), sportCardModel));
        tabs.setupWithViewPager(pager, true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            splashLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            startSwitchAnimation();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SRORT_CARD_MODEL_EXTRA, sportCardModel);
        super.onSaveInstanceState(outState);
    }

    private void startSwitchAnimation() {
//        AnimatorSet animatorSet = new AnimatorSet();
//        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(contentLayout, "alpha", 1F);
//        fadeIn.setDuration(700);
//        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
//        ObjectAnimator fadeout = ObjectAnimator.ofFloat(splashLayout, "alpha", 0F);
//        fadeout.setDuration(700);
//        fadeout.setInterpolator(new AccelerateDecelerateInterpolator());
//        animatorSet.playTogether(fadeIn, fadeout);
//        animatorSet.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                splashLayout.setVisibility(View.GONE);
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//        animatorSet.start();
    }

}
