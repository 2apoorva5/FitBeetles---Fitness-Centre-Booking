package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import maes.tech.intentanim.CustomIntent;

public class InboxActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef;

    ImageView back, settings, notifications;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

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
        notifications = findViewById(R.id.inbox_notifications);
        settings = findViewById(R.id.inbox_settings);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(InboxActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(InboxActivity.this, "right-to-left");
            finish();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InboxActivity.this, NotificationsActivity.class));
                CustomIntent.customType(InboxActivity.this, "left-to-right");
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InboxActivity.this, SettingsActivity.class));
                CustomIntent.customType(InboxActivity.this, "left-to-right");
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.menu_home :
                        startActivity(new Intent(InboxActivity.this, HomeActivity.class));
                        CustomIntent.customType(InboxActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_explore :
                        startActivity(new Intent(InboxActivity.this, ExploreActivity.class));
                        CustomIntent.customType(InboxActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_shop :
                        startActivity(new Intent(InboxActivity.this, ShopActivity.class));
                        CustomIntent.customType(InboxActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_inbox :
                        break;

                    case R.id.menu_profile :
                        startActivity(new Intent(InboxActivity.this, ProfileActivity.class));
                        CustomIntent.customType(InboxActivity.this, "fadein-to-fadeout");
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(InboxActivity.this, HomeActivity.class));
        CustomIntent.customType(InboxActivity.this, "fadein-to-fadeout");
    }
}
