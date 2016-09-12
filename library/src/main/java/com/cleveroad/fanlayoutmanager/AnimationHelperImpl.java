package com.cleveroad.fanlayoutmanager;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.Collection;

/**
 * Created by Alex Yarovoi 16.08.2016
 */
class AnimationHelperImpl implements AnimationHelper {

    private static final int ANIMATION_SINGLE_OPEN_DURATION = 300;
    private static final int ANIMATION_SINGLE_CLOSE_DURATION = 300;
    private static final int ANIMATION_SHIFT_VIEWS_DURATION = 200;
    private static final int ANIMATION_SHIFT_VIEWS_DELAY_THRESHOLD = 50;

    private static final float ANIMATION_VIEW_SCALE_FACTOR = 1.5f;
    private static final float ANIMATION_VIEW_SCALE_FACTOR_THRESHOLD = 0.4F;

    @Override
    public void openItem(@NonNull final View view, int delay, Animator.AnimatorListener animatorListener) {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1F, ANIMATION_VIEW_SCALE_FACTOR + ANIMATION_VIEW_SCALE_FACTOR_THRESHOLD);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();

                if (value < 1F + ANIMATION_VIEW_SCALE_FACTOR_THRESHOLD / 2) {
                    value = Math.abs(value - 2F);
                } else {
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
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight());

        view.setScaleX(value);
        view.setScaleY(value);
    }

    @Override
    public void shiftSideViews(@NonNull final Collection<ViewAnimationInfo> views, int delay, @NonNull final ShiftViewListener listener) {
        ValueAnimator bounceAnimator = ValueAnimator.ofFloat(0F, 1F);
        bounceAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                for (ViewAnimationInfo info : views) {
                    int left = (int) (info.startLeft + value * (info.finishLeft - info.startLeft));
                    int right = (int) (info.startRight + value * (info.finishRight - info.startRight));
                    listener.layoutDecorated(info.view, left, info.top, right, info.bottom);
                }
                listener.updateArcViewPositions();
            }
        });

        bounceAnimator.setDuration(ANIMATION_SHIFT_VIEWS_DURATION);
        bounceAnimator.setStartDelay(delay + ANIMATION_SHIFT_VIEWS_DELAY_THRESHOLD);
        bounceAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        bounceAnimator.start();
    }

    @Override
    public float getViewScaleFactor() {
        return ANIMATION_VIEW_SCALE_FACTOR;
    }

//    public interface ShiftViewListener {
//        void layoutDecorated(View view, int left, int top, int right, int bottom);
//
//        void updateArcViewPositions();
//    }


}
