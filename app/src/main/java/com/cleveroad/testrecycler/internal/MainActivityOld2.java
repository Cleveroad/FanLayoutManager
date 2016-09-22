//package com.cleveroad.testrecycler.ui.activities.main_activity;
//
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.RequiresApi;
//import android.support.v4.app.FragmentManager;
//import android.support.v7.app.AppCompatActivity;
//import android.transition.Fade;
//import android.view.View;
//
//import com.cleveroad.testrecycler.R;
//import com.cleveroad.testrecycler.models.SportCardModel;
//
//import com.cleveroad.testrecycler.ui.fragments.full_info_fragment.FullInfoTabFragmentOld;
//import com.cleveroad.testrecycler.ui.fragments.main_fragment.MainFragmentOld;
//
//public class MainActivityOld extends AppCompatActivity implements MainFragmentOld.MainFragmentCallback {
//
//    private final static String MAIN_FRAGMENT_TAG = "MainFragmentTag";
//    private final static String INFO_FRAGMENT_TAG = "InfoFragmentTag";
//    private final static String SHARED_ELEMENT_TAG = "shared";
//
//    private FragmentManager fragmentManager;
//
//    private MainFragmentOld mainFragmentOld;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        fragmentManager = getSupportFragmentManager();
//
////        mainFragmentOld = (MainFragmentOld) fragmentManager.findFragmentById(R.id.mainFragmentOld);
//
//        mainFragmentOld = MainFragmentOld.newInstance();
//        fragmentManager.beginTransaction()
//                .add(R.id.root, mainFragmentOld)
////                .addToBackStack(INFO_FRAGMENT_TAG)
//                .commit();
//
//    }
//
////    @Override
////    public void onBackPressed() {
////        if (mainFragmentOld == null || !mainFragmentOld.isAdded() || !mainFragmentOld.deselectIfSelected()) {
////            super.onBackPressed();
////        }
////    }
//
//    @Override
//    public void onCardClicked(SportCardModel sportCardModel, View view) {
////        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            makeSharedElementFragmentTransaction(sportCardModel, view);
////        } else {
////            makeFragmentTransactionWithCustomAnim(sportCardModel);
////        }
//
//    }
//
//    private void makeFragmentTransactionWithCustomAnim(SportCardModel sportCardModel) {
//        fragmentManager.beginTransaction()
//                .setCustomAnimations(R.anim.show_info_animation, R.anim.hide_main_transaction,
//                        R.anim.show_info_animation, R.anim.hide_main_transaction)
//                .replace(R.id.root, FullInfoTabFragmentOld.newInstance(sportCardModel, null))
//                .addToBackStack(INFO_FRAGMENT_TAG)
//                .commit();
//    }
//
////    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
////    private void makeSharedElementFragmentTransaction(SportCardModel sportCardModel, View sharedView) {
////
////        FullInfoTabFragmentOld fullInfoTabFragment = FullInfoTabFragmentOld.newInstance(sportCardModel, sharedView.getTransitionName());
////
////        fullInfoTabFragment.setSharedElementEnterTransition(new SharedTransitionSet());
////        fullInfoTabFragment.setEnterTransition(new Fade());
////        mainFragmentOld.setExitTransition(new Fade());
////        fullInfoTabFragment.setSharedElementReturnTransition(new SharedTransitionSet());
////
////
////        fragmentManager.beginTransaction()
////                .addSharedElement(sharedView, SHARED_ELEMENT_TAG)
////                .replace(R.id.root, fullInfoTabFragment)
////                .addToBackStack(INFO_FRAGMENT_TAG)
////                .commit();
////    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    private void makeSharedElementFragmentTransaction(SportCardModel sportCardModel, View sharedView) {
//
////        TestFragment fullInfoTabFragment = TestFragment.newInstance(sportCardModel, sharedView.getTransitionName());
////
////        fullInfoTabFragment.setSharedElementEnterTransition(new SharedTransitionSet());
////        fullInfoTabFragment.setEnterTransition(new Fade());
////        mainFragmentOld.setExitTransition(new Fade());
////        fullInfoTabFragment.setSharedElementReturnTransition(new SharedTransitionSet());
////
////
////        fragmentManager.beginTransaction()
////                .addSharedElement(sharedView, "shared")
////                .replace(R.id.root, fullInfoTabFragment)
////                .addToBackStack(null)
////                .commit();
//
//    }
//
//}
