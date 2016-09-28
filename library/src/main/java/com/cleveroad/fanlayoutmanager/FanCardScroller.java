package com.cleveroad.fanlayoutmanager;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * LinearSmoothScroller for switch views.
 *
 * @author alex yarovoi
 * @version 1.0
 */
class FanCardScroller extends BaseSmoothScroller {
    //TODO Need to change this to make it more flexible.
    private static final float MILLISECONDS_PER_INCH = 200F;

    @Nullable
    private FanCardTimeCallback cardTimeCallback;

    /**
     * LinearSmoothScroller for switch views.
     *
     * @param context Context
     */
    FanCardScroller(Context context) {
        super(context);
    }

    @Override
    protected int getHorizontalSnapPreference() {
        return SNAP_TO_START;
    }

    @Override
    public int calculateDxToMakeVisible(View view, int snapPreference) {
        RecyclerView.LayoutManager layoutManager = getLayoutManager();
        if (layoutManager != null) {
            // add to calculated dx offset. Need to scroll to center of RecyclerView.
            return super.calculateDxToMakeVisible(view, snapPreference) + layoutManager.getWidth() / 2 - view.getWidth() / 2;
        } else {
            // no layoutManager detected - not expected case. can be magic or end of the world...
            return super.calculateDxToMakeVisible(view, snapPreference);
        }
    }

    @Override
    protected int calculateTimeForScrolling(int dx) {
        int time = super.calculateTimeForScrolling(dx);
        if (cardTimeCallback != null) {
            cardTimeCallback.onTimeForScrollingCalculated(getTargetPosition(), time);
        }
        return time;
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }


    @Nullable
    public FanCardTimeCallback getCardTimeCallback() {
        return cardTimeCallback;
    }

    void setCardTimeCallback(@Nullable FanCardTimeCallback cardTimeCallback) {
        this.cardTimeCallback = cardTimeCallback;
    }

    interface FanCardTimeCallback {
        /**
         * @param targetPosition item position to scroll to
         * @param time           scroll duration
         */
        void onTimeForScrollingCalculated(int targetPosition, int time);
    }
}
