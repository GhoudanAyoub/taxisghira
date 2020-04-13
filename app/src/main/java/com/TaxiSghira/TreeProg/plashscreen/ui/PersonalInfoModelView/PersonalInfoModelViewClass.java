package com.TaxiSghira.TreeProg.plashscreen.ui.PersonalInfoModelView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class PersonalInfoModelViewClass extends ViewModel {
    public MutableLiveData<Client> clientMutableLiveData = new MutableLiveData<>();
    private Client client;

    public void getClientInfo() {
        String gmail = "gmail";
        FireBaseClient.getFireBaseClient().getDatabaseReference().child("Client").orderByChild(gmail).equalTo(FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
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

            }
        });
    }


}
