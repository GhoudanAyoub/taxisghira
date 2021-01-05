package com.TaxiSghira.TreeProg.plashscreen.ui;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Insert;
import androidx.room.Query;

import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.Module.FCMResponse;
import com.TaxiSghira.TreeProg.plashscreen.Module.FCMSendData;
import com.TaxiSghira.TreeProg.plashscreen.Module.YourLocations;
import com.TaxiSghira.TreeProg.plashscreen.Module.route;
import com.TaxiSghira.TreeProg.plashscreen.Room.Repository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@SuppressLint("CheckResult")
public class MapViewModel extends ViewModel {

    private final Repository repository;
    private List<YourLocations> yourLocationsList = new ArrayList<>();

    private LiveData<List<Chifor>> listLiveDataFavorChifor = null;
    private LiveData<List<YourLocations>> yourLocationsMutableLiveData = null;
    private MutableLiveData<Client> clientMutableLiveData;
    private MutableLiveData<List<route>> RouteLiveData = new MutableLiveData<>();

    public LiveData<List<Chifor>> getListLiveDataFavorChifor() {
        return listLiveDataFavorChifor;
    }

    public LiveData<Client> getClientMutableLiveData() {
        clientMutableLiveData = new MutableLiveData<>();
        return clientMutableLiveData;
    }

    public LiveData<List<route>> getRouteLiveData() {
        if (RouteLiveData == null) RouteLiveData = new MutableLiveData<>();
        return RouteLiveData;
    }

    public LiveData<List<YourLocations>> getYourLocationsLiveData() {
        return yourLocationsMutableLiveData;
    }

    @ViewModelInject
    public MapViewModel(Repository repository) {
        this.repository = repository;
    }

    //YourLocations Data

    public void InsertLocation(YourLocations yourLocations){
        Observable.timer(1,TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe( aLong -> repository.InsertLocation(yourLocations)
                        ,Throwable::printStackTrace);
    }

    public void  GetLocation(){
        yourLocationsMutableLiveData = repository.GetLocation();
    }

    public void GetYourLocations(){
        FirebaseDatabase.getInstance()
                .getReference(Common.YourLocations_DataBase_Table)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                YourLocations yourLocations = dataSnapshot.getValue(YourLocations.class);
                                if (yourLocations!=null && yourLocations.getUser_id().equals(Common.Current_Client_Id)) {
                                    yourLocationsList.add(yourLocations);
                                    System.out.println(yourLocationsList);
                                    //yourLocationsMutableLiveData.setValue(yourLocationsList);
                                }
                            }
                            //if (yourLocationsList!=null)
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void PushYourLocation(YourLocations yourLocations){
        FirebaseDatabase.getInstance()
                .getReference(Common.YourLocations_DataBase_Table)
                .push()
                .setValue(yourLocations)
                .addOnFailureListener(Throwable::printStackTrace);
    }

    //Send Notification
    public Observable<FCMResponse> sendNotification(FCMSendData body) {
        return repository.sendNotification(body);
    }

    //Client Data
    public void getClientInfo() {
        FirebaseDatabase.getInstance()
                .getReference(Common.Client_DataBase_Table)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Client client = null;
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                client = dataSnapshot1.getValue(Client.class);
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

    //Favor Chifor Data
    public void InsertData(Chifor chifor) {
        repository.InsertData(chifor);
    }

    public void DeleteData(int id) {
        repository.DeleteData(id);
    }

    public void GetData() {
        listLiveDataFavorChifor = repository.GetData();
    }

    //Get Direction For Notification
    public Disposable getDirections(String profile, String to, String access_token) {
        return repository.getDirections(profile, to, access_token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(routes -> RouteLiveData.setValue(routes.getRouteList()), Throwable::printStackTrace);
    }
}
