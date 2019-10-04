package com.TaxiSghira.TreeProg.plashscreen.Client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.TaxiSghira.TreeProg.plashscreen.Adapters.Favor_Adpter;
import com.TaxiSghira.TreeProg.plashscreen.Module.Favor;
import com.TaxiSghira.TreeProg.plashscreen.Profile.Util_List;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopDriver extends AppCompatActivity {

    Favor_Adpter exerciceAdapter;
    private List<Favor> FavorList;
    private RecyclerView Favor_Recycle;
    private TextView txtEmptyFavoritesList;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_driver);

        FavorList = new ArrayList<>();
        exerciceAdapter = new Favor_Adpter(getApplicationContext(), FavorList);
        Favor_Recycle = findViewById(R.id.Favor_Recycle);
        txtEmptyFavoritesList = findViewById(R.id.content_favorite_emptylist);
        Favor_Recycle.setHasFixedSize(true);
        Favor_Recycle.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        findViewById(R.id.listAnim22).setOnClickListener(v->startActivity(new Intent(getApplicationContext(), Util_List.class)));

        databaseReference = FirebaseDatabase.getInstance().getReference("Favor");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    FavorList.clear();
                    for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                        Favor favor = dataSnapshot1.getValue(Favor.class);
                        FavorList.add(favor);
                    }
                    Favor_Recycle.setAdapter(new Favor_Adpter(getApplicationContext(),FavorList));
                }else {
                    txtEmptyFavoritesList.setText("لم تختر بعد سائقك المفضل!!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
