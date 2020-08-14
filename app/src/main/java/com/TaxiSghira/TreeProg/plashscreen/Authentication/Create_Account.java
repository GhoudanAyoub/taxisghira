package com.TaxiSghira.TreeProg.plashscreen.Authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.TaxiSghira.TreeProg.plashscreen.R;

public class Create_Account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__account);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
    }

    @Override
    public void onBackPressed() { }
}