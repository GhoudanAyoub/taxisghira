package com.TaxiSghira.TreeProg.plashscreen.Authentication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.Room.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.ui.MapViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PersonalInfo extends AppCompatActivity {

    private TextInputLayout fullname;
    private TextInputLayout Adress;
    private TextInputLayout Tell;
    private ProgressDialog gProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        mapViewModel.getClientInfo();
        gProgress = new ProgressDialog(this);
        Map<String, Object> childUpdates = new HashMap<>();

        fullname = findViewById(R.id.firstname);
        Adress = findViewById(R.id.lastname);
        Tell = findViewById(R.id.personalAdress);
        mapViewModel.getClientMutableLiveData().observe(this, client -> {
            Objects.requireNonNull(fullname.getEditText()).setText(client.getFullname());
            Objects.requireNonNull(Adress.getEditText()).setText(client.getGmail());
            Objects.requireNonNull(Tell.getEditText()).setText(client.getCity());
        });

        childUpdates.put("/city:" , Objects.requireNonNull(Tell.getEditText()).getText());
        childUpdates.put("/fullname" , Objects.requireNonNull(fullname.getEditText()).getText());


        findViewById(R.id.returnAnim).setOnClickListener(v -> super.onBackPressed());
        findViewById(R.id.gonext3).setOnClickListener(v -> addDataClient(childUpdates));
    }

    private void addDataClient(Map<String, Object> childUpdates) {
        gProgress.setMessage(getString(R.string.PleaseWait));
        gProgress.show();
        FireBaseClient.getFireBaseClient()
                .getDatabaseReference()
                .child(Common.Client_DataBase_Table)
                .updateChildren(childUpdates)
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnSuccessListener(v-> {Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.YourDataUpdated), Snackbar.LENGTH_LONG).show();
                gProgress.dismiss();});
    }

    @Override
    public void onBackPressed() { }
}
