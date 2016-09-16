package com.cleveroad.fanlayoutmanager;

import android.graphics.PointF;

/**
 * Created by Alex Yarovoi 16.08.2016
 */
public interface CardScrollerListener {
    PointF computeScrollVectorForPosition(int targetPosition);

    int getWidth();

    void timeForScrollingCalculated(int time);
}
