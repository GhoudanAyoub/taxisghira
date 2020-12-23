package com.TaxiSghira.TreeProg.plashscreen.Authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.Room.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.ui.Map;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.rxbinding3.view.RxView;

import org.jetbrains.annotations.NotNull;

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
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        compositeDisposable = new CompositeDisposable();
        // Choose authentication
        providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());
        RxView.clicks(findViewById(R.id.buttonphone))
                .throttleFirst(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Unit>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NotNull Unit unit) {
                        findViewById(R.id.buttonphone).setClickable(false);
                        PreBuildLogin();
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Timber.e("Map Activity : Called");
                    }
                });
    }

    private void checkUserFromDataBase(FirebaseUser user) {
        FireBaseClient.getFireBaseClient()
                .getFirebaseDatabase()
                .getReference("Client")
                .orderByChild("id")
                .equalTo(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            FireBaseClient.getFireBaseClient().setFirebaseUser(user);
                            Toast.makeText(getApplicationContext(), " مرحبا بعودتك " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Map.class));
                        } else {
                            FireBaseClient.getFireBaseClient().setFirebaseUser(user);
                            startActivity(new Intent(getApplicationContext(), Create_Account.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.getCode(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void PreBuildLogin() {
        progressDialog.setMessage("المرجو الانتظار قليلا ⌛️");
        progressDialog.show();
        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_auth)
                .setGoogleButtonId(R.id.buttonphone)
                .build();
// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setAuthMethodPickerLayout(authMethodPickerLayout)
                        .setTheme(R.style.AppTheme)      // Set theme
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == Activity.RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                try {
                    FireBaseClient.getFireBaseClient().setFirebaseUser(user);
                    progressDialog.dismiss();
                    startActivity(new
                            Intent(getApplicationContext(), Create_Account.class));
                } catch (Exception e) {
                    Timber.e(e);
                }
            } else {
                if (response == null) {
                    finish();
                }
                assert response != null;
                if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //Show No Internet Notification
                    return;
                }
                if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
