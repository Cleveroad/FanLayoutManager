package com.cleveroad.fanlayoutmanager;

import android.content.Context;

/**
 * @author alex yarovoi
 * @version 1.0
 */
public class FanLayoutManagerSettings {

    private static final float DEFAULT_VIEW_WIDTH_DP = 120;
    private static final float DEFAULT_VIEW_HEIGHT_DP = 160;

    private float viewWidthDp;
    private float viewHeightDp;
    private int viewWidthPx;
    private int viewHeightPx;
    private boolean isFanRadiusEnable;
    private float angleItemBounce;

    private FanLayoutManagerSettings(Builder builder) {
        viewWidthDp = builder.viewWidthDp;
        viewHeightDp = builder.viewHeightDp;
        isFanRadiusEnable = builder.isFanRadiusEnable;
        angleItemBounce = builder.angleItemBounce;
        viewWidthPx = builder.viewWidthPx;
        viewHeightPx = builder.viewHeightPx;
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    float getViewWidthDp() {
        return viewWidthDp;
    }

    float getViewHeightDp() {
        return viewHeightDp;
    }

    boolean isFanRadiusEnable() {
        return isFanRadiusEnable;
    }

    float getAngleItemBounce() {
        return angleItemBounce;
    }

    int getViewWidthPx() {
        return viewWidthPx;
    }

    int getViewHeightPx() {
        return viewHeightPx;
    }


    /**
     * {@code FanLayoutManagerSettings} builder static inner class.
     */
    public static final class Builder {
        private static final float BOUNCE_MAX = 10;
        private Context context;
        private float viewWidthDp;
        private float viewHeightDp;
        private boolean isFanRadiusEnable;
        private float angleItemBounce;
        private int viewWidthPx;
        private int viewHeightPx;


        private Builder(Context context) {
            this.context = context;
        }

        /**
         * Sets the {@code viewWidthDp} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param viewWidthDp the {@code viewWidthDp} to set
         * @return a reference to this Builder
         */
        public Builder withViewWidthDp(float viewWidthDp) {
            this.viewWidthDp = viewWidthDp;
            this.viewWidthPx = Math.round(context.getResources().getDisplayMetrics().density * viewWidthDp);
            this.viewWidthPx = Math.min(context.getResources().getDisplayMetrics().widthPixels, this.viewWidthPx);
            return this;
        }

        /**
         * Sets the {@code viewHeightDp} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param viewHeightDp the {@code viewHeightDp} to set
         * @return a reference to this Builder
         */
        public Builder withViewHeightDp(float viewHeightDp) {
            this.viewHeightDp = viewHeightDp;
            this.viewHeightPx = Math.round(context.getResources().getDisplayMetrics().density * viewHeightDp);
            this.viewHeightPx = Math.min(context.getResources().getDisplayMetrics().heightPixels, this.viewHeightPx);
            return this;
        }

        /**
         * Sets the {@code fanRadius} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param isFanRadiusEnable the {@code isFanRadiusEnable} to set
         * @return a reference to this Builder
         */
        public Builder withFanRadius(boolean isFanRadiusEnable) {
            this.isFanRadiusEnable = isFanRadiusEnable;
            return this;
        }

        /**
         * Sets the {@code angleItemBounce} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param angleItemBounce the {@code angleItemBounce} to set in range 0f...10f
         * @return a reference to this Builder
         */
        public Builder withAngleItemBounce(float angleItemBounce) {
            if (angleItemBounce <= 0F) {
                return this;
            }
            this.angleItemBounce = Math.min(BOUNCE_MAX, angleItemBounce);
            return this;
        }

        /**
         * Returns a {@code FanLayoutManagerSettings} built from the parameters previously set.
         *
         * @return a {@code FanLayoutManagerSettings} built with parameters of this {@code FanLayoutManagerSettings.Builder}
         */
        public FanLayoutManagerSettings build() {
            if (Float.compare(viewWidthDp, 0F) == 0) {
                withViewWidthDp(DEFAULT_VIEW_WIDTH_DP);
            }
            if (Float.compare(viewHeightDp, 0F) == 0) {
                withViewHeightDp(DEFAULT_VIEW_HEIGHT_DP);
            }
            return new FanLayoutManagerSettings(this);
        }
    }
}
