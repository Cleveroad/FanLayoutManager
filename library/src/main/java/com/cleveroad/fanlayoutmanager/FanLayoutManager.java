package com.cleveroad.fanlayoutmanager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by Alex Yarovoi 16.08.2016
 */
public class FanLayoutManager extends RecyclerView.LayoutManager implements
        ShiftViewListener,
        CardScrollerListener {

    public enum Mode {OVERLAPPING, DISTANCE}

    private Mode mode = Mode.OVERLAPPING;

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

    private int mAnchorPos;
    private int selectedViewPosition = DEFAULT_NON_SELECTED_ITEM_POSITION;

    private boolean isSelectAnimationInProcess = false;
    private boolean isDeselectAnimationInProcess = false;
    private boolean isWaitingToSelectAnimation = false;
    private boolean isWaitingToDeselectAnimation = false;
    private boolean isSelectedItemStraightened = false;

    private boolean needToCalculateNewRotations = true;

    public FanLayoutManager(@NonNull Context context) {
        this(context, null);
    }

    public FanLayoutManager(@NonNull Context context, @Nullable FanLayoutManagerSettings settings) {

        this.settings = settings == null ? FanLayoutManagerSettings.newBuilder(context).build() : settings;
        animationHelper = new AnimationHelperImpl();
        fanCardScroller = new FanCardScroller(context, this);
        fanCardScroller.setCardTimeCallback(new FanCardScroller.FanCardTimeCallback() {
            @Override
            public void onTimeForScrollingCalculated(int targetPosition, int time) {
                selectItem(targetPosition, time);
            }
        });
        shiftToCenterCardScroller = new ShiftToCenterCardScroller(context, this);
    }

    public void setAnimationHelper(@Nullable AnimationHelper animationHelper) {
        this.animationHelper = animationHelper == null ? new AnimationHelperImpl() : animationHelper;
    }

//    @Override
//    public void onLayoutCompleted(RecyclerView.State state) {
//        super.onLayoutCompleted(state);
//    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        fill(recycler);
        mAnchorPos = 0;
    }

    private void fill(RecyclerView.Recycler recycler) {
        View anchorView = getAnchorView();
        viewCache.clear();
        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
            View view = getChildAt(i);
            int pos = getPosition(view);
            viewCache.put(pos, view);
        }

        for (int i = 0; i < viewCache.size(); i++) {
            detachView(viewCache.valueAt(i));
        }
        fillRight(anchorView, recycler);

        for (int i = 0; i < viewCache.size(); i++) {
            recycler.recycleView(viewCache.valueAt(i));
        }

        updateArcViewPositions();
    }

    private View getAnchorView() {
        int childCount = getChildCount();

        int minLeftDistance = -getWidth() / 2;
        View anchorView = null;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int left = getDecoratedLeft(view);
            if (left > minLeftDistance) {
                if (anchorView == null) {
                    anchorView = view;
                }
                if (getDecoratedLeft(anchorView) > left) {
                    anchorView = view;
                }
            }
        }
        return anchorView;
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

        if (!isSelectAnimationInProcess && !isDeselectAnimationInProcess &&
                !isWaitingToDeselectAnimation && !isWaitingToSelectAnimation) {

            deselectItem(selectedViewPosition);
        }

        if (isDeselectAnimationInProcess || isSelectAnimationInProcess) {
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

            view.setRotation((float) (rotation + baseViewRotation));
        }
    }

    private void fillRight(View anchorView, RecyclerView.Recycler recycler) {

        int anchorPosition;
        int anchorDecoratedLeft = 0;

        if (anchorView != null) {
            anchorPosition = getPosition(anchorView);
            anchorDecoratedLeft = getDecoratedLeft(anchorView);
        } else {
            anchorPosition = mAnchorPos;
        }

        int viewPosition = anchorPosition;

        boolean fillRight = true;

        int viewDecoratedLeft = anchorDecoratedLeft;
        final int itemCount = getItemCount();

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(settings.getViewWidthPx(), View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(settings.getViewHeightPx(), View.MeasureSpec.EXACTLY);

        // for increase performance
        final int halfViewWidth = settings.getViewWidthPx() / 2;
        final int halfScreenWidth = getWidth() / 2;
        final int overlapDistance;

        // view overlapping distance
        if(settings.getMode().equals(Mode.OVERLAPPING)) {
           overlapDistance = settings.getViewWidthPx() / 4; // make views overlapping
        } else {
            overlapDistance = - settings.getViewWidthPx() / 4; // make distance between views
        }

        // margin to draw cards in bottom
        final int baseTopMargin = Math.max(0, getHeight() - settings.getViewHeightPx());

        if (anchorView != null) {
            while (viewDecoratedLeft > -halfScreenWidth && viewPosition > 0) {
                viewDecoratedLeft -= (settings.getViewWidthPx() - overlapDistance); // left visible distance
                viewPosition--;
            }
            viewDecoratedLeft += overlapDistance; // overlap for first item
        }

        while (fillRight && viewPosition < itemCount) {
            View view = viewCache.get(viewPosition);
            if (view == null) {
                view = recycler.getViewForPosition(viewPosition);
                addView(view);
                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
                layoutDecorated(view, viewDecoratedLeft - overlapDistance, baseTopMargin,
                        viewDecoratedLeft + settings.getViewWidthPx() - overlapDistance, baseTopMargin + settings.getViewHeightPx());
            } else {
                attachView(view);
                viewCache.remove(viewPosition);
            }
            viewDecoratedLeft = getDecoratedRight(view);
            fillRight = viewDecoratedLeft <= getWidth() + halfViewWidth;
            viewPosition++;
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
            if (this.selectedViewPosition != DEFAULT_NON_SELECTED_ITEM_POSITION &&
                    this.selectedViewPosition != selectedViewPosition) {
                deselectItem(recyclerView, this.selectedViewPosition, selectedViewPosition, 0);
                return;
            }
            smoothScrollToPosition(recyclerView, null, selectedViewPosition);
        }
    }

    public boolean isItemSelected() {
        return this.selectedViewPosition != DEFAULT_NON_SELECTED_ITEM_POSITION;
    }

    public void deselectItem() {
        deselectItem(selectedViewPosition);
    }

    private void selectItem(final int position, int delay) {

        if (selectedViewPosition == position) {
            deselectItem(selectedViewPosition);
            return;
        }

        selectedViewPosition = position;

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
                        final Collection<ViewAnimationInfo> infoViews = generateViewAnimationInfos(-delta, delta, position);
                        animationHelper.shiftSideViews(infoViews, 0, FanLayoutManager.this);
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

        if (selectedViewPosition == DEFAULT_NON_SELECTED_ITEM_POSITION) {
            return;
        }

        selectedViewPosition = DEFAULT_NON_SELECTED_ITEM_POSITION;

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
                final Collection<ViewAnimationInfo> infoViews = generateViewAnimationInfos(delta, -delta, position);

                animationHelper.shiftSideViews(infoViews, 0, FanLayoutManager.this);
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
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
        if (position >= getItemCount()) {
            return;
        }

        fanCardScroller.setTargetPosition(position);
        startSmoothScroll(fanCardScroller);
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        if (getChildCount() == 0) {
            return null;
        }
        final int firstChildPos = getPosition(getChildAt(0));
        final int direction = targetPosition < firstChildPos ? -1 : 1;
        return new PointF(direction, 0);
    }


    private Collection<ViewAnimationInfo> generateViewAnimationInfos(int leftViewDelta, int rightViewDelta, int itemPosition) {

        final List<ViewAnimationInfo> infoViews = new ArrayList<>();

        for (int count = getChildCount(), i = 0; i < count; i++) {
            View view = getChildAt(i);
            int viewPosition = getPosition(view);
            if (viewPosition == itemPosition) {
                continue;
            }
            ViewAnimationInfo info = new ViewAnimationInfo();
            info.view = view;
            info.startLeft = getDecoratedLeft(view);
            info.startRight = getDecoratedRight(view);
            info.top = getDecoratedTop(view);
            info.bottom = getDecoratedBottom(view);

            if (viewPosition < itemPosition) {
                // left view
                info.finishLeft = info.startLeft + leftViewDelta;
                info.finishRight = info.startRight + leftViewDelta;
            } else {
                // right view
                info.finishLeft = info.startLeft + rightViewDelta;
                info.finishRight = info.startRight + rightViewDelta;
            }

            infoViews.add(info);
        }
        return infoViews;
    }


    // TODO: 19.08.16 migrate to AnimationHelper
    private void straightenView(View view, int position, @Nullable Animator.AnimatorListener listener) {
        if(view != null) {
            ObjectAnimator viewObjectAnimator = ObjectAnimator.ofFloat(view,
                    "rotation", viewRotationsMap.get(position).floatValue(), 0f);
            viewObjectAnimator.setDuration(150);
            viewObjectAnimator.setInterpolator(new DecelerateInterpolator());
            if(listener != null) {
                viewObjectAnimator.addListener(listener);
            }
            viewObjectAnimator.start();
        }

    }



    private void destraightenView(View view, int position, @Nullable Animator.AnimatorListener listener) {
        if(view != null) {
            ObjectAnimator viewObjectAnimator = ObjectAnimator.ofFloat(view,
                    "rotation", 0f, viewRotationsMap.get(position).floatValue());
            viewObjectAnimator.setDuration(150);
            viewObjectAnimator.setInterpolator(new DecelerateInterpolator());
            if(listener != null) {
                viewObjectAnimator.addListener(listener);
            }
            viewObjectAnimator.start();
        }

    }

    public void straightenSelectedItem() {

        if(selectedViewPosition != DEFAULT_NON_SELECTED_ITEM_POSITION && !isSelectAnimationInProcess &&
                !isDeselectAnimationInProcess && !isSelectedItemStraightened) {
            View viewToRotate = null;
            for (int count = getChildCount(), i = 0; i < count; i++) {
                View view = getChildAt(i);
                if (selectedViewPosition == getPosition(view)) {
                    viewToRotate = view;
                }
            }
            if(viewToRotate != null) {
                straightenView(viewToRotate, selectedViewPosition, null);
                isSelectedItemStraightened = true;
            }

        }
    }

    public void straightenSelectedItem(Animator.AnimatorListener listener) {

        if(selectedViewPosition != DEFAULT_NON_SELECTED_ITEM_POSITION && !isSelectAnimationInProcess &&
                !isDeselectAnimationInProcess && !isSelectedItemStraightened) {
            View viewToRotate = null;
            for (int count = getChildCount(), i = 0; i < count; i++) {
                View view = getChildAt(i);
                if (selectedViewPosition == getPosition(view)) {
                    viewToRotate = view;
                }
            }
            if(viewToRotate != null) {
                straightenView(viewToRotate, selectedViewPosition, listener);
                isSelectedItemStraightened = true;
            }

        }
    }

    public void destraightenSelectedItem(Animator.AnimatorListener listener) {

        if(selectedViewPosition != DEFAULT_NON_SELECTED_ITEM_POSITION && !isSelectAnimationInProcess &&
                !isDeselectAnimationInProcess && isSelectedItemStraightened) {
            View viewToRotate = null;
            for (int count = getChildCount(), i = 0; i < count; i++) {
                View view = getChildAt(i);
                if (selectedViewPosition == getPosition(view)) {
                    viewToRotate = view;
                }
            }
            if(viewToRotate != null) {
                destraightenView(viewToRotate, selectedViewPosition, listener);
                isSelectedItemStraightened = false;
            }

        }
    }

    public void destraightenSelectedItem() {

        if(selectedViewPosition != DEFAULT_NON_SELECTED_ITEM_POSITION && !isSelectAnimationInProcess &&
                !isDeselectAnimationInProcess && isSelectedItemStraightened) {
            View viewToRotate = null;
            for (int count = getChildCount(), i = 0; i < count; i++) {
                View view = getChildAt(i);
                if (selectedViewPosition == getPosition(view)) {
                    viewToRotate = view;
                }
            }
            if(viewToRotate != null) {
                destraightenView(viewToRotate, selectedViewPosition, null);
                isSelectedItemStraightened = false;
            }
        }
    }

    public void switchMode(FanLayoutManager.Mode mode) {
        settings.setMode(mode);
        updateItemsByMode();
    }

    private void updateItemsByMode() {
        selectedViewPosition = DEFAULT_NON_SELECTED_ITEM_POSITION;
        int delta = settings.getViewWidthPx() / 2;
        Collection<ViewAnimationInfo> infoViews;
        int centerViewPos = findCurrentCenterViewPos();
        if(settings.getMode().equals(Mode.OVERLAPPING)) {
            infoViews = generateViewAnimationInfosForModeSwitching(delta, -delta, centerViewPos);
        } else {
            infoViews = generateViewAnimationInfosForModeSwitching(-delta, delta, centerViewPos);
        }

        animationHelper.shiftSideViews(infoViews, 0, FanLayoutManager.this);

    }

    private Collection<ViewAnimationInfo> generateViewAnimationInfosForModeSwitching(int leftViewDelta, int rightViewDelta, int centerPosition) {
        final List<ViewAnimationInfo> infoViews = new ArrayList<>();

        for (int count = getChildCount(), i = 0; i < count; i++) {
            View view = getChildAt(i);
            int viewPosition = getPosition(view);
            if (viewPosition == centerPosition) {
                continue;
            }
            ViewAnimationInfo info = new ViewAnimationInfo();
            info.view = view;
            info.startLeft = getDecoratedLeft(view);
            info.startRight = getDecoratedRight(view);
            info.top = getDecoratedTop(view);
            info.bottom = getDecoratedBottom(view);
            int difference;
            if (viewPosition < centerPosition) {
                // left view
                difference = centerPosition - viewPosition;
                info.finishLeft = info.startLeft + leftViewDelta * difference;
                info.finishRight = info.startRight + leftViewDelta * difference;
            } else {
                // right view
                difference = viewPosition - centerPosition;
                info.finishLeft = info.startLeft + rightViewDelta * difference;
                info.finishRight = info.startRight + rightViewDelta * difference;
            }

            infoViews.add(info);
        }
        return infoViews;
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
}
