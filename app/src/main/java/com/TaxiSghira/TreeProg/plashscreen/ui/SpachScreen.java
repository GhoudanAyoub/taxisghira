package com.TaxiSghira.TreeProg.plashscreen.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.TaxiSghira.TreeProg.plashscreen.Authentication.Auth;
import com.TaxiSghira.TreeProg.plashscreen.Client.Map;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.di.FireBaseClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class SpachScreen extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spachscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();
        Completable.timer(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    if (firebaseAuth.getCurrentUser() != null)
                        checkUserFromDataBase(firebaseAuth.getCurrentUser());
                    else
                        startActivity(new Intent(getApplication(), Auth.class));
                },Throwable::printStackTrace);
    }

    private void checkUserFromDataBase(FirebaseUser user) {
        FireBaseClient.getFireBaseClient()
                .getFirebaseDatabase()
                .getReference("Client")
                .orderByChild("id")
                .equalTo(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            FireBaseClient.getFireBaseClient().setFirebaseUser(user);
                            startActivity(new Intent(getApplicationContext(), Map.class));
                        }else {
                            FireBaseClient.getFireBaseClient().setFirebaseUser(user);
                            startActivity(new Intent(getApplicationContext(), Auth.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.getCode(), Toast.LENGTH_LONG).show();
                    }
                });

    }
}
