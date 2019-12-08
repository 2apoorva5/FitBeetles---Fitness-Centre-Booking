package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.services.fitbeetles.common.Common;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import maes.tech.intentanim.CustomIntent;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef;

    ImageView back, settings;
    TextView userName, userLocation;
    CircleImageView userProfilePic;

    ConstraintLayout viewProfile, payments, notifications, wallet, bookings, memberships,
            bookmarks, bag, favorites, orders, about, termsAndConditions, privacyPolicy, reviews, promoCodes, logOut;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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

        back = findViewById(R.id.back_arrow);
        settings = findViewById(R.id.profile_settings);
        userName = findViewById(R.id.user_name);
        userLocation = findViewById(R.id.user_location);
        userProfilePic = findViewById(R.id.user_profile_pic);
        viewProfile = findViewById(R.id.view_profile);
        payments = findViewById(R.id.payment_settings);
        notifications = findViewById(R.id.notifications);
        wallet = findViewById(R.id.fitbeetles_wallet);
        bookings = findViewById(R.id.bookings);
        memberships = findViewById(R.id.memberships);
        bookmarks = findViewById(R.id.bookmarks);
        bag = findViewById(R.id.shopping_bag);
        favorites = findViewById(R.id.favorites);
        orders = findViewById(R.id.orders);
        about = findViewById(R.id.about);
        termsAndConditions = findViewById(R.id.terms_and_conditions);
        privacyPolicy = findViewById(R.id.privacy_and_policy);
        reviews = findViewById(R.id.reviews);
        promoCodes = findViewById(R.id.promo_codes);
        logOut = findViewById(R.id.log_out);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(ProfileActivity.this, "right-to-left");
            finish();
        }

        DocumentReference userInfo = userRef.document(firebaseUser.getEmail());
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Alerter.create(ProfileActivity.this)
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
                    String location = (city + ", India");

                    if(userName.getVisibility() == View.INVISIBLE)
                    {
                        userName.setVisibility(View.VISIBLE);
                        userName.setText(name);
                    }

                    Uri photoUrl = Uri.parse(image);
                    if(userProfilePic.getVisibility() == View.INVISIBLE)
                    {
                        userProfilePic.setVisibility(View.VISIBLE);
                        Picasso.get().load(photoUrl).into(userProfilePic);
                    }
                    else if(userProfilePic.getVisibility() == View.VISIBLE)
                    {
                        Picasso.get().load(photoUrl).into(userProfilePic);
                    }

                    if(userLocation.getVisibility() == View.INVISIBLE)
                    {
                        userLocation.setVisibility(View.VISIBLE);
                        userLocation.setText(location);
                    }
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
                CustomIntent.customType(ProfileActivity.this, "left-to-right");
            }
        });

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ViewProfileActivity.class));
                CustomIntent.customType(ProfileActivity.this, "bottom-to-up");
            }
        });

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, NotificationsActivity.class));
                CustomIntent.customType(ProfileActivity.this, "left-to-right");
            }
        });

        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, BookmarksActivity.class));
                CustomIntent.customType(ProfileActivity.this, "left-to-right");
            }
        });

        bag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ShoppingBagActivity.class));
                CustomIntent.customType(ProfileActivity.this, "left-to-right");
            }
        });

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ShoppingFavoritesActivity.class));
                CustomIntent.customType(ProfileActivity.this, "left-to-right");
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentWebViewTitle = "About";
                startActivity(new Intent(ProfileActivity.this, WebViewActivity.class));
                CustomIntent.customType(ProfileActivity.this, "bottom-to-up");
            }
        });

        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentWebViewTitle = "Terms & Conditions";
                startActivity(new Intent(ProfileActivity.this, WebViewActivity.class));
                CustomIntent.customType(ProfileActivity.this, "bottom-to-up");
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentWebViewTitle = "Privacy & Policy";
                startActivity(new Intent(ProfileActivity.this, WebViewActivity.class));
                CustomIntent.customType(ProfileActivity.this, "bottom-to-up");
            }
        });

        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, UserReviewsActivity.class));
                CustomIntent.customType(ProfileActivity.this, "left-to-right");
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.isConnectedToInternet(ProfileActivity.this))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                    builder.setMessage("Are you sure you want to Sign Out?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AuthUI.getInstance()
                                    .signOut(ProfileActivity.this)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                            CustomIntent.customType(ProfileActivity.this, "right-to-left");
                                            finish();
                                        }
                                    });
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
                else
                {
                    Alerter.create(ProfileActivity.this)
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
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.menu_home :
                        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                        CustomIntent.customType(ProfileActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_explore :
                        startActivity(new Intent(ProfileActivity.this, ExploreActivity.class));
                        CustomIntent.customType(ProfileActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_shop :
                        startActivity(new Intent(ProfileActivity.this, ShopActivity.class));
                        CustomIntent.customType(ProfileActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_inbox :
                        startActivity(new Intent(ProfileActivity.this, InboxActivity.class));
                        CustomIntent.customType(ProfileActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_profile :
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
        CustomIntent.customType(ProfileActivity.this, "fadein-to-fadeout");
    }
}
