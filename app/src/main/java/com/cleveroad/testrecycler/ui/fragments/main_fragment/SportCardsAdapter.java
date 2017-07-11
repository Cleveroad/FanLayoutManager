package com.cleveroad.testrecycler.ui.fragments.main_fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleveroad.testrecycler.R;
import com.cleveroad.testrecycler.models.SportCardModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class SportCardsAdapter extends RecyclerView.Adapter<SportCardsAdapter.SportCardViewHolder> {
    private final List<SportCardModel> mItems = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    SportCardsAdapter(Context context) {
        this.mContext = context;
    }

    public boolean add(SportCardModel item) {
        boolean isAdded = mItems.add(item);
        if (isAdded) {
            notifyDataSetChanged();
        }
        return isAdded;
    }

    boolean addAll(Collection<SportCardModel> items) {
        boolean isAdded = mItems.addAll(items);
        if (isAdded) {
            notifyDataSetChanged();
        }
        return isAdded;
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public SportCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new SportCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SportCardViewHolder holder, int position) {
        SportCardModel item = mItems.get(position);
        holder.tvSportTitle.setText(item.getSportTitle());
        holder.tvSportSubtitle.setText(item.getSportSubtitle());
        holder.tvSportRound.setText(item.getSportRound());
        holder.tvTime.setText(item.getTime());
        holder.tvDayPart.setText(item.getDayPart());

        ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(mContext, item.getBackgroundColorResId()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setTransitionName(holder.ivSportPreview, "shared" + String.valueOf(position));
        }

        holder.setImage(item.getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(holder.getAdapterPosition(), holder.ivSportPreview);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    SportCardModel getModelByPos(int pos) {
        return mItems.get(pos);

    }

    interface OnItemClickListener {
        void onItemClicked(int pos, View view);
    }

    class SportCardViewHolder extends RecyclerView.ViewHolder {

        final TextView tvSportTitle;
        final TextView tvSportSubtitle;
        final TextView tvSportRound;
        final TextView tvTime;
        final TextView tvDayPart;
        ImageView ivSportPreview;

        SportCardViewHolder(View itemView) {
            super(itemView);
            tvSportTitle = (TextView) itemView.findViewById(R.id.tvSportTitle);
            tvSportSubtitle = (TextView) itemView.findViewById(R.id.tvSportSubtitle);
            tvSportRound = (TextView) itemView.findViewById(R.id.tvSportRound);
            ivSportPreview = (ImageView) itemView.findViewById(R.id.ivSportPreview);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvDayPart = (TextView) itemView.findViewById(R.id.tvDayPart);
        }

        public void setImage(final String image) {
            Transformation transformation = new Transformation() {
                @Override
                public Bitmap transform(Bitmap source) {
                    int targetWidth = ivSportPreview.getWidth();

                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int targetHeight = (int) (targetWidth * aspectRatio);
                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle();
                    }
                    return result;
                }

                @Override
                public String key() {
                    return "transformation" + " desiredWidth";
                }
            };
            Picasso.with(mContext).cancelRequest(ivSportPreview);
            Picasso.with(mContext)
                    .load(image)
                    .transform(transformation)
                    .noPlaceholder()
                    .into(ivSportPreview, new Callback() {
                        @Override
                        public void onSuccess() {
                            //Update drawable color asynchronously
                            Bitmap imageBitmap = ((BitmapDrawable) ivSportPreview.getDrawable()).getBitmap();

                        }

                        @Override
                        public void onError() {
                            String errorMessage = String.format("ERROR: Could not load image with URI %s", image);
                        }
                    });
        }

    }
}
