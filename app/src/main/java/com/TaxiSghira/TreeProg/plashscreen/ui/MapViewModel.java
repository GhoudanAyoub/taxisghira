package com.TaxiSghira.TreeProg.plashscreen.ui;

import android.annotation.SuppressLint;

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
import com.TaxiSghira.TreeProg.plashscreen.Module.FCMResponse;
import com.TaxiSghira.TreeProg.plashscreen.Module.FCMSendData;
import com.TaxiSghira.TreeProg.plashscreen.Module.Trip;
import com.TaxiSghira.TreeProg.plashscreen.Module.route;
import com.TaxiSghira.TreeProg.plashscreen.Room.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Room.Repository;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MapViewModel extends ViewModel {

    private Repository repository;

    private LiveData<List<Chifor>> listLiveDataFavorChifor = null;

    private MutableLiveData<Trip> acceptMutableLiveData;
    private MutableLiveData<Client> clientMutableLiveData;
    private MutableLiveData<Demande> DemandMutableLiveData;
    private MutableLiveData<List<route>> RouteLiveData= new MutableLiveData<>() ;

    public LiveData<List<Chifor>> getListLiveDataFavorChifor() { return listLiveDataFavorChifor; }
    public LiveData<Trip> getAcceptMutableLiveData() { acceptMutableLiveData = new MutableLiveData<>();return acceptMutableLiveData; }
    public LiveData<Client> getClientMutableLiveData() { clientMutableLiveData = new MutableLiveData<>();return clientMutableLiveData; }
    public LiveData<Demande> getDemandMutableLiveData() { DemandMutableLiveData = new MutableLiveData<>();return DemandMutableLiveData; }
    public LiveData<List<route>> getRouteLiveData() { if (RouteLiveData == null ) RouteLiveData = new MutableLiveData<>();return RouteLiveData; }



    @ViewModelInject
    public MapViewModel(Repository repository) {
        this.repository = repository;
    }


    //Send Notification
    public Observable<FCMResponse> sendNotification(FCMSendData body){return repository.sendNotification(body);}

    //Client Data
    public void getClientInfo() {
        FirebaseDatabase.getInstance()
                .getReference(Common.Client_DataBase_Table)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Client client = null;
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                client  = dataSnapshot1.getValue(Client.class);
                            }
                            if (client != null && client.getGmail().equals(Common.Current_Client_Gmail))
                                clientMutableLiveData.setValue(client);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Timber.e(error.getMessage());
                    }
                });
    }

    //Pick Data
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
                                        Trip p = dataSnapshot1.getValue(Trip.class);
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

    //Demand Data
    public void AddDemand(Demande demande) {
        FireBaseClient.getFireBaseClient().getDatabaseReference()
                .child(Common.Demande_DataBase_Table)
                .child(demande.getCity())
                .push()
                .setValue(demande)
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnSuccessListener(v -> Timber.i("DemandAdded"));
    }
    public void GetDemand(){
        FireBaseClient.getFireBaseClient().getDatabaseReference()
                .child(Common.Demande_DataBase_Table)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Demande demande1 = null;
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                demande1 = dataSnapshot1.getValue(Demande.class);
                                if (demande1!=null && demande1.getClientId().equals(Common.Current_Client_Id))
                                    DemandMutableLiveData.setValue(demande1);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void RemoveDemand(Demande demande) {
        FireBaseClient.getFireBaseClient().getDatabaseReference()
                .child(Common.Demande_DataBase_Table)
                .child(demande.getCity())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Demande demande1 = null;
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                demande1 = dataSnapshot1.getValue(Demande.class);
                                if (demande1!=null && demande1.getClientId().equals(Common.Current_Client_Id))
                                    dataSnapshot1.getRef().removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    //Favor Chifor Data
    public void InsertData(Chifor chifor){repository.InsertData(chifor);}
    public void DeleteData(int id){repository.DeleteData(id);}
    public void GetData(){listLiveDataFavorChifor = repository.GetData();}


    //Get Direction For Notification
    @SuppressLint("CheckResult")
    public Disposable getDirections(String profile, String to, String access_token){
        return repository.getDirections(profile,to,access_token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(routes -> RouteLiveData.setValue(routes.getRouteList()),Throwable::printStackTrace);
    }
}
