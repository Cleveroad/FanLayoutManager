package com.cleveroad.testrecycler.ui.activities.main_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.ui.fragments.main_fragment.MainFragment;

public class MainActivity extends AppCompatActivity implements MainFragment.MainFragmentCallback {

    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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



    @Override
    public void onCardClick(View view, String tag, int pos) {

    }
}
