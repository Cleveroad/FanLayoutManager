package com.cleveroad.testrecycler.ui.activities.main_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.ui.fragments.main_fragment.MainFragment;

public class MainActivity extends AppCompatActivity implements MainFragment.MainFragmentCallback {

    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
        mainFragment = new MainFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root, mainFragment)
                .commit();
    }

        @Override
    public void onBackPressed() {
        if (mainFragment == null || !mainFragment.isAdded() || !mainFragment.deselectIfSelected()) {
            super.onBackPressed();
        }
    }

//    @Override
//    public void onAttachFragment(Fragment fragment) {
//        super.onAttachFragment(fragment);
//        if(fragment instanceof MainFragment) {
//            mainFragment = (MainFragment) fragment;
//        }
//    }

    @Override
    public void onCardClick(ImageView imageView, String tag, int pos) {

//        BlankFragment fragment = BlankFragment.newInstance(tag, pos, );
//
//        fragment.setSharedElementEnterTransition(new SharedTransitionSet());
//        fragment.setEnterTransition(new Fade());
//        mainFragment.setExitTransition(new Fade());
//        fragment.setSharedElementReturnTransition(new SharedTransitionSet());
//
//
//        getSupportFragmentManager()
//                .beginTransaction()
//                .addSharedElement(imageView, "Cat")
//                .replace(R.id.root, fragment)
//                .addToBackStack(null)
//                .commit();
    }
}
