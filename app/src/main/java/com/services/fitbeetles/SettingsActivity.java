package com.services.fitbeetles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.services.fitbeetles.common.Common;

import maes.tech.intentanim.CustomIntent;

public class SettingsActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef;

    ImageView back;

    ConstraintLayout about, termsAndConditions, privacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
        about = findViewById(R.id.about);
        termsAndConditions = findViewById(R.id.terms_and_conditions);
        privacyPolicy = findViewById(R.id.privacy_and_policy);

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(SettingsActivity.this, "right-to-left");
            finish();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentWebViewTitle = "About";
                startActivity(new Intent(SettingsActivity.this, WebViewActivity.class));
                CustomIntent.customType(SettingsActivity.this, "bottom-to-up");
            }
        });

        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentWebViewTitle = "Terms & Conditions";
                startActivity(new Intent(SettingsActivity.this, WebViewActivity.class));
                CustomIntent.customType(SettingsActivity.this, "bottom-to-up");
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.currentWebViewTitle = "Privacy & Policy";
                startActivity(new Intent(SettingsActivity.this, WebViewActivity.class));
                CustomIntent.customType(SettingsActivity.this, "bottom-to-up");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(SettingsActivity.this, "right-to-left");
    }
}
