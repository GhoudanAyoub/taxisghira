package com.TaxiSghira.TreeProg.plashscreen.Both;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Client.Map;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kotlin.Unit;
import timber.log.Timber;


public class Auth extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1000;
    private CompositeDisposable compositeDisposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        compositeDisposable = new CompositeDisposable();
        RxView.clicks(findViewById(R.id.buttonphone))
                .throttleFirst(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Unit>() {
                    @Override
                    public void onSubscribe(Disposable d) { compositeDisposable.add(d); }

                    @Override
                    public void onNext(Unit unit) {findViewById(R.id.buttonphone).setClickable(false); PreBuildLogin(); }

                    @Override
                    public void onError(Throwable e) { Timber.e(e); }

                    @Override
                    public void onComplete() { Timber.e("Map Activity : Called");}
                });

    }

    //***********************
    private void PreBuildLogin(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_auth)
                .setGoogleButtonId(R.id.buttonphone)
                .build();
// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(true)
                        .setAuthMethodPickerLayout(authMethodPickerLayout)
                        .setTheme(R.style.AppTheme)      // Set theme
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == Activity.RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                try {
                    FireBaseClient.getFireBaseClient().setFirebaseUser(user);
                } catch (Exception e) {
                    Timber.e(e);
                }startActivity(new Intent(getApplicationContext(), Map.class));
            }

            else{

                if(response == null){
                    finish();
                }
                assert response != null;
                if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //Show No Internet Notification
                    return;
                }

                if(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, response.getError().getErrorCode(), Toast.LENGTH_LONG).show();
                    Timber.d(String.valueOf(response.getError().getErrorCode()));
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FireBaseClient.getFireBaseClient().getFirebaseAuth().getCurrentUser();
        if (user!=null){
            FireBaseClient.getFireBaseClient().setFirebaseUser(user);
//                Snackbar.make(Objects.requireNonNull(getCurrentFocus()), "مرحبا بكم\uD83D\uDE04", Snackbar.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), Map.class));
        }else {
            PreBuildLogin();
        }
    }
}
