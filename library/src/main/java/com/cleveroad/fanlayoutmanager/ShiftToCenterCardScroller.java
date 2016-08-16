package com.cleveroad.fanlayoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Alex Yarovoi 16.08.2016
 */
class ShiftToCenterCardScroller extends LinearSmoothScroller {
    private static final float MILLISECONDS_PER_INCH = 400F;
    @NonNull
    private CardScrollerListener listener;

    public ShiftToCenterCardScroller(Context context, @NonNull CardScrollerListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return listener.computeScrollVectorForPosition(targetPosition);
    }

    @Override
    protected int getHorizontalSnapPreference() {
        return SNAP_TO_START;
    }

    @Override
    public int calculateDxToMakeVisible(View view, int snapPreference) {
        return super.calculateDxToMakeVisible(view, snapPreference) + listener.getWidth() / 2 - view.getWidth() / 2;
    }

    @Override
    protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
        super.onTargetFound(targetView, state, action);
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }

}
