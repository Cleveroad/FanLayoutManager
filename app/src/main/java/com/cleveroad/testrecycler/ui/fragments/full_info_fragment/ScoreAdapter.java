package com.cleveroad.testrecycler.ui.fragments.full_info_fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.models.AthleticModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.AthleticHolder> {

    private final List<AthleticModel> mItems = new ArrayList<>();

    public void addItems(@NonNull Collection<AthleticModel> items) {
        mItems.addAll(items);
        notifyItemRangeInserted(mItems.size() - items.size() - 1, items.size());
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public AthleticHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
        return new AthleticHolder(view);
    }

    @Override
    public void onBindViewHolder(AthleticHolder holder, int position) {
        AthleticModel item = mItems.get(position);
        holder.tvAthleticName.setText(item.getName());
        holder.tvCountry.setText(item.getCountry().getCountry());
        holder.tvScore.setText(String.valueOf(item.getScore()));
        switch (item.getCountry()) {
            case USA: {
                holder.ivAthleticFlag.setImageResource(R.drawable.us_flag);
                break;
            }
            case ROK: {
                holder.ivAthleticFlag.setImageResource(R.drawable.flag_korea);
                break;
            }
            case ITALY: {
                holder.ivAthleticFlag.setImageResource(R.drawable.italy_flag);
                break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class AthleticHolder extends RecyclerView.ViewHolder {
        ImageView ivAthleticFlag;
        TextView tvCountry;
        TextView tvAthleticName;
        TextView tvScore;


        AthleticHolder(View itemView) {
            super(itemView);
            ivAthleticFlag = (ImageView) itemView.findViewById(R.id.ivAthleticFlag);
            tvCountry = (TextView) itemView.findViewById(R.id.tvCountry);
            tvAthleticName = (TextView) itemView.findViewById(R.id.tvAthleticName);
            tvScore = (TextView) itemView.findViewById(R.id.tvScore);
        }
    }
}
