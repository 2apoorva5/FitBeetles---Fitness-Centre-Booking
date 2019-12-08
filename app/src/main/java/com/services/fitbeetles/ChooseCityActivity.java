package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.services.fitbeetles.Interface.ICityLoadListener;
import com.services.fitbeetles.common.Common;
import com.services.fitbeetles.model.Cities;
import com.services.fitbeetles.model.User;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.mateware.snacky.Snacky;
import maes.tech.intentanim.CustomIntent;
import pl.droidsonroids.gif.GifImageView;

public class ChooseCityActivity extends AppCompatActivity implements ICityLoadListener {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    TextView locationGreetings;
    GifImageView cityGif;

    ShimmerFrameLayout shimmerLayout;

    EditText citySearch;
    ImageView textToSpeech;

    RecyclerView recyclerCity;
    CollectionReference userRef, cityRef;
    CityAdapter cityAdapter;
    ICityLoadListener iCityLoadListener;
    List<Cities> citiesList;
    List<Cities> cities;

    private static int LAST_POSITION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);

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
        cityRef = FirebaseFirestore.getInstance().collection("Cities");

        locationGreetings = findViewById(R.id.location_greetings);
        cityGif = findViewById(R.id.city_gif);
        shimmerLayout = findViewById(R.id.shimmer);
        recyclerCity = findViewById(R.id.recycler_city);
        citySearch = findViewById(R.id.city_search);
        textToSpeech = findViewById(R.id.text_to_speech);

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(ChooseCityActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(ChooseCityActivity.this, "right-to-left");
            finish();
        }

        String name = firebaseUser.getDisplayName();
        String[] split = name.split(" ", 2);
        locationGreetings.setText(String.format("Hi, %s", split[0]));

        shimmerLayout.startShimmer();

        citiesList = new ArrayList<>();
        iCityLoadListener = this;

        cityRef.orderBy("name", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot citySnapshot : task.getResult())
                            {
                                Cities city = citySnapshot.toObject(Cities.class);
                                citiesList.add(city);
                            }
                            cities = new ArrayList<>();
                            cities.addAll(citiesList);
                            iCityLoadListener.onCityLoadSuccess(cities);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iCityLoadListener.onCityLoadFailed(e.getMessage());
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
                    Alerter.create(ChooseCityActivity.this)
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

        citySearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cityGif.setForeground(new ColorDrawable(Color.parseColor("#80004d61")));
                    }
                }
                else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        cityGif.setForeground(new ColorDrawable(Color.parseColor("#00000000")));
                    }
                }
            }
        });

        citySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                cityAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        KeyboardVisibilityEvent.setEventListener(ChooseCityActivity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if(isOpen)
                {
                    UIUtil.showKeyboard(ChooseCityActivity.this, citySearch);
                }
                else
                {
                    citySearch.clearFocus();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case 123 :
                if(resultCode == RESULT_OK && data != null)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    citySearch.setText(result.get(0));
                    citySearch.clearFocus();
                }
                break;
        }
    }

    @Override
    public void onCityLoadSuccess(List<Cities> cities) {
        if ((shimmerLayout.getVisibility() == View.VISIBLE))
        {
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
        }

        recyclerCity.setHasFixedSize(true);
        recyclerCity.setLayoutManager(new GridLayoutManager(ChooseCityActivity.this, 2));

        cityAdapter = new CityAdapter(ChooseCityActivity.this, cities);
        cityAdapter.notifyDataSetChanged();

        recyclerCity.setAdapter(cityAdapter);
        recyclerCity.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onCityLoadFailed(String message) {
        Alerter.create(ChooseCityActivity.this)
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

    public class CityAdapter extends RecyclerView.Adapter<CityAdapter.MyViewHolder> implements Filterable
    {
        Context context;
        List<Cities> mData;
        NewFilter filter;

        public CityAdapter(Context context, List<Cities> data) {
            this.context = context;
            this.mData = data;
            filter = new NewFilter(CityAdapter.this);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_city_layout, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            Picasso.get().load(mData.get(position).getImage()).into(holder.cityImage);
            holder.cityName.setText(mData.get(position).getName());

            holder.clickListener.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIUtil.hideKeyboard(ChooseCityActivity.this);
                    final ProgressDialog progressDialog = new ProgressDialog(ChooseCityActivity.this);
                    progressDialog.setMessage("Almost there...");
                    progressDialog.show();
                    Common.currentCity = mData.get(position).getName();
                    notifyDataSetChanged();

                    final String name = firebaseUser.getDisplayName();
                    Uri photoUrl = firebaseUser.getPhotoUrl();
                    final String image = photoUrl.toString();
                    final String email = firebaseUser.getEmail();
                    final String city = Common.currentCity;
                    final String mobile = "";
                    final String about = "";
                    final String gender = "";
                    final String dob = "";
                    final String work = "";
                    final String language = "";

                    DocumentReference currentUser = userRef.document(email);
                    currentUser.get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        final DocumentSnapshot userSnapShot = task.getResult();
                                        final User user = new User(name, image, email, city, mobile, about, gender, dob, work, language);
                                        if(!userSnapShot.exists())
                                        {
                                            userRef.document(email)
                                                    .set(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            progressDialog.dismiss();
                                                            Intent intent1 = new Intent(ChooseCityActivity.this, HomeActivity.class);
                                                            startActivity(intent1);
                                                            CustomIntent.customType(ChooseCityActivity.this, "left-to-right");
                                                            finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Alerter.create(ChooseCityActivity.this)
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
                                            });
                                        }
                                        else
                                        {
                                            userRef.document(email)
                                                    .update("city", city)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            progressDialog.dismiss();
                                                            Intent intent2 = new Intent(ChooseCityActivity.this, HomeActivity.class);
                                                            startActivity(intent2);
                                                            CustomIntent.customType(ChooseCityActivity.this, "left-to-right");
                                                            finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Alerter.create(ChooseCityActivity.this)
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
                                            });
                                        }
                                    }
                                }
                            });
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
            ImageView cityImage;
            TextView cityName;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                clickListener = itemView.findViewById(R.id.click_listener);
                cityImage = itemView.findViewById(R.id.city_image);
                cityName = itemView.findViewById(R.id.city_name);
            }
        }

        public class NewFilter extends Filter
        {
            public CityAdapter mAdapter;
            public NewFilter(CityAdapter mAdapter){
                super();
                this.mAdapter = mAdapter;
            }

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                cities.clear();
                final FilterResults results = new FilterResults();
                if(charSequence == null || charSequence.length() == 0){
                    cities.addAll(citiesList);
                }
                else {
                    final String filterPattern = charSequence.toString().toLowerCase().trim();
                    for(Cities filteredCity : citiesList)
                    {
                        if(filteredCity.getName().toLowerCase().trim().contains(filterPattern))
                        {
                            cities.add(filteredCity);
                        }
                    }
                }
                results.values = cities;
                results.count = cities.size();
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
        shimmerLayout.stopShimmer();
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
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(ChooseCityActivity.this, "right-to-left");
    }
}
