package com.services.fitbeetles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.services.fitbeetles.common.Common;

import java.util.Arrays;

import de.mateware.snacky.Snacky;
import maes.tech.intentanim.CustomIntent;

public class LoginActivity extends AppCompatActivity {

    //firebase variables
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    //extra variables
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            Intent intent1 = new Intent(LoginActivity.this, ChooseCityActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent1);
            CustomIntent.customType(LoginActivity.this, "left-to-right");
            finish();
        } else {
            Authenticate();
        }
    }

    private void Authenticate(){

        AuthMethodPickerLayout methodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_login)
                .setGoogleButtonId(R.id.googleLogin)
                .setFacebookButtonId(R.id.facebookLogin)
                .setTosAndPrivacyPolicyId(R.id.terms_and_conditions)
                .build();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false)
                        .setTosAndPrivacyPolicyUrls("https://www.zomato.com/conditions", "https://www.zomato.com/privacy")
                        .setAuthMethodPickerLayout(methodPickerLayout)
                        .setTheme(R.style.ForLogin)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK){
                Intent intent2 = new Intent(LoginActivity.this, ChooseCityActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
                CustomIntent.customType(LoginActivity.this, "left-to-right");
                finish();
            }
            else {
                if(response == null){
                    onBackPressed();
                    return;
                }

                if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    View view = getLayoutInflater().inflate(R.layout.no_internet_connection, null);
                    Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
                    dialog.setContentView(view);
                    dialog.show();
                }
                else if(response.getError().getErrorCode() == ErrorCodes.PROVIDER_ERROR)
                {
                    Snacky.builder()
                                .setActivity(LoginActivity.this)
                            .setText("Provider Error!")
                            .setDuration(Snacky.LENGTH_SHORT)
                            .error()
                            .show();
                }
                else if(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)
                {
                    Snacky.builder()
                            .setActivity(LoginActivity.this)
                            .setText("Some Unknown Error!")
                            .setDuration(Snacky.LENGTH_SHORT)
                            .error()
                            .show();
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
