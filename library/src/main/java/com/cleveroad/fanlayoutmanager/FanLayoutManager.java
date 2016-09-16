package com.cleveroad.fanlayoutmanager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.Collection;
import java.util.Random;

import static com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings.DirectionMode.FROM_CENTER;

/**
 * Created by Alex Yarovoi 16.08.2016
 */
public class FanLayoutManager extends RecyclerView.LayoutManager implements
        ShiftViewListener {

    public static final String TAG = "FanLayoutManager";
    public static final int DEFAULT_NON_SELECTED_ITEM_POSITION = -1;
    private final FanLayoutManagerSettings settings;
    private final SparseArray<Double> viewRotationsMap = new SparseArray<>();
    private final FanCardScroller fanCardScroller;
    private final ShiftToCenterCardScroller shiftToCenterCardScroller;
    private final Random random = new Random();
    @NonNull
    private AnimationHelper animationHelper;
    private SparseArray<View> viewCache = new SparseArray<>();
    //    private int mAnchorPos;
    private int selectedItemPosition = DEFAULT_NON_SELECTED_ITEM_POSITION;
    private boolean isSelectAnimationInProcess = false;
    private boolean isDeselectAnimationInProcess = false;
    private boolean isWaitingToSelectAnimation = false;
    private boolean isWaitingToDeselectAnimation = false;
    private boolean isSelectedItemStraightened = false;
    private boolean isViewCollapsing = false;

    private int scrollToPosition = DEFAULT_NON_SELECTED_ITEM_POSITION;
    private LinearLayoutManager.SavedState mPendingSavedState;

    public FanLayoutManager(@NonNull Context context) {
        this(context, null);
    }

    public FanLayoutManager(@NonNull Context context, @Nullable FanLayoutManagerSettings settings) {

        this.settings = settings == null ? FanLayoutManagerSettings.newBuilder(context).build() : settings;
        animationHelper = new AnimationHelperImpl();
        fanCardScroller = new FanCardScroller(context);
        fanCardScroller.setCardTimeCallback(new FanCardScroller.FanCardTimeCallback() {
            @Override
            public void onTimeForScrollingCalculated(int targetPosition, int time) {
                selectItem(targetPosition, time);
            }
        });
        shiftToCenterCardScroller = new ShiftToCenterCardScroller(context);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public void setAnimationHelper(@Nullable AnimationHelper animationHelper) {
        this.animationHelper = animationHelper == null ? new AnimationHelperImpl() : animationHelper;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
//        mAnchorPos = selectedItemPosition == DEFAULT_NON_SELECTED_ITEM_POSITION ? 0 : selectedItemPosition;
        fill(recycler);
    }

    private void fill(RecyclerView.Recycler recycler) {
//        View anchorView = getAnchorView(recycler);
        View centerView = findCurrentCenterView();
        int centerViewPosition = centerView == null ? 0 : getPosition(centerView);

        int centerViewOffset = centerView == null ? (int) (getWidth() / 2F - settings.getViewWidthPx() / 2F) :
                getDecoratedLeft(centerView);


        viewCache.clear();
        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            viewCache.put(pos, view);
        }

        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }
        if (scrollToPosition != DEFAULT_NON_SELECTED_ITEM_POSITION) {
            fillRightFromCenter(scrollToPosition, centerViewOffset, recycler);
        } else {
//            fillRight(anchorView, recycler);
            fillRightFromCenter(centerViewPosition, centerViewOffset, recycler);
        }

        for (int i = 0; i < viewCache.size(); i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }

        updateArcViewPositions();
//        Log.e(TAG, " childcount = " + getChildCount());
    }

