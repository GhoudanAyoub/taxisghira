package com.TaxiSghira.TreeProg.plashscreen.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import timber.log.Timber;

public class PersonalInfoModelViewClass extends ViewModel {
    private MutableLiveData<Client> clientMutableLiveData;
    private Client client;

    public LiveData<Client> getClientMutableLiveData() {
        clientMutableLiveData = new MutableLiveData<>();
        return clientMutableLiveData;
    }

    public void getClientInfo() {
        FireBaseClient.getFireBaseClient().getDatabaseReference()
                .child(Common.Client_DataBase_Table)
                .orderByChild("id")
                .equalTo(Common.Current_Client_Id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        client = dataSnapshot1.getValue(Client.class);
                    }
                    clientMutableLiveData.setValue(client);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.e(databaseError.getMessage());
            }
        });
    }
}
