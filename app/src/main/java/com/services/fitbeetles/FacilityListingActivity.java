package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.services.fitbeetles.Interface.IFacilityLoadListener;
import com.services.fitbeetles.common.Common;
import com.services.fitbeetles.model.Facilities;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tapadoo.alerter.Alerter;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import de.mateware.snacky.Snacky;
import maes.tech.intentanim.CustomIntent;

public class FacilityListingActivity extends AppCompatActivity implements IFacilityLoadListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef, facilityRef, bookmarksRef;

    ImageView back, settings, notifications, textToSpeech;

    TextView categoryName, categoryInCity;
    EditText facilitySearch;

    ShimmerFrameLayout shimmerLayout;

    RecyclerView recyclerCategoryItem;
    FacilityAdapter facilityAdapter;
    IFacilityLoadListener iFacilityLoadListener;
    List<Facilities> facilitiesList;
    List<Facilities> facilities;

    String city;
    private static int LAST_POSITION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_listing);

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
        bookmarksRef = FirebaseFirestore.getInstance().collection("Users").document(firebaseUser.getEmail()).collection("Bookmarks");

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(FacilityListingActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(FacilityListingActivity.this, "right-to-left");
            finish();
        }

        back = findViewById(R.id.back_arrow);
        settings = findViewById(R.id.listing_settings);
        notifications = findViewById(R.id.listing_notifications);
        categoryName = findViewById(R.id.category_name);
        categoryInCity = findViewById(R.id.category_in_city);
        facilitySearch = findViewById(R.id.facility_search);
        textToSpeech = findViewById(R.id.text_to_speech);
        shimmerLayout = findViewById(R.id.shimmer);
        recyclerCategoryItem = findViewById(R.id.recycler_item);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FacilityListingActivity.this, NotificationsActivity.class));
                CustomIntent.customType(FacilityListingActivity.this, "left-to-right");
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FacilityListingActivity.this, SettingsActivity.class));
                CustomIntent.customType(FacilityListingActivity.this, "left-to-right");
            }
        });


        textToSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] split = firebaseUser.getDisplayName().split(" ", 2);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, String.format("Hi, %s! Speak up something!", split[0]));

                try {
                    startActivityForResult(intent, 123);
                }catch (ActivityNotFoundException e) {
                    Alerter.create(FacilityListingActivity.this)
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

        facilitySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                facilityAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        KeyboardVisibilityEvent.setEventListener(FacilityListingActivity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if(isOpen)
                {
                    UIUtil.showKeyboard(FacilityListingActivity.this, facilitySearch);
                }
                else
                {
                    facilitySearch.clearFocus();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case 123 :
                if(resultCode == RESULT_OK && data != null)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    facilitySearch.setText(result.get(0));
                    facilitySearch.clearFocus();
                }
                break;
        }
    }

    @Override
    public void onFacilityLoadSuccess(List<Facilities> facilities) {
        if ((shimmerLayout.getVisibility() == View.VISIBLE))
        {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
        }

        recyclerCategoryItem.setHasFixedSize(true);
        recyclerCategoryItem.setLayoutManager(new LinearLayoutManager(FacilityListingActivity.this));

        facilityAdapter = new FacilityAdapter(FacilityListingActivity.this, facilities);
        facilityAdapter.notifyDataSetChanged();

        recyclerCategoryItem.setAdapter(facilityAdapter);
        recyclerCategoryItem.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onFacilityLoadFailed(String message) {
        Alerter.create(FacilityListingActivity.this)
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

    public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.MyViewHolder> implements Filterable
    {
        Context context;
        List<Facilities> mData;
        NewFilter filter;

        public FacilityAdapter(Context context, List<Facilities> mData) {
            this.context = context;
            this.mData = mData;
            filter = new NewFilter(FacilityAdapter.this);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_facility_layout, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            Picasso.get().load(mData.get(position).getHeaderImage()).into(holder.facilityImage);
            holder.facilityName.setText(mData.get(position).getName());
            holder.facilityAddress.setText(mData.get(position).getAddress());
            holder.facilityReviews.setText(String.valueOf(mData.get(position).getReviews()));
            holder.facilityOpenClosed.setText(mData.get(position).getOpenOrClosed());

            if(holder.facilityOpenClosed.getText().toString().equals("Open"))
            {
                holder.openClosed.setCardBackgroundColor(Color.parseColor("#76a21e"));
            }
            else if(holder.facilityOpenClosed.getText().toString().equals("Closed"))
            {
                holder.openClosed.setCardBackgroundColor(Color.parseColor("#ed3833"));
            }

            if(holder.facilityReviews.getText().toString().equals("0.0"))
            {
                holder.facilityReviews.setVisibility(View.GONE);
            }
            else if(!holder.facilityReviews.getText().toString().equals("0.0"))
            {
                holder.facilityReviews.setVisibility(View.VISIBLE);
            }

            holder.seeWhatsInside.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.currentFacility = mData.get(position).getName();
                    notifyDataSetChanged();

                    startActivity(new Intent(FacilityListingActivity.this, FacilityDetailsActivity.class));
                    CustomIntent.customType(FacilityListingActivity.this, "bottom-to-up");
                }
            });

            holder.bookNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(FacilityListingActivity.this, MembershipPriceActivity.class));
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

        @Override
        public Filter getFilter() {
            return filter;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            TextView facilityReviews, facilityName, facilityAddress, facilityOpenClosed;
            ImageView facilityImage;

            CardView openClosed;
            ConstraintLayout seeWhatsInside, bookNow;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                facilityReviews = itemView.findViewById(R.id.facility_reviews);
                facilityName = itemView.findViewById(R.id.facility_name);
                facilityAddress = itemView.findViewById(R.id.facility_address);
                facilityOpenClosed = itemView.findViewById(R.id.open_or_closed_text);

                facilityImage = itemView.findViewById(R.id.facility_image);

                openClosed = itemView.findViewById(R.id.open_or_closed_card);

                seeWhatsInside = itemView.findViewById(R.id.see_whats_inside);
                bookNow = itemView.findViewById(R.id.book_now);
            }
        }

        public class NewFilter extends Filter
        {
            public FacilityAdapter mAdapter;

            public NewFilter(FacilityAdapter mAdapter){
                super();
                this.mAdapter = mAdapter;
            }

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                facilities.clear();
                final FilterResults results = new FilterResults();
                if(charSequence == null || charSequence.length() == 0){
                    facilities.addAll(facilitiesList);
                }
                else {
                    final String filterPattern = charSequence.toString().toLowerCase().trim();
                    for(Facilities filteredFacility : facilitiesList)
                    {
                        if(filteredFacility.getName().toLowerCase().trim().contains(filterPattern))
                        {
                            facilities.add(filteredFacility);
                        }
                    }
                }
                results.values = facilities;
                results.count = facilities.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                this.mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        facilitiesList = new ArrayList<>();
        iFacilityLoadListener = FacilityListingActivity.this;

        DocumentReference userInfo = userRef.document(firebaseUser.getEmail());
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Alerter.create(FacilityListingActivity.this)
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

                    facilityRef = FirebaseFirestore.getInstance().collection("Cities").document(city).collection(Common.currentCategory);

                    categoryName.setText(String.format("Explore %s", Common.currentCategory));

                    facilitySearch.setHint(String.format("Search %s", Common.currentCategory));

                    facilityRef.orderBy("name", Query.Direction.ASCENDING).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        int count = 0;
                                        for(QueryDocumentSnapshot facilitySnapshot : task.getResult())
                                        {
                                            count++;
                                            Facilities facility = facilitySnapshot.toObject(Facilities.class);
                                            facilitiesList.add(facility);
                                        }

                                        if(count != 0)
                                        {
                                            if(categoryInCity.getVisibility() == View.INVISIBLE)
                                            {
                                                categoryInCity.setVisibility(View.VISIBLE);
                                                categoryInCity.setText(String.format("Showing %d best-rated outlets for %s in %s.", count, Common.currentCategory, city));
                                            }
                                        }
                                        else
                                        {
                                            if(categoryInCity.getVisibility() == View.INVISIBLE)
                                            {
                                                categoryInCity.setVisibility(View.VISIBLE);
                                                categoryInCity.setText(String.format("Whoops! There are no outlets for %s in %s.", Common.currentCategory, city));
                                            }
                                        }
                                        facilities = new ArrayList<>();
                                        facilities.addAll(facilitiesList);
                                        iFacilityLoadListener.onFacilityLoadSuccess(facilities);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    iFacilityLoadListener.onFacilityLoadFailed(e.getMessage());
                                }
                            });
                }
            }
        });

        shimmerLayout.startShimmer();
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
    protected void onStop() {
        super.onStop();
        shimmerLayout.stopShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerLayout.stopShimmer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(FacilityListingActivity.this, "right-to-left");
    }
}
