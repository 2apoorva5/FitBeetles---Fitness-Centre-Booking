package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.services.fitbeetles.Interface.IFacilityReviewsLoadListener;
import com.services.fitbeetles.common.Common;
import com.services.fitbeetles.model.FacilitySectionReviews;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class FacilityReviewsActivity extends AppCompatActivity implements IFacilityReviewsLoadListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef, facilityReviewRef;
    DocumentReference userInfo;

    ImageView close;

    TextView facilityName;
    TextView averageReview;

    RatingBar ratingBar;

    ShimmerFrameLayout shimmerFrameLayout;

    String city;

    RecyclerView recyclerReviews;
    FacilityReviewsAdapter facilityReviewsAdapter;
    IFacilityReviewsLoadListener iFacilityReviewsLoadListener;
    List<FacilitySectionReviews> facilitySectionReviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_reviews);

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
            Intent intent1 = new Intent(FacilityReviewsActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(FacilityReviewsActivity.this, "up-to-bottom");
            finish();
        }

        close = findViewById(R.id.close);
        facilityName = findViewById(R.id.facility_name);
        averageReview = findViewById(R.id.average_review);
        ratingBar = findViewById(R.id.rating_bar);
        shimmerFrameLayout = findViewById(R.id.shimmer);
        recyclerReviews = findViewById(R.id.recycler_reviews);

        shimmerFrameLayout.startShimmer();

        userInfo = userRef.document(firebaseUser.getEmail());
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Alerter.create(FacilityReviewsActivity.this)
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

                    facilityReviewRef = FirebaseFirestore.getInstance().collection("Cities").document(city)
                            .collection(Common.currentCategory).document(Common.currentFacility).collection("Reviews");

                    facilityName.setText(Common.currentFacility);

                    facilitySectionReviewsList = new ArrayList<>();

                    iFacilityReviewsLoadListener = FacilityReviewsActivity.this;

                    facilityReviewRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable final QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if(e != null)
                            {
                                Alerter.create(FacilityReviewsActivity.this)
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
                            if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty())
                            {
                                if(queryDocumentSnapshots.size() > 1)
                                {
                                    facilityReviewRef.orderBy("userName", Query.Direction.ASCENDING).get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        for(QueryDocumentSnapshot reviewSnapshot : task.getResult())
                                                        {
                                                            FacilitySectionReviews review = reviewSnapshot.toObject(FacilitySectionReviews.class);
                                                            facilitySectionReviewsList.add(review);
                                                        }
                                                        iFacilityReviewsLoadListener.onFacilityReviewsLoadSuccess(facilitySectionReviewsList);

                                                        facilityReviewRef.document("Average")
                                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @SuppressLint("ResourceAsColor")
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    DocumentSnapshot ratingSnapshot = task.getResult();
                                                                    if(ratingSnapshot != null && ratingSnapshot.exists())
                                                                    {
                                                                        float rating = ratingSnapshot.getDouble("average").floatValue();

                                                                        rating /= Math.pow(10, (int) Math.log10(rating));
                                                                        rating = ((int) (rating * 10)) / 10.0f;

                                                                        String review = String.valueOf(rating);

                                                                        if((queryDocumentSnapshots.size()-1) > 1)
                                                                        {
                                                                            averageReview.setText(String.format("%s (%d reviews)", review, (queryDocumentSnapshots.size()-1)));
                                                                        }
                                                                        else if((queryDocumentSnapshots.size()-1) == 1)
                                                                        {
                                                                            averageReview.setText(String.format("%s (%d review)", review, (queryDocumentSnapshots.size()-1)));
                                                                        }
                                                                        ratingBar.setRating(rating);
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    iFacilityReviewsLoadListener.onFacilityReviewsLoadFailed(e.getMessage());
                                                }
                                            });
                                }
                                else
                                {
                                    ratingBar.setVisibility(View.GONE);
                                    facilityName.setText("No Reviews yet!");
                                }
                            }
                        }
                    });
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
    public void onFacilityReviewsLoadSuccess(List<FacilitySectionReviews> facilitySectionReviews) {
        if ((shimmerFrameLayout.getVisibility() == View.VISIBLE))
        {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
        }

        recyclerReviews.setHasFixedSize(true);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(FacilityReviewsActivity.this));

        facilityReviewsAdapter = new FacilityReviewsAdapter(FacilityReviewsActivity.this, facilitySectionReviews);
        facilityReviewsAdapter.notifyDataSetChanged();

        recyclerReviews.setAdapter(facilityReviewsAdapter);
        recyclerReviews.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFacilityReviewsLoadFailed(String message) {
        Alerter.create(FacilityReviewsActivity.this)
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



    public class FacilityReviewsAdapter extends RecyclerView.Adapter<FacilityReviewsAdapter.FacilityReviewsViewHolder>
    {
        Context context;
        List<FacilitySectionReviews> mData;

        public FacilityReviewsAdapter(Context context, List<FacilitySectionReviews> mData) {
            this.context = context;
            this.mData = mData;
        }

        @NonNull
        @Override
        public FacilityReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_facility_reviews_layout, parent, false);
            return new FacilityReviewsViewHolder(itemView);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(@NonNull FacilityReviewsViewHolder holder, int position) {
            Picasso.get().load(mData.get(position).getUserPic()).into(holder.reviewUserPic);
            holder.reviewUserName.setText(mData.get(position).getUserName());
            holder.detailedReview.setText(mData.get(position).getDetailedRating());
            holder.ratingBar.setRating(Float.parseFloat(mData.get(position).getOverallRating()));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class FacilityReviewsViewHolder extends RecyclerView.ViewHolder
        {
            CircleImageView reviewUserPic;
            TextView reviewUserName, detailedReview;

            RatingBar ratingBar;

            public FacilityReviewsViewHolder(@NonNull View itemView) {
                super(itemView);

                reviewUserPic = itemView.findViewById(R.id.review_user_pic);
                reviewUserName = itemView.findViewById(R.id.review_user_name);
                detailedReview = itemView.findViewById(R.id.detailed_review);
                ratingBar = itemView.findViewById(R.id.rating_bar);
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
        CustomIntent.customType(FacilityReviewsActivity.this, "up-to-bottom");
    }
}
