//package com.cleveroad.testrecycler.internal;
//
//import android.content.Context;
//import android.graphics.PointF;
//import android.graphics.Rect;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.LinearSmoothScroller;
//import android.support.v7.widget.RecyclerView;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.util.SparseArray;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.cleveroad.testrecycler.AnimationHelper;
//import com.cleveroad.testrecycler.AnimationHelperImpl;
//
//public class FanLayoutManagerOld2 extends RecyclerView.LayoutManager implements AnimationHelperImpl.AnimationHelperListener {
//    private static final float VIEW_HEIGHT_PERCENT = 1f / 3f;
//    private static final float VIEW_WIDTH_PERCENT = 1f / 3f;
//    @NonNull
//    private final AnimationHelper animationHelper;
//    private SparseArray<View> viewCache = new SparseArray<>();
//    private Context context;
//    private int mAnchorPos;
//    private int selectedViewPosition = -1;
//
//    public FanLayoutManagerOld2(Context context) {
//        this.context = context;
//        animationHelper = new AnimationHelperImpl();
//    }
//
//    @Override
//    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//    }
//
//    @Override
//    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        Log.e("FanManager", "onLayoutChildren");
//
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
//        updateViewScale();
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
//    @Override
//    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
////        if (selectedViewPosition != -1 && !animationHelper.isAnimationInProcess()) {
////            switchItem(selectedViewPosition, 0);
////        }
//        int delta = scrollHorizontallyInternal(dx);
//        offsetChildrenHorizontal(-delta);
//        fill(recycler);
//        return delta;
//    }
//
//    private int scrollHorizontallyInternal(int dx) {
//        int childCount = getChildCount();
//        int itemCount = getItemCount();
//        if (childCount == 0) {
//            Log.e("FanManager", "childCount == 0");
//            return 0;
//        }
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
//        //Случай, когда все вьюшки поместились на экране
//        int viewSpan = getDecoratedRight(rightView) > getWidth() ? getDecoratedRight(rightView) : getWidth() -
//                (getDecoratedLeft(leftView) < 0 ? getDecoratedLeft(leftView) : 0);
//        if (viewSpan < getWidth()) {
//            return 0;
//        }
//
//        int delta = 0;
//        //если контент уезжает left
//        if (dx < 0) {
////            View firstView = getChildAt(0);
//            int firstViewAdapterPos = getPosition(leftView);
//            if (firstViewAdapterPos > 0) { //если верхняя вюшка не самая первая в адаптере
//                delta = dx;
//            } else { //если левая вьюшка самая первая в адаптере и выше вьюшек больше быть не может
//                int viewLeft = getDecoratedLeft(leftView) - getWidth() / 2 + getDecoratedMeasuredWidth(leftView) / 2;
//                delta = Math.max(viewLeft, dx);
//            }
//        } else if (dx > 0) { //если контент уезжает вверх
////            View lastView = getChildAt(childCount - 1);
//            int lastViewAdapterPos = getPosition(rightView);
//            if (lastViewAdapterPos < itemCount - 1) { //если нижняя вюшка не самая последняя в адаптере
//                delta = dx;
//            } else { //если праввая вьюшка самая последняя в адаптере и ниже вьюшек больше быть не может
//                int viewRight = getDecoratedRight(rightView) + getWidth() / 2 - getDecoratedMeasuredWidth(rightView) / 2;
//                int parentRight = getWidth();
//                delta = Math.min(viewRight - parentRight, dx);
//            }
//        }
//        return delta;
//    }
//
//    private void updateViewScale() {
//        int childCount = getChildCount();
//        float halfWidth = getWidth() / 2;
//        double radius = getHeight();
//        double powRadius = radius * radius;
//
//        for (int i = 0; i < childCount; i++) {
//            View view = getChildAt(i);
//
//            view.setPivotX(view.getWidth() / 2);
//            view.setPivotY(view.getHeight());
////
////            double deltaX = halfWidth - getDecoratedLeft(view) - view.getWidth() / 2;
////            double deltaY = radius - Math.sqrt(powRadius - deltaX * deltaX);
////
////            view.setTranslationY((float) deltaY);
////
////            double rotation = Math.toDegrees(Math.asin((radius - deltaY) / radius)) - 90;
//////            // set rotation and side where is card
////            view.setRotation((float) (rotation * Math.signum(deltaX)));
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
//     * @param recyclerView current recycler view. Need to smooth scroll.
//     * @param pos          item view position
//     */
//    public void switchItem(@Nullable RecyclerView recyclerView, final int pos) {
//        if (animationHelper.isAnimationInProcess()) {
//            return;
//        }
//        if (recyclerView != null) {
//            smoothScrollToPosition(recyclerView, null, pos);
//        }
//    }
//
//    private void selectItem(View view, int position) {
//        selectedViewPosition = position;
//    }
//
//
////    private void switchItem(final int position, final int delay) {
////        Collection<View> viewsToClose = new ArrayList<>();
////        View viewToOpen = null;
////        int childCount = getChildCount();
////        for (int i = 0; i < childCount; i++) {
////            View view = getChildAt(i);
////            int pos = getPosition(view);
////            if (pos == position) {
////                viewToOpen = view;
////            } else {
//////                if (Float.compare(view.getScaleX(), 1F) != 0) {
//////                    viewsToClose.add(view);
//////                }
////            }
////        }
////
////        if (viewToOpen != null) {
////            selectedViewPosition = selectedViewPosition != position ? position : -1;
////
////            final View finalViewToOpen = viewToOpen;
////            animationHelper.switchItem(viewToOpen, viewsToClose, delay, new Animator.AnimatorListener() {
////                @Override
////                public void onAnimationStart(Animator animator) {
////
////                    final List<View> leftViews = new ArrayList<>();
////                    final List<View> rightViews = new ArrayList<>();
////                    final List<ViewAnimationInfo> rightViewInfos = new ArrayList<ViewAnimationInfo>();
////
////                    final float halfWidth = getWidth() / 2;
////                    final int viewWidth = (int) (getWidth() * VIEW_WIDTH_PERCENT);
////
////                    int anchorViewPosition = getPosition(finalViewToOpen);
////                    for (int count = getChildCount(), i = 0; i < count; i++) {
////                        View view = getChildAt(i);
////                        int viewPosition = getPosition(view);
////                        if (viewPosition == anchorViewPosition) {
////                            continue;
////                        }
////                        if (viewPosition < anchorViewPosition) {
////                            leftViews.add(view);
////                        } else {
////                            rightViews.add(view);
////                            ViewAnimationInfo info = new ViewAnimationInfo();
////                            info.view = view;
//////                            info.centerX = (int) (getWidth() * 3 / 4 + (viewWidth / 2 * (position - anchorViewPosition)));
////                        }
////                    }
////
////                    ValueAnimator valueAnimator = ValueAnimator.ofInt(
////                            selectedViewPosition == -1 ? 200 : 0,
////                            selectedViewPosition == -1 ? 0 : 200);
////
////                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
////                        @Override
////                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
////                            int value = (int) valueAnimator.getAnimatedValue();
////                            for (View view : leftViews) {
////                                view.setTranslationX(-value);
////                                Log.e("FanManager", "onAnimationUpdate: " + (-value));
////                            }
////
//////                            for (ViewAnimationInfo info : rightViewInfos) {
////////                                float translationX = ;
//////                                info.view.setTranslationX(info);
//////                            }
////                        }
////                    });
////                    valueAnimator.start();
////                }
////
////                @Override
////                public void onAnimationEnd(Animator animator) {
////
////                }
////
////                @Override
////                public void onAnimationCancel(Animator animator) {
////
////                }
////
////                @Override
////                public void onAnimationRepeat(Animator animator) {
////
////                }
////            });
////        }
////
////    }
//
//    @Override
//    public void onScrollStateChanged(int state) {
//        super.onScrollStateChanged(state);
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
//        LinearSmoothScroller scroller = new LinearSmoothScroller(recyclerView.getContext()) {
//            @Override
//            public PointF computeScrollVectorForPosition(int targetPosition) {
//                return FanLayoutManagerOld2.this.computeScrollVectorForPosition(targetPosition);
//            }
//
//            @Override
//            protected int getHorizontalSnapPreference() {
//                return SNAP_TO_START;
//            }
//
////            @Override
////            protected void onSeekTargetStep(int dx, int dy, RecyclerView.State state, Action action) {
////                super.onSeekTargetStep(dx, dy, state, action);
////                Log.e("FanManager", "dx = " + dx + " dy = " + dy + " state = " + state + " action = " + action);
////            }
//
//            @Override
//            public int calculateDxToMakeVisible(View view, int snapPreference) {
//                return super.calculateDxToMakeVisible(view, snapPreference) + getWidth() / 2 - view.getWidth() / 2;
//            }
//
//            @Override
//            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
//                super.onTargetFound(targetView, state, action);
//            }
//
//            @Override
//            protected int calculateTimeForScrolling(int dx) {
//                int time = super.calculateTimeForScrolling(dx);
//                Log.e("FanManager", "calculateTimeForScrolling " + time);
////                switchItem(position, time);
//                return time;
//            }
//
//            @Override
//            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
//                return 100f / displayMetrics.densityDpi;
//            }
//        };
//        scroller.setTargetPosition(position);
//        startSmoothScroll(scroller);
//    }
//
//    private PointF computeScrollVectorForPosition(int targetPosition) {
//        if (getChildCount() == 0) {
//            return null;
//        }
//        final int firstChildPos = getPosition(getChildAt(0));
//        final int direction = targetPosition < firstChildPos ? -1 : 1;
//        return new PointF(direction, 0);
//    }
//
//    private void bounceViews(final View centerView) {
//
//    }
//
//    @Override
//    public void onAnimationStart() {
//
//    }
//
//    @Override
//    public void onAnimationEnd() {
//
//    }
//
//
////    private void openView(final View viewToAnimate) {
////        final ArrayList<ViewAnimationInfo> animationInfos = new ArrayList<>();
////        int childCount = getChildCount();
////        int animatedPos = getPosition(viewToAnimate);
////        for (int i = 0; i < childCount; i++) {
////            View view = getChildAt(i);
////            int pos = getPosition(view);
////            int posDelta = pos - animatedPos;
////            final ViewAnimationInfo viewAnimationInfo = new ViewAnimationInfo();
////            viewAnimationInfo.startTop = getDecoratedTop(view);
////            viewAnimationInfo.startBottom = getDecoratedBottom(view);
////            viewAnimationInfo.finishTop = getHeight() * posDelta;
////            viewAnimationInfo.finishBottom = getHeight() * posDelta + getHeight();
////            viewAnimationInfo.view = view;
////            animationInfos.add(viewAnimationInfo);
////        }
////        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
////        animator.setDuration(300);
////        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
////            @Override
////            public void onAnimationUpdate(ValueAnimator animation) {
//////                float animationProgress = (float) animation.getAnimatedValue();
//////                for (ViewAnimationInfo animationInfo : animationInfos) {
//////                    int top = (int) (animationInfo.startTop + animationProgress * (animationInfo.finishTop - animationInfo.startTop));
//////                    int bottom = (int) (animationInfo.startBottom + animationProgress * (animationInfo.finishBottom - animationInfo.startBottom));
//////                    layoutDecorated(animationInfo.view, 0, top, getWidth(), bottom);
//////                }
////                updateViewScale();
////            }
////        });
////        animator.addListener(new Animator.AnimatorListener() {
////            @Override
////            public void onAnimationStart(Animator animation) {
////            }
////
////            @Override
////            public void onAnimationEnd(Animator animation) {
//////                setOrientation(ArticleLayoutManager.Orientation.HORIZONTAL);
////            }
////
////            @Override
////            public void onAnimationCancel(Animator animation) {
////            }
////
////            @Override
////            public void onAnimationRepeat(Animator animation) {
////            }
////        });
////        animator.start();
////    }
//
//    private static class ViewAnimationInfo {
//        int centerX;
//        View view;
//    }
//
//
//}
