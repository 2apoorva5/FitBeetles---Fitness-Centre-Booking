package com.services.fitbeetles;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.Calendar;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;
import de.mateware.snacky.Snacky;
import maes.tech.intentanim.CustomIntent;

public class ViewProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CollectionReference userRef;
    FirebaseStorage storage;
    StorageReference storageReference;

    ConstraintLayout constraintLayout;
    ImageView back;
    CircleImageView userProfilePic;
    EditText userMobile, userAbout, userWork;
    TextView saveProfile, editImage, userName, userLocation, userEmail, userGender, userDOB, userLanguage;

    DatePickerDialog.OnDateSetListener setListener;

    //for Dialog
    TextView male, female, other;
    TextView english, hindi, deutsch, espanol, francais, italiano, german, tamil, marathi, bengali, punjabi;

    private static final int PICK_GALLERY_IMAGE_REQUEST = 123;
    private Uri galleryImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

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
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("UserProfilePics/");

        constraintLayout = findViewById(R.id.constraintLayout);
        back = findViewById(R.id.back_arrow);
        saveProfile = findViewById(R.id.save_profile);
        userProfilePic = findViewById(R.id.user_profile_pic);
        editImage = findViewById(R.id.edit_image);
        userName = findViewById(R.id.user_name);
        userLocation = findViewById(R.id.user_location);
        userEmail = findViewById(R.id.user_email);
        userMobile = findViewById(R.id.user_mobile);
        userAbout = findViewById(R.id.user_about);
        userGender = findViewById(R.id.user_gender);
        userDOB = findViewById(R.id.user_dob);
        userWork = findViewById(R.id.user_work);
        userLanguage = findViewById(R.id.user_language);

        if(firebaseUser == null)
        {
            Intent intent1 = new Intent(ViewProfileActivity.this, LoginActivity.class);
            startActivity(intent1);
            CustomIntent.customType(ViewProfileActivity.this, "right-to-left");
            finish();
        }

        DocumentReference userInfo = userRef.document(firebaseUser.getEmail());
        userInfo.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null)
                {
                    Alerter.create(ViewProfileActivity.this)
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
                    String image = documentSnapshot.getData().get("image").toString();
                    String name = documentSnapshot.getData().get("name").toString();
                    String city = documentSnapshot.getData().get("city").toString();
                    String email = documentSnapshot.getData().get("email").toString();
                    String mobile = documentSnapshot.getData().get("mobile").toString();
                    String about = documentSnapshot.getData().get("about").toString();
                    String gender = documentSnapshot.getData().get("gender").toString();
                    String dob = documentSnapshot.getData().get("dob").toString();
                    String work = documentSnapshot.getData().get("work").toString();
                    String language = documentSnapshot.getData().get("language").toString();
                    String location = (city + ", India");

                    if(userName.getVisibility() == View.INVISIBLE)
                    {
                        userName.setVisibility(View.VISIBLE);
                        userName.setText(name);
                    }

                    Uri photoUrl = Uri.parse(image);
                    if(userProfilePic.getVisibility() == View.INVISIBLE)
                    {
                        userProfilePic.setVisibility(View.VISIBLE);
                        Picasso.get().load(photoUrl).into(userProfilePic);
                    }

                    if(userLocation.getVisibility() == View.INVISIBLE)
                    {
                        userLocation.setVisibility(View.VISIBLE);
                        userLocation.setText(location);
                    }

                    if(userEmail.getVisibility() == View.INVISIBLE)
                    {
                        userEmail.setVisibility(View.VISIBLE);
                        userEmail.setText(email);
                    }

                    userMobile.setText(mobile);
                    userAbout.setText(about);
                    userGender.setText(gender);
                    userDOB.setText(dob);
                    userWork.setText(work);
                    userLanguage.setText(language);
                }
            }
        });

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        KeyboardVisibilityEvent.setEventListener(ViewProfileActivity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if(!isOpen)
                {
                    userMobile.clearFocus();
                    userAbout.clearFocus();
                    userWork.clearFocus();
                }
            }
        });


        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(ContextCompat.checkSelfPermission(ViewProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    openImageChooser();
                }
                else
                {
                    Dexter.withActivity(ViewProfileActivity.this)
                            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    openImageChooser();
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                    if(response.isPermanentlyDenied())
                                    {
                                        new AlertDialog.Builder(ViewProfileActivity.this)
                                                .setTitle("Permission Denied!")
                                                .setMessage("Permission to access this device's photos, media and files has been permanently denied. Head over to FitBeetles Settings in device's App's Settings to manually grant the permission.")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        Intent intent = new Intent();
                                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    }
                                    else
                                    {
                                        Snacky.builder()
                                                .setActivity(ViewProfileActivity.this)
                                                .setText("Permission Denied!")
                                                .setDuration(Snacky.LENGTH_SHORT)
                                                .error()
                                                .show();
                                    }
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, final PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            })
                            .check();
                }
            }
        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtil.hideKeyboard(ViewProfileActivity.this);
                saveProfileToFirebaseFirestore();
            }
        });

        userGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseGenderDialog();
            }
        });

        userDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(ViewProfileActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                userDOB.setText(date);
            }
        };

        userLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseLanguageDialog();
            }
        });
    }

    private void openImageChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_GALLERY_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            galleryImageUri = data.getData();
            Picasso.get().load(galleryImageUri).into(userProfilePic);

            final ProgressDialog progressDialog = new ProgressDialog(ViewProfileActivity.this);
            progressDialog.setMessage("Updating Profile Picture...");
            progressDialog.show();

            if(galleryImageUri != null)
            {
                final StorageReference fileRef = storageReference.child(firebaseUser.getUid() + "." + getFileExtension(galleryImageUri));

                fileRef.putFile(galleryImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String img = uri.toString();
                                        final String mail = userEmail.getText().toString().trim();

                                        DocumentReference currentUser = userRef.document(mail);
                                        currentUser.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            userRef.document(mail)
                                                                    .update("image", img)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            progressDialog.dismiss();
                                                                            Alerter.create(ViewProfileActivity.this)
                                                                                    .setTitle("Profile Picture Updated!")
                                                                                    .setTextAppearance(R.style.AddToBookmarkAlert)
                                                                                    .setBackgroundColorRes(R.color.bookmarkColor)
                                                                                    .setIcon(R.drawable.save_profile_icon)
                                                                                    .setDuration(3000)
                                                                                    .enableSwipeToDismiss()
                                                                                    .enableIconPulse(true)
                                                                                    .enableVibration(true)
                                                                                    .show();
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Alerter.create(ViewProfileActivity.this)
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
                                                });
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Alerter.create(ViewProfileActivity.this)
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
                Toast.makeText(ViewProfileActivity.this, "No Image Selected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showChooseGenderDialog()
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(ViewProfileActivity.this).create();

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.choose_gender_dialog, null);

        male = view.findViewById(R.id.male);
        female = view.findViewById(R.id.female);
        other = view.findViewById(R.id.other);

        alertDialog.setView(view);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userGender.setText(male.getText().toString().trim());
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userGender.setText(female.getText().toString().trim());
            }
        });

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userGender.setText(other.getText().toString().trim());
            }
        });

        alertDialog.show();
    }

    private void showChooseLanguageDialog()
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(ViewProfileActivity.this).create();

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.choose_language_dialog, null);

        english = view.findViewById(R.id.english);
        hindi = view.findViewById(R.id.hindi);
        deutsch = view.findViewById(R.id.deutsch);
        espanol = view.findViewById(R.id.espanol);
        francais = view.findViewById(R.id.francais);
        italiano = view.findViewById(R.id.italiano);
        german = view.findViewById(R.id.german);
        tamil = view.findViewById(R.id.tamil);
        marathi = view.findViewById(R.id.marathi);
        bengali = view.findViewById(R.id.bengali);
        punjabi = view.findViewById(R.id.punjabi);

        alertDialog.setView(view);

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userLanguage.setText(english.getText().toString().trim());
            }
        });

        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userLanguage.setText(hindi.getText().toString().trim());
            }
        });

        deutsch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userLanguage.setText(deutsch.getText().toString().trim());
            }
        });

        espanol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userLanguage.setText(espanol.getText().toString().trim());
            }
        });

        francais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userLanguage.setText(francais.getText().toString().trim());
            }
        });

        italiano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userLanguage.setText(italiano.getText().toString().trim());
            }
        });

        german.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userLanguage.setText(german.getText().toString().trim());
            }
        });

        tamil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userLanguage.setText(tamil.getText().toString().trim());
            }
        });

        marathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userLanguage.setText(marathi.getText().toString().trim());
            }
        });

        bengali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userLanguage.setText(bengali.getText().toString().trim());
            }
        });

        punjabi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                userLanguage.setText(punjabi.getText().toString().trim());
            }
        });

        alertDialog.show();
    }

    private void saveProfileToFirebaseFirestore()
    {
        final ProgressDialog progressDialog = new ProgressDialog(ViewProfileActivity.this);
        progressDialog.setMessage("Uploading Your Information...");
        progressDialog.show();

        final String email = userEmail.getText().toString().trim();
        final String mobile = userMobile.getText().toString().trim();
        final String about = userAbout.getText().toString().trim();
        final String gender = userGender.getText().toString().trim();
        final String dob = userDOB.getText().toString().trim();
        final String work = userWork.getText().toString().trim();
        final String language = userLanguage.getText().toString().trim();

        DocumentReference currentUser = userRef.document(email);
        currentUser.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            userRef.document(email)
                                    .update("mobile", mobile, "about", about, "dob", dob, "gender", gender, "language", language, "work", work)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            Alerter.create(ViewProfileActivity.this)
                                                    .setTitle("Profile updated successfully!")
                                                    .setTextAppearance(R.style.AddToBookmarkAlert)
                                                    .setBackgroundColorRes(R.color.bookmarkColor)
                                                    .setIcon(R.drawable.info_icon)
                                                    .setDuration(3000)
                                                    .enableSwipeToDismiss()
                                                    .enableIconPulse(true)
                                                    .enableVibration(true)
                                                    .show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Alerter.create(ViewProfileActivity.this)
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
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(ViewProfileActivity.this, "up-to-bottom");
    }
}
