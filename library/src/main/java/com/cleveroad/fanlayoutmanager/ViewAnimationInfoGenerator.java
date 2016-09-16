package com.cleveroad.fanlayoutmanager;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ViewAnimationInfoGenerator {
    static Collection<ViewAnimationInfo> generate(int delta,
                                                  @FanLayoutManagerSettings.DirectionMode int mode,
                                                  @NonNull RecyclerView.LayoutManager layoutManager,
                                                  int centerViewPos,
                                                  @FanLayoutManagerSettings.DirectionCollapse int collapseMode) {

        final List<ViewAnimationInfo> infoViews = new ArrayList<>();

        for (int count = layoutManager.getChildCount(), i = 0; i < count; i++) {
            View view = layoutManager.getChildAt(i);
            int viewPosition = layoutManager.getPosition(view);
            if (viewPosition == centerViewPos) {
                continue;
            }
            ViewAnimationInfo info = new ViewAnimationInfo();
            info.view = view;
            info.startLeft = layoutManager.getDecoratedLeft(view);
            info.startRight = layoutManager.getDecoratedRight(view);
            info.top = layoutManager.getDecoratedTop(view);
            info.bottom = layoutManager.getDecoratedBottom(view);

            if (viewPosition < centerViewPos) {
                // left view
                int koef = mode == FanLayoutManagerSettings.DirectionMode.FROM_CENTER ? -1 : 1;
                int collapseKoef = collapseMode == FanLayoutManagerSettings.DirectionCollapse.FROM_CENTER ? 1 : centerViewPos - viewPosition;

                info.finishLeft = info.startLeft + delta * koef * collapseKoef;
                info.finishRight = info.startRight + delta * koef * collapseKoef;
            } else {
                // right view
                int koef = mode == FanLayoutManagerSettings.DirectionMode.FROM_CENTER ? 1 : -1;
                int collapseKoef = collapseMode == FanLayoutManagerSettings.DirectionCollapse.FROM_CENTER ? 1 : viewPosition - centerViewPos;

                info.finishLeft = info.startLeft + delta * koef * collapseKoef;
                info.finishRight = info.startRight + delta * koef * collapseKoef;
            }

            infoViews.add(info);
        }
        return infoViews;
    }


}
