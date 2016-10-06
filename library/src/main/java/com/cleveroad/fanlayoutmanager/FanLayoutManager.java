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
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.Random;

//import static com.cleveroad.fanlayoutmanager.AnimationHelperImpl.ANIMATION_VIEW_SCALE_FACTOR;

/**
 * Custom implementation of {@link RecyclerView.LayoutManager}
 * FanLayoutManager change view's position, rotation and translation to create effect fan scrolling.
 * <p>
 * How to use:
 * <p>
 * 1) Create object of FanLayoutManager class. You can use
 * {@link #FanLayoutManager(Context)} with default settings
 * or
 * {@link #FanLayoutManager(Context, FanLayoutManagerSettings)} with custom settings.
 * See {@link FanLayoutManagerSettings} to create custom settings.
 * <p>
 * 2) Set the FanLayoutManager to your RecyclerView. See {@link RecyclerView#setLayoutManager(RecyclerView.LayoutManager)}
 * <p>
 * 3) Use methods {@link #switchItem(RecyclerView, int)} ot select and deselect item.
 * <p>
 * 4) Use method {@link #collapseViews()} to collapse views.
 * <p>
 * 5) Use method {@link #straightenSelectedItem(Animator.AnimatorListener)} to straight selected view.
 * <p>
 * 6) Use method {@link #getSelectedItemPosition()} to get selected item position
 * <p>
 * 7) Use method {@link #isItemSelected()} to check if item is selected or not.
 *
 * @author alex yarovoi
 * @version 1.0
 */
public class FanLayoutManager extends RecyclerView.LayoutManager {
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
    /**
     * true if item selected
     */
    private boolean isSelected = false;

    /**
     * true if views collapsed
     */
    private boolean isCollapsed = false;

    public FanLayoutManager(@NonNull Context context) {
        this(context, null);
    }

