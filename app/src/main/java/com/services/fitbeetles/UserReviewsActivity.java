package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.services.fitbeetles.Interface.IUserReviewsLoadListener;
import com.services.fitbeetles.common.Common;
import com.services.fitbeetles.model.Bookmarks;
import com.services.fitbeetles.model.FacilitySectionReviews;
import com.services.fitbeetles.model.UserSectionReviews;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;
import pl.droidsonroids.gif.GifImageView;

public class UserReviewsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef, userReviewRef;

    ImageView back;
    TextView reviewsCount;
    GifImageView ratingGif;

    RecyclerView recyclerReviews;
    UserReviewsAdapter userReviewsAdapter;

    private static int LAST_POSITION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reviews);

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
        userReviewRef = FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getEmail()).collection("Reviews");

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(UserReviewsActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(UserReviewsActivity.this, "up-to-bottom");
            finish();
        }

        back = findViewById(R.id.back_arrow);
        reviewsCount = findViewById(R.id.reviews_count);
        ratingGif = findViewById(R.id.rating_gif);
        recyclerReviews = findViewById(R.id.recycler_reviews);

        setUpRecyclerViewForReviews();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setUpRecyclerViewForReviews()
    {
        Query query = userReviewRef.orderBy("facilityName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<UserSectionReviews> options = new FirestoreRecyclerOptions.Builder<UserSectionReviews>()
                .setQuery(query, UserSectionReviews.class)
                .build();

        userReviewsAdapter = new UserReviewsAdapter(options);
        userReviewsAdapter.notifyDataSetChanged();

        recyclerReviews.setHasFixedSize(true);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(UserReviewsActivity.this));
        recyclerReviews.setAdapter(userReviewsAdapter);

        recyclerReviews.getAdapter().notifyDataSetChanged();
    }



    public class UserReviewsAdapter extends FirestoreRecyclerAdapter<UserSectionReviews, UserReviewsAdapter.MyViewHolder>
    {
        public UserReviewsAdapter(@NonNull FirestoreRecyclerOptions<UserSectionReviews> options) {
            super(options);
        }

        @Override
        protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull UserSectionReviews userSectionReviews) {
            holder.facilityName.setText(userSectionReviews.getFacilityName());
            holder.detailedReview.setText(userSectionReviews.getDetailedRating());
            holder.ratingBar.setRating(Float.parseFloat(userSectionReviews.getOverallRating()));

            setAnimation(holder.itemView, position);
        }

        public void setAnimation(View viewToAnimate, int position)
        {
            if(position > LAST_POSITION)
            {
                ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(1000);

                viewToAnimate.setAnimation(scaleAnimation);
                LAST_POSITION = position;
            }
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_user_reviews_layout, parent, false);
            return new MyViewHolder(itemView);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onDataChanged() {
            super.onDataChanged();

            if(getItemCount() != 0)
            {
                if(getItemCount() > 1)
                {
                    reviewsCount.setText(String.format("You have reviewed %d outlets or places till now.", getItemCount()));
                }
                else if(getItemCount() == 1)
                {
                    reviewsCount.setText(String.format("You have reviewed %d outlet or place till now.", getItemCount()));
                }
                if(ratingGif.getVisibility() == View.VISIBLE)
                {
                    ratingGif.setVisibility(View.GONE);
                }
                if(reviewsCount.getVisibility() == View.INVISIBLE)
                {
                    reviewsCount.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                reviewsCount.setText("Your reviewed places or outlets appear here.");
                if(ratingGif.getVisibility() == View.GONE)
                {
                    ratingGif.setVisibility(View.VISIBLE);
                }
                if(reviewsCount.getVisibility() == View.INVISIBLE)
                {
                    reviewsCount.setVisibility(View.VISIBLE);
                }
            }
        }

        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            TextView facilityName, detailedReview;
            RatingBar ratingBar;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                facilityName = itemView.findViewById(R.id.review_facility_name);
                ratingBar = itemView.findViewById(R.id.rating_bar);
                detailedReview = itemView.findViewById(R.id.detailed_review);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userReviewsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userReviewsAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userReviewsAdapter.stopListening();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        userReviewsAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userReviewsAdapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(UserReviewsActivity.this, "right-to-left");
    }
}
