package com.services.fitbeetles;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.services.fitbeetles.common.Common;

import maes.tech.intentanim.CustomIntent;

public class WebViewActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    ImageView back;
    TextView webViewTitle;
    ProgressBar progressBar;
    WebView webView;

    String title;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(WebViewActivity.this, LoginActivity.class);
            startActivity(intent1);
            finish();
        }

        back = findViewById(R.id.back_arrow);
        webViewTitle = findViewById(R.id.web_view_title);
        progressBar = findViewById(R.id.progress_bar);
        webView = findViewById(R.id.web_view);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if(newProgress == 100)
                {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        title = Common.currentWebViewTitle;

        webViewTitle.setText(title);

        if(title.equals("About"))
        {
            webView.loadUrl("https://www.zomato.com/about");
        }
        else if(title.equals("Terms & Conditions"))
        {
            webView.loadUrl("https://www.zomato.com/conditions");
        }
        else if(title.equals("Privacy & Policy"))
        {
            webView.loadUrl("https://www.zomato.com/privacy");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(WebViewActivity.this, "up-to-bottom");
    }
}
