//package com.cleveroad.testrecycler.internal;
//
//import android.animation.Animator;
//import android.animation.AnimatorSet;
//import android.animation.ValueAnimator;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.view.View;
//
//import com.cleveroad.testrecycler.AnimationHelper;
//
//import java.util.Collection;
//
//public class AnimationHelperImplOld implements AnimationHelper {
//
//    private static final int ANIMATION_SINGLE_OPEN_DURATION = 300;
//    private static final int ANIMATION_SINGLE_CLOSE_DURATION = 300;
//    private static final int ANIMATION_SIDES_OPEN_DURATION = 300;
//    private static final int ANIMATION_SIDES_CLOSE_DURATION = 300;
//
//    private static final float ANIMATION_VIEW_SCALE_FACTOR = 1.5f;
//
//    private boolean animationInProcess = false;
//    //    @Nullable
////    private AnimationHelperListener animationHelperListener;
//    private final Animator.AnimatorListener animationListener = new Animator.AnimatorListener() {
//
//
//        @Override
//        public void onAnimationStart(Animator animator) {
//            animationInProcess = true;
//        }
//
//        @Override
//        public void onAnimationEnd(Animator animator) {
//            animationInProcess = false;
//        }
//
//        @Override
//        public void onAnimationCancel(Animator animator) {
//            animationInProcess = false;
//        }
//
//        @Override
//        public void onAnimationRepeat(Animator animator) {
//
//        }
//    };
//
//
//    @Override
//    public void switchItem(@Nullable final View viewToSwitch, @Nullable final Collection<View> viewToClose, int delay,
//                           @Nullable Animator.AnimatorListener listener) {
//        if (animationInProcess) {
//            return;
//        }
//
//        ValueAnimator animatorSingle = ValueAnimator.ofFloat(0f, 1f);
//
//        if (viewToSwitch != null) {
//            float fromValue = viewToSwitch.getScaleX();
//            float toValue = viewToSwitch.getScaleX() > 1F ? 1F : ANIMATION_VIEW_SCALE_FACTOR;
//            animatorSingle = ValueAnimator.ofFloat(fromValue, toValue);
//
//            animatorSingle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                    float animationProgress = (float) valueAnimator.getAnimatedValue();
//
//                    viewToSwitch.setPivotX(viewToSwitch.getWidth() / 2);
//                    viewToSwitch.setPivotY(viewToSwitch.getHeight());
//
//                    viewToSwitch.setScaleX(animationProgress);
//                    viewToSwitch.setScaleY(animationProgress);
//                }
//            });
//        }
//
//        ValueAnimator animatorCloseGroup = ValueAnimator.ofFloat(ANIMATION_VIEW_SCALE_FACTOR, 1f);
//        if (viewToClose != null && viewToClose.size() > 0) {
//            animatorCloseGroup.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                @Override
//                public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                    float animationProgress = (float) valueAnimator.getAnimatedValue();
//                    for (View view : viewToClose) {
//                        if (view != null && view.getScaleX() > animationProgress) {
//
//                            view.setPivotX(view.getWidth() / 2);
//                            view.setPivotY(view.getHeight());
//
//                            view.setScaleX(animationProgress);
//                            view.setScaleY(animationProgress);
//                        }
//                    }
//                }
//            });
//        }
//
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(animatorSingle, animatorCloseGroup);
//        animatorSet.addListener(animationListener);
//        if (listener != null) {
//            animatorSet.addListener(listener);
//        }
//        animatorSet.setStartDelay(delay);
//        animatorSet.start();
//    }
//
//    public void openItem(@NonNull final View view, int delay) {
//
//        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1F, 2F);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                float value = (float) valueAnimator.getAnimatedValue();
//                view.setPivotX(view.getWidth() / 2);
//                view.setPivotY(view.getHeight());
//
//                view.setScaleX(value);
//                view.setScaleY(value);
//            }
//        });
//
//
//
//    }
//
//    @Override
//    public boolean isAnimationInProcess() {
//        return animationInProcess;
//    }
//
//
//    public interface AnimationHelperListener {
//        void onAnimationStart();
//
//        void onAnimationEnd();
//    }
//}
