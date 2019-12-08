package com.services.fitbeetles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.services.fitbeetles.Adapter.WelcomeSliderAdapter;

import maes.tech.intentanim.CustomIntent;

public class WelcomeActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    ViewPager welcomeViewPager;
    LinearLayout dotsLayout;
    TextView next;

    int currentPage;
    TextView[] dots;

    WelcomeSliderAdapter welcomeSliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        welcomeViewPager = findViewById(R.id.welcome_view_pager);
        dotsLayout = findViewById(R.id.dots_layout);
        next = findViewById(R.id.next_signin);

        welcomeSliderAdapter = new WelcomeSliderAdapter(this);

        welcomeViewPager.setAdapter(welcomeSliderAdapter);

        addDotsIndicator(0);

        welcomeViewPager.addOnPageChangeListener(pageChangeListener);

        if(firebaseUser == null){
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(welcomeViewPager.getCurrentItem() == dots.length - 1)
                    {
                        Intent intent1 = new Intent(WelcomeActivity.this, LoginActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent1);
                        CustomIntent.customType(WelcomeActivity.this, "left-to-right");
                        finish();
                    }
                    else
                    {
                        welcomeViewPager.setCurrentItem(currentPage + 1);
                    }
                }
            });
        }
        else
        {
            Intent intent2 = new Intent(WelcomeActivity.this, ChooseCityActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent2);
            CustomIntent.customType(WelcomeActivity.this, "left-to-right");
            finish();
        }
    }

    public void addDotsIndicator(int position)
    {
        dots = new TextView[4];
        dotsLayout.removeAllViews();

        for(int i = 0; i < dots.length; i++)
        {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226; "));
            dots[i].setTextSize(40);
            dots[i].setTextColor(getResources().getColor(R.color.dotsIndicator));

            dotsLayout.addView(dots[i]);
        }

        if(dots.length > 0)
        {
            dots[position].setText(Html.fromHtml("&#8226; "));
            dots[position].setTextSize(40);
            dots[position].setTextColor(getResources().getColor(R.color.textColorOnDarkPrimary));
        }
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage = position;

            if(position == 0)
            {
                next.setEnabled(true);
                next.setText("Next");
            }
            else if(position == dots.length - 1)
            {
                next.setEnabled(true);
                next.setText("Sign in to Continue");
            }
            else
            {
                next.setEnabled(true);
                next.setText("Next");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
