package com.TaxiSghira.TreeProg.plashscreen.ui.MapModelView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.Demande;
import com.TaxiSghira.TreeProg.plashscreen.Module.Pickup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class MapViewModel extends ViewModel {
    private MutableLiveData<List<Chifor>> chiforMutableLiveData ;
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

    public void GetChiforDataLocation(){
        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection(Common.Chifor_DataBase_Table)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Chifor chifor = document.toObject(Chifor.class);
                                chiforList.add(chifor);
                            }
                            chiforMutableLiveData.setValue(chiforList);
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                });
    }

    public void DelateDemande(Demande demande){
        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection(Common.Demande_DataBase_Table)
                .document(demande.getClientName())
                .delete().addOnCompleteListener(task -> Toast.makeText(getApplicationContext(),"تم إلغاء الطلب",Toast.LENGTH_SHORT).show());
    }

    public void GetAcceptDemandeList(){
        FireBaseClient.getFireBaseClient().getFirebaseDatabase()
                .getReference(Common.Pickup_DataBase_Table)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                                try {
                                    Pickup p = dataSnapshot1.getValue(Pickup.class);
                                    assert p != null;
                                    if (p.getDemande().getClientName().equals(Common.Current_Client_DispalyName)){
                                        acceptMutableLiveData.setValue(p);
                                    }
                                }catch (Exception e){ }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection(Common.Pickup_DataBase_Table)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            try {
                                //acceptMutableLiveData.setValue(document.toObject(Pickup.class));
                            }catch (Throwable t){
                                Timber.e(t);
                            }
                        }
                    }
                });
    }

    public void AddDemande(Demande demande){
        FireBaseClient.getFireBaseClient().getDatabaseReference()
                .child(Common.Demande_DataBase_Table)
                .push()
                .setValue(demande);
        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection(Common.Demande_DataBase_Table)
                .document(demande.getClientName())
                .set(demande);
    }
}
