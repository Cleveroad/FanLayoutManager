package com.cleveroad.fanlayoutmanager;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.Random;

import static com.cleveroad.fanlayoutmanager.AnimationHelperImpl.ANIMATION_VIEW_SCALE_FACTOR;
import static com.cleveroad.fanlayoutmanager.FanLayoutManagerSettings.DirectionMode.FROM_CENTER;

/**
 * Created by Alex Yarovoi 16.08.2016
 */
public class FanLayoutManager extends RecyclerView.LayoutManager {

    public static final String TAG = "FanLayoutManager";
    /**
     * Settings for fan layout manager. {@link FanLayoutManagerSettings.Builder}
     */
    private final FanLayoutManagerSettings settings;

    /**
     * Map with view (card) rotations. This need to save bounce rotations for views.
     * {@link #updateArcViewPositions}
     */
    private final SparseArray<Double> viewRotationsMap = new SparseArray<>();
    /**
     * Map with view cache.
     */
    private final SparseArray<View> viewCache = new SparseArray<>();
    /**
     * LinearSmoothScroller for switch views.
     */
    private final FanCardScroller fanCardScroller;
    /**
     * LinearSmoothScroller to show view in the middle of the screen.
     */
    private final ShiftToCenterCardScroller shiftToCenterCardScroller;

    /**
     * Just random ))
     */
    private final Random random = new Random();

    /**
     * Helper module need to implement 'open','close', 'shift' views functionality.
     * By default using {@link AnimationHelperImpl}
     * Can be changed {@link #setAnimationHelper(AnimationHelper)}
     */
    @NonNull
    private AnimationHelper animationHelper;
    /**
     * Position of selected item in adapter. ADAPTER!!
     */
    private int selectedItemPosition = RecyclerView.NO_POSITION;
    /**
     * Position of item we need to scroll to right now.
     */
    private int scrollToPosition = RecyclerView.NO_POSITION;
    /**
     * Need to block some events between smooth scroll and select item animation.
     * true before start smoothScroll to selected item
     * false after smooth scroll finished and after select animation is started.
     */
    private boolean isWaitingToSelectAnimation = false;
    /**
     * Need to block some events while scaling view.
     * true right after smooth scroll finished scrolling.
     */
    private boolean isSelectAnimationInProcess = false;

    /**
     * Need to block some events while deselecting item is preparing.
     */
    private boolean isWaitingToDeselectAnimation = false;
    /**
     * Need to block some events.
     */
    private boolean isDeselectAnimationInProcess = false;

    /**
     * Flag using to change bounce radius.
     */
    private boolean isSelectedItemStraightened = false;

    /**
     * Need to block some events while collapsing views.
     */
    private boolean isViewCollapsing = false;

