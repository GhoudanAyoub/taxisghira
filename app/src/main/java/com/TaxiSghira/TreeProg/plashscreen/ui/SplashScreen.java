package com.TaxiSghira.TreeProg.plashscreen.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.TaxiSghira.TreeProg.plashscreen.Authentication.Auth;
import com.TaxiSghira.TreeProg.plashscreen.Authentication.Create_Account;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.Room.FireBaseClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.installations.FirebaseInstallations;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class SplashScreen extends AppCompatActivity {

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
                    if (firebaseAuth.getCurrentUser() != null) {
                        FirebaseInstallations.getInstance()
                                .getId()
                                .addOnFailureListener(Throwable::printStackTrace)
                                .addOnSuccessListener(instanceIdResult -> {
                                    if (instanceIdResult != null)
                                        UserUtils.UpdateToken(this, instanceIdResult);
                                });
                        checkUserFromDataBase(firebaseAuth.getCurrentUser());
                    } else
                        startActivity(new Intent(getApplication(), Auth.class));
                });
    }

    private void checkUserFromDataBase(FirebaseUser user) {
        FireBaseClient.getFireBaseClient()
                .getFirebaseDatabase()
                .getReference("Client")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                Client client = dataSnapshot1.getValue(Client.class);
                                if (client!=null && (client.getId().equals(firebaseAuth.getCurrentUser().getUid()) || client.getGmail().equals(user.getEmail()))){
                                    FireBaseClient.getFireBaseClient().setFirebaseUser(user);
                                    Toasty.success(getApplicationContext(), " مرحبا بعودتك " + user.getDisplayName(), Toasty.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Map.class));
                                }else {
                                    FireBaseClient.getFireBaseClient().setFirebaseUser(user);
                                    startActivity(new Intent(getApplicationContext(), Auth.class));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.getCode(), Toast.LENGTH_LONG).show();
                    }
                });

    }
}
