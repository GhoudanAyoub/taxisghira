package com.TaxiSghira.TreeProg.plashscreen.Client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.TaxiSghira.TreeProg.plashscreen.Adapters.FavorAdapter;
import com.TaxiSghira.TreeProg.plashscreen.Profile.Util_List;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.ui.FavorViewModel;

public class FavorDrivers extends AppCompatActivity {

    private RecyclerView Favor_Recycle;
    FavorViewModel favorViewModel;
    FavorAdapter favorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor_driver);

        favorViewModel = ViewModelProviders.of(this).get(FavorViewModel.class);
        favorViewModel.getFavor();

        findViewById(R.id.listAnim22).setOnClickListener(v->startActivity(new Intent(getApplicationContext(), Util_List.class)));
        favorAdapter = new FavorAdapter(getApplicationContext());
        Favor_Recycle = findViewById(R.id.Favor_Recycle);
        Favor_Recycle.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        Favor_Recycle.setHasFixedSize(true);
        Favor_Recycle.setAdapter(favorAdapter);

        favorViewModel.getMutableLiveData().observe(this, favors -> favorAdapter.setList(favors));
    }
}
