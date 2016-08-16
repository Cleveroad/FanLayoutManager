package com.cleveroad.fanlayoutmanager.callbacks;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Alex Yarovoi 16.08.2016
 */
public class RecursiveChildDrawingOrderCallback implements RecyclerView.ChildDrawingOrderCallback {

    @Override
    public int onGetChildDrawingOrder(int childCount, int i) {
        return childCount - i - 1;
    }
}
