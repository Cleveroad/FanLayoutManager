package com.cleveroad.testrecycler.ui.fragments.full_info_fragment;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cleveroad.testrecycler.models.SportCardModel;
import com.cleveroad.testrecycler.ui.fragments.full_info_inner_fragment.FullInfoInnerFragment;

class CardsInfoAdapter extends FragmentStatePagerAdapter {

    private static final int TABS_COUNT = 3;
    private SportCardModel sportCardModel;
    private String[] titles = {"MEN", "WOMEN", "ATHLETES"};

    CardsInfoAdapter(FragmentManager fm, SportCardModel sportCardModel) {
        super(fm);
        this.sportCardModel = sportCardModel;
    }

    @Override
    public Fragment getItem(int position) {
        return FullInfoInnerFragment.newInstance(sportCardModel);
    }

    @Override
    public int getCount() {
        return TABS_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
