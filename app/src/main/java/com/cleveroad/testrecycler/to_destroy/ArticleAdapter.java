//package com.cleveroad.testrecycler.ui.fragments.main_fragment;
//
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.cleveroad.testrecycler.R;
//import com.cleveroad.testrecycler.models.ArticleModel;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {
//    private final List<ArticleModel> items = new ArrayList<>();
//    private OnItemClickListener itemClickListener;
//
//    public boolean add(ArticleModel item) {
//        boolean isAdded = items.add(item);
//        if (isAdded) {
//            notifyItemInserted(items.size() - 1);
//        }
//        return isAdded;
//    }
//
//    public void clear() {
//        items.clear();
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
//        return new ArticleViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(ArticleViewHolder holder, final int position) {
//        ArticleModel item = items.get(position);
//        holder.tvTitle.setText(item.getTitle());
//        holder.tvText.setText(item.getText());
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (itemClickListener != null) {
//                    itemClickListener.onItemClicked(position);
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    public OnItemClickListener getItemClickListener() {
//        return itemClickListener;
//    }
//
//    public void setItemClickListener(OnItemClickListener itemClickListener) {
//        this.itemClickListener = itemClickListener;
//    }
//
//    public interface OnItemClickListener {
//        void onItemClicked(int pos);
//    }
//
//    public class ArticleViewHolder extends RecyclerView.ViewHolder {
//        TextView tvTitle;
//        TextView tvText;
//
//        public ArticleViewHolder(View itemView) {
//            super(itemView);
//            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
//            tvText = (TextView) itemView.findViewById(R.id.tvText);
//        }
//    }
//}
