package com.services.fitbeetles.bottomSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.services.fitbeetles.R;
import com.squareup.picasso.Picasso;

public class FBAssuredBottomSheet extends BottomSheetDialogFragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fbassured_bottom_sheet_layout, container, false);

        String fb_assured = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBAssured%2Ffb_assured.png?alt=media&token=e1168150-6c02-4089-b753-3d62053a7f12";
        String fb_assured1 = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBAssured%2Ffbassured1.png?alt=media&token=00b7033f-bcab-4630-b549-7be459fa5a29";
        String fb_assured2 = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBAssured%2Ffbassured2.png?alt=media&token=296a3e0a-9a93-49e5-b20e-721542d01d5f";
        String fb_assured3 = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBAssured%2Ffbassured3.png?alt=media&token=2f08a54b-31e0-4200-b7fb-13d89900cc8b";
        String fb_assured4 = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBAssured%2Ffbassured4.png?alt=media&token=e8b97787-3623-477a-b343-23b659f46721";

        ImageView fbAssured = view.findViewById(R.id.fb_assured);
        ImageView fbAssured1 = view.findViewById(R.id.realtime_image);
        ImageView fbAssured2 = view.findViewById(R.id.payment_image);
        ImageView fbAssured3 = view.findViewById(R.id.service_image);
        ImageView fbAssured4 = view.findViewById(R.id.price_image);

        Picasso.get().load(fb_assured).into(fbAssured);
        Picasso.get().load(fb_assured1).into(fbAssured1);
        Picasso.get().load(fb_assured2).into(fbAssured2);
        Picasso.get().load(fb_assured3).into(fbAssured3);
        Picasso.get().load(fb_assured4).into(fbAssured4);

        return view;
    }
}
