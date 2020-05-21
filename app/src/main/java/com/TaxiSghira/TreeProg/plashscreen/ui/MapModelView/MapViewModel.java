package com.TaxiSghira.TreeProg.plashscreen.ui.MapModelView;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Chifor;
import com.TaxiSghira.TreeProg.plashscreen.Module.Demande;
import com.TaxiSghira.TreeProg.plashscreen.Module.Pickup;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class MapViewModel extends ViewModel {
    private MutableLiveData<Chifor> chiforMutableLiveData ;
    Pickup pickup;
    private MutableLiveData<Pickup> acceptMutableLiveData;

    public LiveData<Chifor> getChiforMutableLiveData() {
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
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            try {
                                chiforMutableLiveData.setValue(document.toObject(Chifor.class));
                            }catch (Exception e){Timber.e(e);}
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
        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection(Common.Pickup_DataBase_Table)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            try {
                                pickup = document.toObject(Pickup.class);
                                if (Common.ClientName_String.equals(pickup.getDemande().getClientName()))
                                acceptMutableLiveData.setValue(document.toObject(Pickup.class));
                            }catch (Throwable t){
                                Timber.e(t);
                            }
                        }
                    }
                });
    }

    public void AddDemande(Demande demande){
        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection(Common.Demande_DataBase_Table)
                .document(demande.getClientName())
                .set(demande);
    }
}
