package com.services.fitbeetles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.services.fitbeetles.model.Bookmarks;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import maes.tech.intentanim.CustomIntent;
import pl.droidsonroids.gif.GifImageView;

public class BookmarksActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef, bookmarksRef;

    ImageView back;
    TextView bookmarksCount;
    GifImageView bookmarksGif;

    RecyclerView recyclerBookmarksItem;
    BookmarksAdapter bookmarksAdapter;

    private static int LAST_POSITION = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

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
            Intent intent1 = new Intent(BookmarksActivity.this, LoginActivity.class);
            startActivity(intent1);
            finish();
        }

        back = findViewById(R.id.back_arrow);
        bookmarksCount = findViewById(R.id.bookmarks_count);
        bookmarksGif = findViewById(R.id.bookmarks_gif);
        recyclerBookmarksItem = findViewById(R.id.recycler_bookmarks_item);

        setUpRecyclerViewForBookmarks();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpRecyclerViewForBookmarks()
    {
        Query query = bookmarksRef.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Bookmarks> options = new FirestoreRecyclerOptions.Builder<Bookmarks>()
                .setQuery(query, Bookmarks.class)
                .build();

        bookmarksAdapter = new BookmarksAdapter(options);
        bookmarksAdapter.notifyDataSetChanged();

        recyclerBookmarksItem.setHasFixedSize(true);
        recyclerBookmarksItem.setLayoutManager(new LinearLayoutManager(BookmarksActivity.this));
        recyclerBookmarksItem.setAdapter(bookmarksAdapter);

        recyclerBookmarksItem.getAdapter().notifyDataSetChanged();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                bookmarksAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerBookmarksItem);
    }

    public class BookmarksAdapter extends FirestoreRecyclerAdapter<Bookmarks, BookmarksAdapter.MyViewHolder>
    {

        public BookmarksAdapter(@NonNull FirestoreRecyclerOptions<Bookmarks> options) {
            super(options);
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_bookmark_layout, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull final Bookmarks bookmarks) {
            Picasso.get().load(bookmarks.getHeaderImage()).into(holder.bookmarksImage);
            holder.bookmarksName.setText(bookmarks.getName());
            holder.bookmarksAddress.setText(bookmarks.getAddress());

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


        public void deleteItem(int position)
        {
            String deleteItemName = getSnapshots().getSnapshot(position).get("name").toString();
            getSnapshots().getSnapshot(position).getReference().delete();
            Alerter.create(BookmarksActivity.this)
                    .setTitle(deleteItemName + " has been untagged!")
                    .setTextAppearance(R.style.RemoveFromBookmarkAlert)
                    .setBackgroundColorRes(R.color.colorAccent)
                    .setIcon(R.drawable.bookmark_icon2)
                    .setDuration(3000)
                    .enableSwipeToDismiss()
                    .enableIconPulse(true)
                    .enableVibration(true)
                    .show();
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onDataChanged() {
            super.onDataChanged();

            if(getItemCount() != 0)
            {   bookmarksCount.setText(String.format("You have %d tagged or bookmarked outlets in your list.", getItemCount()));
                if(bookmarksGif.getVisibility() == View.VISIBLE)
                {
                    bookmarksGif.setVisibility(View.GONE);
                }
                if(bookmarksCount.getVisibility() == View.INVISIBLE)
                {
                    bookmarksCount.setVisibility(View.VISIBLE);
                }
            }
            else
            {
                bookmarksCount.setText("Your tagged or bookmarked places or outlets appear here.");
                if(bookmarksGif.getVisibility() == View.GONE)
                {
                    bookmarksGif.setVisibility(View.VISIBLE);
                }
                if(bookmarksCount.getVisibility() == View.INVISIBLE)
                {
                    bookmarksCount.setVisibility(View.VISIBLE);
                }
            }
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {
            ImageView bookmarksImage;
            TextView bookmarksName, bookmarksAddress;
            ConstraintLayout clickListener;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                bookmarksImage = itemView.findViewById(R.id.bookmark_image);
                bookmarksName = itemView.findViewById(R.id.bookmark_name);
                bookmarksAddress = itemView.findViewById(R.id.bookmark_address);
                clickListener = itemView.findViewById(R.id.click_listener);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bookmarksAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bookmarksAdapter.stopListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bookmarksAdapter.stopListening();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bookmarksAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bookmarksAdapter.startListening();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(BookmarksActivity.this, "right-to-left");
    }
}
