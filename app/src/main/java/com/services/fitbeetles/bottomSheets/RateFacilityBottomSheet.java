package com.services.fitbeetles.bottomSheets;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.services.fitbeetles.ChooseCityActivity;
import com.services.fitbeetles.FacilityDetailsActivity;
import com.services.fitbeetles.R;
import com.services.fitbeetles.common.Common;
import com.services.fitbeetles.model.FacilitySectionReviews;
import com.services.fitbeetles.model.UserSectionReviews;
import com.tapadoo.alerter.Alerter;

public class RateFacilityBottomSheet extends BottomSheetDialogFragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef, userReviewRef, facilityReviewRef;
    DocumentReference userInfo;

    TextView textViewFacility;
    RatingBar overallRating;

    EditText detailedReview;

    ConstraintLayout submitRating;

    String city;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facility_review_bottom_sheet, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = FirebaseFirestore.getInstance().collection("Users");

        textViewFacility = view.findViewById(R.id.textView_facility);
        overallRating = view.findViewById(R.id.overall_rating);
        detailedReview = view.findViewById(R.id.detailed_review);
        submitRating = view.findViewById(R.id.submit_rating);

        textViewFacility.setText(String.format("Your overall experience at %s", Common.currentFacility));

        userInfo = userRef.document(firebaseUser.getEmail());
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable final FirebaseFirestoreException e) {
                if(e != null)
                {
                    Alerter.create(getActivity())
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

                    final String userName = documentSnapshot.getData().get("name").toString();
                    final String userProfilePic = documentSnapshot.getData().get("image").toString();

                    userReviewRef = FirebaseFirestore.getInstance().collection("Users")
                            .document(firebaseUser.getEmail()).collection("Reviews");
                    facilityReviewRef = FirebaseFirestore.getInstance().collection("Cities").document(city)
                            .collection(Common.currentCategory).document(Common.currentFacility).collection("Reviews");

                    submitRating.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String rating1 = String.valueOf(overallRating.getRating());
                            final String detail = detailedReview.getText().toString().trim();

                            if(overallRating.getRating() != 0)
                            {
                                DocumentReference userReview = userReviewRef.document(Common.currentFacility);
                                userReview.get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    final DocumentSnapshot userReviewSnapshot = task.getResult();
                                                    final UserSectionReviews reviews1 = new UserSectionReviews(Common.currentFacility, rating1, detail);

                                                    if(!userReviewSnapshot.exists())
                                                    {
                                                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                                        progressDialog.setMessage("Saving your review...");
                                                        progressDialog.show();

                                                        userReviewRef.document(Common.currentFacility)
                                                                .set(reviews1)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        DocumentReference facilityReview = facilityReviewRef.document(firebaseUser.getEmail());
                                                                        facilityReview.get()
                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            final DocumentSnapshot facilityReviewSnapshot = task.getResult();
                                                                                            final FacilitySectionReviews reviews2 = new FacilitySectionReviews(userName, userProfilePic, rating1, detail);

                                                                                            if(!facilityReviewSnapshot.exists())
                                                                                            {
                                                                                                facilityReviewRef.document(firebaseUser.getEmail())
                                                                                                        .set(reviews2)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                final DocumentReference averageRatingRef = facilityReviewRef.document("Average");
                                                                                                                averageRatingRef.get()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                if(task.isSuccessful())
                                                                                                                                {
                                                                                                                                    final DocumentSnapshot ratingSnapshot = task.getResult();
                                                                                                                                    if(ratingSnapshot != null && ratingSnapshot.exists())
                                                                                                                                    {
                                                                                                                                        float total = ratingSnapshot.getDouble("total").floatValue();

                                                                                                                                        final float newTotalValue = (total + overallRating.getRating());

                                                                                                                                        facilityReviewRef.document("Average")
                                                                                                                                                .update("total", newTotalValue)
                                                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                                                                        DocumentReference avgRatingRef = facilityReviewRef.document("Average");
                                                                                                                                                        avgRatingRef.get()
                                                                                                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                                                                    @Override
                                                                                                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                                                        if(task.isSuccessful())
                                                                                                                                                                        {
                                                                                                                                                                            DocumentSnapshot avgRatingSnapshot = task.getResult();
                                                                                                                                                                            if(avgRatingSnapshot != null && avgRatingSnapshot.exists())
                                                                                                                                                                            {
                                                                                                                                                                                final float value = avgRatingSnapshot.getDouble("total").floatValue();

                                                                                                                                                                                facilityReviewRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                                                                    @Override
                                                                                                                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                                                                                        if(task.isSuccessful())
                                                                                                                                                                                        {
                                                                                                                                                                                            float average = value / (task.getResult().size() - 1);
                                                                                                                                                                                            average /= Math.pow(10, (int) Math.log10(average));
                                                                                                                                                                                            average = ((int) (average * 10)) / 10.0f;

                                                                                                                                                                                            final float reviews = average;
                                                                                                                                                                                            facilityReviewRef.document("Average")
                                                                                                                                                                                                    .update("average", average)
                                                                                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                                                        @Override
                                                                                                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                                                                                                            DocumentReference review = FirebaseFirestore.getInstance().collection("Cities").document(city)
                                                                                                                                                                                                                    .collection(Common.currentCategory).document(Common.currentFacility);
                                                                                                                                                                                                            review.update("reviews", reviews)
                                                                                                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                                                                        @Override
                                                                                                                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                                                                                                                            progressDialog.dismiss();
                                                                                                                                                                                                                            dismiss();
                                                                                                                                                                                                                            Alerter.create(getActivity())
                                                                                                                                                                                                                                    .setTitle("Your Review has been Saved!")
                                                                                                                                                                                                                                    .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                                                                                                                                                                                                                                    .setBackgroundColorRes(R.color.bookmarkColor)
                                                                                                                                                                                                                                    .setIcon(R.drawable.info_icon)
                                                                                                                                                                                                                                    .setDuration(3000)
                                                                                                                                                                                                                                    .enableSwipeToDismiss()
                                                                                                                                                                                                                                    .enableIconPulse(true)
                                                                                                                                                                                                                                    .enableVibration(true)
                                                                                                                                                                                                                                    .show();
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                    })
                                                                                                                                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                                                                                                                                        @Override
                                                                                                                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                                                                                                                            Alerter.create(getActivity())
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
                                                                                                                                                                                                                    });
                                                                                                                                                                                                        }
                                                                                                                                                                                                    })
                                                                                                                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                                                                                                                        @Override
                                                                                                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                                                                                                            Alerter.create(getActivity())
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
                                                                                                                                                                                                    });
                                                                                                                                                                                        }
                                                                                                                                                                                    }
                                                                                                                                                                                });
                                                                                                                                                                            }
                                                                                                                                                                        }
                                                                                                                                                                    }
                                                                                                                                                                });
                                                                                                                                                    }
                                                                                                                                                })
                                                                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                                                                        Alerter.create(getActivity())
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
                                                                                                                                                });
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                Alerter.create(getActivity())
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
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Alerter.create(getActivity())
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
                                                                });
                                                    }
                                                    else
                                                    {
                                                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                                        progressDialog.setMessage("Updating your review...");
                                                        progressDialog.show();


                                                        userReviewRef.document(Common.currentFacility)
                                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    DocumentSnapshot previousSnapshot = task.getResult();
                                                                    if(previousSnapshot != null && previousSnapshot.exists())
                                                                    {
                                                                        final float previousRating = Float.parseFloat(previousSnapshot.getData().get("overallRating").toString());

                                                                        final DocumentReference averageRatingRef = facilityReviewRef.document("Average");
                                                                        averageRatingRef.get()
                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                        if(task.isSuccessful())
                                                                                        {
                                                                                            DocumentSnapshot ratingSnapshot = task.getResult();
                                                                                            if(ratingSnapshot != null && ratingSnapshot.exists())
                                                                                            {
                                                                                                float total = ratingSnapshot.getDouble("total").floatValue();
                                                                                                total = total-previousRating;
                                                                                                final float newTotalValue = (total + overallRating.getRating());

                                                                                                facilityReviewRef.document("Average")
                                                                                                        .update("total", newTotalValue)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                userReviewRef.document(Common.currentFacility)
                                                                                                                        .update("facilityName", Common.currentFacility,
                                                                                                                                "overallRating", rating1,
                                                                                                                                "detailedRating", detail)
                                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onSuccess(Void aVoid) {
                                                                                                                                DocumentReference facilityReview = facilityReviewRef.document(firebaseUser.getEmail());
                                                                                                                                facilityReview.get()
                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                                            @Override
                                                                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                                if(task.isSuccessful())
                                                                                                                                                {
                                                                                                                                                    final DocumentSnapshot facilityReviewSnapshot = task.getResult();
                                                                                                                                                    final FacilitySectionReviews reviews2 = new FacilitySectionReviews(userName, userProfilePic, rating1, detail);

                                                                                                                                                    if(facilityReviewSnapshot.exists())
                                                                                                                                                    {
                                                                                                                                                        facilityReviewRef.document(firebaseUser.getEmail())
                                                                                                                                                                .update("userName", userName,
                                                                                                                                                                        "userPic", userProfilePic,
                                                                                                                                                                        "overallRating", rating1,
                                                                                                                                                                        "detailedRating", detail)
                                                                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                    @Override
                                                                                                                                                                    public void onSuccess(Void aVoid) {
                                                                                                                                                                        DocumentReference avgRatingRef = facilityReviewRef.document("Average");
                                                                                                                                                                        avgRatingRef.get()
                                                                                                                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                                                                                    @Override
                                                                                                                                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                                                                                        if(task.isSuccessful())
                                                                                                                                                                                        {
                                                                                                                                                                                            DocumentSnapshot avgRatingSnapshot = task.getResult();
                                                                                                                                                                                            if(avgRatingSnapshot != null && avgRatingSnapshot.exists())
                                                                                                                                                                                            {
                                                                                                                                                                                                final float value = avgRatingSnapshot.getDouble("total").floatValue();

                                                                                                                                                                                                facilityReviewRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                                                                                                                                    @Override
                                                                                                                                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                                                                                                                                        if(task.isSuccessful())
                                                                                                                                                                                                        {
                                                                                                                                                                                                            float average = value / (task.getResult().size() - 1);
                                                                                                                                                                                                            average /= Math.pow(10, (int) Math.log10(average));
                                                                                                                                                                                                            average = ((int) (average * 10)) / 10.0f;

                                                                                                                                                                                                            final float reviews = average;
                                                                                                                                                                                                            facilityReviewRef.document("Average")
                                                                                                                                                                                                                    .update("average", average)
                                                                                                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                                                                        @Override
                                                                                                                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                                                                                                                            DocumentReference review = FirebaseFirestore.getInstance().collection("Cities").document(city)
                                                                                                                                                                                                                                    .collection(Common.currentCategory).document(Common.currentFacility);
                                                                                                                                                                                                                            review.update("reviews", reviews)
                                                                                                                                                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                                                                                                        @Override
                                                                                                                                                                                                                                        public void onSuccess(Void aVoid) {
                                                                                                                                                                                                                                            progressDialog.dismiss();
                                                                                                                                                                                                                                            dismiss();
                                                                                                                                                                                                                                            Alerter.create(getActivity())
                                                                                                                                                                                                                                                    .setTitle("Your Review has been Updated!")
                                                                                                                                                                                                                                                    .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                                                                                                                                                                                                                                                    .setBackgroundColorRes(R.color.bookmarkColor)
                                                                                                                                                                                                                                                    .setIcon(R.drawable.info_icon)
                                                                                                                                                                                                                                                    .setDuration(3000)
                                                                                                                                                                                                                                                    .enableSwipeToDismiss()
                                                                                                                                                                                                                                                    .enableIconPulse(true)
                                                                                                                                                                                                                                                    .enableVibration(true)
                                                                                                                                                                                                                                                    .show();
                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                    })
                                                                                                                                                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                                                                                                                                                        @Override
                                                                                                                                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                                                                                                                                            Alerter.create(getActivity())
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
                                                                                                                                                                                                                                    });
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                    })
                                                                                                                                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                                                                                                                                        @Override
                                                                                                                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                                                                                                                            Alerter.create(getActivity())
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
                                                                                                                                                                                                                    });
                                                                                                                                                                                                        }
                                                                                                                                                                                                    }
                                                                                                                                                                                                });
                                                                                                                                                                                            }
                                                                                                                                                                                        }
                                                                                                                                                                                    }
                                                                                                                                                                                });
                                                                                                                                                                    }
                                                                                                                                                                })
                                                                                                                                                                .addOnFailureListener(new OnFailureListener() {
                                                                                                                                                                    @Override
                                                                                                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                                                                                                        Alerter.create(getActivity())
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
                                                                                                                                                                });
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        });
                                                                                                                            }
                                                                                                                        })
                                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                                            @Override
                                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                                Alerter.create(getActivity())
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
                                                                                                                        });
//
                                                                                                            }
                                                                                                        })
                                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                                            @Override
                                                                                                            public void onFailure(@NonNull Exception e) {
                                                                                                                Alerter.create(getActivity())
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
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            }
                                                        });
//
                                                    }
                                                }
                                            }
                                        });
                            }
                            else
                            {
                                dismiss();
                                Alerter.create(getActivity())
                                        .setTitle("Whoops! Review not saved!")
                                        .setText("The rating can't be 0!")
                                        .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                                        .setBackgroundColorRes(R.color.colorPrimary)
                                        .setIcon(R.drawable.info_icon)
                                        .setDuration(3000)
                                        .enableSwipeToDismiss()
                                        .enableIconPulse(true)
                                        .enableVibration(true)
                                        .show();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }
}
