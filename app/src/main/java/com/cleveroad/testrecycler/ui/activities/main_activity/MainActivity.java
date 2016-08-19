package com.cleveroad.testrecycler.ui.activities.main_activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.models.SportCardModel;
import com.cleveroad.testrecycler.ui.fragments.full_info_fragment.FullInfoTabFragment;
import com.cleveroad.testrecycler.ui.fragments.main_fragment.MainFragment;

public class MainActivity extends AppCompatActivity implements MainFragment.MainFragmentCallback {

    private final static String MAIN_FRAGMENT_TAG = "MainFragmentTag";
    private final static String INFO_FRAGMENT_TAG = "InfoFragmentTag";

    private FragmentManager fragmentManager;

    private MainFragment mainFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        mainFragment = (MainFragment) fragmentManager.findFragmentById(R.id.mainFragment);

    }

    @Override
    public void onBackPressed() {
        if (mainFragment == null || !mainFragment.isAdded() || !mainFragment.deselectIfSelected()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onCardClicked(SportCardModel sportCardModel) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.show_info_animation, R.anim.hide_main_transaction,
                        R.anim.show_info_animation, R.anim.hide_main_transaction)
                .replace(R.id.root, FullInfoTabFragment.newInstance(sportCardModel))
                .addToBackStack(INFO_FRAGMENT_TAG)
                .commit();

    }



}
