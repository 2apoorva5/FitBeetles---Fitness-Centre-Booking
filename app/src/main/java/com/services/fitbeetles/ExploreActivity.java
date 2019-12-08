package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.services.fitbeetles.Interface.ICategoryLoadListener;
import com.services.fitbeetles.common.Common;
import com.services.fitbeetles.model.Categories;
import com.squareup.picasso.Picasso;
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

public class ExploreActivity extends AppCompatActivity implements ICategoryLoadListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef, categoryRef;

    TextView explore;
    ImageView back, settings, bookmarks, notifications, textToSpeech;
    EditText categorySearch;

    ShimmerFrameLayout shimmerLayout;

    BottomNavigationView bottomNavigationView;

    RecyclerView recyclerExploreCategory;
    CategoryAdapter categoryAdapter;
    ICategoryLoadListener iCategoryLoadListener;
    List<Categories> categoriesList;
    List<Categories> categories;

    private static int LAST_POSITION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

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
            Intent intent1 = new Intent(ExploreActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(ExploreActivity.this, "right-to-left");
            finish();
        }

        back = findViewById(R.id.back_arrow);
        bookmarks = findViewById(R.id.explore_bookmarks);
        notifications = findViewById(R.id.explore_notifications);
        settings = findViewById(R.id.explore_settings);
        explore = findViewById(R.id.explore_in_city);
        categorySearch = findViewById(R.id.category_search);
        textToSpeech = findViewById(R.id.text_to_speech);
        shimmerLayout = findViewById(R.id.shimmer);
        recyclerExploreCategory = findViewById(R.id.recycler_explore_category);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        shimmerLayout.startShimmer();

        DocumentReference userInfo = userRef.document(firebaseUser.getEmail());
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Alerter.create(ExploreActivity.this)
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
                    String city = documentSnapshot.getData().get("city").toString();

                    if(explore.getVisibility() == View.INVISIBLE)
                    {
                        explore.setVisibility(View.VISIBLE);
                        explore.setText(String.format("Find out top-rated facilities in %s with amenities you need.", city));
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

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExploreActivity.this, NotificationsActivity.class));
                CustomIntent.customType(ExploreActivity.this, "left-to-right");
            }
        });

        bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExploreActivity.this, BookmarksActivity.class));
                CustomIntent.customType(ExploreActivity.this, "left-to-right");
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ExploreActivity.this, SettingsActivity.class));
                CustomIntent.customType(ExploreActivity.this, "left-to-right");
            }
        });

        categoriesList = new ArrayList<>();
        iCategoryLoadListener = this;

        categoryRef.get()
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
                            categories = new ArrayList<>();
                            categories.addAll(categoriesList);
                            iCategoryLoadListener.onCategoryLoadSuccess(categories);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iCategoryLoadListener.onCategoryLoadFailed(e.getMessage());
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
                    Alerter.create(ExploreActivity.this)
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

        categorySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                categoryAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        KeyboardVisibilityEvent.setEventListener(ExploreActivity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if(isOpen)
                {
                    UIUtil.showKeyboard(ExploreActivity.this, categorySearch);
                }
                else
                {
                    categorySearch.clearFocus();
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.menu_home :
                        startActivity(new Intent(ExploreActivity.this, HomeActivity.class));
                        CustomIntent.customType(ExploreActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_explore :
                        break;

                    case R.id.menu_shop :
                        startActivity(new Intent(ExploreActivity.this, ShopActivity.class));
                        CustomIntent.customType(ExploreActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_inbox :
                        startActivity(new Intent(ExploreActivity.this, InboxActivity.class));
                        CustomIntent.customType(ExploreActivity.this, "fadein-to-fadeout");
                        break;

                    case R.id.menu_profile :
                        startActivity(new Intent(ExploreActivity.this, ProfileActivity.class));
                        CustomIntent.customType(ExploreActivity.this, "fadein-to-fadeout");
                        break;
                }
                return false;
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
                    categorySearch.setText(result.get(0));
                    categorySearch.clearFocus();
                }
                break;
        }
    }

    @Override
    public void onCategoryLoadSuccess(List<Categories> categories) {
        if ((shimmerLayout.getVisibility() == View.VISIBLE))
        {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
        }

        recyclerExploreCategory.setHasFixedSize(true);
        recyclerExploreCategory.setLayoutManager(new GridLayoutManager(ExploreActivity.this, 2));

        categoryAdapter = new CategoryAdapter(ExploreActivity.this, categories);
        categoryAdapter.notifyDataSetChanged();

        recyclerExploreCategory.setAdapter(categoryAdapter);
        recyclerExploreCategory.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCategoryLoadFailed(String message) {
        Alerter.create(ExploreActivity.this)
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

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> implements Filterable
    {
        Context context;
        List<Categories> mData;
        NewFilter filter;

        public CategoryAdapter(Context context, List<Categories> mData) {
            this.context = context;
            this.mData = mData;
            filter = new NewFilter(CategoryAdapter.this);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_explore_category_layout, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            Picasso.get().load(mData.get(position).getImage()).into(holder.categoryImage);
            holder.categoryName.setText(mData.get(position).getName());

            holder.clickListener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIUtil.hideKeyboard(ExploreActivity.this);
                    Common.currentCategory = mData.get(position).getName();
                    notifyDataSetChanged();

                    startActivity(new Intent(ExploreActivity.this, FacilityListingActivity.class));
                    CustomIntent.customType(ExploreActivity.this, "left-to-right");
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

        public class NewFilter extends Filter
        {
            public CategoryAdapter mAdapter;

            public NewFilter(CategoryAdapter mAdapter){
                super();
                this.mAdapter = mAdapter;
            }

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                categories.clear();
                final FilterResults results = new FilterResults();
                if(charSequence == null || charSequence.length() == 0){
                    categories.addAll(categoriesList);
                }
                else {
                    final String filterPattern = charSequence.toString().toLowerCase().trim();
                    for(Categories filteredCategory : categoriesList)
                    {
                        if(filteredCategory.getName().toLowerCase().trim().contains(filterPattern))
                        {
                            categories.add(filteredCategory);
                        }
                    }
                }
                results.values = categories;
                results.count = categories.size();
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
    public void onBackPressed() {
        startActivity(new Intent(ExploreActivity.this, HomeActivity.class));
        CustomIntent.customType(ExploreActivity.this, "fadein-to-fadeout");
    }
}
