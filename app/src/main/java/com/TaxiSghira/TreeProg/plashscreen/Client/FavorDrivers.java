package com.TaxiSghira.TreeProg.plashscreen.Client;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.TaxiSghira.TreeProg.plashscreen.Adapters.FavorAdapter;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.ui.MapViewModel;
import com.TaxiSghira.TreeProg.plashscreen.ui.Util_List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavorDrivers extends AppCompatActivity {

    private RecyclerView Favor_Recycle;
    MapViewModel mapViewModel;
    FavorAdapter favorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor_driver);

        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        mapViewModel.GetData();

        findViewById(R.id.listAnim22).setOnClickListener(v->startActivity(new Intent(getApplicationContext(), Util_List.class)));
        favorAdapter = new FavorAdapter(getApplicationContext());
        Favor_Recycle = findViewById(R.id.Favor_Recycle);
        Favor_Recycle.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        Favor_Recycle.setHasFixedSize(true);
        Favor_Recycle.setAdapter(favorAdapter);

        mapViewModel.getListLiveDataFavorChifor().observe(this, chifors ->favorAdapter.setList(chifors));
    }
}
