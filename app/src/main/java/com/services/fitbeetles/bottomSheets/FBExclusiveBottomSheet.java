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

public class FBExclusiveBottomSheet extends BottomSheetDialogFragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fbexclusive_bottom_sheet_layout, container, false);

        String fb_exclusive = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBExclusive%2Ffb_exclusive_white.jpg?alt=media&token=4bf40293-d13e-43a6-9008-dc679c3c7887";
        String fb_exclusive1 = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBExclusive%2Ffbexclusive1.png?alt=media&token=bc7260f2-d03f-4159-ad41-5457336a52ce";
        String fb_exclusive2 = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBExclusive%2Ffbexclusive2.png?alt=media&token=bdd2061e-6e0d-40ba-b2eb-4a6d9dcd9682";
        String fb_exclusive3 = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBExclusive%2Ffbexclusive3.png?alt=media&token=cdcb9619-4654-4c4d-94a7-475c7d57cf25";
        String fb_exclusive4 = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBExclusive%2Ffbexclusive4.png?alt=media&token=db6bf4cf-a8a5-43b9-821e-d763227d7b1b";

        ImageView fbExclusive = view.findViewById(R.id.fb_exclusive);
        ImageView fbExclusive1 = view.findViewById(R.id.money_image);
        ImageView fbExclusive2 = view.findViewById(R.id.easy_image);
        ImageView fbExclusive3 = view.findViewById(R.id.access_image);
        ImageView fbExclusive4 = view.findViewById(R.id.addicted_image);

        Picasso.get().load(fb_exclusive).into(fbExclusive);
        Picasso.get().load(fb_exclusive1).into(fbExclusive1);
        Picasso.get().load(fb_exclusive2).into(fbExclusive2);
        Picasso.get().load(fb_exclusive3).into(fbExclusive3);
        Picasso.get().load(fb_exclusive4).into(fbExclusive4);

        return view;
    }
}
