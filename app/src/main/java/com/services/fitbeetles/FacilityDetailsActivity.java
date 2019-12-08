package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.services.fitbeetles.Adapter.GalleryAdapter;
import com.services.fitbeetles.Interface.IAvailableAmenitiesLoadListener;
import com.services.fitbeetles.Interface.IAvailableOfferingsLoadListener;
import com.services.fitbeetles.Interface.IFacilityReviewsLoadListener;
import com.services.fitbeetles.bottomSheets.FBAssuredBottomSheet;
import com.services.fitbeetles.bottomSheets.FBExclusiveBottomSheet;
import com.services.fitbeetles.bottomSheets.RateFacilityBottomSheet;
import com.services.fitbeetles.common.Common;
import com.services.fitbeetles.model.AvailableAmenities;
import com.services.fitbeetles.model.AvailableOfferings;
import com.services.fitbeetles.model.Bookmarks;
import com.services.fitbeetles.model.Facilities;
import com.services.fitbeetles.model.FacilitySectionReviews;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import de.mateware.snacky.Snacky;
import maes.tech.intentanim.CustomIntent;

public class FacilityDetailsActivity extends AppCompatActivity implements OnMapReadyCallback, IAvailableOfferingsLoadListener,
                                                                          IAvailableAmenitiesLoadListener, IFacilityReviewsLoadListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef, bookmarksRef, availableOfferingsRef, availableAmenitiesRef, reviewsRef;
    DocumentReference userInfo, facilityDetails;

    CardView openOrClosedCard, cardAddReview;
    SliderView imagePager;
    ImageView close, bookmarkFacility, callFacility, shareFacility, fbAssured, fbExclusive, payPerSessionInfo;
    TextView name, openOrClosedText, address, reviews, timing1, timing2, timing3, timing4, timing5, timing6, timing7, fbExclusiveInfo,
             viewAllOfferings, viewAllAmenities, noReviewsYet, seeAllReviews;
    RatingBar ratingBar;
    ConstraintLayout viewInMap, addReview;

    String city, number;
    String[] imageUrls;

    RecyclerView recyclerOfferings, recyclerAmenities, recyclerReviews;
    OfferingsAdapter offeringsAdapter;
    AmenitiesAdapter amenitiesAdapter;
    FacilityReviewsAdapter facilityReviewsAdapter;
    IAvailableOfferingsLoadListener iAvailableOfferingsLoadListener;
    IAvailableAmenitiesLoadListener iAvailableAmenitiesLoadListener;
    IFacilityReviewsLoadListener iFacilityReviewsLoadListener;
    List<AvailableOfferings> availableOfferingsList;
    List<AvailableAmenities> availableAmenitiesList;
    List<FacilitySectionReviews> facilitySectionReviewsList;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if(ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_details);

        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(FacilityDetailsActivity.this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = FirebaseFirestore.getInstance().collection("Users");

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(FacilityDetailsActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(FacilityDetailsActivity.this, "right-to-left");
            finish();
        }

        imagePager = findViewById(R.id.slider_gallery);
        close = findViewById(R.id.close);
        fbAssured = findViewById(R.id.fb_assured);
        openOrClosedCard = findViewById(R.id.open_or_closed_card);
        bookmarkFacility = findViewById(R.id.bookmark_facility);
        callFacility = findViewById(R.id.call_facility);
        shareFacility = findViewById(R.id.share_facility);

        name = findViewById(R.id.name);
        openOrClosedText = findViewById(R.id.open_or_closed_text);
        address = findViewById(R.id.address);
        viewInMap = findViewById(R.id.view_in_map);
        reviews = findViewById(R.id.reviews);
        timing1 = findViewById(R.id.timing1);
        timing2 = findViewById(R.id.timing2);
        timing3 = findViewById(R.id.timing3);
        timing4 = findViewById(R.id.timing4);
        timing5 = findViewById(R.id.timing5);
        timing6 = findViewById(R.id.timing6);
        timing7 = findViewById(R.id.timing7);
        fbExclusive = findViewById(R.id.fb_exclusive);
        payPerSessionInfo = findViewById(R.id.pay_per_session_info);
        fbExclusiveInfo = findViewById(R.id.fb_exclusive_info1);
        addReview = findViewById(R.id.add_review);
        viewAllOfferings = findViewById(R.id.view_all_offerings);
        viewAllAmenities = findViewById(R.id.view_all_amenities);
        recyclerOfferings = findViewById(R.id.recycler_offerings);
        recyclerAmenities = findViewById(R.id.recycler_amenities);
        recyclerReviews = findViewById(R.id.recycler_reviews);
        noReviewsYet = findViewById(R.id.no_reviews_yet_text);
        seeAllReviews = findViewById(R.id.see_all_reviews);
        cardAddReview = findViewById(R.id.card_add_review);
        addReview = findViewById(R.id.add_review);
        ratingBar = findViewById(R.id.rating_bar);

        userInfo = userRef.document(firebaseUser.getEmail());
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Alerter.create(FacilityDetailsActivity.this)
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

                    facilityDetails = FirebaseFirestore.getInstance().collection("Cities").document(city)
                                      .collection(Common.currentCategory).document(Common.currentFacility);
                    bookmarksRef = FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getEmail())
                                   .collection("Bookmarks");
                    reviewsRef = FirebaseFirestore.getInstance().collection("Cities").document(city).collection(Common.currentCategory)
                                 .document(Common.currentFacility).collection("Reviews");

                    availableOfferingsRef = FirebaseFirestore.getInstance().collection("Cities").document(city)
                            .collection(Common.currentCategory).document(Common.currentFacility).collection("AvailableOfferings");
                    availableAmenitiesRef = FirebaseFirestore.getInstance().collection("Cities").document(city)
                            .collection(Common.currentCategory).document(Common.currentFacility).collection("AvailableAmenities");

                    availableOfferingsList = new ArrayList<>();
                    availableAmenitiesList = new ArrayList<>();
                    facilitySectionReviewsList = new ArrayList<>();

                    iAvailableOfferingsLoadListener = FacilityDetailsActivity.this;
                    iAvailableAmenitiesLoadListener = FacilityDetailsActivity.this;
                    iFacilityReviewsLoadListener = FacilityDetailsActivity.this;

                    availableOfferingsRef.limit(4).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        for(QueryDocumentSnapshot offeringSnapshot : task.getResult())
                                        {
                                            AvailableOfferings offering = offeringSnapshot.toObject(AvailableOfferings.class);
                                            availableOfferingsList.add(offering);
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

                    availableAmenitiesRef.limit(4).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        for(QueryDocumentSnapshot amenitySnapshot : task.getResult())
                                        {
                                            AvailableAmenities amenity = amenitySnapshot.toObject(AvailableAmenities.class);
                                            availableAmenitiesList.add(amenity);
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

                    facilityDetails.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable final FirebaseFirestoreException e) {
                            if(e != null)
                            {
                                Alerter.create(FacilityDetailsActivity.this)
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
                                final String facilityName = documentSnapshot.getData().get("name").toString();
                                final String facilityOpenOrClosed = documentSnapshot.getData().get("openOrClosed").toString();
                                final String facilityAddress = documentSnapshot.getData().get("address").toString();
                                final String facilityContact = documentSnapshot.getData().get("contact").toString();
                                final String facilityTiming1 = documentSnapshot.getData().get("timing1").toString();
                                final String facilityTiming2 = documentSnapshot.getData().get("timing2").toString();
                                final String facilityTiming3 = documentSnapshot.getData().get("timing3").toString();
                                final String facilityTiming4 = documentSnapshot.getData().get("timing4").toString();
                                final String facilityTiming5 = documentSnapshot.getData().get("timing5").toString();
                                final String facilityTiming6 = documentSnapshot.getData().get("timing6").toString();
                                final String facilityTiming7 = documentSnapshot.getData().get("timing7").toString();

                                final String facilityImage = documentSnapshot.getData().get("headerImage").toString();
                                final String gallery1 = documentSnapshot.getData().get("galleryImage1").toString();
                                final String gallery2 = documentSnapshot.getData().get("galleryImage2").toString();
                                final String gallery3 = documentSnapshot.getData().get("galleryImage3").toString();
                                final String gallery4 = documentSnapshot.getData().get("galleryImage4").toString();
                                final String gallery5 = documentSnapshot.getData().get("galleryImage5").toString();
                                final String gallery6 = documentSnapshot.getData().get("galleryImage6").toString();

                                final Boolean facilityFBAssured = documentSnapshot.getBoolean("fbAssured");

                                name.setText(facilityName);
                                openOrClosedText.setText(facilityOpenOrClosed);
                                address.setText(facilityAddress);
                                timing1.setText(String.format("Monday : %s", facilityTiming1));
                                timing2.setText(String.format("Tuesday : %s", facilityTiming2));
                                timing3.setText(String.format("Wednesday : %s", facilityTiming3));
                                timing4.setText(String.format("Thursday : %s", facilityTiming4));
                                timing5.setText(String.format("Friday : %s", facilityTiming5));
                                timing6.setText(String.format("Saturday : %s", facilityTiming6));
                                timing7.setText(String.format("Sunday : %s", facilityTiming7));
                                fbExclusiveInfo.setText(String.format("Now working out at %s is possible even without buying a membership.", facilityName));

                                if(openOrClosedText.getText().toString().equals("Open"))
                                {
                                    openOrClosedCard.setCardBackgroundColor(Color.parseColor("#76a21e"));
                                }
                                else if(openOrClosedText.getText().toString().equals("Closed"))
                                {
                                    openOrClosedCard.setCardBackgroundColor(Color.parseColor("#ed3833"));
                                }

                                number = facilityContact;

                                imageUrls = new String[]{facilityImage, gallery1, gallery2, gallery3, gallery4, gallery5, gallery6};
                                GalleryAdapter galleryAdapter = new GalleryAdapter(FacilityDetailsActivity.this, imageUrls);
                                imagePager.setSliderAdapter(galleryAdapter);
                                imagePager.setIndicatorAnimation(IndicatorAnimations.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                                imagePager.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
                                imagePager.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                                imagePager.setIndicatorSelectedColor(Color.parseColor("#ffffff"));
                                imagePager.setIndicatorUnselectedColor(Color.parseColor("#6bc5d2"));
                                imagePager.setScrollTimeInSec(4); //set scroll delay in seconds :
                                imagePager.startAutoCycle();

                                if(facilityFBAssured == true)
                                {
                                    fbAssured.setVisibility(View.VISIBLE);
                                    String fb_assured = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBAssured%2Ffb_assured.png?alt=media&token=e1168150-6c02-4089-b753-3d62053a7f12";
                                    Picasso.get().load(fb_assured).into(fbAssured);
                                    fbAssured.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            FBAssuredBottomSheet fbAssuredBottomSheet = new FBAssuredBottomSheet();
                                            fbAssuredBottomSheet.show(getSupportFragmentManager(), "fbAssuredBottomSheet");
                                        }
                                    });
                                }
                                else if(facilityFBAssured == false)
                                {
                                    fbAssured.setVisibility(View.GONE);
                                }

                                reviewsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @SuppressLint("ResourceAsColor")
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if(e != null)
                                        {
                                            Alerter.create(FacilityDetailsActivity.this)
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
                                                seeAllReviews.setEnabled(true);
                                                seeAllReviews.setText("See All Reviews");

                                                reviewsRef.orderBy("userName", Query.Direction.ASCENDING).limit(1).get()
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

                                                                    facilityDetails
                                                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if(task.isSuccessful())
                                                                            {
                                                                                DocumentSnapshot ratingSnapshot = task.getResult();
                                                                                if(ratingSnapshot != null && ratingSnapshot.exists())
                                                                                {
                                                                                    float rating = ratingSnapshot.getDouble("reviews").floatValue();

                                                                                    rating /= Math.pow(10, (int) Math.log10(rating));
                                                                                    rating = ((int) (rating * 10)) / 10.0f;

                                                                                    String review = String.valueOf(rating);

                                                                                    reviews.setText(review);
                                                                                    reviews.setTextColor(Color.parseColor("#004D61"));
                                                                                    ratingBar.setRating(rating);
                                                                                    reviews.setVisibility(View.VISIBLE);
                                                                                    ratingBar.setVisibility(View.VISIBLE);
                                                                                    noReviewsYet.setVisibility(View.GONE);
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
                                                seeAllReviews.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        startActivity(new Intent(FacilityDetailsActivity.this, FacilityReviewsActivity.class));
                                                        CustomIntent.customType(FacilityDetailsActivity.this, "bottom_to_up");
                                                    }
                                                });
                                            }
                                            else
                                            {
                                                reviews.setText("No Reviews Yet");
                                                reviews.setTextColor(Color.parseColor("#ff502f"));
                                                reviews.setVisibility(View.VISIBLE);
                                                ratingBar.setVisibility(View.GONE);
                                                noReviewsYet.setVisibility(View.VISIBLE);
                                                seeAllReviews.setEnabled(false);
                                                seeAllReviews.setText("Be the First to Review");
                                            }
                                        }
                                    }
                                });

                                DocumentReference bookmarked = bookmarksRef.document(Common.currentFacility);
                                bookmarked.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot documentSnapshot, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                                        if(e != null)
                                        {
                                            Alerter.create(FacilityDetailsActivity.this)
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
                                        else if(documentSnapshot != null && documentSnapshot.exists())
                                        {
                                            bookmarkFacility.setImageResource(R.drawable.add_to_bookmarks2);
                                        }
                                        else if(documentSnapshot == null && !documentSnapshot.exists())
                                        {
                                            bookmarkFacility.setImageResource(R.drawable.add_to_bookmarks1);
                                        }
                                    }
                                });

                                bookmarkFacility.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final String bookmarkName = facilityName;
                                        final String bookmarkHeaderImage = facilityImage;
                                        final String bookmarkAddress = facilityAddress;

                                        DocumentReference bookmarkedFacility = bookmarksRef.document(bookmarkName);
                                        bookmarkedFacility.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            final DocumentSnapshot bookmarkSnapShot = task.getResult();
                                                            final Bookmarks bookmark = new Bookmarks(bookmarkName, bookmarkHeaderImage, bookmarkAddress);
                                                            if(!bookmarkSnapShot.exists())
                                                            {
                                                                bookmarksRef.document(bookmarkName)
                                                                        .set(bookmark)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                bookmarkFacility.setImageResource(R.drawable.add_to_bookmarks2);
                                                                                Alerter.create(FacilityDetailsActivity.this)
                                                                                        .setTitle(bookmarkName + " has been tagged!")
                                                                                        .setTextAppearance(R.style.AddToBookmarkAlert)
                                                                                        .setBackgroundColorRes(R.color.bookmarkColor)
                                                                                        .setIcon(R.drawable.bookmark_icon2)
                                                                                        .setDuration(3000)
                                                                                        .enableSwipeToDismiss()
                                                                                        .enableIconPulse(true)
                                                                                        .enableVibration(true)
                                                                                        .show();
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Alerter.create(FacilityDetailsActivity.this)
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
                                                                bookmarksRef.document(bookmarkName)
                                                                        .delete()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                bookmarkFacility.setImageResource(R.drawable.add_to_bookmarks1);
                                                                                Alerter.create(FacilityDetailsActivity.this)
                                                                                        .setTitle(bookmarkName + " has been untagged!")
                                                                                        .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                                                                                        .setBackgroundColorRes(R.color.colorAccent)
                                                                                        .setIcon(R.drawable.bookmark_icon2)
                                                                                        .setDuration(3000)
                                                                                        .enableSwipeToDismiss()
                                                                                        .enableIconPulse(true)
                                                                                        .enableVibration(true)
                                                                                        .show();
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Alerter.create(FacilityDetailsActivity.this)
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
                                });

                                callFacility.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        makePhoneCall();
                                    }
                                });

                                shareFacility.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(Common.isConnectedToInternet(FacilityDetailsActivity.this))
                                        {
                                            Picasso.get().load(facilityImage).into(target);
                                        }
                                        else
                                        {
                                            Alerter.create(FacilityDetailsActivity.this)
                                                    .setTitle("Please check your connection!")
                                                    .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                                                    .setBackgroundColorRes(R.color.errorColor)
                                                    .setIcon(R.drawable.error_icon)
                                                    .setDuration(3000)
                                                    .enableSwipeToDismiss()
                                                    .enableIconPulse(true)
                                                    .enableVibration(true)
                                                    .show();
                                            return;
                                        }
                                    }
                                });
                            }
                        }
                    });

                    viewInMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(FacilityDetailsActivity.this, MapActivity.class));
                            CustomIntent.customType(FacilityDetailsActivity.this, "bottom_to_up");
                        }
                    });

                    String fb_exclusive = "https://firebasestorage.googleapis.com/v0/b/fitbeetles-210597.appspot.com/o/FBExclusive%2Ffb_exclusive_yellow.jpg?alt=media&token=0c6f172f-3c70-4f4f-89e2-bb1646e5f215";
                    Picasso.get().load(fb_exclusive).into(fbExclusive);
                    payPerSessionInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FBExclusiveBottomSheet fbExclusiveBottomSheet = new FBExclusiveBottomSheet();
                            fbExclusiveBottomSheet.show(getSupportFragmentManager(), "fbExclusiveBottomSheet");
                        }
                    });

                    viewAllOfferings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Common.currentOfferingsAmenities = "Offerings";
                            startActivity(new Intent(FacilityDetailsActivity.this, OfferingsAmenitiesActivity.class));
                            CustomIntent.customType(FacilityDetailsActivity.this, "bottom_to_up");
                        }
                    });

                    viewAllAmenities.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Common.currentOfferingsAmenities = "Amenities";
                            startActivity(new Intent(FacilityDetailsActivity.this, OfferingsAmenitiesActivity.class));
                            CustomIntent.customType(FacilityDetailsActivity.this, "bottom_to_up");
                        }
                    });

                    addReview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RateFacilityBottomSheet rateFacilityBottomSheet = new RateFacilityBottomSheet();
                            rateFacilityBottomSheet.show(getSupportFragmentManager(), "rateFacilityBottomSheet");
                        }
                    });
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void makePhoneCall()
    {
        if(number.length() > 0)
        {
            if(ContextCompat.checkSelfPermission(FacilityDetailsActivity.this,
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
            {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
            else
            {
                Dexter.withActivity(FacilityDetailsActivity.this)
                        .withPermission(Manifest.permission.CALL_PHONE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                makePhoneCall();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if(response.isPermanentlyDenied())
                                {
                                    new AlertDialog.Builder(FacilityDetailsActivity.this)
                                            .setTitle("Permission Denied!")
                                            .setMessage("Permission to access this device's phone and manage calls has been permanently denied. Head over to FitBeetles Settings in device's App's Settings to manually grant the permission.")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                                else
                                {
                                    Snacky.builder()
                                            .setActivity(FacilityDetailsActivity.this)
                                            .setText("Permission Denied!")
                                            .setDuration(Snacky.LENGTH_SHORT)
                                            .error()
                                            .show();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .check();
            }
        }
        else
        {
            Alerter.create(FacilityDetailsActivity.this)
                    .setTitle("Whoa! " + Common.currentFacility + " has no calling number!")
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

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        final GoogleMap mMap = googleMap;
        FirebaseAuth mapAuth;
        FirebaseUser mapUser;
        CollectionReference mapUserRef;
        final DocumentReference mapUserInfo;

        mapAuth = FirebaseAuth.getInstance();
        mapUser = mapAuth.getCurrentUser();
        mapUserRef = FirebaseFirestore.getInstance().collection("Users");

        mapUserInfo = mapUserRef.document(mapUser.getEmail());
        mapUserInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Alerter.create(FacilityDetailsActivity.this)
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
                    String mapCity = documentSnapshot.getData().get("city").toString();

                    DocumentReference mapFacilityDetails = FirebaseFirestore.getInstance().collection("Cities")
                                                           .document(mapCity).collection(Common.currentCategory).document(Common.currentFacility);

                    mapFacilityDetails.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(e != null)
                            {
                                Alerter.create(FacilityDetailsActivity.this)
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
                                String facilityLatitude = documentSnapshot.getData().get("latitude").toString();
                                String facilityLongitude = documentSnapshot.getData().get("longitude").toString();

                                double latitude = Double.parseDouble(facilityLatitude);
                                double longitude = Double.parseDouble(facilityLongitude);

                                LatLng facilityLocation = new LatLng(latitude, longitude);
                                Calendar calendar = Calendar.getInstance();
                                int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

                                if(timeOfDay >= 18 && timeOfDay < 24)
                                {
                                    try{
                                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(FacilityDetailsActivity.this, R.raw.style_map_night));

                                        if(!success)
                                        {
                                            Alerter.create(FacilityDetailsActivity.this)
                                                    .setTitle("Style Parsing Failed!")
                                                    .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                                                    .setBackgroundColorRes(R.color.colorPrimary)
                                                    .setIcon(R.drawable.info_icon)
                                                    .setDuration(3000)
                                                    .enableSwipeToDismiss()
                                                    .enableIconPulse(true)
                                                    .enableVibration(true)
                                                    .show();
                                        }
                                    }catch (Resources.NotFoundException error){
                                        Alerter.create(FacilityDetailsActivity.this)
                                                .setTitle("Can't find style!")
                                                .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                                                .setBackgroundColorRes(R.color.colorAccent)
                                                .setIcon(R.drawable.error_icon)
                                                .setDuration(3000)
                                                .enableSwipeToDismiss()
                                                .enableIconPulse(true)
                                                .enableVibration(true)
                                                .show();
                                    }
                                }
                                else if(timeOfDay >= 0 && timeOfDay < 5)
                                {
                                    try{
                                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(FacilityDetailsActivity.this, R.raw.style_map_night));

                                        if(!success)
                                        {
                                            Alerter.create(FacilityDetailsActivity.this)
                                                    .setTitle("Style Parsing Failed!")
                                                    .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                                                    .setBackgroundColorRes(R.color.colorPrimary)
                                                    .setIcon(R.drawable.info_icon)
                                                    .setDuration(3000)
                                                    .enableSwipeToDismiss()
                                                    .enableIconPulse(true)
                                                    .enableVibration(true)
                                                    .show();
                                        }
                                    }catch (Resources.NotFoundException error){
                                        Alerter.create(FacilityDetailsActivity.this)
                                                .setTitle("Can't find style!")
                                                .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                                                .setBackgroundColorRes(R.color.colorAccent)
                                                .setIcon(R.drawable.error_icon)
                                                .setDuration(3000)
                                                .enableSwipeToDismiss()
                                                .enableIconPulse(true)
                                                .enableVibration(true)
                                                .show();
                                    }
                                }
                                else if(timeOfDay >= 5 && timeOfDay < 18)
                                {
                                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                                }

                                mMap.addMarker(new MarkerOptions().position(facilityLocation).title(Common.currentFacility));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(facilityLocation));
                                mMap.setTrafficEnabled(false);
                                mMap.getUiSettings().setCompassEnabled(true);
                                mMap.getUiSettings().setMapToolbarEnabled(false);
                                mMap.getUiSettings().setZoomControlsEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onAvailableOfferingsLoadSuccess(List<AvailableOfferings> availableOfferings) {
        recyclerOfferings.setHasFixedSize(true);
        recyclerOfferings.setLayoutManager(new GridLayoutManager(FacilityDetailsActivity.this, 4));

        offeringsAdapter = new OfferingsAdapter(FacilityDetailsActivity.this, availableOfferings);
        offeringsAdapter.notifyDataSetChanged();

        recyclerOfferings.setAdapter(offeringsAdapter);
        recyclerOfferings.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onAvailableOfferingsLoadFailed(String message) {
        Alerter.create(FacilityDetailsActivity.this)
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
        recyclerAmenities.setHasFixedSize(true);
        recyclerAmenities.setLayoutManager(new GridLayoutManager(FacilityDetailsActivity.this, 4));

        amenitiesAdapter = new AmenitiesAdapter(FacilityDetailsActivity.this, availableAmenities);
        amenitiesAdapter.notifyDataSetChanged();

        recyclerAmenities.setAdapter(amenitiesAdapter);
        recyclerAmenities.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onAvailableAmenitiesLoadFailed(String message) {
        Alerter.create(FacilityDetailsActivity.this)
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
    public void onFacilityReviewsLoadSuccess(List<FacilitySectionReviews> facilitySectionReviews) {
        recyclerReviews.setHasFixedSize(true);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(FacilityDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));

        facilityReviewsAdapter = new FacilityReviewsAdapter(FacilityDetailsActivity.this, facilitySectionReviews);
        facilityReviewsAdapter.notifyDataSetChanged();

        recyclerReviews.setAdapter(facilityReviewsAdapter);
        recyclerReviews.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFacilityReviewsLoadFailed(String message) {
        Alerter.create(FacilityDetailsActivity.this)
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


    public class OfferingsAdapter extends RecyclerView.Adapter<OfferingsAdapter.OfferingsViewHolder>
    {
        Context context;
        List<AvailableOfferings> mData;

        public OfferingsAdapter(Context context, List<AvailableOfferings> mData) {
            this.context = context;
            this.mData = mData;
        }

        @NonNull
        @Override
        public OfferingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_offerings_amenities_layout, parent, false);
            return new OfferingsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull OfferingsViewHolder holder, int position) {
            Picasso.get().load(mData.get(position).getImage()).into(holder.offeringsImage);
            holder.offeringsName.setText(mData.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class OfferingsViewHolder extends RecyclerView.ViewHolder
        {
            ImageView offeringsImage;
            TextView offeringsName;

            public OfferingsViewHolder(@NonNull View itemView) {
                super(itemView);

                offeringsImage = itemView.findViewById(R.id.service_image);
                offeringsName = itemView.findViewById(R.id.service_name);
            }
        }
    }


    public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.AmenitiesViewHolder>
    {
        Context context;
        List<AvailableAmenities> mData;

        public AmenitiesAdapter(Context context, List<AvailableAmenities> mData) {
            this.context = context;
            this.mData = mData;
        }

        @NonNull
        @Override
        public AmenitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_offerings_amenities_layout, parent, false);
            return new AmenitiesViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AmenitiesViewHolder holder, int position) {
            Picasso.get().load(mData.get(position).getImage()).into(holder.amenitiesImage);
            holder.amenitiesName.setText(mData.get(position).getName());
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class AmenitiesViewHolder extends RecyclerView.ViewHolder
        {
            ImageView amenitiesImage;
            TextView amenitiesName;

            public AmenitiesViewHolder(@NonNull View itemView) {
                super(itemView);

                amenitiesImage = itemView.findViewById(R.id.service_image);
                amenitiesName = itemView.findViewById(R.id.service_name);
            }
        }
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
                    .inflate(R.layout.recycler_facility_details_reviews_layout, parent, false);
            return new FacilityReviewsViewHolder(itemView);
        }

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

    public static void setWindowFlag(FacilityDetailsActivity facilityDetailsActivity, final int bits, boolean on) {
        Window window = facilityDetailsActivity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        if(on){
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(FacilityDetailsActivity.this, "up-to-bottom");
    }
}
