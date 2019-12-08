package com.services.fitbeetles.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.services.fitbeetles.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

public class GalleryAdapter extends SliderViewAdapter<GalleryAdapter.GalleryViewHolder>
{
    private Context context;
    private String[] imageUrls;

    public GalleryAdapter(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_gallery_layout, null);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder viewHolder, int position) {
        Glide.with(viewHolder.itemView).load(imageUrls[position]).into(viewHolder.galleryImageView);
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    class GalleryViewHolder extends SliderViewAdapter.ViewHolder
    {
        View itemView;
        ImageView galleryImageView;

        public GalleryViewHolder(View itemView) {
            super(itemView);

            galleryImageView = itemView.findViewById(R.id.gallery_image);
            this.itemView = itemView;
        }
    }
}
