package com.ux.ok_baby.view.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ux.ok_baby.R;
import com.ux.ok_baby.model.Baby;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


public class BabyRecyclerUtils {

    interface BabyClickCallback {
        void onBabyClick(Baby baby);
    }

    static class BabyHolder extends RecyclerView.ViewHolder {

        public final ImageView babyImage;

        public BabyHolder(@NonNull View itemView) {
            super(itemView);
            babyImage = itemView.findViewById(R.id.oneBabyImg);
        }
    }

    static class BabyCallback extends DiffUtil.ItemCallback<Baby> {

        @Override
        public boolean areItemsTheSame(@NonNull Baby b1, @NonNull Baby b2) {
            return b1.getBid().equals(b2.getBid());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Baby b1, @NonNull Baby b2) {
            return b1.equals(b2);
        }
    }

    static class BabyAdapter extends ListAdapter<Baby, BabyHolder> {

        public BabyAdapter() {
            super(new BabyCallback());
        }


        public BabyClickCallback callback;

        @NonNull
        @Override
        public BabyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_one_baby, parent, false);
            final BabyHolder babyHolder = new BabyHolder(itemView);
            babyHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Baby baby = getItem(babyHolder.getAdapterPosition());
                    if (callback != null) {
                        callback.onBabyClick(baby);
                    }
                }
            });
            return babyHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull BabyHolder babyHolder, int position) {
            Baby baby = getItem(position);

            if (baby.getImageUrl() != null) {
                Glide.with(babyHolder.itemView.getContext())
                        .load(baby.getImageUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(babyHolder.babyImage);
            }
        }
    }
}
