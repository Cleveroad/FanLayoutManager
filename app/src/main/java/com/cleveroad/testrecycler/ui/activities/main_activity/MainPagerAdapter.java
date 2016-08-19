package com.cleveroad.testrecycler.ui.activities.main_activity;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.models.SportCardModel;
import com.cleveroad.testrecycler.ui.fragments.full_info_fragment.FullInfoTabFragment;
import com.cleveroad.testrecycler.ui.fragments.main_fragment.MainFragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MainFragment.newInstance();
            case 1:
                return FullInfoTabFragment.newInstance(SportCardModel
                        .newBuilder()
                        .withSportTitle("Archery")
                        .withSportSubtitle("Men's team")
                        .withSportRound("Round of 16")
                        .withImageResId(R.drawable.pic_card_5)
                        .withTime("3:00")
                        .withDayPart("PM")
                        .withBackgroundColorResId(R.color.portland_orange)
                        .build());
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
