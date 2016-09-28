package com.cleveroad.testrecycler.ui.fragments.main_fragment;


import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class SharedTransitionSet extends TransitionSet {

    public SharedTransitionSet(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SharedTransitionSet() {
        super();
        init();
    }

    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}
