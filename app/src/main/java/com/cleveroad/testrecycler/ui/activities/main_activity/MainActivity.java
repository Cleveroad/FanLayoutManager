package com.cleveroad.testrecycler.ui.activities.main_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.ui.fragments.main_fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.root, mainFragment = MainFragment.newInstance())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (mainFragment == null || !mainFragment.isAdded() || !mainFragment.deselectIfSelected()) {
            super.onBackPressed();
        }
    }

}
