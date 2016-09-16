package com.cleveroad.fanlayoutmanager;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collection;

/**
 * Created by Alex Yarovoi 16.08.2016
 */
public interface AnimationHelper {

    void openItem(@NonNull final View view, int delay, Animator.AnimatorListener animatorListener);

    void closeItem(@NonNull final View view, int delay, Animator.AnimatorListener animatorListener);

    void shiftSideViews(@NonNull final Collection<ViewAnimationInfo> views, int delay, @NonNull RecyclerView.LayoutManager layoutManager, @Nullable Animator.AnimatorListener animatorListener);

    float getViewScaleFactor();
}
