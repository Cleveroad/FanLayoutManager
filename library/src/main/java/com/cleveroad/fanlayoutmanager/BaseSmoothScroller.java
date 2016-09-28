package com.cleveroad.fanlayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;

/**
 * @author alex yarovoi
 * @version 1.0
 */
abstract class BaseSmoothScroller extends LinearSmoothScroller {
    BaseSmoothScroller(Context context) {
        super(context);
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        // calculate vector for position
        if (layoutManager != null && layoutManager instanceof FanLayoutManager) {
            if (getChildCount() == 0) {
                return null;
            }
            final int firstChildPos = layoutManager.getPosition(layoutManager.getChildAt(0));
            final int direction = targetPosition < firstChildPos ? -1 : 1;
            return new PointF(direction, 0);
        }
        return new PointF();
    }
}
