package com.cleveroad.fanlayoutmanager;

import android.view.View;

/**
 * Created by Alex Yarovoi 16.08.2016
 */
public interface ShiftViewListener {
    void layoutDecorated(View view, int left, int top, int right, int bottom);

    void updateArcViewPositions();
}
