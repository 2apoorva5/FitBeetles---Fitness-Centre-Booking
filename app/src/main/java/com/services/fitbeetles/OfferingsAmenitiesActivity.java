package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.services.fitbeetles.Interface.IAvailableAmenitiesLoadListener;
import com.services.fitbeetles.Interface.IAvailableOfferingsLoadListener;
import com.services.fitbeetles.Interface.IUnavailableAmenitiesLoadListener;
import com.services.fitbeetles.Interface.IUnavailableOfferingsLoadListener;
import com.services.fitbeetles.common.Common;
import com.services.fitbeetles.model.AvailableAmenities;
import com.services.fitbeetles.model.AvailableOfferings;
import com.services.fitbeetles.model.UnavailableAmenities;
import com.services.fitbeetles.model.UnavailableOfferings;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class OfferingsAmenitiesActivity extends AppCompatActivity implements IAvailableOfferingsLoadListener, IUnavailableOfferingsLoadListener,
                                                                             IAvailableAmenitiesLoadListener, IUnavailableAmenitiesLoadListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef, availableOfferingsRef, unavailableOfferingsRef, availableAmenitiesRef, unavailableAmenitiesRef;
    DocumentReference userInfo;

    ImageView close;

    TextView title, titleByFacility, includedText, notIncludedText;

    String city;

    RecyclerView recyclerAvailableFacilities, recyclerUnavailableFacilities;
    AvailableOfferingsAdapter availableOfferingsAdapter;
    UnavailableOfferingsAdapter unavailableOfferingsAdapter;
    AvailableAmenitiesAdapter availableAmenitiesAdapter;
    UnavailableAmenitiesAdapter unavailableAmenitiesAdapter;
    IAvailableOfferingsLoadListener iAvailableOfferingsLoadListener;
    IUnavailableOfferingsLoadListener iUnavailableOfferingsLoadListener;
    IAvailableAmenitiesLoadListener iAvailableAmenitiesLoadListener;
    IUnavailableAmenitiesLoadListener iUnavailableAmenitiesLoadListener;
    List<AvailableOfferings> availableOfferingsList;
    List<UnavailableOfferings> unavailableOfferingsList;
    List<AvailableAmenities> availableAmenitiesList;
    List<UnavailableAmenities> unavailableAmenitiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerings_amenities);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = FirebaseFirestore.getInstance().collection("Users");

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(OfferingsAmenitiesActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(OfferingsAmenitiesActivity.this, "up-to-bottom");
            finish();
        }

        close = findViewById(R.id.close);
        title = findViewById(R.id.title);
        titleByFacility = findViewById(R.id.title_by_facility);
        includedText = findViewById(R.id.included_text);
        notIncludedText = findViewById(R.id.not_included_text);
        recyclerAvailableFacilities = findViewById(R.id.recycler_available_facilities);
        recyclerUnavailableFacilities = findViewById(R.id.recycler_unavailable_facilities);

        userInfo = userRef.document(firebaseUser.getEmail());
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Alerter.create(OfferingsAmenitiesActivity.this)
                            .setTitle("ERROR!")
                            .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                            .setBackgroundColorRes(R.color.errorColor)
                            .setIcon(R.drawable.error_icon)
                            .setDuration(3000)
                            .enableSwipeToDismiss()
                            .enableIconPulse(true)
                            .enableVibration(true)
                            .show();
                }
                if(documentSnapshot != null && documentSnapshot.exists())
                {
                    city = documentSnapshot.getData().get("city").toString();

                    availableOfferingsRef = FirebaseFirestore.getInstance().collection("Cities").document(city)
                            .collection(Common.currentCategory).document(Common.currentFacility).collection("AvailableOfferings");
                    unavailableOfferingsRef = FirebaseFirestore.getInstance().collection("Cities").document(city)
                            .collection(Common.currentCategory).document(Common.currentFacility).collection("UnavailableOfferings");
                    availableAmenitiesRef = FirebaseFirestore.getInstance().collection("Cities").document(city)
                            .collection(Common.currentCategory).document(Common.currentFacility).collection("AvailableAmenities");
                    unavailableAmenitiesRef = FirebaseFirestore.getInstance().collection("Cities").document(city)
                            .collection(Common.currentCategory).document(Common.currentFacility).collection("UnavailableAmenities");

                    availableOfferingsList = new ArrayList<>();
                    unavailableOfferingsList = new ArrayList<>();
                    availableAmenitiesList = new ArrayList<>();
                    unavailableAmenitiesList = new ArrayList<>();

                    if(Common.currentOfferingsAmenities.equals("Offerings"))
                    {
                        title.setText("Offerings");
                        titleByFacility.setText(String.format("The offerings and services offered by %s", Common.currentFacility));

                        iAvailableOfferingsLoadListener = OfferingsAmenitiesActivity.this;
                        iUnavailableOfferingsLoadListener = OfferingsAmenitiesActivity.this;

                        availableOfferingsRef.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful())
                                        {
                                            for(QueryDocumentSnapshot availableOfferingSnapshot : task.getResult())
                                            {
                                                AvailableOfferings availableOfferings = availableOfferingSnapshot.toObject(AvailableOfferings.class);
                                                availableOfferingsList.add(availableOfferings);
                                            }
                                            iAvailableOfferingsLoadListener.onAvailableOfferingsLoadSuccess(availableOfferingsList);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        iAvailableOfferingsLoadListener.onAvailableOfferingsLoadFailed(e.getMessage());
                                    }
                                });

                        unavailableOfferingsRef.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful())
                                        {
                                            for(QueryDocumentSnapshot unavailableOfferingSnapshot : task.getResult())
                                            {
                                                UnavailableOfferings unavailableOfferings = unavailableOfferingSnapshot.toObject(UnavailableOfferings.class);
                                                unavailableOfferingsList.add(unavailableOfferings);
                                            }
                                            iUnavailableOfferingsLoadListener.onUnavailableOfferingsLoadSuccess(unavailableOfferingsList);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        iUnavailableOfferingsLoadListener.onUnavailableOfferingsLoadFailed(e.getMessage());
                                    }
                                });
                    }
                    else if(Common.currentOfferingsAmenities.equals("Amenities"))
                    {
                        title.setText("Amenities");
                        titleByFacility.setText(String.format("The facilities and amenities offered by %s", Common.currentFacility));

                        iAvailableAmenitiesLoadListener = OfferingsAmenitiesActivity.this;
                        iUnavailableAmenitiesLoadListener = OfferingsAmenitiesActivity.this;

                        availableAmenitiesRef.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful())
                                        {
                                            for(QueryDocumentSnapshot availableAmenitySnapshot : task.getResult())
                                            {
                                                AvailableAmenities availableAmenities = availableAmenitySnapshot.toObject(AvailableAmenities.class);
                                                availableAmenitiesList.add(availableAmenities);
                                            }
                                            iAvailableAmenitiesLoadListener.onAvailableAmenitiesLoadSuccess(availableAmenitiesList);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        iAvailableAmenitiesLoadListener.onAvailableAmenitiesLoadFailed(e.getMessage());
                                    }
                                });

                        unavailableAmenitiesRef.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful())
                                        {
                                            for(QueryDocumentSnapshot unavailableAmenitySnapshot : task.getResult())
                                            {
                                                UnavailableAmenities unavailableAmenities = unavailableAmenitySnapshot.toObject(UnavailableAmenities.class);
                                                unavailableAmenitiesList.add(unavailableAmenities);
                                            }
                                            iUnavailableAmenitiesLoadListener.onUnavailableAmenitiesLoadSuccess(unavailableAmenitiesList);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        iUnavailableAmenitiesLoadListener.onUnavailableAmenitiesLoadFailed(e.getMessage());
                                    }
                                });
                    }
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onAvailableOfferingsLoadSuccess(List<AvailableOfferings> availableOfferings) {
        if(includedText.getVisibility() == View.INVISIBLE)
        {
            includedText.setVisibility(View.VISIBLE);
        }
        recyclerAvailableFacilities.setHasFixedSize(true);
        recyclerAvailableFacilities.setLayoutManager(new GridLayoutManager(OfferingsAmenitiesActivity.this, 4));

        availableOfferingsAdapter = new AvailableOfferingsAdapter(OfferingsAmenitiesActivity.this, availableOfferings);
        availableOfferingsAdapter.notifyDataSetChanged();

        recyclerAvailableFacilities.setAdapter(availableOfferingsAdapter);
        recyclerAvailableFacilities.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onAvailableOfferingsLoadFailed(String message) {
        Alerter.create(OfferingsAmenitiesActivity.this)
                .setTitle("ERROR!")
                .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                .setBackgroundColorRes(R.color.errorColor)
                .setIcon(R.drawable.error_icon)
                .setDuration(3000)
                .enableSwipeToDismiss()
                .enableIconPulse(true)
                .enableVibration(true)
                .show();
    }

    @Override
    public void onUnavailableOfferingsLoadSuccess(List<UnavailableOfferings> unavailableOfferings) {
        if(notIncludedText.getVisibility() == View.INVISIBLE)
        {
            notIncludedText.setVisibility(View.VISIBLE);
        }
        recyclerUnavailableFacilities.setHasFixedSize(true);
        recyclerUnavailableFacilities.setLayoutManager(new GridLayoutManager(OfferingsAmenitiesActivity.this, 4));

        unavailableOfferingsAdapter = new UnavailableOfferingsAdapter(OfferingsAmenitiesActivity.this, unavailableOfferings);
        unavailableOfferingsAdapter.notifyDataSetChanged();

        recyclerUnavailableFacilities.setAdapter(unavailableOfferingsAdapter);
        recyclerUnavailableFacilities.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onUnavailableOfferingsLoadFailed(String message) {
        Alerter.create(OfferingsAmenitiesActivity.this)
                .setTitle("ERROR!")
                .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                .setBackgroundColorRes(R.color.errorColor)
                .setIcon(R.drawable.error_icon)
                .setDuration(3000)
                .enableSwipeToDismiss()
                .enableIconPulse(true)
                .enableVibration(true)
                .show();
    }

    @Override
    public void onAvailableAmenitiesLoadSuccess(List<AvailableAmenities> availableAmenities) {
        if(includedText.getVisibility() == View.INVISIBLE)
        {
            includedText.setVisibility(View.VISIBLE);
        }
        recyclerAvailableFacilities.setHasFixedSize(true);
        recyclerAvailableFacilities.setLayoutManager(new GridLayoutManager(OfferingsAmenitiesActivity.this, 4));

        availableAmenitiesAdapter = new AvailableAmenitiesAdapter(OfferingsAmenitiesActivity.this, availableAmenities);
        availableAmenitiesAdapter.notifyDataSetChanged();

        recyclerAvailableFacilities.setAdapter(availableAmenitiesAdapter);
        recyclerAvailableFacilities.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onAvailableAmenitiesLoadFailed(String message) {
        Alerter.create(OfferingsAmenitiesActivity.this)
                .setTitle("ERROR!")
                .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                .setBackgroundColorRes(R.color.errorColor)
                .setIcon(R.drawable.error_icon)
                .setDuration(3000)
                .enableSwipeToDismiss()
                .enableIconPulse(true)
                .enableVibration(true)
                .show();
    }

    @Override
    public void onUnavailableAmenitiesLoadSuccess(List<UnavailableAmenities> unavailableAmenities) {
        if(notIncludedText.getVisibility() == View.INVISIBLE)
        {
            notIncludedText.setVisibility(View.VISIBLE);
        }
        recyclerUnavailableFacilities.setHasFixedSize(true);
        recyclerUnavailableFacilities.setLayoutManager(new GridLayoutManager(OfferingsAmenitiesActivity.this, 4));

        unavailableAmenitiesAdapter = new UnavailableAmenitiesAdapter(OfferingsAmenitiesActivity.this, unavailableAmenities);
        unavailableAmenitiesAdapter.notifyDataSetChanged();

        recyclerUnavailableFacilities.setAdapter(unavailableAmenitiesAdapter);
        recyclerUnavailableFacilities.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onUnavailableAmenitiesLoadFailed(String message) {
        Alerter.create(OfferingsAmenitiesActivity.this)
                .setTitle("ERROR!")
                .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                .setBackgroundColorRes(R.color.errorColor)
                .setIcon(R.drawable.error_icon)
                .setDuration(3000)
                .enableSwipeToDismiss()
                .enableIconPulse(true)
                .enableVibration(true)
                .show();
    }


    public class AvailableOfferingsAdapter extends RecyclerView.Adapter<AvailableOfferingsAdapter.AvailableOfferingsViewHolder>
    {
        Context context;
        List<AvailableOfferings> mData;

        public AvailableOfferingsAdapter(Context context, List<AvailableOfferings> mData) {
            this.context = context;
            this.mData = mData;
        }

        @NonNull
        @Override
        public AvailableOfferingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_offerings_amenities_layout, parent, false);
            return new AvailableOfferingsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AvailableOfferingsViewHolder holder, int position) {
            Picasso.get().load(mData.get(position).getImage()).into(holder.availableOfferingsImg);
            holder.availableOfferingsName.setText(mData.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class AvailableOfferingsViewHolder extends RecyclerView.ViewHolder
        {
            ImageView availableOfferingsImg;
            TextView availableOfferingsName;

            public AvailableOfferingsViewHolder(@NonNull View itemView) {
                super(itemView);

                availableOfferingsImg = itemView.findViewById(R.id.service_image);
                availableOfferingsName = itemView.findViewById(R.id.service_name);
            }
        }
    }


    public class UnavailableOfferingsAdapter extends RecyclerView.Adapter<UnavailableOfferingsAdapter.UnavailableOfferingsViewHolder>
    {
        Context context;
        List<UnavailableOfferings> mData;

        public UnavailableOfferingsAdapter(Context context, List<UnavailableOfferings> mData) {
            this.context = context;
            this.mData = mData;
        }

        @NonNull
        @Override
        public UnavailableOfferingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_offerings_amenities_layout, parent, false);
            return new UnavailableOfferingsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull UnavailableOfferingsViewHolder holder, int position) {
            Picasso.get().load(mData.get(position).getImage()).into(holder.unavailableOfferingsImg);
            holder.unavailableOfferingsName.setText(mData.get(position).getName());
            holder.unavailableOfferingsName.setTextColor(Color.parseColor("#b1bed5"));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class UnavailableOfferingsViewHolder extends RecyclerView.ViewHolder
        {
            ImageView unavailableOfferingsImg;
            TextView unavailableOfferingsName;

            public UnavailableOfferingsViewHolder(@NonNull View itemView) {
                super(itemView);

                unavailableOfferingsImg = itemView.findViewById(R.id.service_image);
                unavailableOfferingsName = itemView.findViewById(R.id.service_name);
            }
        }
    }


    public class AvailableAmenitiesAdapter extends RecyclerView.Adapter<AvailableAmenitiesAdapter.AvailableAmenitiesViewHolder>
    {
        Context context;
        List<AvailableAmenities> mData;

        public AvailableAmenitiesAdapter(Context context, List<AvailableAmenities> mData) {
            this.context = context;
            this.mData = mData;
        }

        @NonNull
        @Override
        public AvailableAmenitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_offerings_amenities_layout, parent, false);
            return new AvailableAmenitiesViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AvailableAmenitiesViewHolder holder, int position) {
            Picasso.get().load(mData.get(position).getImage()).into(holder.availableAmenitiesImg);
            holder.availableAmenitiesName.setText(mData.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class AvailableAmenitiesViewHolder extends RecyclerView.ViewHolder
        {
            ImageView availableAmenitiesImg;
            TextView availableAmenitiesName;

            public AvailableAmenitiesViewHolder(@NonNull View itemView) {
                super(itemView);

                availableAmenitiesImg = itemView.findViewById(R.id.service_image);
                availableAmenitiesName = itemView.findViewById(R.id.service_name);
            }
        }
    }


    public class UnavailableAmenitiesAdapter extends RecyclerView.Adapter<UnavailableAmenitiesAdapter.UnavailableAmenitiesViewHolder>
    {
        Context context;
        List<UnavailableAmenities> mData;

        public UnavailableAmenitiesAdapter(Context context, List<UnavailableAmenities> mData) {
            this.context = context;
            this.mData = mData;
        }

        @NonNull
        @Override
        public UnavailableAmenitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_offerings_amenities_layout, parent, false);
            return new UnavailableAmenitiesViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull UnavailableAmenitiesViewHolder holder, int position) {
            Picasso.get().load(mData.get(position).getImage()).into(holder.unavailableAmenitiesImg);
            holder.unavailableAmenitiesName.setText(mData.get(position).getName());
            holder.unavailableAmenitiesName.setTextColor(Color.parseColor("#b1bed5"));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class UnavailableAmenitiesViewHolder extends RecyclerView.ViewHolder
        {
            ImageView unavailableAmenitiesImg;
            TextView unavailableAmenitiesName;

            public UnavailableAmenitiesViewHolder(@NonNull View itemView) {
                super(itemView);

                unavailableAmenitiesImg = itemView.findViewById(R.id.service_image);
                unavailableAmenitiesName = itemView.findViewById(R.id.service_name);
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(OfferingsAmenitiesActivity.this, "up-to-bottom");
    }
}
