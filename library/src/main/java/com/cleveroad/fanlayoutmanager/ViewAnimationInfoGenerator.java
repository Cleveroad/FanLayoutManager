package com.cleveroad.fanlayoutmanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generate data util.
 *
 * @author alex yarovoi
 * @version 1.0
 */
class ViewAnimationInfoGenerator {

    /**
     * Generate collection for open/close or shift animations.
     *
     * @param delta         delta x (shift distance) for views
     * @param isSelected    flag if have selected item
     * @param layoutManager the layout manager
     * @param centerViewPos the center view position
     * @param isCollapsed   flag if have collapsed items
     * @return collection of view data
     */
    static Collection<ViewAnimationInfo> generate(int delta,
                                                  boolean isSelected,
                                                  @NonNull RecyclerView.LayoutManager layoutManager,
                                                  int centerViewPos,
                                                  boolean isCollapsed) {

        final List<ViewAnimationInfo> infoViews = new ArrayList<>();
        // +++++ prepare data +++++
        View view;
        int viewPosition;
        ViewAnimationInfo info;
        int isSelectedKoef;
        int collapseKoef;
        // ----- prepare data -----


        for (int count = layoutManager.getChildCount(), i = 0; i < count; i++) {
            view = layoutManager.getChildAt(i);
            viewPosition = layoutManager.getPosition(view);
            if (viewPosition == centerViewPos) {
                continue;
            }
            info = new ViewAnimationInfo();
            info.view = view;
            info.startLeft = layoutManager.getDecoratedLeft(view);
            info.startRight = layoutManager.getDecoratedRight(view);
            info.top = layoutManager.getDecoratedTop(view);
            info.bottom = layoutManager.getDecoratedBottom(view);
            if (viewPosition < centerViewPos) {
                // left view

                // show views with overlapping if have selected item.
                isSelectedKoef = isSelected ? -1 : 1;

                // make distance between each item if isCollapsed = true
                collapseKoef = isCollapsed ? centerViewPos - viewPosition : 1;

                info.finishLeft = info.startLeft + delta * isSelectedKoef * collapseKoef;
                info.finishRight = info.startRight + delta * isSelectedKoef * collapseKoef;
            } else {
                // right view

                // show views with overlapping if have selected item.
                isSelectedKoef = isSelected ? 1 : -1;

                // make distance between each item if isCollapsed = true
                collapseKoef = isCollapsed ? viewPosition - centerViewPos : 1;

                info.finishLeft = info.startLeft + delta * isSelectedKoef * collapseKoef;
                info.finishRight = info.startRight + delta * isSelectedKoef * collapseKoef;
            }

            infoViews.add(info);
        }
        return infoViews;
    }


}
