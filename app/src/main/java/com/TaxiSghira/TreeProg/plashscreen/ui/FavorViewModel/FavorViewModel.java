package com.TaxiSghira.TreeProg.plashscreen.ui.FavorViewModel;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Favor;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class FavorViewModel extends ViewModel {
    List<Favor> favorList = new ArrayList<>();
    private MutableLiveData<List<Favor>> mutableLiveData;

    public LiveData<List<Favor>> getMutableLiveData() {
        mutableLiveData = new MutableLiveData<>();
        return mutableLiveData;
    }

    public void getFavor() {
        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection(Common.Favor_DataBase_Table)
                .document(Objects.requireNonNull(Common.Current_Client_DispalyName))
                .collection("FavorClient")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            try {
                                Favor favor = document.toObject(Favor.class);
                                if (favor.getClient_name().equalsIgnoreCase(Common.Current_Client_DispalyName)) {
                                    favorList.add(favor);
                                }
                            } catch (Throwable t) {
                                Timber.e(t);
                            }
                        }
                        mutableLiveData.setValue(favorList);
                    }
                });
    }

    public void AddFAvor(String mAuth, String Name, String Chh_Num, String taxinum, String clientname) {
        FireBaseClient.getFireBaseClient().getFirebaseFirestore()
                .collection(Common.Favor_DataBase_Table)
                .document(Objects.requireNonNull(Common.Current_Client_DispalyName))
                .collection("FavorClient")
                .document()
                .set(new Favor(mAuth, Name, Chh_Num, taxinum, clientname))
                .addOnCompleteListener(task -> Toast.makeText(getApplicationContext(), "تمت الاضافة", Toast.LENGTH_SHORT).show());
    }

}