    /**
     * Saved state for layout manager.
     */
    private SavedState mPendingSavedState;

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
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState();
        savedState.mCenterItemPosition = findCurrentCenterViewPos();
        savedState.isSelected = selectedItemPosition != RecyclerView.NO_POSITION;
        savedState.isCollapsed = settings.getDirectionCollapse() == FanLayoutManagerSettings.DirectionCollapse.FROM_EACH_OTHER;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state != null && state instanceof FanLayoutManager.SavedState) {
            mPendingSavedState = (FanLayoutManager.SavedState) state;
            settings.setDirectionCollapse(mPendingSavedState.isCollapsed ?
                    FanLayoutManagerSettings.DirectionCollapse.FROM_EACH_OTHER :
                    FanLayoutManagerSettings.DirectionCollapse.FROM_CENTER);

            settings.setDirectionMode(mPendingSavedState.isSelected ?
                    FanLayoutManagerSettings.DirectionMode.FROM_CENTER :
                    FanLayoutManagerSettings.DirectionMode.TO_CENTER);

            scrollToPosition = mPendingSavedState.mCenterItemPosition;
            selectedItemPosition = mPendingSavedState.isSelected ? scrollToPosition : RecyclerView.NO_POSITION;
            requestLayout();
        }
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    /**
     * Setter for custom animation helper
     *
     * @param animationHelper custom animation helper.
     */
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
        fill(recycler);
    }

    private void fill(RecyclerView.Recycler recycler) {

        // find center view before detach or recycle all views
        View centerView = findCurrentCenterView();

        // position for center view
        int centerViewPosition = centerView == null ? 0 : getPosition(centerView);

        // left offset for center view
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

        // main fill logic
        if (scrollToPosition != RecyclerView.NO_POSITION) {
            fillRightFromCenter(scrollToPosition, centerViewOffset, recycler);
        } else {
            fillRightFromCenter(centerViewPosition, centerViewOffset, recycler);
        }

        // after fillRightFromCenter(...) we don't need this param.
        scrollToPosition = RecyclerView.NO_POSITION;

        for (int i = 0; i < viewCache.size(); i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }
        // update rotations.
        updateArcViewPositions();
    }

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
        if (selectedItemPosition != RecyclerView.NO_POSITION && !isSelectAnimationInProcess && !isDeselectAnimationInProcess &&
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

    private void updateArcViewPositions() {
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
        Log.e(TAG, "fillRightFromCenter");
        // +++++++++++ Prepare data +++++++++++

        // left limit. need to prepare with before they will be show to user.
        int leftBorder = -settings.getViewWidthPx();
        int rightBorder = getWidth() + settings.getViewWidthPx();
        int leftViewOffset = centerViewOffset;
        int leftViewPosition = centerViewPosition;

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

        boolean hasPendingStateSelectedItem = mPendingSavedState != null && mPendingSavedState.isSelected &&
                mPendingSavedState.mCenterItemPosition != RecyclerView.NO_POSITION;

        // offset for left and right views in case we have to restore pending state with selected view.
        // this is delta distance between overlap cards state and collapse (selected card) card state
        // need to use ones for all left view and right views
        float deltaOffset = settings.getViewWidthPx() / 2;

        // --------- Prepare data ---------

        // search left position for first view
        while (leftViewOffset > leftBorder) {
            if (settings.getDirectionCollapse() == FanLayoutManagerSettings.DirectionCollapse.FROM_EACH_OTHER) {
                leftViewOffset -= (settings.getViewWidthPx() + Math.abs(overlapDistance));
            } else {
                leftViewOffset -= (settings.getViewWidthPx() - Math.abs(overlapDistance));
            }
            leftViewPosition--;
        }

        if (leftViewPosition < 0) {
            if (settings.getDirectionCollapse() == FanLayoutManagerSettings.DirectionCollapse.FROM_EACH_OTHER) {
                leftViewOffset += (settings.getViewWidthPx() + Math.abs(overlapDistance)) * Math.abs(leftViewPosition);
            } else {
                leftViewOffset += (settings.getViewWidthPx() - Math.abs(overlapDistance)) * Math.abs(leftViewPosition);
            }
            leftViewPosition = 0;
        }
//        Log.e(TAG, "centerViewPosition = " + centerViewPosition + " || leftViewPosition = " + leftViewPosition + " || leftViewOffset = " + leftViewOffset);

        // offset for left views if we restore state and have selected item
        if (hasPendingStateSelectedItem && leftViewPosition != mPendingSavedState.mCenterItemPosition) {
            leftViewOffset += -deltaOffset;
        }

        while (fillRight && leftViewPosition < getItemCount()) {

            // offset for current view if we restore state and have selected item
            if (hasPendingStateSelectedItem && leftViewPosition == mPendingSavedState.mCenterItemPosition && leftViewPosition != 0) {
                leftViewOffset += deltaOffset;
            }

            // get view from local cache
            View view = viewCache.get(leftViewPosition);

            if (view == null) {
                view = recycler.getViewForPosition(leftViewPosition);

                // optimization for view rotation
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                addView(view);
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);

                layoutDecorated(view, leftViewOffset, baseTopMargin,
                        leftViewOffset + settings.getViewWidthPx(), baseTopMargin + settings.getViewHeightPx());
            } else {
                attachView(view);
                viewCache.remove(leftViewPosition);
            }

            // calculate position for next view. last position + view height - overlap between views.
            leftViewOffset = leftViewOffset + settings.getViewWidthPx() - overlapDistance;

            // check right border. stop loop if next view is > then right border.
            fillRight = leftViewOffset < rightBorder;

            // offset for right views if we restore state and have selected item
            if (hasPendingStateSelectedItem && leftViewPosition == mPendingSavedState.mCenterItemPosition) {
                leftViewOffset += deltaOffset;
            }

            leftViewPosition++;
        }

        // if we have to restore state with selected item
        // this part need to scale center selected view
        if (hasPendingStateSelectedItem) {
            View view = findCurrentCenterView();
            if (view != null) {
                view.setScaleX(ANIMATION_VIEW_SCALE_FACTOR);
                view.setScaleY(ANIMATION_VIEW_SCALE_FACTOR);
            }
            mPendingSavedState = null;
        }
    }

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
            if (this.selectedItemPosition != RecyclerView.NO_POSITION &&
                    this.selectedItemPosition != selectedViewPosition) {
                deselectItem(recyclerView, this.selectedItemPosition, selectedViewPosition, 0);
                return;
            }
            smoothScrollToPosition(recyclerView, null, selectedViewPosition);
        }
    }

    public boolean isItemSelected() {
        return this.selectedItemPosition != RecyclerView.NO_POSITION;
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

                        animationHelper.shiftSideViews(infoViews, 0, FanLayoutManager.this, null, new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                updateArcViewPositions();
                            }
                        });
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
        deselectItem(null, position, RecyclerView.NO_POSITION, 0);
    }

    private void deselectItem(final RecyclerView recyclerView, final int position, final int scrollToPosition, final int delay) {

        if (selectedItemPosition == RecyclerView.NO_POSITION) {
            return;
        }

        selectedItemPosition = RecyclerView.NO_POSITION;

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

                animationHelper.shiftSideViews(infoViews, 0, FanLayoutManager.this, null, new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        updateArcViewPositions();
                    }
                });
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isDeselectAnimationInProcess = false;
                if (recyclerView != null && scrollToPosition != RecyclerView.NO_POSITION) {
                    smoothScrollToPosition(recyclerView, null, scrollToPosition);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                isDeselectAnimationInProcess = false;
                if (recyclerView != null && scrollToPosition != RecyclerView.NO_POSITION) {
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

//    // TODO: 19.08.16 migrate to AnimationHelper
//    private void straightenView(View view, float radius, @Nullable Animator.AnimatorListener listener) {
//        if (view != null) {
//            ObjectAnimator viewObjectAnimator = ObjectAnimator.ofFloat(view,
//                    "rotation", radius, 0f);
//            viewObjectAnimator.setDuration(150);
//            viewObjectAnimator.setInterpolator(new DecelerateInterpolator());
//            if (listener != null) {
//                viewObjectAnimator.addListener(listener);
//            }
//            viewObjectAnimator.start();
//        }
//
//    }

    /**
     * Method need to remove bounce item radius
     *
     * @param listener straighten function listener
     */
    public void straightenSelectedItem(Animator.AnimatorListener listener) {

        if (selectedItemPosition != RecyclerView.NO_POSITION && !isSelectAnimationInProcess &&
                !isDeselectAnimationInProcess && !isSelectedItemStraightened) {
            View viewToRotate = null;
            for (int count = getChildCount(), i = 0; i < count; i++) {
                View view = getChildAt(i);
                if (selectedItemPosition == getPosition(view)) {
                    viewToRotate = view;
                }
            }
            if (viewToRotate != null) {
                animationHelper.straightenView(viewToRotate, viewRotationsMap.get(selectedItemPosition).floatValue(), listener);
                isSelectedItemStraightened = true;
            }

        }
    }

    /**
     * Method collapsed views (cards) from center card or each other.
     */
    public void collapseViews() {
        // 1) Lock screen (Stop scrolling)
        // 2) Collapse all cards
        // 3) Unlock screen
        // 4) Scroll to center nearest card if not selected

        // 1) lock screen
        isViewCollapsing = !isViewCollapsing;
//TODO need to fix direction mode with collapse!!
        settings.setDirectionMode(settings.getDirectionMode() == FanLayoutManagerSettings.DirectionMode.TO_CENTER ?
                FanLayoutManagerSettings.DirectionMode.FROM_CENTER : FanLayoutManagerSettings.DirectionMode.TO_CENTER);

        settings.setDirectionCollapse(settings.getDirectionMode() == FanLayoutManagerSettings.DirectionMode.FROM_CENTER ?
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
        }, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                updateArcViewPositions();
            }
        });
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

    private int findCurrentCenterViewPos() {
        return getPosition(findCurrentCenterView());
    }

    /**
     * @hide
     */
    public static class SavedState implements Parcelable {

        public static final Parcelable.Creator<FanLayoutManager.SavedState> CREATOR
                = new Parcelable.Creator<FanLayoutManager.SavedState>() {
            @Override
            public FanLayoutManager.SavedState createFromParcel(Parcel in) {
                return new FanLayoutManager.SavedState(in);
            }

            @Override
            public FanLayoutManager.SavedState[] newArray(int size) {
                return new FanLayoutManager.SavedState[size];
            }
        };

        int mCenterItemPosition = RecyclerView.NO_POSITION;
        boolean isCollapsed;
        boolean isSelected;

        public SavedState() {

        }

        SavedState(Parcel in) {
            mCenterItemPosition = in.readInt();
            isCollapsed = in.readInt() == 1;
            isSelected = in.readInt() == 1;
        }

        public SavedState(FanLayoutManager.SavedState other) {
            mCenterItemPosition = other.mCenterItemPosition;
            isCollapsed = other.isCollapsed;
            isSelected = other.isSelected;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(mCenterItemPosition);
            dest.writeInt(isCollapsed ? 1 : 0);
            dest.writeInt(isSelected ? 1 : 0);
        }
    }

}
