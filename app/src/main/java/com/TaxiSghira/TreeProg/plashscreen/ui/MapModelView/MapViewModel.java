package com.TaxiSghira.TreeProg.plashscreen.ui.MapModelView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Module.Accept;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.Module.Demande;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class MapViewModel extends ViewModel {
    public MutableLiveData<Chifor> chiforMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Accept> acceptMutableLiveData = new MutableLiveData<>();
    Chifor chifor ;
    Accept accept;

    public void GetChiforDataLocation(){
        FireBaseClient.getFireBaseClient().getDatabaseReference().child("Chifor").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                       chifor = dataSnapshot1.getValue(Chifor.class);
                    }
                    assert chifor != null;
                    chiforMutableLiveData.setValue(chifor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public void DelateDemande(){
        FireBaseClient.getFireBaseClient().getDatabaseReference().child("Demande").orderByChild("ClientName").equalTo(FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
                Timber.e(databaseError.getMessage());
            }
        });
    }

    public void GetAcceptDemandeList(){
        FireBaseClient.getFireBaseClient().getDatabaseReference().child("Accept").orderByChild("ClientName").equalTo(FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        accept = dataSnapshot1.getValue(Accept.class);
                    }
                    acceptMutableLiveData.setValue(accept);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"لايوجد اي طلب الان !!!",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void AddDemande(Demande demande){
        FireBaseClient.getFireBaseClient().getFirebaseFirestore().collection("Demande").add(demande).addOnCompleteListener(task -> {});
        /*
        FireBaseClient.getFireBaseClient().getDatabaseReference().child("Demande").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference NewOne = FireBaseClient.getFireBaseClient().getDatabaseReference().child("Demande").push();
                NewOne.setValue(demande);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
         */
    }
}
