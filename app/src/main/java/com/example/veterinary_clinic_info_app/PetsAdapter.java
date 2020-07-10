package com.example.veterinary_clinic_info_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.ItemViewHolder> {
    private Context context;
    //TODO change mDataset -> data
    private List<Pet> data;

    public PetsAdapter(Context context,List<Pet> mDataset) {
        this.context = context;
        this.data = mDataset;
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
        holder.textView.setOnClickListener(v -> showPetInfo(data.get(position).getContent_url()));

        holder.imageView.setImageBitmap(data.get(position).getBitmap());
        holder.imageView.setOnClickListener(v -> showPetInfo(data.get(position).getContent_url()));

    }

    private void showPetInfo(String content_url) {
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.container,PetContentsFragment.newInstance(content_url));
        fragmentTransaction.commit();

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<Pet> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageItem);
            textView = itemView.findViewById(R.id.textTitle);
        }
    }
}
