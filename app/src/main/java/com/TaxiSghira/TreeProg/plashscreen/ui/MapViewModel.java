package com.TaxiSghira.TreeProg.plashscreen.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.Demande;
import com.TaxiSghira.TreeProg.plashscreen.Module.Pickup;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class MapViewModel extends ViewModel {
    private MutableLiveData<List<Chifor>> chiforMutableLiveData;
    private MutableLiveData<Pickup> acceptMutableLiveData;
    private List<Chifor> chiforList = new ArrayList<>();

    public LiveData<List<Chifor>> getChiforMutableLiveData() {
        chiforMutableLiveData = new MutableLiveData<>();
        return chiforMutableLiveData;
    }

    public LiveData<Pickup> getAcceptMutableLiveData() {
        acceptMutableLiveData = new MutableLiveData<>();
        return acceptMutableLiveData;
    }

    public void GetChiforDataLocation() {

        FireBaseClient.getFireBaseClient().getDatabaseReference()
                .child(Common.Chifor_DataBase_Table)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            try {
                                for (DataSnapshot Ds1 : dataSnapshot.getChildren()) {
                                    Chifor ch1 = Ds1.getValue(Chifor.class);
                                    chiforList.add(ch1);
                                }
                                chiforMutableLiveData.setValue(chiforList);
                            } catch (Throwable t) {
                                Timber.e(t);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Timber.e(databaseError.getMessage());
                    }
                });
    }


    public void GetPickDemand() {
        FireBaseClient.getFireBaseClient()
                .getFirebaseDatabase()
                .getReference(Common.Pickup_DataBase_Table)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            try {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                    if (dataSnapshot1.exists()) {
                                        Pickup p = dataSnapshot1.getValue(Pickup.class);
                                        assert p != null;
                                        if (p.getDemande().getClientId().equals(Common.Current_Client_Id)) {
                                            acceptMutableLiveData.setValue(p);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Timber.e(e);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Timber.e(databaseError.getMessage());
                    }
                });
    }

    public void AddDemand(Demande demande) {
        FireBaseClient.getFireBaseClient().getDatabaseReference()
                .child(Common.Demande_DataBase_Table)
                .child(demande.getCity())
                .push()
                .setValue(demande)
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnSuccessListener(v -> Timber.i("DemandAdded"));
    }
}
