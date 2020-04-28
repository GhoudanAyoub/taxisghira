package com.TaxiSghira.TreeProg.plashscreen.ui.MapModelView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Module.Accept;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.Demande;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class MapViewModel extends ViewModel {
    private MutableLiveData<Chifor> chiforMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Accept> acceptMutableLiveData = new MutableLiveData<>();
    Chifor chifor ;
    Accept accept;

    public LiveData<Chifor> getChiforMutableLiveData() {
        return chiforMutableLiveData;
    }

    public LiveData<Accept> getAcceptMutableLiveData() {
        return acceptMutableLiveData;
    }

    public void GetChiforDataLocation(){
        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection("Chifor")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            chiforMutableLiveData.setValue(document.toObject(Chifor.class));
                        }
                    }
                });
    }

    public void DelateDemande(){
        FireBaseClient.getFireBaseClient()
                .getDatabaseReference().child("Demande")
                .orderByChild("ClientName").equalTo(FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getDisplayName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
        FireBaseClient.getFireBaseClient()
                .getDatabaseReference().child("Accept").orderByChild("ClientName")
                .equalTo(FireBaseClient.getFireBaseClient().getUserLogEdInAccount().getDisplayName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection("Demande")
                .document(demande.getClientName())
                .set(demande).addOnCompleteListener(task -> {
        });
    }
}
