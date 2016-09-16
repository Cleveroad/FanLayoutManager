//package com.cleveroad.testrecycler.internal;
//
//import android.content.Context;
//import android.graphics.Rect;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.util.SparseArray;
//import android.view.View;
//import android.view.ViewGroup;
//
//public class FanLayoutManagerOld extends RecyclerView.LayoutManager {
//    private static final float VIEW_HEIGHT_PERCENT = 1f / 3f;
//    private static final float VIEW_WIDTH_PERCENT = 1f / 3f;
//    private SparseArray<View> viewCache = new SparseArray<>();
////    private int mAnchorPos;
//    private Context context;
//
//    public FanLayoutManagerOld(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//    }
//
//    @Override
//    public boolean supportsPredictiveItemAnimations() {
//        return false;
//    }
//
//    @Override
//    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
//        Log.e("FanManager","onLayoutChildren");
//
//        detachAndScrapAttachedViews(recycler);
//        fill(recycler);
////        mAnchorPos = 0;
//    }
//
//    private void fill(RecyclerView.Recycler recycler) {
////        View anchorView = getAnchorView();
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
//
//
//
////        fillFromLeft(recycler, 1);
////        fillFromLeft(recycler, 0);
////        fillLeft(anchorView, recycler);
////        fillRight(getAnchorView(), recycler);
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
////            int top = getDecoratedTop(view);
////            int bottom = getDecoratedBottom(view);
//            int left = getDecoratedLeft(view);
////            int right = getDecoratedRight(view);
////            Rect viewRect = new Rect(left, top, right, bottom);
////            boolean intersect = viewRect.intersect(mainRect);
////            if (intersect) {
////                int square = viewRect.width() * viewRect.height();
////                if (square > maxSquare) {
////                    anchorView = view;
////                }
////            }
//        }
//        return anchorView;
//    }
//
//    private void fillFromLeft(RecyclerView.Recycler recycler, int startPosition) {
//        int pos = startPosition;
//        boolean fillFromLeft = true;
//        int viewLeft = -getWidth() / 2;
//
//
//        int viewWidth = (int) (getWidth() * VIEW_WIDTH_PERCENT);
//        int viewHeight = (int) (getHeight() * VIEW_HEIGHT_PERCENT);
//        final int widthSpec = View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY);
//        final int heightSpec = View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY);
//
//        if (startPosition == 1) {
//            viewLeft += viewWidth * 3 / 4;
//        }
//
//        while (fillFromLeft && pos >= 0 && pos < getItemCount()) {
//            View view = viewCache.get(pos);
//            if (view == null) {
//                view = recycler.getViewForPosition(pos);
//                addView(view, 0);
//                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
//                final int baseTopMargin = getHeight() - viewHeight;
//                layoutDecorated(view, viewLeft - viewWidth / 4, baseTopMargin, viewLeft + viewWidth - viewWidth / 4, getHeight());
//            } else {
//                attachView(view);
//                viewCache.remove(pos);
//            }
//
//            viewLeft = getDecoratedRight(view) + viewWidth * 3 / 4;
//            fillFromLeft = (viewLeft < getWidth() * 1.5);
////            fillLeft = (viewRight > - leftBorder);
//            pos += 2;
//        }
//
//    }
//
//
////    private void fillLeft(@Nullable View anchorView, RecyclerView.Recycler recycler) {
////        int anchorPos;
////        int anchorLeft = 0;
////        if (anchorView != null) {
////            anchorPos = getPosition(anchorView);
////            anchorLeft = getDecoratedLeft(anchorView);
////        } else {
////            anchorPos = mAnchorPos;
////        }
////
////        boolean fillLeft = true;
////        int pos = anchorPos - 1;
////        int viewRight = anchorLeft;
////        int width = (int) (getWidth() * VIEW_WIDTH_PERCENT);
////        int height = (int) (getHeight() * VIEW_HEIGHT_PERCENT);
////        final int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
////        final int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
////
//////        int leftBorder = (int) (Math.PI * height); // half of perimeter.
//////        leftBorder = (leftBorder - getWidth()) ; // calculate center *----|----*
////
////
////        while (fillLeft && pos >= 0) {
////            View view = viewCache.get(pos);
////            if (view == null) {
////                view = recycler.getViewForPosition(pos);
////                addView(view, 0);
////                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
////                final int baseTopMargin = getHeight() - height;
////                layoutDecorated(view, viewRight - width + width / 4, baseTopMargin, viewRight + width / 4, getHeight());
////            } else {
////                attachView(view);
////                viewCache.remove(pos);
////            }
////
////            viewRight = getDecoratedLeft(view);
////            fillLeft = (viewRight > -width / 2);
//////            fillLeft = (viewRight > - leftBorder);
////            pos--;
////        }
////    }
////
////    private void fillRight(View anchorView, RecyclerView.Recycler recycler) {
////        int anchorPos;
////        int anchorLeft = 0;
////        if (anchorView != null) {
////            anchorPos = getPosition(anchorView);
////            anchorLeft = getDecoratedLeft(anchorView);
////        } else {
////            anchorPos = mAnchorPos;
////        }
////
////        int pos = anchorPos;
////        boolean fillRight = true;
////        int viewLeft = anchorLeft;
////        int itemCount = getItemCount();
////        int width = (int) (getWidth() * VIEW_WIDTH_PERCENT);
////        int height = (int) (getHeight() * VIEW_HEIGHT_PERCENT);
////        final int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
////        final int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
////
//////        int leftBorder = (int) (Math.PI * height); // half of perimeter.
//////        leftBorder = (leftBorder - getWidth()); // calculate center *----|----*
////
////        while (fillRight && pos < itemCount) {
////            View view = viewCache.get(pos);
////            if (view == null) {
////                view = recycler.getViewForPosition(pos);
////                addView(view);
////                measureChildWithDecorationsAndMargin(view, widthSpec, heightSpec);
////                final int baseTopMargin = getHeight() - height;
////                layoutDecorated(view, viewLeft - width / 4, baseTopMargin, viewLeft + width - width / 4, getHeight());
////            } else {
////                attachView(view);
////                viewCache.remove(pos);
////            }
////            viewLeft = getDecoratedRight(view);
////            fillRight = viewLeft <= getWidth() + width / 2;
//////            fillRight = viewLeft <= getWidth() + leftBorder;
////            pos++;
////        }
////    }
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
//        final int mode = View.MeasureSpec.getDirectionMode(spec);
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
//        int delta = scrollHorizontallyInternal(dx);
//        offsetChildrenHorizontal(-delta);
//        fill(recycler);
////        Log.e("FanManager", "delta = " + delta);
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
//
//        for (int i = 0; i < childCount; i++) {
//            View view = getChildAt(i);
//
////                view.setPivotX(view.getWidth() / 2);
////                view.setPivotY(view.getHeight() * 2);
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
//}
