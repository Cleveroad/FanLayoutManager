//package com.cleveroad.testrecycler.ui.fragments.main_fragment;
//
//
//import android.animation.Animator;
//import android.content.Context;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.RecyclerView;
//import android.transition.Fade;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.cleveroad.fanlayoutmanager.FanLayoutManager;
//import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
//import com.cleveroad.fanlayoutmanager.callbacks.FanChildDrawingOrderCallback;
//import com.cleveroad.testrecycler.R;
//import com.cleveroad.testrecycler.SportCardsUtils;
//import com.cleveroad.testrecycler.models.SportCardModel;
//import com.cleveroad.testrecycler.ui.activities.main_activity.SharedTransitionSet;
//import com.cleveroad.testrecycler.ui.fragments.full_info_fragment.FullInfoTabFragmentOld;
//
//public class MainFragmentOld extends Fragment {
//
//    private MainFragmentCallback callback;
//
//    private RecyclerView rvArticles;
//    private SportCardsAdapter adapter;
//    private FanLayoutManager fanLayoutManager;
//
//    private int selectedCardPos = -1;
//    private final static int ANIMATION_DELAY = 500;
//
//    public static MainFragmentOld newInstance() {
//        return new MainFragmentOld();
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        Fragment parent = getParentFragment();
//        if(parent instanceof MainFragmentCallback) {
//            callback = (MainFragmentCallback) parent;
//        } else if(context instanceof MainFragmentCallback) {
//            callback = (MainFragmentCallback) context;
//        } else {
//            throw new RuntimeException("implementation of MainFragmentCallback is required");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        callback = null;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View root = inflater.inflate(R.layout.fragment_main, container, false);
//
//        rvArticles = (RecyclerView) root.findViewById(R.id.rvArticles);
//            adapter = new SportCardsAdapter(getContext());
//
//            adapter.addAll(SportCardsUtils.generateSportCards());
//
//            fanLayoutManager = new FanLayoutManager(getContext(),
//                    FanLayoutManagerSettings.newBuilder(getContext())
//                            .withFanRadius(true)
//                            .withAngleItemBounce(5)
//                            .build());
//
//            rvArticles.setItemAnimator(new DefaultItemAnimator());/**/
//
//            rvArticles.setAdapter(adapter);
//
//            rvArticles.setLayoutManager(fanLayoutManager);
//
////            rvArticles.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//
//            rvArticles.setChildDrawingOrderCallback(new FanChildDrawingOrderCallback(fanLayoutManager));
//
//            adapter.setItemClickListener(new SportCardsAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClicked(int pos, final View view) {
//                    if(selectedCardPos != pos) {
//                        fanLayoutManager.switchItem(rvArticles, pos);
//                        selectedCardPos = pos;
//                    } else {
//                        if(callback != null) {
//                            fanLayoutManager.straightenSelectedItem(new Animator.AnimatorListener() {
//                                @Override
//                                public void onAnimationStart(Animator animator) {
//
//                                }
//
//                                @Override
//                                public void onAnimationEnd(Animator animator) {
//                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                        sharedEmemntTransaction(selectedCardPos, view);
//                                    } else {
//                                        callback.onCardClicked(adapter.getModelByPos(selectedCardPos), view);
//                                    }
//                                }
//
//                                @Override
//                                public void onAnimationCancel(Animator animator) {
//
//                                }
//
//                                @Override
//                                public void onAnimationRepeat(Animator animator) {
//
//                                }
//                            });
//
//                        }
////                        fanLayoutManager.straightenSelectedItem();
//                    }
//
//            }
//        });
//
//        return root;
//    }
//
//    public boolean deselectIfSelected() {
////        if (fanLayoutManager.isItemSelected()) {
////            fanLayoutManager.deselectItem();
////            selectedCardPos = -1;
////            return true;
////        } else {
////            return false;
////        }
//        return true;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        if(fanLayoutManager != null) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    fanLayoutManager.destraightenSelectedItem();
//                }
//            }, ANIMATION_DELAY);
//
//        }
//    }
//
//    public interface MainFragmentCallback {
//        void onCardClicked(SportCardModel sportCardModel, View view);
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void sharedEmemntTransaction(int pos, View view) {
//        FullInfoTabFragmentOld fullInfoTabFragmentOld = FullInfoTabFragmentOld.newInstance(adapter.getModelByPos(pos), view.getTransitionName());
//
//        fullInfoTabFragmentOld.setSharedElementEnterTransition(new SharedTransitionSet());
//        fullInfoTabFragmentOld.setEnterTransition(new Fade());
//        setExitTransition(new Fade());
//        fullInfoTabFragmentOld.setSharedElementReturnTransition(new SharedTransitionSet());
//
//
//        getActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .addSharedElement(view, "Cat")
//                .replace(R.id.root, fullInfoTabFragmentOld)
//                .addToBackStack(null)
//                .commit();
//
//    }
//
//}
