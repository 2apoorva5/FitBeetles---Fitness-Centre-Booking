package com.services.fitbeetles.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.services.fitbeetles.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class WelcomeSliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public WelcomeSliderAdapter(Context context)
    {
        this.context = context;
    }

    public int[] slide_images = {

            R.drawable.welcome1,
            R.drawable.welcome2,
            R.drawable.welcome3,
            R.drawable.welcome4
    };

    public String[] slide_headings = {

            "Welcome to FitBeetles.",
            "Avail the best.",
            "Go Flexible.",
            "Shop products online."
    };

    public String[] slide_descs = {

            "Get all your Fitness & Sports related things sorted at one place.",
            "Book & Workout at the best Classes & Gyms, anywhere, anytime.",
            "Workout on the basis of your very own schedule.",
            "Purchase from a variety of products & merchandise based on your fitness choice."
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.welcome_slider_layout, container, false);

        KenBurnsView sliderImage = view.findViewById(R.id.welcome_slider_image);
        TextView sliderHeading = view.findViewById(R.id.welcome_heading);
        TextView sliderDesc = view.findViewById(R.id.welcome_desc);

        sliderImage.setImageResource(slide_images[position]);
        sliderHeading.setText(slide_headings[position]);
        sliderDesc.setText(slide_descs[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
