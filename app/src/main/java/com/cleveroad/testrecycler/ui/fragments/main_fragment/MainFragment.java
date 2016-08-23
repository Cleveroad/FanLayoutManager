package com.cleveroad.testrecycler.ui.fragments.main_fragment;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.cleveroad.fanlayoutmanager.callbacks.FanChildDrawingOrderCallback;
import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.SportCardsUtils;
import com.cleveroad.testrecycler.ui.activities.main_activity.SharedTransitionSet;
import com.cleveroad.testrecycler.ui.fragments.full_info_fragment.FullInfoTabFragment;


public class MainFragment extends Fragment {

    private MainFragmentCallback mListener;

    private SportCardsAdapter adapter;
    private int selectedCardPos = -1;

    FanLayoutManager fanLayoutManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rvCards);
        fanLayoutManager = new FanLayoutManager(getContext(),
                    FanLayoutManagerSettings.newBuilder(getContext())
                            .withFanRadius(true)
                            .withAngleItemBounce(5)
                            .build());
        recyclerView.setLayoutManager(fanLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new SportCardsAdapter(getContext());
        adapter.addAll(SportCardsUtils.generateSportCards());
        adapter.setItemClickListener(new SportCardsAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int pos, final View view) {
                if(selectedCardPos != pos) {
                    fanLayoutManager.switchItem(recyclerView, pos);
                    selectedCardPos = pos;
                } else {
                        fanLayoutManager.straightenSelectedItem(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animator) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animator) {

                                onClick((ImageView) view, view.getTransitionName(), selectedCardPos);
                            }

                            @Override
                            public void onAnimationCancel(Animator animator) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animator) {

                            }
                        });

                }

            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.setChildDrawingOrderCallback(new FanChildDrawingOrderCallback(fanLayoutManager));


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainFragmentCallback) {
            mListener = (MainFragmentCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MainFragmentCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onClick(ImageView imageView, String tag, int pos) {
//        mListener.onCardClick(imageView, tag, pos);
        FullInfoTabFragment fragment = FullInfoTabFragment.newInstance(adapter.getModelByPos(pos), tag);

        fragment.setSharedElementEnterTransition(new SharedTransitionSet());
        fragment.setEnterTransition(new Fade());
        setExitTransition(new Fade());
        fragment.setSharedElementReturnTransition(new SharedTransitionSet());


        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(imageView, "shared")
                .replace(R.id.root, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        selectedCardPos = -1;
    }

        public boolean deselectIfSelected() {
        if (fanLayoutManager.isItemSelected()) {
            fanLayoutManager.deselectItem();
            selectedCardPos = -1;
            return true;
        } else {
            return false;
        }
    }

    public interface MainFragmentCallback {

        void onCardClick(ImageView imageView, String tag, int pos);
    }
}
