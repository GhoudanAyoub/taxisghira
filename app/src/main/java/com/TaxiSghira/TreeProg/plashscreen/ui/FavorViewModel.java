package com.TaxiSghira.TreeProg.plashscreen.ui;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Favor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class FavorViewModel extends ViewModel {
    List<Favor> favorList = new ArrayList<>();
    private MutableLiveData<List<Favor>> mutableLiveData;

    public LiveData<List<Favor>> getMutableLiveData() {
        mutableLiveData = new MutableLiveData<>();
        return mutableLiveData;
    }

    public void getFavor() {
        FireBaseClient.getFireBaseClient().getFirebaseDatabase()
                .getReference(Common.Favor_DataBase_Table)
                .child(Common.Client_Id_String)
                .equalTo(Common.Current_Client_Id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                                try {
                                    Favor favor = ds1.getValue(Favor.class);
                                    favorList.add(favor);
                                } catch (Throwable t) {
                                    Timber.e(t);
                                }
                            }
                            mutableLiveData.setValue(favorList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Timber.e(databaseError.getMessage());
                    }
                });
    }

    public void AddFAvor(String userId, String Name, String Chh_Num, String taxinum, String clientname) {
        FireBaseClient.getFireBaseClient().getFirebaseDatabase()
                .getReference(Common.Favor_DataBase_Table)
                .push()
                .setValue(new Favor(userId, Name, Chh_Num, taxinum, clientname))
                .addOnFailureListener(Timber::e)
                .addOnSuccessListener(task -> Toast.makeText(getApplicationContext(), "تمت الاضافة", Toast.LENGTH_SHORT).show());
    }

}
