package com.example.veterinary_clinic_info_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.ItemViewHolder> {
    private List<Pet> data;
    private OnClickListener onClickListener;
    private OnImageEmptyListener onImageEmptyListener;

    public PetsAdapter(List<Pet> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.textView.setText(data.get(position).getTitle());
        if (data.get(position).getBitmap() == null) {
            holder.imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            onImageEmptyListener.onImageEmpty(data.get(position).getImageUrl(), position);
        } else {
            holder.imageView.setImageBitmap(data.get(position).getBitmap());
        }
        holder.parent.setOnClickListener(v -> onClickListener.onClick(data.get(position).getContentUrl()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<Pet> data) {
        this.data = data;
        notifyDataSetChanged();;
    }

    public void updateItem(List<Pet> data,int position) {
        this.data = data;
        notifyItemChanged(position);;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public View parent;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageItem);
            textView = itemView.findViewById(R.id.textTitle);
            parent = itemView.findViewById(R.id.parent);
        }
    }

    public void setClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    public interface OnClickListener {
        void onClick(String content_url);
    }

    public void setImageEmptyListener(OnImageEmptyListener listener) {
        onImageEmptyListener = listener;
    }
    public interface OnImageEmptyListener {
        void onImageEmpty(String content_url, int position);
    }
}
