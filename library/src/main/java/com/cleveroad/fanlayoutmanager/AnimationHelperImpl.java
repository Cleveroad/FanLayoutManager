package com.cleveroad.fanlayoutmanager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Collection;

/**
 * Custom implementation of {@link AnimationHelper}.
 * <p>
 * Responsible for open/close item scale animation, shift views to sides or to center animation,
 * straight view animation.
 *
 * @author alex yarovoi
 * @version 1.0
 */
class AnimationHelperImpl implements AnimationHelper {
    // scale factor for view in open/close animations
    private static final float ANIMATION_VIEW_SCALE_FACTOR = 1.5f;

    // base duration for open animation
    private static final int ANIMATION_SINGLE_OPEN_DURATION = 300;

    // base duration for close animation
    private static final int ANIMATION_SINGLE_CLOSE_DURATION = 300;

    // base duration for shift animation
    private static final int ANIMATION_SHIFT_VIEWS_DURATION = 200;

    // base threshold duration for shift animation
    private static final int ANIMATION_SHIFT_VIEWS_DELAY_THRESHOLD = 50;

    // base threshold duration for open/close animation (bounce effect)
    private static final float ANIMATION_VIEW_SCALE_FACTOR_THRESHOLD = 0.4F;

    @Override
    public void openItem(@NonNull final View view, int delay, Animator.AnimatorListener animatorListener) {
        // create animator
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1F, ANIMATION_VIEW_SCALE_FACTOR + ANIMATION_VIEW_SCALE_FACTOR_THRESHOLD);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // get update value
                float value = (float) valueAnimator.getAnimatedValue();

                if (value < 1F + ANIMATION_VIEW_SCALE_FACTOR_THRESHOLD / 2) {
                    // make view less
                    value = Math.abs(value - 2F);
                } else {
                    // make view bigger
                    value -= ANIMATION_VIEW_SCALE_FACTOR_THRESHOLD;
                }

                scaleView(view, value);

            }
        });

        valueAnimator.setStartDelay(delay);
        valueAnimator.setDuration(ANIMATION_SINGLE_OPEN_DURATION);
        valueAnimator.addListener(animatorListener);
        valueAnimator.start();

    }

    @Override
    public void closeItem(final @NonNull View view, int delay, Animator.AnimatorListener animatorListener) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(ANIMATION_VIEW_SCALE_FACTOR, 1F);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // get update value
                float value = (float) valueAnimator.getAnimatedValue();
                scaleView(view, value);
            }
        });
        valueAnimator.setStartDelay(delay);
        valueAnimator.setDuration(ANIMATION_SINGLE_CLOSE_DURATION);
        valueAnimator.addListener(animatorListener);
        valueAnimator.start();
    }

    private void scaleView(final View view, float value) {

        // change pivot point to the bottom middle
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight());

        // scale view
        view.setScaleX(value);
        view.setScaleY(value);
    }

    @Override
    public void shiftSideViews(@NonNull final Collection<ViewAnimationInfo> views,
                               int delay,
                               @NonNull final RecyclerView.LayoutManager layoutManager,
                               @Nullable final Animator.AnimatorListener animatorListener,
                               @Nullable final ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        ValueAnimator bounceAnimator = ValueAnimator.ofFloat(0F, 1F);
        bounceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // get update value
                float value = (float) valueAnimator.getAnimatedValue();

                for (ViewAnimationInfo info : views) {

                    // left offset for view for current update value
                    int left = (int) (info.startLeft + value * (info.finishLeft - info.startLeft));

                    // right offset for view for current update value
                    int right = (int) (info.startRight + value * (info.finishRight - info.startRight));

                    // update view with new params
                    layoutManager.layoutDecorated(info.view, left, info.top, right, info.bottom);
                }
                if (animatorUpdateListener != null) {
                    // update listener
                    animatorUpdateListener.onAnimationUpdate(valueAnimator);
                }
            }
        });

        bounceAnimator.setDuration(ANIMATION_SHIFT_VIEWS_DURATION);
        bounceAnimator.setStartDelay(delay + ANIMATION_SHIFT_VIEWS_DELAY_THRESHOLD);
        if (animatorListener != null) {
            bounceAnimator.addListener(animatorListener);
        }
        bounceAnimator.start();
    }

    @Override
    public float getViewScaleFactor() {
        return ANIMATION_VIEW_SCALE_FACTOR;
    }

    @Override
    public void straightenView(View view, @Nullable Animator.AnimatorListener listener) {
        if (view != null) {
            ObjectAnimator viewObjectAnimator = ObjectAnimator.ofFloat(view,
                    "rotation", view.getRotation(), 0f);
            viewObjectAnimator.setDuration(150);
            viewObjectAnimator.setInterpolator(new DecelerateInterpolator());
            if (listener != null) {
                viewObjectAnimator.addListener(listener);
            }
            viewObjectAnimator.start();
        }

    }


}
