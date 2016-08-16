//package com.cleveroad.testrecycler.to_destroy;
//
//import android.animation.Animator;
//import android.content.Context;
//import android.graphics.PointF;
//import android.graphics.Rect;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.util.SparseArray;
//import android.view.View;
//import android.view.ViewGroup;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Random;
//
//public class FanLayoutManager extends RecyclerView.LayoutManager implements
//        ShiftViewListener,
//        CardScrollerListener {
//    public static final String TAG = "FanLayoutManager";
//    private static final float VIEW_HEIGHT_PERCENT = 1f / 3f;
//    private static final float VIEW_WIDTH_PERCENT = 1f / 3f;
//
//    private final SparseArray<Double> viewRotationsMap = new SparseArray<>();
//    @NonNull
//    private final AnimationHelper animationHelper;
//
//    private final FanCardScroller fanCardScroller;
//    private final ShiftToCenterCardScroller shiftToCenterCardScroller;
//    private final Random random = new Random();
//    private SparseArray<View> viewCache = new SparseArray<>();
//    private Context context;
//    private int mAnchorPos;
//    private int selectedViewPosition = -1;
//    private boolean isSelectAnimationInProcess = false;
//    private boolean isDeselectAnimationInProcess = false;
//    private boolean isWaitingToSelectAnimation = false;
//    private boolean isWaitingToDeselectAnimation = false;
//
//    public FanLayoutManager(Context context) {
//        this.context = context;
//        animationHelper = new AnimationHelperImpl();
//        fanCardScroller = new FanCardScroller(context, this);
//        fanCardScroller.setCardTimeCallback(new FanCardScroller.FanCardTimeCallback() {
//            @Override
//            public void onTimeForScrollingCalculated(int targetPosition, int time) {
//                selectItem(targetPosition, time);
//            }
//        });
//        shiftToCenterCardScroller = new ShiftToCenterCardScroller(context, this);
//    }
//
//    @Override
//    public void onLayoutCompleted(RecyclerView.State state) {
//        super.onLayoutCompleted(state);
//    }
//
//    @Override
//    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//    }
//
//    @Override
//    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        detachAndScrapAttachedViews(recycler);
//        fill(recycler);
//        mAnchorPos = 0;
//    }
//
//    private void fill(RecyclerView.Recycler recycler) {
//        View anchorView = getAnchorView();
//        viewCache.clear();
//        for (int i = 0, cnt = getChildCount(); i < cnt; i++) {
//            View view = getChildAt(i);
//            int pos = getPosition(view);
//            viewCache.put(pos, view);
//        }
//
//        for (int i = 0; i < viewCache.size(); i++) {
//            detachView(viewCache.valueAt(i));
//        }
//        fillRight(anchorView, recycler);
//
//        for (int i = 0; i < viewCache.size(); i++) {
//            recycler.recycleView(viewCache.valueAt(i));
//        }
//
//        updateArcViewPositions();
//    }
//
//    private View getAnchorView() {
//        int childCount = getChildCount();
//
//        int minLeftDistance = -getWidth() / 2;
//        View anchorView = null;
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
//        return anchorView;
//    }
//
//    private void measureChildWithDecorationsAndMargin(View child, int widthSpec, int heightSpec) {
//        Rect decorRect = new Rect();
//        calculateItemDecorationsForChild(child, decorRect);
//        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) child.getLayoutParams();
//        widthSpec = updateSpecWithExtra(widthSpec, lp.leftMargin + decorRect.left,
//                lp.rightMargin + decorRect.right);
//        heightSpec = updateSpecWithExtra(heightSpec, lp.topMargin + decorRect.top,
//                lp.bottomMargin + decorRect.bottom);
//        child.measure(widthSpec, heightSpec);
//    }
//
//    private int updateSpecWithExtra(int spec, int startInset, int endInset) {
//        if (startInset == 0 && endInset == 0) {
//            return spec;
//        }
//        final int mode = View.MeasureSpec.getMode(spec);
//        if (mode == View.MeasureSpec.AT_MOST || mode == View.MeasureSpec.EXACTLY) {
//            return View.MeasureSpec.makeMeasureSpec(
//                    View.MeasureSpec.getSize(spec) - startInset - endInset, mode);
//        }
//        return spec;
//    }
//
//    @Override
//    public boolean canScrollHorizontally() {
//        return true;
//    }
//
//
//    @Override
//    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
//
//        if (!isSelectAnimationInProcess && !isDeselectAnimationInProcess && !isWaitingToDeselectAnimation && !isWaitingToSelectAnimation) {
//            deselectItem(selectedViewPosition);
//        }
//
//        if (isDeselectAnimationInProcess || isSelectAnimationInProcess) {
//            return 0;
//        }
//
//        int delta = scrollHorizontallyInternal(dx);
//        offsetChildrenHorizontal(-delta);
//        fill(recycler);
//        return delta;
//    }
//
//    private int scrollHorizontallyInternal(int dx) {
//        int childCount = getChildCount();
//
//        if (childCount == 0) {
//            return 0;
//        }
//
//        int itemCount = getItemCount();
//
//        View leftView = getChildAt(0);
//        View rightView = getChildAt(childCount - 1);
//
//        for (int i = 0; i < getChildCount(); i++) {
//            View view = getChildAt(i);
//            if (getDecoratedLeft(leftView) > getDecoratedLeft(view)) {
//                leftView = view;
//            }
//            if (getDecoratedRight(rightView) < getDecoratedRight(view)) {
//                rightView = view;
//            }
//        }
//
//        int viewSpan = getDecoratedRight(rightView) > getWidth() ? getDecoratedRight(rightView) : getWidth() -
//                (getDecoratedLeft(leftView) < 0 ? getDecoratedLeft(leftView) : 0);
//
//        if (viewSpan < getWidth()) {
//            return 0;
//        }
//
//        int delta = 0;
//        if (dx < 0) {
//            int firstViewAdapterPos = getPosition(leftView);
//            if (firstViewAdapterPos > 0) { //если верхняя вюшка не самая первая в адаптере
//                delta = dx;
//            } else {
//                int viewLeft = getDecoratedLeft(leftView) - getWidth() / 2 + getDecoratedMeasuredWidth(leftView) / 2;
//                delta = Math.max(viewLeft, dx);
//            }
//        } else if (dx > 0) {
//            int lastViewAdapterPos = getPosition(rightView);
//            if (lastViewAdapterPos < itemCount - 1) { //если нижняя вюшка не самая последняя в адаптере
//                delta = dx;
//            } else {
//                int viewRight = getDecoratedRight(rightView) + getWidth() / 2 - getDecoratedMeasuredWidth(rightView) / 2;
//                int parentRight = getWidth();
//                delta = Math.min(viewRight - parentRight, dx);
//            }
//        }
//        return delta;
//    }
//
//    @Override
//    public void updateArcViewPositions() {
//        int childCount = getChildCount();
//        float halfWidth = getWidth() / 2;
//        double radius = getHeight();
//        double powRadius = radius * radius;
//
//        for (int i = 0; i < childCount; i++) {
//            View view = getChildAt(i);
//
//            float halfViewWidth = view.getWidth() / 2;
//
//            view.setPivotX(halfViewWidth);
//            view.setPivotY(view.getHeight());
//
//            double deltaX = halfWidth - getDecoratedLeft(view) - halfViewWidth;
//            double deltaY = radius - Math.sqrt(powRadius - deltaX * deltaX);
//
//            view.setTranslationY((float) deltaY);
//
//            double rotation = Math.toDegrees(Math.asin((radius - deltaY) / radius)) - 90;
////            // set rotation and side where is card
//            int viewPosition = getPosition(view);
//            Double baseViewRotation = viewRotationsMap.get(viewPosition);
//
//            if (baseViewRotation == null) {
//                viewRotationsMap.put(viewPosition, baseViewRotation = random.nextDouble() * 10 - 5);
//            }
//
//            view.setRotation((float) (rotation * Math.signum(deltaX) + baseViewRotation));
//        }
//    }
//
//    private void fillRight(View anchorView, RecyclerView.Recycler recycler) {
//
//        int anchorPosition;
//        int anchorDecoratedLeft = 0;
//
//        if (anchorView != null) {
//            anchorPosition = getPosition(anchorView);
//            anchorDecoratedLeft = getDecoratedLeft(anchorView);
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
//        final int viewWidth = (int) (getWidth() * VIEW_WIDTH_PERCENT);
//        final int viewHeight = (int) (getHeight() * VIEW_HEIGHT_PERCENT);
//
//        final int widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
//        final int heightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY);
//
//        // for increase performance
//        final int halfWidth = viewWidth / 2;
//        final int halfScreenWidth = getWidth() / 2;
//
//        // view overlapping distance
//        final int overlapDistance = viewWidth / 4;
//        // margin to draw cards in bottom
//        final int baseTopMargin = getHeight() - viewHeight;
//
//        if (anchorView != null) {
//            while (viewDecoratedLeft > -halfScreenWidth && viewPosition > 0) {
//                viewDecoratedLeft -= (viewWidth - overlapDistance); // left visible distance
//                viewPosition--;
//            }
//            viewDecoratedLeft += overlapDistance; // overlap for first item
//        }
//
//        while (fillRight && viewPosition < itemCount) {
//            View view = viewCache.get(viewPosition);
//            if (view == null) {
//                view = recycler.getViewForPosition(viewPosition);
//                addView(view);
//                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
//                layoutDecorated(view, viewDecoratedLeft - overlapDistance, baseTopMargin, viewDecoratedLeft + viewWidth - overlapDistance, getHeight());
//            } else {
//                attachView(view);
//                viewCache.remove(viewPosition);
//            }
//            viewDecoratedLeft = getDecoratedRight(view);
//            fillRight = viewDecoratedLeft <= getWidth() + halfWidth;
//            viewPosition++;
//        }
//    }
//
//    /**
//     * Method change item state from close to open and open to close (switch state)
//     *
//     * @param recyclerView         current recycler view. Need to smooth scroll.
//     * @param selectedViewPosition item view position
//     */
//    public void switchItem(@Nullable RecyclerView recyclerView, final int selectedViewPosition) {
//        if (isDeselectAnimationInProcess || isSelectAnimationInProcess) {
//            return;
//        }
//
//        if (recyclerView != null) {
//            if (this.selectedViewPosition != -1 && this.selectedViewPosition != selectedViewPosition) {
//                deselectItem(recyclerView, this.selectedViewPosition, selectedViewPosition, 0);
//                return;
//            }
//            smoothScrollToPosition(recyclerView, null, selectedViewPosition);
//        }
//    }
//
//    private void selectItem(final int position, int delay) {
//
//        if (selectedViewPosition == position) {
//            deselectItem(selectedViewPosition);
//            return;
//        }
//
//        selectedViewPosition = position;
//
//        View viewToSelect = null;
//        for (int count = getChildCount(), i = 0; i < count; i++) {
//            View view = getChildAt(i);
//            if (position == getPosition(view)) {
//                viewToSelect = view;
//            }
//        }
//
//        if (viewToSelect == null) {
//            return;
//        }
//        // open item animation wait for start but not in process.
//        isWaitingToSelectAnimation = true;
//
//        animationHelper.openItem(viewToSelect, delay * 3 /*need to finish scroll before start open*/,
//                new SimpleAnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animator) {
//                        super.onAnimationStart(animator);
//
//                        isSelectAnimationInProcess = true;
//                        isWaitingToSelectAnimation = false;
//
//                        final int delta = (int) (getWidth() * VIEW_WIDTH_PERCENT / 2);
//                        final Collection<ViewAnimationInfo> infoViews = generateViewAnimationInfos(-delta, delta, position);
//                        animationHelper.shiftSideViews(infoViews, 0, FanLayoutManager.this);
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animator) {
//                        isSelectAnimationInProcess = false;
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animator) {
//                        isSelectAnimationInProcess = false;
//                    }
//                });
//    }
//
//    private void deselectItem(final int position) {
//        deselectItem(null, position, -1, 0);
//    }
//
//    private void deselectItem(final RecyclerView recyclerView, final int position, final int scrollToPosition, final int delay) {
//        if (selectedViewPosition == -1) {
//            return;
//        }
//        selectedViewPosition = -1;
//
//        View viewToDeselect = null;
//        for (int count = getChildCount(), i = 0; i < count; i++) {
//            View view = getChildAt(i);
//            if (position == getPosition(view)) {
//                viewToDeselect = view;
//            }
//        }
//
//        if (viewToDeselect == null) {
//            return;
//        }
//        isWaitingToDeselectAnimation = true;
//        animationHelper.closeItem(viewToDeselect, delay, new SimpleAnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//                super.onAnimationStart(animator);
//                isDeselectAnimationInProcess = true;
//                isWaitingToDeselectAnimation = false;
//                final int delta = (int) (getWidth() * VIEW_WIDTH_PERCENT / 2);
//                final Collection<ViewAnimationInfo> infoViews = generateViewAnimationInfos(delta, -delta, position);
//
//                animationHelper.shiftSideViews(infoViews, 0, FanLayoutManager.this);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                isDeselectAnimationInProcess = false;
//                if (recyclerView != null && scrollToPosition != -1) {
//                    smoothScrollToPosition(recyclerView, null, scrollToPosition);
//                }
//
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//                isDeselectAnimationInProcess = false;
//                if (recyclerView != null && scrollToPosition != -1) {
//                    smoothScrollToPosition(recyclerView, null, scrollToPosition);
//                }
//            }
//        });
//
//    }
//
//    /**
//     * {@link RecyclerView#SCROLL_STATE_IDLE},
//     * {@link RecyclerView#SCROLL_STATE_DRAGGING},
//     * {@link RecyclerView#SCROLL_STATE_SETTLING}
//     */
//    @Override
//    public void onScrollStateChanged(int state) {
//        super.onScrollStateChanged(state);
//        if (state == RecyclerView.SCROLL_STATE_IDLE) {
//            scrollToCenter();
//        }
//    }
//
//    private void scrollToCenter() {
//        float centerX = getWidth() / 2;
//        float viewHalfWidth = (getWidth() * VIEW_WIDTH_PERCENT) / 2;
//        View nearestToCenterView = null;
//        int nearestDeltaX = 0;
//        for (int count = getChildCount(), i = 0; i < count; i++) {
//            View item = getChildAt(i);
//            int centerXView = (int) (getDecoratedLeft(item) + viewHalfWidth);
//            if (nearestToCenterView == null || Math.abs(nearestDeltaX) > Math.abs(centerX - centerXView)) {
//                nearestToCenterView = item;
//                nearestDeltaX = (int) (centerX - centerXView);
//            }
//        }
//
//        if (nearestToCenterView != null) {
//            shiftToCenterCardScroller.setTargetPosition(getPosition(nearestToCenterView));
//            startSmoothScroll(shiftToCenterCardScroller);
//        }
//
//    }
//
//    @Override
//    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, final int position) {
//        if (position >= getItemCount()) {
//            Log.e("FanManager", "Cannot scroll to " + position + ", item count is " + getItemCount());
//            return;
//        }
//
//        fanCardScroller.setTargetPosition(position);
//        startSmoothScroll(fanCardScroller);
//    }
//
//    @Override
//    public PointF computeScrollVectorForPosition(int targetPosition) {
//        if (getChildCount() == 0) {
//            return null;
//        }
//        final int firstChildPos = getPosition(getChildAt(0));
//        final int direction = targetPosition < firstChildPos ? -1 : 1;
//        return new PointF(direction, 0);
//    }
//
//
//    private Collection<ViewAnimationInfo> generateViewAnimationInfos(int leftViewDelta, int rightViewDelta, int itemPosition) {
//
//        final List<ViewAnimationInfo> infoViews = new ArrayList<>();
//
//        for (int count = getChildCount(), i = 0; i < count; i++) {
//            View view = getChildAt(i);
//            int viewPosition = getPosition(view);
//            if (viewPosition == itemPosition) {
//                continue;
//            }
//            ViewAnimationInfo info = new ViewAnimationInfo();
//            info.view = view;
//            info.startLeft = getDecoratedLeft(view);
//            info.startRight = getDecoratedRight(view);
//            info.top = getDecoratedTop(view);
//            info.bottom = getDecoratedBottom(view);
//
//            if (viewPosition < itemPosition) {
//                // left view
//                info.finishLeft = info.startLeft + leftViewDelta;
//                info.finishRight = info.startRight + leftViewDelta;
//            } else {
//                // right view
//                info.finishLeft = info.startLeft + rightViewDelta;
//                info.finishRight = info.startRight + rightViewDelta;
//            }
//
//            infoViews.add(info);
//        }
//        return infoViews;
//    }
//}
