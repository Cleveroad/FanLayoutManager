package com.cleveroad.testrecycler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

import com.cleveroad.fanlayoutmanager.FanLayoutManager;
import com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings;
import com.cleveroad.fanlayoutmanager.callbacks.FanChildDrawingOrderCallback;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvArticles;
    private SportCardsAdapter adapter;
    private FanLayoutManager fanLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        adapter = new SportCardsAdapter(this);

        adapter.addAll(SportCardsUtils.generateSportCards());

        fanLayoutManager = new FanLayoutManager(this,
                FanLayoutManagerSettings.newBuilder(this)
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
                fanLayoutManager.switchItem(rvArticles, pos);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fanLayoutManager.isItemSelected()) {
            fanLayoutManager.deselectItem();
        } else {
            super.onBackPressed();
        }
    }
}
