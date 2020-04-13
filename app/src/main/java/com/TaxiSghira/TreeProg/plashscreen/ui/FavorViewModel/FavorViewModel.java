package com.TaxiSghira.TreeProg.plashscreen.ui.FavorViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Module.Favor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class FavorViewModel extends ViewModel {
    public MutableLiveData<List<Favor>> mutableLiveData = new MutableLiveData<>();
    private List<Favor> FavorList = new ArrayList<>();

    public List<Favor> getFavor() {
        FireBaseClient.getFireBaseClient().getDatabaseReference().child("Favor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    FavorList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Favor favor = dataSnapshot1.getValue(Favor.class);
                        FavorList.add(favor);
                    }
                    mutableLiveData.setValue(FavorList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.tag("FE").e(databaseError.getMessage());
            }
        });
        return FavorList;
    }

    public List<Favor> getFavorListFireStore(){
        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection("Favor")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Favor favor = (Favor) document.getData();
                            FavorList.add(favor);
                        }
                        mutableLiveData.setValue(FavorList);
                    }
                })
                .addOnFailureListener(e -> { });
        return FavorList;
    }

}