//    private View getAnchorView(RecyclerView.Recycler recycler) {
//        View anchorView = null;
//
//        int childCount = getChildCount();
//
//        int minLeftDistance = -getWidth() / 2;
////        if (scrollToPosition != DEFAULT_NON_SELECTED_ITEM_POSITION) {
////            minLeftDistance = (int) (settings.getViewWidthPx() * scrollToPosition * 0.75F) + settings.getViewWidthPx() / 2 - getWidth();
////        }
//        for (int i = 0; i < childCount; i++) {
//            View view = getChildAt(i);
//            int left = getDecoratedLeft(view);
//            if (left > minLeftDistance) {
//                if (anchorView == null) {
//                    anchorView = view;
//                }
//                if (getDecoratedLeft(anchorView) > left) {
//                    anchorView = view;
//                }
//            }
//        }
//        scrollToPosition = DEFAULT_NON_SELECTED_ITEM_POSITION;
//
//        return anchorView;
//    }

    private void measureChildWithDecorationsAndMargin(View child, int widthSpec, int heightSpec) {

        Rect decorRect = new Rect();
        calculateItemDecorationsForChild(child, decorRect);
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
        widthSpec = updateSpecWithExtra(widthSpec, lp.leftMargin + decorRect.left,
                lp.rightMargin + decorRect.right);
        heightSpec = updateSpecWithExtra(heightSpec, lp.topMargin + decorRect.top,
                lp.bottomMargin + decorRect.bottom);
        child.measure(widthSpec, heightSpec);

    }

    private int updateSpecWithExtra(int spec, int startInset, int endInset) {
        if (startInset == 0 && endInset == 0) {
            return spec;
        }
        final int mode = View.MeasureSpec.getMode(spec);
        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
            return View.MeasureSpec.makeMeasureSpec(
                    View.MeasureSpec.getSize(spec) - startInset - endInset, mode);
        }
        return spec;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (selectedItemPosition != DEFAULT_NON_SELECTED_ITEM_POSITION && !isSelectAnimationInProcess && !isDeselectAnimationInProcess &&
                !isWaitingToDeselectAnimation && !isWaitingToSelectAnimation) {
            deselectItem(selectedItemPosition);
        }

        if (isDeselectAnimationInProcess || isSelectAnimationInProcess || isViewCollapsing) {
            return 0;
        }
        int delta = scrollHorizontallyInternal(dx);
        offsetChildrenHorizontal(-delta);
        fill(recycler);
        return delta;
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {

        int heightMode = View.MeasureSpec.getMode(heightSpec);
        long scaledHeight = (long) (settings.getViewHeightPx() * animationHelper.getViewScaleFactor());
        long scaledWidth = (long) (settings.getViewWidthPx() * animationHelper.getViewScaleFactor());
        int height = heightMode == View.MeasureSpec.EXACTLY ? View.MeasureSpec.getSize(heightSpec) :
                (int) (Math.sqrt(scaledHeight * scaledHeight + scaledWidth * scaledWidth));

        //noinspection Range
        heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }

    private int scrollHorizontallyInternal(int dx) {
        int childCount = getChildCount();
        Log.e(TAG, "childCount = " + childCount);
        if (childCount == 0) {
            return 0;
        }

        int itemCount = getItemCount();

        View leftView = getChildAt(0);
        View rightView = getChildAt(childCount - 1);

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (getDecoratedLeft(leftView) > getDecoratedLeft(view)) {
                leftView = view;
            }
            if (getDecoratedRight(rightView) < getDecoratedRight(view)) {
                rightView = view;
            }
        }

        int viewSpan = getDecoratedRight(rightView) > getWidth() ? getDecoratedRight(rightView) : getWidth() -
                (getDecoratedLeft(leftView) < 0 ? getDecoratedLeft(leftView) : 0);

        if (viewSpan < getWidth()) {
            return 0;
        }

        int delta = 0;
        if (dx < 0) {
            int firstViewAdapterPos = getPosition(leftView);
            if (firstViewAdapterPos > 0) {
                delta = dx;
            } else {
                int viewLeft = getDecoratedLeft(leftView) - getWidth() / 2 + getDecoratedMeasuredWidth(leftView) / 2;
                delta = Math.max(viewLeft, dx);
            }
        } else if (dx > 0) {
            int lastViewAdapterPos = getPosition(rightView);
            if (lastViewAdapterPos < itemCount - 1) {
                delta = dx;
            } else {
                int viewRight = getDecoratedRight(rightView) + getWidth() / 2 - getDecoratedMeasuredWidth(rightView) / 2;
                int parentRight = getWidth();
                delta = Math.min(viewRight - parentRight, dx);
            }
        }
        return delta;
    }

    @Override
    public void updateArcViewPositions() {
        int childCount = getChildCount();
        float halfWidth = getWidth() / 2;
        // minimal radius is recyclerView width * 2
        double radius = getWidth() * 2;
        double powRadius = radius * radius;

        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            double rotation = 0;
            if (settings.isFanRadiusEnable()) {

                float halfViewWidth = view.getWidth() / 2;

                view.setPivotX(halfViewWidth);
                view.setPivotY(view.getHeight());

                double deltaX = halfWidth - getDecoratedLeft(view) - halfViewWidth;
                double deltaY = radius - Math.sqrt(powRadius - deltaX * deltaX);

                view.setTranslationY((float) deltaY);

                rotation = (Math.toDegrees(Math.asin((radius - deltaY) / radius)) - 90) * Math.signum(deltaX);

            }
            // set rotation and side where is card
            int viewPosition = getPosition(view);

            Double baseViewRotation = viewRotationsMap.get(viewPosition);

            if (baseViewRotation == null) {
                baseViewRotation = random.nextDouble() * settings.getAngleItemBounce() * 2 - settings.getAngleItemBounce();
                viewRotationsMap.put(viewPosition, baseViewRotation);
            }
//

            view.setRotation((float) (rotation + baseViewRotation));
//            view.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    /**
     * Method draw view using center view position. Need to first show with view in the middle of the screen.
     *
     * @param centerViewPosition position of center view (anchor). This view will be in center
     * @param recycler           Recycler
     */
    private void fillRightFromCenter(int centerViewPosition, int centerViewOffset, RecyclerView.Recycler recycler) {

        // left limit. need to prepare with before they will be show to user.
        int leftBorder = -settings.getViewWidthPx();
        int leftViewOffset = centerViewOffset;
        int leftViewPosition = centerViewPosition;

        while (leftViewOffset > leftBorder) {
            // overlap distance is 25% of view.
            if (settings.getDirectionCollapse() == FanLayoutManagerSettings.DirectionCollapse.FROM_EACH_OTHER) {
                leftViewOffset -= settings.getViewWidthPx() * 1.25F;
            } else {
                leftViewOffset -= settings.getViewWidthPx() * 0.75F;
            }
            leftViewPosition--;
        }

        if (leftViewPosition < 0) {
            if (settings.getDirectionCollapse() == FanLayoutManagerSettings.DirectionCollapse.FROM_EACH_OTHER) {
                leftViewOffset += settings.getViewWidthPx() * 1.25F * Math.abs(leftViewPosition);
            } else {
                leftViewOffset += settings.getViewWidthPx() * 0.75F * Math.abs(leftViewPosition);
            }
            leftViewPosition = 0;
        }
        Log.e(TAG, "centerViewPosition = " + centerViewPosition + " || leftViewPosition = " + leftViewPosition + " || leftViewOffset = " + leftViewOffset);
        // margin to draw cards in bottom
        final int baseTopMargin = Math.max(0, getHeight() - settings.getViewHeightPx() - settings.getViewWidthPx() / 4);
        int overlapDistance;
        if (settings.getDirectionCollapse() == FanLayoutManagerSettings.DirectionCollapse.FROM_EACH_OTHER) {
            overlapDistance = -settings.getViewWidthPx() / 4;
        } else {
            overlapDistance = settings.getViewWidthPx() / 4;
        }

        boolean fillRight = true;

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(settings.getViewWidthPx(), View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(settings.getViewHeightPx(), View.MeasureSpec.EXACTLY);

        while (fillRight && leftViewPosition < getItemCount()) {
            View view = viewCache.get(leftViewPosition);
            if (view == null) {
                view = recycler.getViewForPosition(leftViewPosition);
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                addView(view);
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
                layoutDecorated(view, leftViewOffset, baseTopMargin,
                        leftViewOffset + settings.getViewWidthPx(), baseTopMargin + settings.getViewHeightPx());
            } else {
                attachView(view);
                viewCache.remove(leftViewPosition);
            }
            leftViewOffset = leftViewOffset + settings.getViewWidthPx() - overlapDistance;
//            leftViewOffset += settings.getViewWidthPx() * 0.75F;
            fillRight = leftViewOffset < getWidth() + settings.getViewWidthPx();
            leftViewPosition++;
        }

        scrollToPosition = DEFAULT_NON_SELECTED_ITEM_POSITION;
    }

//    private void fillRight(View anchorView, RecyclerView.Recycler recycler) {
//
////        int anchorPosition = scrollToPosition != DEFAULT_NON_SELECTED_ITEM_POSITION ? Math.max(scrollToPosition - 2, 0) : 0;
//        int anchorPosition;
//
//        int anchorDecoratedLeft = 0;
//
//        if (anchorView != null) {
//            anchorPosition = getPosition(anchorView);
//            anchorDecoratedLeft = getDecoratedLeft(anchorView);
////            mAnchorPos = anchorPosition;
//        } else {
//            anchorPosition = mAnchorPos;
//        }
//
//        int viewPosition = anchorPosition;
//
//        boolean fillRight = true;
//
//        int viewDecoratedLeft = anchorDecoratedLeft;
//        final int itemCount = getItemCount();
//
//        final int widthSpec = View.MeasureSpec.makeMeasureSpec(settings.getViewWidthPx(), View.MeasureSpec.EXACTLY);
//        final int heightSpec = View.MeasureSpec.makeMeasureSpec(settings.getViewHeightPx(), View.MeasureSpec.EXACTLY);
//
//        // for increase performance
//        final int halfViewWidth = settings.getViewWidthPx() / 2;
//        final int halfScreenWidth = getWidth() / 2;
//        final int overlapDistance;
//
//        // view overlapping distance
//        if (settings.getDirectionMode() == FanLayoutManagerSettings.DirectionMode.TO_CENTER) {
//            overlapDistance = settings.getViewWidthPx() / 4; // make views overlapping
//        } else {
//            overlapDistance = -settings.getViewWidthPx() / 4; // make distance between views
//        }
//
//        // margin to draw cards in bottom
//        final int baseTopMargin = Math.max(0, getHeight() - settings.getViewHeightPx());
//
//        if (anchorView != null) {
//            while (viewDecoratedLeft > -halfScreenWidth && viewPosition > 0) {
//                viewDecoratedLeft -= (settings.getViewWidthPx() - overlapDistance); // left visible distance
//                viewPosition--;
//            }
//            viewDecoratedLeft += overlapDistance; // overlap for first item
//        }
//
//        while (fillRight && viewPosition < itemCount) {
//            View view = viewCache.get(viewPosition);
//            if (view == null) {
//                view = recycler.getViewForPosition(viewPosition);
//                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//                addView(view);
//                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
//                layoutDecorated(view, viewDecoratedLeft - overlapDistance, baseTopMargin,
//                        viewDecoratedLeft + settings.getViewWidthPx() - overlapDistance, baseTopMargin + settings.getViewHeightPx());
//            } else {
//                attachView(view);
//                viewCache.remove(viewPosition);
//            }
//            viewDecoratedLeft = getDecoratedRight(view);
//            fillRight = viewDecoratedLeft <= getWidth() + halfViewWidth;
//            viewPosition++;
//        }
//        if (scrollToPosition != DEFAULT_NON_SELECTED_ITEM_POSITION) {
//            scrollToPosition = DEFAULT_NON_SELECTED_ITEM_POSITION;
////            scrollToCenter();
//        }
//
//    }

    /**
     * Method change item state from close to open and open to close (switch state)
     *
     * @param recyclerView         current recycler view. Need to smooth scroll.
     * @param selectedViewPosition item view position
     */
    public void switchItem(@Nullable RecyclerView recyclerView, final int selectedViewPosition) {
        if (isDeselectAnimationInProcess || isSelectAnimationInProcess) {
            return;
        }

        if (recyclerView != null) {
            if (this.selectedItemPosition != DEFAULT_NON_SELECTED_ITEM_POSITION &&
                    this.selectedItemPosition != selectedViewPosition) {
                deselectItem(recyclerView, this.selectedItemPosition, selectedViewPosition, 0);
                return;
            }
            smoothScrollToPosition(recyclerView, null, selectedViewPosition);
        }
    }

    public boolean isItemSelected() {
        return this.selectedItemPosition != DEFAULT_NON_SELECTED_ITEM_POSITION;
    }

    public void deselectItem() {
        deselectItem(selectedItemPosition);
    }

    private void selectItem(final int position, int delay) {

        if (selectedItemPosition == position) {
            deselectItem(selectedItemPosition);
            return;
        }

        selectedItemPosition = position;

        View viewToSelect = null;
        for (int count = getChildCount(), i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (position == getPosition(view)) {
                viewToSelect = view;
            }
        }

        if (viewToSelect == null) {
            return;
        }
        // open item animation wait for start but not in process.
        isWaitingToSelectAnimation = true;

        animationHelper.openItem(viewToSelect, delay * 3 /*need to finish scroll before start open*/,
                new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        super.onAnimationStart(animator);

                        isSelectAnimationInProcess = true;
                        isWaitingToSelectAnimation = false;

                        final int delta = settings.getViewWidthPx() / 2;
//                        final Collection<ViewAnimationInfo> infoViews = generateViewAnimationInfos(-delta, delta);
                        final Collection<ViewAnimationInfo> infoViews = ViewAnimationInfoGenerator.generate(delta,
                                FROM_CENTER,
                                FanLayoutManager.this,
                                selectedItemPosition,
                                FanLayoutManagerSettings.DirectionCollapse.FROM_CENTER);
                        animationHelper.shiftSideViews(infoViews, 0, FanLayoutManager.this, null);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        isSelectAnimationInProcess = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        isSelectAnimationInProcess = false;
                    }
                });
    }

    private void deselectItem(final int position) {
        deselectItem(null, position, DEFAULT_NON_SELECTED_ITEM_POSITION, 0);
    }

    private void deselectItem(final RecyclerView recyclerView, final int position, final int scrollToPosition, final int delay) {

        if (selectedItemPosition == DEFAULT_NON_SELECTED_ITEM_POSITION) {
            return;
        }

        selectedItemPosition = DEFAULT_NON_SELECTED_ITEM_POSITION;

        View viewToDeselect = null;
        for (int count = getChildCount(), i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (position == getPosition(view)) {
                viewToDeselect = view;
            }
        }

        if (viewToDeselect == null) {
            return;
        }
        isWaitingToDeselectAnimation = true;
        animationHelper.closeItem(viewToDeselect, delay, new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                super.onAnimationStart(animator);
                isDeselectAnimationInProcess = true;
                isWaitingToDeselectAnimation = false;

                final int delta = settings.getViewWidthPx() / 2;
//                final Collection<ViewAnimationInfo> infoViews = generateViewAnimationInfos(delta, -delta);
                final Collection<ViewAnimationInfo> infoViews =
                        ViewAnimationInfoGenerator.generate(delta,
                                FanLayoutManagerSettings.DirectionMode.TO_CENTER,
                                FanLayoutManager.this,
                                position,
                                FanLayoutManagerSettings.DirectionCollapse.FROM_CENTER);

                animationHelper.shiftSideViews(infoViews, 0, FanLayoutManager.this, null);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isDeselectAnimationInProcess = false;
                if (recyclerView != null && scrollToPosition != DEFAULT_NON_SELECTED_ITEM_POSITION) {
                    smoothScrollToPosition(recyclerView, null, scrollToPosition);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                isDeselectAnimationInProcess = false;
                if (recyclerView != null && scrollToPosition != DEFAULT_NON_SELECTED_ITEM_POSITION) {
                    smoothScrollToPosition(recyclerView, null, scrollToPosition);
                }
            }
        });

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        // when user stop scrolling
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            scrollToCenter();
        }
    }

    private void scrollToCenter() {
        float centerX = getWidth() / 2;
        float viewHalfWidth = settings.getViewWidthPx() / 2;
        View nearestToCenterView = null;
        int nearestDeltaX = 0;
        for (int count = getChildCount(), i = 0; i < count; i++) {
            View item = getChildAt(i);
            int centerXView = (int) (getDecoratedLeft(item) + viewHalfWidth);
            if (nearestToCenterView == null || Math.abs(nearestDeltaX) > Math.abs(centerX - centerXView)) {
                nearestToCenterView = item;
                nearestDeltaX = (int) (centerX - centerXView);
            }
        }

        if (nearestToCenterView != null) {
            shiftToCenterCardScroller.setTargetPosition(getPosition(nearestToCenterView));
            startSmoothScroll(shiftToCenterCardScroller);
        }

    }

    @Override
    public void scrollToPosition(int position) {
        scrollToPosition = position;
        requestLayout();
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
        if (position >= getItemCount()) {
            return;
        }

        fanCardScroller.setTargetPosition(position);
        startSmoothScroll(fanCardScroller);
    }

    // TODO: 19.08.16 migrate to AnimationHelper
    private void straightenView(View view, int position, @Nullable Animator.AnimatorListener listener) {
        if (view != null) {
            ObjectAnimator viewObjectAnimator = ObjectAnimator.ofFloat(view,
                    "rotation", viewRotationsMap.get(position).floatValue(), 0f);
            viewObjectAnimator.setDuration(150);
            viewObjectAnimator.setInterpolator(new DecelerateInterpolator());
            if (listener != null) {
                viewObjectAnimator.addListener(listener);
            }
            viewObjectAnimator.start();
        }

    }

    public void straightenSelectedItem(Animator.AnimatorListener listener) {

        if (selectedItemPosition != DEFAULT_NON_SELECTED_ITEM_POSITION && !isSelectAnimationInProcess &&
                !isDeselectAnimationInProcess && !isSelectedItemStraightened) {
            View viewToRotate = null;
            for (int count = getChildCount(), i = 0; i < count; i++) {
                View view = getChildAt(i);
                if (selectedItemPosition == getPosition(view)) {
                    viewToRotate = view;
                }
            }
            if (viewToRotate != null) {
                straightenView(viewToRotate, selectedItemPosition, listener);
                isSelectedItemStraightened = true;
            }

        }
    }

    /**
     * Method collapsed views (cards) from center card or each other.
     *
     * @param mode collapsed mode
     */
    public void collapseViews(@NonNull @FanLayoutManagerSettings.DirectionMode int mode) {
        // 1) Lock screen (Stop scrolling)
        // 2) Collapse all cards
        // 3) Unlock screen
        // 4) Scroll to center nearest card if not selected

        // 1) lock screen
        isViewCollapsing = !isViewCollapsing;
        settings.setDirectionMode(mode);
        settings.setDirectionCollapse(mode == FanLayoutManagerSettings.DirectionMode.FROM_CENTER ?
                FanLayoutManagerSettings.DirectionCollapse.FROM_EACH_OTHER : FanLayoutManagerSettings.DirectionCollapse.FROM_CENTER);
        // 2) Collapse all cards
        updateItemsByMode();
    }

    private void updateItemsByMode() {
        int delta = settings.getViewWidthPx() / 2;

        final Collection<ViewAnimationInfo> infoViews =
                ViewAnimationInfoGenerator.generate(delta,
                        settings.getDirectionMode(),
                        FanLayoutManager.this,
                        findCurrentCenterViewPos(),
                        FanLayoutManagerSettings.DirectionCollapse.FROM_EACH_OTHER);

        animationHelper.shiftSideViews(infoViews, 0, FanLayoutManager.this, new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                // 3) Unlock screen
                isViewCollapsing = !isViewCollapsing;
                // 4) Scroll to center nearest card if not selected
                scrollToCenter();
            }
        });
    }

    private int findCurrentCenterViewPos() {
        float centerX = getWidth() / 2;
        float viewHalfWidth = settings.getViewWidthPx() / 2;
        View nearestToCenterView = null;
        int nearestDeltaX = 0;
        for (int count = getChildCount(), i = 0; i < count; i++) {
            View item = getChildAt(i);
            int centerXView = (int) (getDecoratedLeft(item) + viewHalfWidth);
            if (nearestToCenterView == null || Math.abs(nearestDeltaX) > Math.abs(centerX - centerXView)) {
                nearestToCenterView = item;
                nearestDeltaX = (int) (centerX - centerXView);
            }
        }
        return nearestToCenterView != null ? getPosition(nearestToCenterView) : 0;
    }

    private View findCurrentCenterView() {
        float centerX = getWidth() / 2;
        float viewHalfWidth = settings.getViewWidthPx() / 2;
        View nearestToCenterView = null;
        int nearestDeltaX = 0;
        for (int count = getChildCount(), i = 0; i < count; i++) {
            View item = getChildAt(i);
            int centerXView = (int) (getDecoratedLeft(item) + viewHalfWidth);
            if (nearestToCenterView == null || Math.abs(nearestDeltaX) > Math.abs(centerX - centerXView)) {
                nearestToCenterView = item;
                nearestDeltaX = (int) (centerX - centerXView);
            }
        }
        return nearestToCenterView;
    }


}
