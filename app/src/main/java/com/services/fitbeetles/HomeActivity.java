package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;


import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import com.services.fitbeetles.Interface.ICategoryLoadListener;
import com.services.fitbeetles.Interface.IFacilityLoadListener;
import com.services.fitbeetles.bottomSheets.FBExclusiveBottomSheet;
import com.services.fitbeetles.common.Common;
import com.services.fitbeetles.model.Categories;

import com.services.fitbeetles.model.Facilities;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import maes.tech.intentanim.CustomIntent;

public class HomeActivity extends AppCompatActivity implements ICategoryLoadListener, IFacilityLoadListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef, categoryRef, popularGymRef, popularSportsRef;

    ImageView settings, bookmarks, notifications, payPerSessionInfo;
    TextView currentDate, location, editLocation, homeGreetings1, services, viewAllCategory,
             exploreText, popularGyms, popularSports, viewAllGyms, viewAllSports, aboutTerms, privacyPolicy;
    CircleImageView homeProfilePic;

    ShimmerFrameLayout shimmerLayout;

    BottomNavigationView bottomNavigationView;

    RecyclerView recyclerHomeCategory, recyclerPopularGyms, recyclerPopularSports;
    CategoryAdapter categoryAdapter;
    PopularGymsAdapter popularGymsAdapter;
    PopularSportsAdapter popularSportsAdapter;
    ICategoryLoadListener iCategoryLoadListener;
    IFacilityLoadListener iFacilityLoadListener;
    List<Categories> categoriesList;
    List<Facilities> facilitiesList;

    private static int LAST_POSITION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        categoryRef = FirebaseFirestore.getInstance().collection("Categories");

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(HomeActivity.this, "right-to-left");
            finish();
        }

        bookmarks = findViewById(R.id.home_bookmarks);
        notifications = findViewById(R.id.home_notifications);
        settings = findViewById(R.id.home_settings);
        currentDate = findViewById(R.id.current_date);
        editLocation = findViewById(R.id.edit_location);
        location = findViewById(R.id.location);
        homeGreetings1 = findViewById(R.id.home_greetings1);
        homeProfilePic = findViewById(R.id.user_profile_pic);
        services = findViewById(R.id.services_in_city);
        viewAllCategory = findViewById(R.id.view_all_category);
        shimmerLayout = findViewById(R.id.shimmer);
        recyclerHomeCategory = findViewById(R.id.recycler_home_category);
        popularGyms = findViewById(R.id.popular_gyms);
        popularSports = findViewById(R.id.popular_sports);
        viewAllGyms = findViewById(R.id.view_all_popular_gyms);
        viewAllSports = findViewById(R.id.view_all_popular_sports);
        recyclerPopularGyms = findViewById(R.id.recycler_popular_gyms);
        recyclerPopularSports = findViewById(R.id.recycler_popular_sports);
        payPerSessionInfo = findViewById(R.id.pay_per_session_info);
        exploreText = findViewById(R.id.explore_text);
        aboutTerms = findViewById(R.id.about_terms);
        privacyPolicy = findViewById(R.id.privacy_and_policy);


        Calendar calendar = Calendar.getInstance();
        String date = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        currentDate.setText(date);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NotificationsActivity.class));
                CustomIntent.customType(HomeActivity.this, "left-to-right");
            }
        });

        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, BookmarksActivity.class));
                CustomIntent.customType(HomeActivity.this, "left-to-right");
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                CustomIntent.customType(HomeActivity.this, "left-to-right");
            }
        });

        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ChooseCityActivity.class));
                CustomIntent.customType(HomeActivity.this, "left-to-right");
            }
        });

        homeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
            }
        });

        viewAllCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ExploreActivity.class));
                CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
            }
        });


        String clickable = "About Us    Terms and Conditions";
        SpannableString spannableString = new SpannableString(clickable);

        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Common.currentWebViewTitle = "About";
                startActivity(new Intent(HomeActivity.this, WebViewActivity.class));
                CustomIntent.customType(HomeActivity.this, "bottom-to-up");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);

                ds.setColor(Color.parseColor("#ff502f"));
                ds.setUnderlineText(false);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Common.currentWebViewTitle = "Terms & Conditions";
                startActivity(new Intent(HomeActivity.this, WebViewActivity.class));
                CustomIntent.customType(HomeActivity.this, "bottom-to-up");
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);

                ds.setColor(Color.parseColor("#ff502f"));
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan1, 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2, 12, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        aboutTerms.setText(spannableString);
        aboutTerms.setMovementMethod(LinkMovementMethod.getInstance());


        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentWebViewTitle = "Privacy & Policy";
                startActivity(new Intent(HomeActivity.this, WebViewActivity.class));
                CustomIntent.customType(HomeActivity.this, "bottom-to-up");
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.menu_home :
                        break;

                    case R.id.menu_explore :
                        startActivity(new Intent(HomeActivity.this, ExploreActivity.class));
                        CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_shop :
                        startActivity(new Intent(HomeActivity.this, ShopActivity.class));
                        CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_inbox :
                        startActivity(new Intent(HomeActivity.this, InboxActivity.class));
                        CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_profile :
                        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                        CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onCategoryLoadSuccess(List<Categories> categories) {
        if ((shimmerLayout.getVisibility() == View.VISIBLE))
        {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
        }

        recyclerHomeCategory.setHasFixedSize(true);
        recyclerHomeCategory.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));

        categoryAdapter = new CategoryAdapter(HomeActivity.this, categories);
        categoryAdapter.notifyDataSetChanged();

        recyclerHomeCategory.setAdapter(categoryAdapter);
        recyclerHomeCategory.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCategoryLoadFailed(String message) {
        Alerter.create(HomeActivity.this)
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
    public void onFacilityLoadSuccess(List<Facilities> facilities) {
        //POPULAR GYMS
        recyclerPopularGyms.setHasFixedSize(true);
        recyclerPopularGyms.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));

        popularGymsAdapter = new PopularGymsAdapter(HomeActivity.this, facilities);
        popularGymsAdapter.notifyDataSetChanged();

        recyclerPopularGyms.setAdapter(popularGymsAdapter);
        recyclerPopularGyms.getAdapter().notifyDataSetChanged();


        //POPULAR SPORTS
        recyclerPopularSports.setHasFixedSize(true);
        recyclerPopularSports.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));

        popularSportsAdapter = new PopularSportsAdapter(HomeActivity.this, facilities);
        popularSportsAdapter.notifyDataSetChanged();

        recyclerPopularSports.setAdapter(popularSportsAdapter);
        recyclerPopularSports.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFacilityLoadFailed(String message) {
        Alerter.create(HomeActivity.this)
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



    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>
    {
        Context context;
        List<Categories> mData;

        public CategoryAdapter(Context context, List<Categories> mData) {
            this.context = context;
            this.mData = mData;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_home_category_layout, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            Picasso.get().load(mData.get(position).getImage()).into(holder.categoryImage);
            holder.categoryName.setText(mData.get(position).getName());

            holder.clickListener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.currentCategory = mData.get(position).getName();
                    notifyDataSetChanged();

                    startActivity(new Intent(HomeActivity.this, FacilityListingActivity.class));
                    CustomIntent.customType(HomeActivity.this, "left-to-right");
                }
            });

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

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            ConstraintLayout clickListener;
            ImageView categoryImage;
            TextView categoryName;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                clickListener = itemView.findViewById(R.id.click_listener);
                categoryImage = itemView.findViewById(R.id.category_image);
                categoryName = itemView.findViewById(R.id.category_name);
            }
        }
    }



    public class PopularGymsAdapter extends RecyclerView.Adapter<PopularGymsAdapter.GymViewHolder>
    {
        Context context;
        List<Facilities> mData;

        public PopularGymsAdapter(Context context, List<Facilities> mData) {
            this.context = context;
            this.mData = mData;
        }

        @NonNull
        @Override
        public GymViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_home_popular_layout, parent, false);
            return new GymViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull GymViewHolder holder, final int position) {
            Picasso.get().load(mData.get(position).getHeaderImage()).into(holder.gymImage);
            holder.gymName.setText(mData.get(position).getName());
            holder.gymAddress.setText(mData.get(position).getAddress());
            holder.reviews.setText(String.valueOf(mData.get(position).getReviews()));
            holder.openOrClosed.setText(mData.get(position).getOpenOrClosed());

            if(holder.openOrClosed.getText().toString().equals("Open"))
            {
                holder.openOrClosedCard.setCardBackgroundColor(Color.parseColor("#76a21e"));
            }
            else if(holder.openOrClosed.getText().toString().equals("Closed"))
            {
                holder.openOrClosedCard.setCardBackgroundColor(Color.parseColor("#ed3833"));
            }

            if(holder.reviews.getText().toString().equals("0.0"))
            {
                holder.reviews.setVisibility(View.GONE);
            }
            else if(!holder.reviews.getText().toString().equals("0.0"))
            {
                holder.reviews.setVisibility(View.VISIBLE);
            }


            holder.clickListener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.currentCategory = "Gyms";
                    Common.currentFacility = mData.get(position).getName();
                    notifyDataSetChanged();

                    startActivity(new Intent(HomeActivity.this, FacilityDetailsActivity.class));
                    CustomIntent.customType(HomeActivity.this, "bottom-to-up");
                }
            });

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

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class GymViewHolder extends RecyclerView.ViewHolder
        {
            ImageView gymImage;
            TextView gymName, gymAddress, reviews, openOrClosed;

            CardView openOrClosedCard;
            ConstraintLayout clickListener;

            public GymViewHolder(@NonNull View itemView) {
                super(itemView);

                gymName = itemView.findViewById(R.id.popular_gym_name);
                gymImage = itemView.findViewById(R.id.popular_gym_image);
                gymAddress = itemView.findViewById(R.id.popular_gym_address);
                reviews = itemView.findViewById(R.id.popular_gym_reviews);
                openOrClosed = itemView.findViewById(R.id.open_or_closed_text);
                openOrClosedCard = itemView.findViewById(R.id.open_or_closed_card);
                clickListener = itemView.findViewById(R.id.click_listener);
            }
        }
    }



    public class PopularSportsAdapter extends RecyclerView.Adapter<PopularSportsAdapter.SportsViewHolder>
    {
        Context context;
        List<Facilities> mData;

        public PopularSportsAdapter(Context context, List<Facilities> mData) {
            this.context = context;
            this.mData = mData;
        }

        @NonNull
        @Override
        public SportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_home_popular_layout, parent, false);
            return new SportsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SportsViewHolder holder, final int position) {
            Picasso.get().load(mData.get(position).getHeaderImage()).into(holder.sportsImage);
            holder.sportsName.setText(mData.get(position).getName());
            holder.sportsAddress.setText(mData.get(position).getAddress());
            holder.reviews.setText(String.valueOf(mData.get(position).getReviews()));
            holder.openOrClosed.setText(mData.get(position).getOpenOrClosed());

            if(holder.openOrClosed.getText().toString().equals("Open"))
            {
                holder.openOrClosedCard.setCardBackgroundColor(Color.parseColor("#76a21e"));
            }
            else if(holder.openOrClosed.getText().toString().equals("Closed"))
            {
                holder.openOrClosedCard.setCardBackgroundColor(Color.parseColor("#ed3833"));
            }

            if(holder.reviews.getText().toString().equals("0.0"))
            {
                holder.reviews.setVisibility(View.GONE);
            }
            else if(!holder.reviews.getText().toString().equals("0.0"))
            {
                holder.reviews.setVisibility(View.VISIBLE);
            }


            holder.clickListener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Common.currentCategory = "Gyms";
                    Common.currentFacility = mData.get(position).getName();
                    notifyDataSetChanged();

                    startActivity(new Intent(HomeActivity.this, FacilityDetailsActivity.class));
                    CustomIntent.customType(HomeActivity.this, "bottom-to-up");
                }
            });

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

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class SportsViewHolder extends RecyclerView.ViewHolder
        {
            ImageView sportsImage;
            TextView sportsName, sportsAddress, reviews, openOrClosed;

            ConstraintLayout clickListener;
            CardView openOrClosedCard;

            public SportsViewHolder(@NonNull View itemView) {
                super(itemView);

                sportsName = itemView.findViewById(R.id.popular_gym_name);
                sportsImage = itemView.findViewById(R.id.popular_gym_image);
                sportsAddress = itemView.findViewById(R.id.popular_gym_address);
                reviews = itemView.findViewById(R.id.popular_gym_reviews);
                openOrClosed = itemView.findViewById(R.id.open_or_closed_text);
                openOrClosedCard = itemView.findViewById(R.id.open_or_closed_card);
                clickListener = itemView.findViewById(R.id.click_listener);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        shimmerLayout.startShimmer();

        DocumentReference userInfo = userRef.document(firebaseUser.getEmail());
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Alerter.create(HomeActivity.this)
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
                    String name = documentSnapshot.getData().get("name").toString();
                    String image = documentSnapshot.getData().get("image").toString();
                    String city = documentSnapshot.getData().get("city").toString();
                    String address = city + ", India";

                    if(location.getVisibility() == View.INVISIBLE)
                    {
                        location.setVisibility(View.VISIBLE);
                        location.setText(address);
                    }

                    String[] split = name.split(" ", 2);
                    if(homeGreetings1.getVisibility() == View.INVISIBLE)
                    {
                        homeGreetings1.setVisibility(View.VISIBLE);
                        homeGreetings1.setText(String.format("Hey, %s", split[0]));
                    }

                    Uri photoUrl = Uri.parse(image);
                    if(homeProfilePic.getVisibility() == View.INVISIBLE)
                    {
                        homeProfilePic.setVisibility(View.VISIBLE);
                        Picasso.get().load(photoUrl).into(homeProfilePic);
                    }
                    else if(homeProfilePic.getVisibility() == View.VISIBLE)
                    {
                        Picasso.get().load(photoUrl).into(homeProfilePic);
                    }

                    if(services.getVisibility() == View.INVISIBLE)
                    {
                        services.setVisibility(View.VISIBLE);
                        services.setText(String.format("Services in %s", city));
                    }

                    payPerSessionInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FBExclusiveBottomSheet fbExclusiveBottomSheet = new FBExclusiveBottomSheet();
                            fbExclusiveBottomSheet.show(getSupportFragmentManager(), "fbExclusiveBottomSheet");
                        }
                    });

                    exploreText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(HomeActivity.this, ExploreActivity.class));
                            CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
                        }
                    });


                    popularGymRef = FirebaseFirestore.getInstance().collection("Cities").document(city).collection("Gyms");
                    popularSportsRef = FirebaseFirestore.getInstance().collection("Cities").document(city).collection("Sports");

                    facilitiesList = new ArrayList<>();
                    iFacilityLoadListener = HomeActivity.this;

                    popularGymRef.orderBy("reviews", Query.Direction.DESCENDING).limit(4).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        for(QueryDocumentSnapshot gymSnapshot : task.getResult())
                                        {
                                            Facilities gyms = gymSnapshot.toObject(Facilities.class);
                                            facilitiesList.add(gyms);
                                        }
                                        iFacilityLoadListener.onFacilityLoadSuccess(facilitiesList);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    iFacilityLoadListener.onFacilityLoadFailed(e.getMessage());
                                }
                            });

                    popularSportsRef.orderBy("reviews", Query.Direction.DESCENDING).limit(4).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        for(QueryDocumentSnapshot sportSnapshot : task.getResult())
                                        {
                                            Facilities sports = sportSnapshot.toObject(Facilities.class);
                                            facilitiesList.add(sports);
                                        }
                                        iFacilityLoadListener.onFacilityLoadSuccess(facilitiesList);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    iFacilityLoadListener.onFacilityLoadFailed(e.getMessage());
                                }
                            });


                    viewAllGyms.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Common.currentCategory = "Gyms";

                            startActivity(new Intent(HomeActivity.this, FacilityListingActivity.class));
                            CustomIntent.customType(HomeActivity.this, "left-to-right");
                        }
                    });


                    viewAllSports.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Common.currentCategory = "Sports";

                            startActivity(new Intent(HomeActivity.this, FacilityListingActivity.class));
                            CustomIntent.customType(HomeActivity.this, "left-to-right");
                        }
                    });
                }
            }
        });


        categoriesList = new ArrayList<>();
        iCategoryLoadListener = HomeActivity.this;

        categoryRef.limit(6).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot categorySnapshot : task.getResult())
                            {
                                Categories category = categorySnapshot.toObject(Categories.class);
                                categoriesList.add(category);
                            }
                            iCategoryLoadListener.onCategoryLoadSuccess(categoriesList);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iCategoryLoadListener.onCategoryLoadFailed(e.getMessage());
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerLayout.stopShimmer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        shimmerLayout.stopShimmer();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        shimmerLayout.startShimmer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerLayout.startShimmer();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Are you sure you want to Exit?");
        builder.setCancelable(false);
        builder.setPositiveButton("YES!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