    public FanLayoutManager(@NonNull Context context, @Nullable FanLayoutManagerSettings settings) {
        // create default settings
        this.settings = settings == null ? FanLayoutManagerSettings.newBuilder(context).build() : settings;
        // create default animation helper
        animationHelper = new AnimationHelperImpl();
        // create default FanCardScroller
        fanCardScroller = new FanCardScroller(context);
        // set callback which return calculated scroll time
        fanCardScroller.setCardTimeCallback(new FanCardScroller.FanCardTimeCallback() {
            @Override
            public void onTimeForScrollingCalculated(int targetPosition, int time) {
                // select item after scroll to item
                selectItem(targetPosition, time);
            }
        });
        // create default smooth scroller to show item in the middle of the screen
        shiftToCenterCardScroller = new ShiftToCenterCardScroller(context);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState();
        // save center view position
        savedState.mCenterItemPosition = findCurrentCenterViewPos();
        // save selected state for center view
        savedState.isSelected = isSelected;
        // save collapsed state for views
        savedState.isCollapsed = isCollapsed;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state != null && state instanceof FanLayoutManager.SavedState) {
            mPendingSavedState = (FanLayoutManager.SavedState) state;
            // center view position
            scrollToPosition = mPendingSavedState.mCenterItemPosition;
            // position for selected item
            selectedItemPosition = mPendingSavedState.isSelected ? scrollToPosition : RecyclerView.NO_POSITION;
            // selected state
            isSelected = mPendingSavedState.isSelected;
            // collapsed state
            isCollapsed = mPendingSavedState.isCollapsed;
        }
    }

    /**
     * @return selected item position
     */
    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    /**
     * Setter for custom animation helper
     *
     * @param animationHelper custom animation helper.
     */
    @Deprecated
    void setAnimationHelper(@Nullable AnimationHelper animationHelper) {
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

    /**
     * Method create or reuse views for recyclerView.
     *
     * @param recycler recycler from the recyclerView
     */
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
            // fill views if start position not in the middle of screen (restore state)
            fillRightFromCenter(scrollToPosition, centerViewOffset, recycler);
        } else {
            // fill views if start position in the middle of the screen
            fillRightFromCenter(centerViewPosition, centerViewOffset, recycler);
        }

        for (int i = 0; i < viewCache.size(); i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }
        // update rotations.
        updateArcViewPositions();
    }

    /**
     * Measure view with margins and specs
     *
     * @param child      view to measure
     * @param widthSpec  spec for width
     * @param heightSpec spec for height
     */
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

        // after fillRightFromCenter(...) we don't need this param.
        scrollToPosition = RecyclerView.NO_POSITION;
        // after fillRightFromCenter(...) we don't need this param.
        mPendingSavedState = null;

        if (selectedItemPosition != RecyclerView.NO_POSITION && !isSelectAnimationInProcess && !isDeselectAnimationInProcess &&
                !isWaitingToDeselectAnimation && !isWaitingToSelectAnimation) {
            // if item selected and any animation isn't in progress
            deselectItem(selectedItemPosition);
        }
        // if animation in progress block scroll
        if (isDeselectAnimationInProcess || isSelectAnimationInProcess || isViewCollapsing) {
            return 0;
        }
        //
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

    /**
     * Calculate delta x for views.
     *
     * @param dx fling (user scroll gesture) delta x
     * @return delta x for views
     */
    private int scrollHorizontallyInternal(int dx) {
        int childCount = getChildCount();
        // check child count
        if (childCount == 0) {
            return 0;
        }
        // items count in the adapter
        int itemCount = getItemCount();

        View leftView = getChildAt(0);
        View rightView = getChildAt(childCount - 1);
        // search left and right views.
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (getDecoratedLeft(leftView) > getDecoratedLeft(view)) {
                leftView = view;
            }
            if (getDecoratedRight(rightView) < getDecoratedRight(view)) {
                rightView = view;
            }
        }
        // area with filling views. need to find borders
        int viewSpan = getDecoratedRight(rightView) > getWidth() ? getDecoratedRight(rightView) : getWidth() -
                (getDecoratedLeft(leftView) < 0 ? getDecoratedLeft(leftView) : 0);

        // check left and right borders
        if (viewSpan < getWidth()) {
            return 0;
        }

        int delta = 0;
        if (dx < 0) {
            // move views left

            // position for left item in the adapter
            int firstViewAdapterPos = getPosition(leftView);

            if (firstViewAdapterPos > 0) {
                // if item isn't first in the adapter
                delta = dx;
            } else {
                // if item first in the adapter

                // stop scrolling if item in the middle.
                int viewLeft = getDecoratedLeft(leftView) - getWidth() / 2 + getDecoratedMeasuredWidth(leftView) / 2;
                delta = Math.max(viewLeft, dx);
            }
        } else if (dx > 0) {
            // move views right

            // position for right item in the adapter
            int lastViewAdapterPos = getPosition(rightView);

            if (lastViewAdapterPos < itemCount - 1) {
                // if item isn't last in the adapter
                delta = dx;
            } else {
                // if item last in the adapter

                // stop scrolling if item in the middle.
                int viewRight = getDecoratedRight(rightView) + getWidth() / 2 - getDecoratedMeasuredWidth(rightView) / 2;
                int parentRight = getWidth();
                delta = Math.min(viewRight - parentRight, dx);
            }
        }
        return delta;
    }

    /**
     * Change pivot, rotation, translation for view to create fan effect.
     * Change rotation to create bounce effect.
     */
    private void updateArcViewPositions() {

        // +++++ init params +++++
        float halfWidth = getWidth() / 2;
        // minimal radius is recyclerView width * 2
        double radius = getWidth() * 2;
        double powRadius = radius * radius;
        double rotation;
        float halfViewWidth;
        double deltaX;
        double deltaY;
        int viewPosition;
        // ----- init params -----

        for (int count = getChildCount(), i = 0; i < count; i++) {
            View view = getChildAt(i);
            rotation = 0;
            // need to show views in "fan" style

            halfViewWidth = view.getWidth() / 2;

            // change pivot point to center bottom of the view
            view.setPivotX(halfViewWidth);
            view.setPivotY(view.getHeight());

            if (settings.isFanRadiusEnable()) {

                // distance between center of screen to center of view in x-axis
                deltaX = halfWidth - getDecoratedLeft(view) - halfViewWidth;

                // distance in which need to move view in y-axis. Low accuracy
                deltaY = radius - Math.sqrt(powRadius - deltaX * deltaX);
                view.setTranslationY((float) deltaY);

                // calculate view rotation
                rotation = (Math.toDegrees(Math.asin((radius - deltaY) / radius)) - 90) * Math.signum(deltaX);

            }

            viewPosition = getPosition(view);
            Double baseViewRotation = viewRotationsMap.get(viewPosition);

            if (baseViewRotation == null) {
                // generate base (bounce) rotation for view.
                baseViewRotation = random.nextDouble() * settings.getAngleItemBounce() * 2 - settings.getAngleItemBounce();
                viewRotationsMap.put(viewPosition, baseViewRotation);
            }
            view.setRotation((float) (rotation + baseViewRotation));
        }
    }

    /**
     * Method draw view using center view position.
     *
     * @param centerViewPosition position of center view (anchor). This view will be in center
     * @param recycler           Recycler from the recyclerView
     */
    private void fillRightFromCenter(int centerViewPosition, int centerViewOffset, RecyclerView.Recycler recycler) {
        // +++++++++++ Prepare data +++++++++++

        // left limit. need to prepare with before they will be show to user.
        int leftBorder = -(settings.getViewWidthPx() + (isCollapsed ? settings.getViewWidthPx() : 0));

        // right limit.
        int rightBorder = getWidth() + (settings.getViewWidthPx() + (isCollapsed ? settings.getViewWidthPx() : 0));
        int leftViewOffset = centerViewOffset;
        int leftViewPosition = centerViewPosition;

        // margin to draw cards in bottom
        final int baseTopMargin = Math.max(0, getHeight() - settings.getViewHeightPx() - settings.getViewWidthPx() / 4);
        int overlapDistance;
        if (isCollapsed) {

            // overlap distance if views are collapsed
            overlapDistance = -settings.getViewWidthPx() / 4;
        } else {

            // overlap distance if views aren't collapsed
            overlapDistance = settings.getViewWidthPx() / 4;
        }

        boolean fillRight = true;

        // specs for item views
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(settings.getViewWidthPx(), View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(settings.getViewHeightPx(), View.MeasureSpec.EXACTLY);

        // if have to restore state with selected item
        boolean hasPendingStateSelectedItem = mPendingSavedState != null && mPendingSavedState.isSelected &&
                mPendingSavedState.mCenterItemPosition != RecyclerView.NO_POSITION;

        // offset for left and right views in case we have to restore pending state with selected view.
        // this is delta distance between overlap cards state and collapse (selected card) card state
        // need to use ones for all left view and right views
        float deltaOffset = settings.getViewWidthPx() / 2;

        // --------- Prepare data ---------

        // search left position for first view
        while (leftViewOffset > leftBorder) {
            if (isCollapsed) {
                // offset for collapsed views
                leftViewOffset -= (settings.getViewWidthPx() + Math.abs(overlapDistance));
            } else {
                // offset for NOT collapsed views
                leftViewOffset -= (settings.getViewWidthPx() - Math.abs(overlapDistance));
            }
            leftViewPosition--;
        }

        if (leftViewPosition < 0) {
            // if theoretically position for left view is less than left view.
            if (isCollapsed) {
                // offset for collapsed views
                leftViewOffset += (settings.getViewWidthPx() + Math.abs(overlapDistance)) * Math.abs(leftViewPosition);
            } else {
                // offset for NOT collapsed views
                leftViewOffset += (settings.getViewWidthPx() - Math.abs(overlapDistance)) * Math.abs(leftViewPosition);
            }
            leftViewPosition = 0;
        }

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
                // get view from recycler
                view = recycler.getViewForPosition(leftViewPosition);

                // optimization for view rotation
                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                // add vew to the recyclerView
                addView(view);
                // measuring view
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
                // set offsets, with and height in the recyclerView
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
                view.setScaleX(animationHelper.getViewScaleFactor());
                view.setScaleY(animationHelper.getViewScaleFactor());
            }
        }
    }

    /**
     * Method change item state from close to open and open to close (switch state)
     *
     * @param recyclerView         current recycler view. Need to smooth scroll.
     * @param selectedViewPosition item view position
     */
    public void switchItem(@Nullable RecyclerView recyclerView, final int selectedViewPosition) {
        if (isDeselectAnimationInProcess || isSelectAnimationInProcess || isViewCollapsing ||
                isWaitingToDeselectAnimation || isWaitingToSelectAnimation || isSelectedItemStraightened) {
            // block event if any animation in progress
            return;
        }

        if (recyclerView != null) {
            if (this.selectedItemPosition != RecyclerView.NO_POSITION &&
                    this.selectedItemPosition != selectedViewPosition) {
                // if item selected
                deselectItem(recyclerView, this.selectedItemPosition, selectedViewPosition, 0);
                return;
            }
            // if item not selected need to smooth scroll and then select item
            smoothScrollToPosition(recyclerView, null, selectedViewPosition);
        }
    }

    public boolean isItemSelected() {
        return this.selectedItemPosition != RecyclerView.NO_POSITION;
    }


    private void selectItem(final int position, int delay) {

        if (selectedItemPosition == position) {
            // if select already selected item
            deselectItem(selectedItemPosition);
            return;
        }

        // search view by position
        View viewToSelect = null;
        for (int count = getChildCount(), i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (position == getPosition(view)) {
                viewToSelect = view;
            }
        }

        if (viewToSelect == null) {
            // view to select not found!!!
            return;
        }
        // save position of view which will be selected
        selectedItemPosition = position;
        // save selected stay... no way back...
        isSelected = true;
        // open item animation wait for start but not in process.
        // select item animation prepare and wait until smooth scroll is finished
        isWaitingToSelectAnimation = true;

        animationHelper.openItem(viewToSelect, delay * 3 /*need to finish scroll before start open*/,
                new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        super.onAnimationStart(animator);
                        // change state of select animation progress
                        isSelectAnimationInProcess = true;
                        isWaitingToSelectAnimation = false;
                        // shift distance between center view and left, right views.
                        final int delta = settings.getViewWidthPx() / 2;
                        // generate data for animation helper. (calculate final positions for all views)
                        final Collection<ViewAnimationInfo> infoViews = ViewAnimationInfoGenerator.generate(delta,
                                true,
                                FanLayoutManager.this,
                                selectedItemPosition,
                                false);

                        // animate shifting let and right views
                        animationHelper.shiftSideViews(
                                infoViews,
                                0,
                                FanLayoutManager.this,
                                null,
                                new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        // update rotation and translation for all views
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

    /**
     * Deselect selected item. {@link #deselectItem(int)}
     */
    public void deselectItem() {
        deselectItem(selectedItemPosition);
    }

    /**
     * Deselect item with default params. {@link #deselectItem(RecyclerView, int, int, int)}
     *
     * @param position selected item position
     */
    private void deselectItem(final int position) {
        deselectItem(null, position, RecyclerView.NO_POSITION, 0);
    }

    /**
     * Deselect item
     *
     * @param recyclerView     RecyclerView for this LayoutManager
     * @param position         position item for deselect
     * @param scrollToPosition position to scroll after deselect
     * @param delay            waiting duration before start deselect
     */

    private void deselectItem(final RecyclerView recyclerView, final int position, final int scrollToPosition, final int delay) {

        if (position == RecyclerView.NO_POSITION) {
            // if position is default non selected value
            return;
        }

        // search view by position
        View viewToDeselect = null;
        for (int count = getChildCount(), i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (position == getPosition(view)) {
                viewToDeselect = view;
            }
        }

        if (viewToDeselect == null) {
            // search error!!! No view found!!!
            return;
        }

        // remove selected item position
        selectedItemPosition = RecyclerView.NO_POSITION;

        // remove selected state... no way back...
        isSelected = false;

        // wait for start deselect animation
        isWaitingToDeselectAnimation = true;

        // close item animation
        animationHelper.closeItem(viewToDeselect, delay, new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

                // change states
                isDeselectAnimationInProcess = true;
                isWaitingToDeselectAnimation = false;

                // shift distance between center view and left, right views.
                final int delta = settings.getViewWidthPx() / 2;

                // generate data for animation helper. (calculate final positions for all views)
                final Collection<ViewAnimationInfo> infoViews =
                        ViewAnimationInfoGenerator.generate(delta,
                                false,
                                FanLayoutManager.this,
                                position,
                                false);

                // animate shifting let and right views
                animationHelper.shiftSideViews(
                        infoViews,
                        0,
                        FanLayoutManager.this,
                        null,
                        new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                // update rotation and translation for all views
                                updateArcViewPositions();
                            }
                        });
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                isDeselectAnimationInProcess = false;
                if (recyclerView != null && scrollToPosition != RecyclerView.NO_POSITION) {
                    // scroll to new position after deselect animation end
                    smoothScrollToPosition(recyclerView, null, scrollToPosition);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                isDeselectAnimationInProcess = false;
                if (recyclerView != null && scrollToPosition != RecyclerView.NO_POSITION) {
                    // scroll to new position after deselect animation cancel
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
            // show view in the middle of screen
            scrollToCenter();
        }
    }

    /**
     * Scroll views left or right so nearest view will be in the middle of screen
     */
    private void scrollToCenter() {
        View nearestToCenterView = findCurrentCenterView();
        if (nearestToCenterView != null) {
            // scroll to the nearest view
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
            // if position is not in range
            return;
        }
        // smooth scroll to position
        fanCardScroller.setTargetPosition(position);
        startSmoothScroll(fanCardScroller);
    }

    /**
     * Method need to remove bounce item radius
     *
     * @param listener straighten function listener
     */
    public void straightenSelectedItem(Animator.AnimatorListener listener) {
        // check all animations
        if (selectedItemPosition == RecyclerView.NO_POSITION || isSelectAnimationInProcess ||
                isDeselectAnimationInProcess || isSelectedItemStraightened || isWaitingToDeselectAnimation ||
                isWaitingToSelectAnimation || isViewCollapsing) {
            // block if any animation in progress
            return;
        }

        // +++++ prepare data +++++
        View viewToRotate = null;
        View view;
        // ----- prepare data -----

        // search selected view
        for (int count = getChildCount(), i = 0; i < count; i++) {
            view = getChildAt(i);

            if (selectedItemPosition == getPosition(view)) {
                viewToRotate = view;
            }
        }


        if (viewToRotate != null) {

            // start straight animation
            animationHelper.straightenView(viewToRotate, listener);

            // save state
            isSelectedItemStraightened = true;
        }

    }

    /**
     * Method collapsed views (cards).
     */
    public void collapseViews() {
        // check all animations
        if (isSelectAnimationInProcess || isWaitingToSelectAnimation ||
                isDeselectAnimationInProcess || isWaitingToDeselectAnimation ||
                isSelectedItemStraightened || isViewCollapsing) {
            return;
        }
        // steps:
        // 1) Lock screen (Stop scrolling)
        // 2) Collapse all cards
        // 3) Unlock screen
        // 4) Scroll to center nearest card if not selected

        // 1) lock screen
        isViewCollapsing = true;

        // 2) Collapse all cards
        updateItemsByMode();
    }

    /**
     * Method collapsing all views
     */
    private void updateItemsByMode() {

        // collapse distance
        int delta = settings.getViewWidthPx() / 2;

        // generate data for collapse animation
        final Collection<ViewAnimationInfo> infoViews =
                ViewAnimationInfoGenerator.generate(delta,
                        isCollapsed = !isCollapsed,
                        FanLayoutManager.this,
                        findCurrentCenterViewPos(),
                        true);

        // collapse views
        animationHelper.shiftSideViews(infoViews,
                0,
                FanLayoutManager.this,
                new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        // 3) Unlock screen
                        isViewCollapsing = !isViewCollapsing;
                        // 4) Scroll to center nearest card
                        scrollToCenter();
                    }
                }, new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        // update rotation and translation for all views
                        updateArcViewPositions();
                    }
                });
    }

    /**
     * Find view in the middle of screen
     *
     * @return center View
     */
    private View findCurrentCenterView() {
        // +++++ prepare data +++++

        // center of the screen in x-axis
        float centerX = getWidth() / 2;
        float viewHalfWidth = settings.getViewWidthPx() / 2;
        View nearestToCenterView = null;
        int nearestDeltaX = 0;
        View item;
        int centerXView;
        // ----- prepare data -----

        // search nearest to center view
        for (int count = getChildCount(), i = 0; i < count; i++) {
            item = getChildAt(i);
            centerXView = (int) (getDecoratedLeft(item) + viewHalfWidth);
            if (nearestToCenterView == null || Math.abs(nearestDeltaX) > Math.abs(centerX - centerXView)) {
                nearestToCenterView = item;
                nearestDeltaX = (int) (centerX - centerXView);
            }
        }
        return nearestToCenterView;
    }

    /**
     * Find position of view in the middle of screen
     *
     * @return position of center view
     */
    private int findCurrentCenterViewPos() {
        return getPosition(findCurrentCenterView());
    }

    private static class SavedState implements Parcelable {

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
