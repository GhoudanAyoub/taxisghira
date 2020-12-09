package com.TaxiSghira.TreeProg.plashscreen.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.Module.Demande;
import com.TaxiSghira.TreeProg.plashscreen.Module.Pickup;
import com.TaxiSghira.TreeProg.plashscreen.di.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.di.Repository;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import timber.log.Timber;

public class MapViewModel extends ViewModel {

    private Repository repository;

    private LiveData<List<Chifor>> listLiveDataFavorChifor = null;

    private MutableLiveData<Pickup> acceptMutableLiveData;
    private MutableLiveData<Client> clientMutableLiveData;

    public LiveData<List<Chifor>> getListLiveDataFavorChifor() { return listLiveDataFavorChifor; }
    public LiveData<Pickup> getAcceptMutableLiveData() { acceptMutableLiveData = new MutableLiveData<>();return acceptMutableLiveData; }
    public LiveData<Client> getClientMutableLiveData() { clientMutableLiveData = new MutableLiveData<>();return clientMutableLiveData; }


    @ViewModelInject
    public MapViewModel(Repository repository) {
        this.repository = repository;
    }

    //Client Data
    public void getClientInfo() {
        FirebaseDatabase.getInstance()
                .getReference(Common.Client_DataBase_Table)
                .orderByChild(Common.Gmail_String)
                .equalTo(Common.Current_Client_Gmail.toLowerCase())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            clientMutableLiveData.setValue(dataSnapshot.getValue(Client.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Timber.e(error.getMessage());
                    }
                });
    }

    //Demand Data
    public void GetPickDemand() {
        FireBaseClient.getFireBaseClient()
                .getFirebaseDatabase()
                .getReference(Common.Pickup_DataBase_Table)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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

    //Favor Chifor Data
    public void InsertData(Chifor chifor){repository.InsertData(chifor);}
    public void DeleteData(int id){repository.DeleteData(id);}
    public void GetData(){listLiveDataFavorChifor = repository.GetData();}
}
