//package com.cleveroad.testrecycler.internal;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.RecyclerView;
//
//import com.cleveroad.testrecycler.to_destroy.FanLayoutManager;
//import com.cleveroad.testrecycler.R;
//import com.thedeanda.lorem.Lorem;
//import com.thedeanda.lorem.LoremIpsum;
//
//public class MainActivityOld extends AppCompatActivity {
//    private RecyclerView rvArticles;
//    private ArticleAdapter adapter;
//    private ArticleLayoutManager layoutManager;
//    private FanLayoutManager fanLayoutManager;
//    private TestLayoutManager testLayoutManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
//        adapter = new ArticleAdapter();
//        final Lorem lorem = LoremIpsum.getInstance();
//        for (int i = 0; i < 40; i++) {
//            adapter.add(new ArticleModel("Title " + i, lorem.getParagraphs(1, 2)));
//        }
//        layoutManager = new ArticleLayoutManager(this);
//        testLayoutManager = new TestLayoutManager();
//        fanLayoutManager = new FanLayoutManager(this);
//
//        rvArticles.setItemAnimator(new DefaultItemAnimator());
////        rvArticles.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
//        rvArticles.setAdapter(adapter);
//
//        rvArticles.setLayoutManager(fanLayoutManager);
////        rvArticles.setChildDrawingOrderCallback(new RecyclerView.ChildDrawingOrderCallback() {
////            @Override
////            public int onGetChildDrawingOrder(int childCount, int i) {
////                View startView = fanLayoutManager.getChildAt(0);
////                int position = fanLayoutManager.getPosition(startView);
////                View view = fanLayoutManager.getChildAt(i);
////                boolean isStartFromBelow = true;
//////                boolean isBelow = false;
////                String title = ((TextView) view.findViewById(R.id.tvTitle)).getText().toString();
//////                Log.e("ORDER", "position = " + childCount + " " + i + " " + position);
////
////                if (position % 2 == 0) {
////
////                    isStartFromBelow = true;
//////                    position = i == 0 ? 0 : i / 2;
////                } else {
//////                    Log.e("ORDER", title + " position % 2 != 0 " + ((childCount + 1) / 2 + i / 2));
////                    isStartFromBelow = false;
//////                    position = (childCount + 1) / 2 + i / 2;
////                }
////
////                if (isStartFromBelow) {
////                    if (i % 2 == 0) {
////                        //below
////                        Log.e("ORDER", title + " 1 below " + position + " " + (i == 0 ? 0 : i / 2) + " left = " + fanLayoutManager.getDecoratedLeft(view));
////                        return i == 0 ? 0 : i / 2;
////                    } else {
////                        //front
////                        Log.e("ORDER", title + " 2 front " + position + " " + ((childCount + 1) / 2 + i / 2) + " left = " + fanLayoutManager.getDecoratedLeft(view));
////                        return (childCount + 1) / 2 + i / 2;
////                    }
////                } else {
////                    if (i % 2 == 0) {
////                        //below
////                        Log.e("ORDER", title + " 3 below " + position + " " + (i == 0 ? 0 : i / 2) + " left = " + fanLayoutManager.getDecoratedLeft(view));
////                        return i == 0 ? 0 : i / 2;
////                    } else {
////                        //front
////                        Log.e("ORDER", title + " 4 front " + position + " " + (childCount / 2 + i / 2) + " left = " + fanLayoutManager.getDecoratedLeft(view));
////                        return childCount / 2 + i / 2;
////                    }
////                }
////
////
//////                return position;
////
////
//////                if (position % 2 == 0) {
//////                    Log.e("ORDER", title + " position % 2 == 0 " + (position == 0 ? 0 : position / 2));
//////                    isBelow = true;
//////                    position = position == 0 ? 0 : position / 2;
//////                } else {
//////                    Log.e("ORDER", title + " position % 2 != 0 " + ((fanLayoutManager.getChildCount() + 1) / 2 + position / 2));
//////                    isBelow = false;
//////                    position = (fanLayoutManager.getChildCount() + 1) / 2 + position / 2;
//////                }
//////                View childView = null;
//////                for (int count = fanLayoutManager.getChildCount(), j = 0; j < count; j++) {
//////                    View item = fanLayoutManager.getChildAt(j);
//////                    if (position == fanLayoutManager.getPosition(item)) {
//////                        return j;
//////                    }
//////                }
//////                return i;
//////                rvArticles.getChildAdapterPosition();
//////                return childCount - i - 1;
////            }
////        });
//
//        adapter.setItemClickListener(new ArticleAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClicked(int pos) {
//                fanLayoutManager.switchItem(rvArticles, pos);
////                fanLayoutManager.smoothScrollToPosition(rvArticles, null, pos);
//            }
//        });
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
////        if (layoutManager.getOrientation() == ArticleLayoutManager.Orientation.HORIZONTAL){
////            layoutManager.setOrientation(ArticleLayoutManager.Orientation.VERTICAL);
////        } else {
////            super.onBackPressed();
////        }
//    }
//}
