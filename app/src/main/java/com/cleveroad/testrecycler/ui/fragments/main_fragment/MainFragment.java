package com.cleveroad.testrecycler.ui.fragments.main_fragment;


import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.cleveroad.fanlayoutmanager.callbacks.FanChildDrawingOrderCallback;
import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.SportCardsUtils;
import com.cleveroad.testrecycler.models.SportCardModel;

public class MainFragment extends Fragment {

    private MainFragmentCallback callback;

    private RecyclerView rvArticles;
    private SportCardsAdapter adapter;
    private FanLayoutManager fanLayoutManager;

    private int selectedCardPos = -1;
    private final static int ANIMATION_DELAY = 700;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Fragment parent = getParentFragment();
        if(parent instanceof MainFragmentCallback) {
            callback = (MainFragmentCallback) parent;
        } else if(context instanceof MainFragmentCallback) {
            callback = (MainFragmentCallback) context;
        } else {
            throw new RuntimeException("implementation of MainFragmentCallback is required");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_main, container, false);

        rvArticles = (RecyclerView) root.findViewById(R.id.rvArticles);
            adapter = new SportCardsAdapter(getContext());

            adapter.addAll(SportCardsUtils.generateSportCards());

            fanLayoutManager = new FanLayoutManager(getContext(),
                    FanLayoutManagerSettings.newBuilder(getContext())
                            .withFanRadius(true)
                            .withAngleItemBounce(5)
                            .build());

            rvArticles.setItemAnimator(new DefaultItemAnimator());/**/

            rvArticles.setAdapter(adapter);

            rvArticles.setLayoutManager(fanLayoutManager);

            rvArticles.setChildDrawingOrderCallback(new FanChildDrawingOrderCallback(fanLayoutManager));

            adapter.setItemClickListener(new SportCardsAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(int pos) {
                    if(selectedCardPos != pos) {
                        fanLayoutManager.switchItem(rvArticles, pos);
                        selectedCardPos = pos;
                    } else {
                        if(callback != null) {
                            fanLayoutManager.straightenSelectedItem(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    callback.onCardClicked(adapter.getModelByPos(selectedCardPos));
                                }

                                @Override
                                public void onAnimationCancel(Animator animator) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animator) {

                                }
                            });

                        }
                        fanLayoutManager.straightenSelectedItem();
                    }
                }
            });

        return root;
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

    @Override
    public void onResume() {
        super.onResume();

        if(fanLayoutManager != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fanLayoutManager.destraightenSelectedItem();
                }
            }, ANIMATION_DELAY);

        }
    }

    public interface MainFragmentCallback {
        void onCardClicked(SportCardModel sportCardModel);
    }

}
