package com.TaxiSghira.TreeProg.plashscreen.ui.FavorViewModel;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Commun;
import com.TaxiSghira.TreeProg.plashscreen.Module.Favor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class FavorViewModel extends ViewModel {
    private MutableLiveData<List<Favor>> mutableLiveData ;
    private List<Favor> FavorList = new ArrayList<>();

    public LiveData<List<Favor>> getMutableLiveData() {
        mutableLiveData = new MutableLiveData<>();
        return mutableLiveData;
    }

    public List<Favor> getFavor() {
        FireBaseClient.getFireBaseClient().getDatabaseReference().child(Commun.Favor_DataBase_Table)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    FavorList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Favor favor = dataSnapshot1.getValue(Favor.class);
                        FavorList.add(favor);
                    }
                    mutableLiveData.setValue(FavorList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.tag("FE").e(databaseError.getMessage());
            }
        });
        return FavorList;
    }

    public  void AddFAvor( String mAuth, String Name, String Chh_Num, String taxinum){
        FireBaseClient.getFireBaseClient().getDatabaseReference().child(Commun.Favor_DataBase_Table)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference newPost2 = FireBaseClient.getFireBaseClient().getDatabaseReference().child(Commun.Favor_DataBase_Table).push();
                newPost2.setValue(new Favor(mAuth,Name,Chh_Num,taxinum));
                Toast.makeText(getApplicationContext(),"تمت الاضافة بنجاح\uD83D\uDE04",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
