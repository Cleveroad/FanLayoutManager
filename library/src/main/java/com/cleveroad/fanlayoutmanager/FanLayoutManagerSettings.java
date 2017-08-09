package com.cleveroad.fanlayoutmanager;

import android.content.Context;

/**
 * @author alex yarovoi
 * @version 1.0
 */
public class FanLayoutManagerSettings {

    private static final float DEFAULT_VIEW_WIDTH_DP = 120F;
    private static final float DEFAULT_VIEW_HEIGHT_DP = 160F;

    private float mViewWidthDp;
    private float mViewHeightDp;
    private int mViewWidthPx;
    private int mViewHeightPx;
    private boolean mIsFanRadiusEnable;
    private float mAngleItemBounce;

    private FanLayoutManagerSettings(Builder builder) {
        mViewWidthDp = builder.mViewWidthDp;
        mViewHeightDp = builder.mViewHeightDp;
        mIsFanRadiusEnable = builder.mIsFanRadiusEnable;
        mAngleItemBounce = builder.mAngleItemBounce;
        mViewWidthPx = builder.mViewWidthPx;
        mViewHeightPx = builder.mViewHeightPx;
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    float getViewWidthDp() {
        return mViewWidthDp;
    }

    float getViewHeightDp() {
        return mViewHeightDp;
    }

    boolean isFanRadiusEnable() {
        return mIsFanRadiusEnable;
    }

    float getAngleItemBounce() {
        return mAngleItemBounce;
    }

    int getViewWidthPx() {
        return mViewWidthPx;
    }

    int getViewHeightPx() {
        return mViewHeightPx;
    }


    /**
     * {@code FanLayoutManagerSettings} builder static inner class.
     */
    public static final class Builder {
        private static final float BOUNCE_MAX = 10F;
        private Context mContext;
        private float mViewWidthDp;
        private float mViewHeightDp;
        private boolean mIsFanRadiusEnable;
        private float mAngleItemBounce;
        private int mViewWidthPx;
        private int mViewHeightPx;


        private Builder(Context context) {
            mContext = context;
        }

        /**
         * Sets the {@code mViewWidthDp} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param viewWidthDp the {@code mViewWidthDp} to set
         * @return a reference to this Builder
         */
        public Builder withViewWidthDp(float viewWidthDp) {
            mViewWidthDp = viewWidthDp;
            mViewWidthPx = Math.round(mContext.getResources().getDisplayMetrics().density * viewWidthDp);
            mViewWidthPx = Math.min(mContext.getResources().getDisplayMetrics().widthPixels, mViewWidthPx);
            return this;
        }

        /**
         * Sets the {@code mViewHeightDp} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param viewHeightDp the {@code mViewHeightDp} to set
         * @return a reference to this Builder
         */
        public Builder withViewHeightDp(float viewHeightDp) {
            mViewHeightDp = viewHeightDp;
            mViewHeightPx = Math.round(mContext.getResources().getDisplayMetrics().density * viewHeightDp);
            mViewHeightPx = Math.min(mContext.getResources().getDisplayMetrics().heightPixels, mViewHeightPx);
            return this;
        }

        /**
         * Sets the {@code fanRadius} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param isFanRadiusEnable the {@code mIsFanRadiusEnable} to set
         * @return a reference to this Builder
         */
        public Builder withFanRadius(boolean isFanRadiusEnable) {
            mIsFanRadiusEnable = isFanRadiusEnable;
            return this;
        }

        /**
         * Sets the {@code mAngleItemBounce} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param angleItemBounce the {@code mAngleItemBounce} to set in range 0f...10f
         * @return a reference to this Builder
         */
        public Builder withAngleItemBounce(float angleItemBounce) {
            if (angleItemBounce <= 0F) {
                return this;
            }
            mAngleItemBounce = Math.min(BOUNCE_MAX, angleItemBounce);
            return this;
        }

        /**
         * Returns a {@code FanLayoutManagerSettings} built from the parameters previously set.
         *
         * @return a {@code FanLayoutManagerSettings} built with parameters of this {@code FanLayoutManagerSettings.Builder}
         */
        public FanLayoutManagerSettings build() {
            if (Float.compare(mViewWidthDp, 0F) == 0) {
                withViewWidthDp(DEFAULT_VIEW_WIDTH_DP);
            }
            if (Float.compare(mViewHeightDp, 0F) == 0) {
                withViewHeightDp(DEFAULT_VIEW_HEIGHT_DP);
            }
            return new FanLayoutManagerSettings(this);
        }
    }
}
