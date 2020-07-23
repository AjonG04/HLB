package com.example.leafpiction;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.leafpiction.Model.DataModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewViewHolder> {

    private List<DataModel> list;
    public CardViewAdapter(List<DataModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CardViewAdapter.CardViewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card, viewGroup, false);
        return new CardViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewAdapter.CardViewViewHolder holder, int position) {
        DataModel dataModel = list.get(position);

        byte[] image = dataModel.getPhoto();
        Bitmap bmp= BitmapFactory.decodeByteArray(image, 0 , image.length);

        Glide.with(holder.itemView.getContext())
                .load(bmp)
                .apply(new RequestOptions().override(350, 550))
                .into(holder.imgPhoto);
        holder.tvName.setText(dataModel.getFilename());
        holder.tvDetail.setText(dataModel.getDatetime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardViewViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPhoto;
        TextView tvName, tvDetail;

        CardViewViewHolder(View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_item_photo);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvDetail = itemView.findViewById(R.id.tv_item_date);
        }
    }
}
