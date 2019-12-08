package com.services.fitbeetles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.services.fitbeetles.common.Common;
import com.tapadoo.alerter.Alerter;

import java.util.Calendar;
import java.util.Map;

import de.mateware.snacky.Snacky;
import maes.tech.intentanim.CustomIntent;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef;
    DocumentReference userInfo, facilityDetails;

    ImageView close;
    TextView facilityName, facilityAddress;
    ConstraintLayout openDetails;

    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.WHITE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userRef = FirebaseFirestore.getInstance().collection("Users");

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(MapActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(MapActivity.this, "right-to-left");
            finish();
        }

        close = findViewById(R.id.close);
        facilityName = findViewById(R.id.facility_name);
        facilityAddress = findViewById(R.id.facility_address);
        openDetails = findViewById(R.id.open_details);

        userInfo = userRef.document(firebaseUser.getEmail());
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Alerter.create(MapActivity.this)
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

                    facilityDetails.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(e != null)
                            {
                                Alerter.create(MapActivity.this)
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
                                String address = documentSnapshot.getData().get("address").toString();

                                facilityName.setText(name);
                                facilityAddress.setText(address);
                            }
                        }
                    });
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        openDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapActivity.this, FacilityDetailsActivity.class));
                CustomIntent.customType(MapActivity.this, "bottom_to_up");
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
                    Alerter.create(MapActivity.this)
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
                                Alerter.create(MapActivity.this)
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
                                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapActivity.this, R.raw.style_map_night));

                                        if(!success)
                                        {
                                            Alerter.create(MapActivity.this)
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
                                        Alerter.create(MapActivity.this)
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
                                        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapActivity.this, R.raw.style_map_night));

                                        if(!success)
                                        {
                                            Alerter.create(MapActivity.this)
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
                                        Alerter.create(MapActivity.this)
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
                                mMap.setTrafficEnabled(true);
                                mMap.setOnPoiClickListener(MapActivity.this);
                                mMap.getUiSettings().setCompassEnabled(true);
                                mMap.getUiSettings().setMapToolbarEnabled(true);
                                mMap.getUiSettings().setZoomControlsEnabled(true);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest)
    {
        Snacky.builder()
                .setActivity(MapActivity.this)
                .setText(pointOfInterest.name.trim())
                .setDuration(Snacky.LENGTH_SHORT)
                .info()
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(MapActivity.this, "up-to-bottom");
    }
}
