//package com.cleveroad.testrecycler.internal;
//
//import android.graphics.Rect;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.util.SparseArray;
//import android.view.View;
//import android.view.ViewGroup;
//
//public class TestLayoutManager extends RecyclerView.LayoutManager {
//    private static final float ITEM_HEIGHT_PERCENT = 0.75f;
//    private static final float ITEM_WIDTH_PERCENT = 0.33f;
//    private SparseArray<View> viewCache = new SparseArray<>();
//    private Orientation orientation = Orientation.HORIZONTAL;
//    private int mAnchorPos;
//
//    @Override
//    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
//
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
//
//        switch (orientation) {
//
//            case VERTICAL:
//                fillUp(anchorView, recycler);
//                fillDown(anchorView, recycler);
//                break;
//            case HORIZONTAL:
//                fillLeft(anchorView, recycler);
//                fillRight(anchorView, recycler);
//                break;
//        }
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
//        Rect mainRect = new Rect(0, 0, getWidth(), getHeight());
//        int maxSquare = 0;
//        View anchorView = null;
//        for (int i = 0; i < childCount; i++) {
//            View view = getChildAt(i);
//            int top = getDecoratedTop(view);
//            int bottom = getDecoratedBottom(view);
//            int left = getDecoratedLeft(view);
//            int right = getDecoratedRight(view);
//            Rect viewRect = new Rect(left, top, right, bottom);
//            boolean intersect = viewRect.intersect(mainRect);
//            if (intersect) {
//                int square = viewRect.width() * viewRect.height();
//                if (square > maxSquare) {
//                    anchorView = view;
//                }
//            }
//        }
//        return anchorView;
//    }
//
//
//    private void fillLeft(@Nullable View anchorView, RecyclerView.Recycler recycler) {
//        int anchorPos;
//        int anchorLeft = 0;
//        if (anchorView != null) {
//            anchorPos = getPosition(anchorView);
//            anchorLeft = getDecoratedLeft(anchorView);
//        } else {
//            anchorPos = mAnchorPos;
//        }
//
//        boolean fillLeft = true;
//        int pos = anchorPos - 1;
//        int viewRight = anchorLeft;
//        int width = (int) (getWidth() * ITEM_WIDTH_PERCENT);
//        int height = (int) (getHeight() * ITEM_WIDTH_PERCENT);
//        final int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
//        final int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
//        while (fillLeft && pos >= 0) {
//            View view = viewCache.get(pos);
//            if (view == null) {
//                view = recycler.getViewForPosition(pos);
//                addView(view, 0);
//                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
//                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
//                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
//                final int baseTopMargin = getHeight() - height;
//                layoutDecorated(view, viewRight - width, baseTopMargin, viewRight, getHeight());
//            } else {
//                attachView(view);
//                viewCache.remove(pos);
//            }
//            viewRight = getDecoratedLeft(view);
//            fillLeft = (viewRight > 0);
//            pos--;
//        }
//    }
//
//    private void fillRight(View anchorView, RecyclerView.Recycler recycler) {
//        int anchorPos;
//        int anchorLeft = 0;
//        if (anchorView != null) {
//            anchorPos = getPosition(anchorView);
//            anchorLeft = getDecoratedLeft(anchorView);
//        } else {
//            anchorPos = mAnchorPos;
//        }
//
//        int pos = anchorPos;
//        boolean fillRight = true;
//        int viewLeft = anchorLeft;
//        int itemCount = getItemCount();
//        int width = (int) (getWidth() * ITEM_WIDTH_PERCENT);
//        int height = (int) (getHeight() * ITEM_WIDTH_PERCENT);
//        final int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
//        final int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
//
//        while (fillRight && pos < itemCount) {
//            View view = viewCache.get(pos);
//            if (view == null) {
//                view = recycler.getViewForPosition(pos);
//                addView(view);
//                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
//                int decoratedMeasuredHeight = getDecoratedMeasuredHeight(view);
//                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
//                final int baseTopMargin = getHeight() - height;
//                layoutDecorated(view, viewLeft, baseTopMargin, viewLeft + width, getHeight());
//            } else {
//                attachView(view);
//                viewCache.remove(pos);
//            }
//            viewLeft = getDecoratedRight(view);
//            fillRight = viewLeft <= getWidth();
//            pos++;
//        }
//    }
//
//    private void fillUp(@Nullable View anchorView, RecyclerView.Recycler recycler) {
//        int anchorPos;
//        int anchorTop = 0;
//        if (anchorView != null) {
//            anchorPos = getPosition(anchorView);
//            anchorTop = getDecoratedTop(anchorView);
//        } else {
//            anchorPos = mAnchorPos;
//        }
//
//        boolean fillUp = true;
//        int pos = anchorPos - 1;
//        int viewBottom = anchorTop;
//        int viewHeight = (int) (getHeight() * ITEM_HEIGHT_PERCENT);
//        final int widthSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
//        final int heightSpec = View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.EXACTLY);
//        while (fillUp && pos >= 0) {
//            View view = viewCache.get(pos);
//            if (view == null) {
//                view = recycler.getViewForPosition(pos);
//                addView(view, 0);
//                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
//                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
//                layoutDecorated(view, 0, viewBottom - viewHeight, decoratedMeasuredWidth, viewBottom);
//            } else {
//                attachView(view);
//                viewCache.remove(pos);
//            }
//            viewBottom = getDecoratedTop(view);
//            fillUp = (viewBottom > 0);
//            pos--;
//        }
//    }
//
//    private void fillDown(@Nullable View anchorView, RecyclerView.Recycler recycler) {
//        int anchorPos;
//        int anchorTop = 0;
//        if (anchorView != null) {
//            anchorPos = getPosition(anchorView);
//            anchorTop = getDecoratedTop(anchorView);
//        } else {
//            anchorPos = mAnchorPos;
//        }
//
//        int pos = anchorPos;
//        boolean fillDown = true;
//        int height = getHeight();
//        int viewTop = anchorTop;
//        int itemCount = getItemCount();
//        int viewHeight = (int) (getHeight() * ITEM_HEIGHT_PERCENT);
//        final int widthSpec = View.MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY);
//        final int heightSpec = View.MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.EXACTLY);
//
//        while (fillDown && pos < itemCount) {
//            View view = viewCache.get(pos);
//            if (view == null) {
//                view = recycler.getViewForPosition(pos);
//                addView(view);
//                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
//                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(view);
//                layoutDecorated(view, 0, viewTop, decoratedMeasuredWidth, viewTop + viewHeight);
//            } else {
//                attachView(view);
//                viewCache.remove(pos);
//            }
//            viewTop = getDecoratedBottom(view);
//            fillDown = viewTop <= height;
//            pos++;
//        }
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
//    public boolean canScrollVertically() {
//        return orientation == Orientation.VERTICAL;
//    }
//
//    @Override
//    public boolean canScrollHorizontally() {
//        return orientation == Orientation.HORIZONTAL;
//    }
//
//    @Override
//    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        int delta = scrollVerticallyInternal(dy);
//        offsetChildrenVertical(-delta);
//        fill(recycler);
//        return delta;
//    }
//
//    private int scrollVerticallyInternal(int dy) {
//        int childCount = getChildCount();
//        int itemCount = getItemCount();
//        if (childCount == 0) {
//            return 0;
//        }
//
//        final View topView = getChildAt(0);
//        final View bottomView = getChildAt(childCount - 1);
//
//        int viewSpan = getDecoratedBottom(bottomView) - getDecoratedTop(topView);
//        if (viewSpan <= getHeight()) {
//            return 0;
//        }
//
//        int delta = 0;
//        if (dy < 0) {
//            View firstView = getChildAt(0);
//            int firstViewAdapterPos = getPosition(firstView);
//            if (firstViewAdapterPos > 0) {
//                delta = dy;
//            } else {
//                int viewTop = getDecoratedTop(firstView);
//                delta = Math.max(viewTop, dy);
//            }
//        } else if (dy > 0) {
//            View lastView = getChildAt(childCount - 1);
//            int lastViewAdapterPos = getPosition(lastView);
//            if (lastViewAdapterPos < itemCount - 1) {
//                delta = dy;
//            } else {
//                int viewBottom = getDecoratedBottom(lastView);
//                int parentBottom = getHeight();
//                delta = Math.min(viewBottom - parentBottom, dy);
//            }
//        }
//        return delta;
//    }
//
//    private void updateViewScale() {
//        int childCount = getChildCount();
//        float halfWidth = getWidth() / 2;
//
//        for (int i = 0; i < childCount; i++) {
//            View view = getChildAt(i);
//
////            view.setPivotX(view.getWidth() / 2);
////            view.setPivotY(view.getHeight() * 2);
////
//            double deltaX = halfWidth - (getDecoratedLeft(view) + view.getWidth() / 2);
//            double deltaY = getHeight() - Math.sqrt(getHeight() * getHeight() - deltaX * deltaX);
//
//            view.setTranslationY((float) deltaY);
//
//            double rotation = Math.toDegrees(Math.asin((getHeight() - deltaY) / getHeight())) - 90;
//            // set rotation and side where is card
//            view.setRotation((float) (rotation * Math.signum(deltaX)));
//
////            double translationX = 2 * Math.cos(Math.abs(rotation));
////            view.setTranslationX((float) (deltaX));
//
//        }
//    }
//
//    @Override
//    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
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
//
//    public enum Orientation {VERTICAL, HORIZONTAL}
//
//}
