package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import maes.tech.intentanim.CustomIntent;

public class ShopActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef;

    ImageView back, settings, favorites, cart;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

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
        favorites = findViewById(R.id.shop_favorites);
        cart = findViewById(R.id.shop_cart);
        settings = findViewById(R.id.shop_settings);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(ShopActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(ShopActivity.this, "right-to-left");
            finish();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopActivity.this, ShoppingFavoritesActivity.class));
                CustomIntent.customType(ShopActivity.this, "left-to-right");
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopActivity.this, ShoppingBagActivity.class));
                CustomIntent.customType(ShopActivity.this, "left-to-right");
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShopActivity.this, SettingsActivity.class));
                CustomIntent.customType(ShopActivity.this, "left-to-right");
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.menu_home :
                        startActivity(new Intent(ShopActivity.this, HomeActivity.class));
                        CustomIntent.customType(ShopActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_explore :
                        startActivity(new Intent(ShopActivity.this, ExploreActivity.class));
                        CustomIntent.customType(ShopActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_shop :
                        break;

                    case R.id.menu_inbox :
                        startActivity(new Intent(ShopActivity.this, InboxActivity.class));
                        CustomIntent.customType(ShopActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_profile :
                        startActivity(new Intent(ShopActivity.this, ProfileActivity.class));
                        CustomIntent.customType(ShopActivity.this, "fadein-to-fadeout");
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ShopActivity.this, HomeActivity.class));
        CustomIntent.customType(ShopActivity.this, "fadein-to-fadeout");
    }
}
