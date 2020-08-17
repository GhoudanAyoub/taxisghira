package com.TaxiSghira.TreeProg.plashscreen.Authentication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.ui.PersonalInfoModelViewClass;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalInfo extends AppCompatActivity {

    TextInputLayout fullname;
    TextInputLayout Adress;
    TextInputLayout Tell;
    private ProgressDialog gProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        PersonalInfoModelViewClass personalInfoModelViewClass = ViewModelProviders.of(this).get(PersonalInfoModelViewClass.class);
        personalInfoModelViewClass.getClientInfo();
        gProgress = new ProgressDialog(this);
        Map<String, Object> childUpdates = new HashMap<>();

        fullname = findViewById(R.id.firstname);
        Adress = findViewById(R.id.lastname);
        Tell = findViewById(R.id.personalAdress);
        personalInfoModelViewClass.getClientMutableLiveData().observe(this, client -> {
            Objects.requireNonNull(fullname.getEditText()).setText(client.getFullname());
            Objects.requireNonNull(Adress.getEditText()).setText(client.getGmail());
            Objects.requireNonNull(Tell.getEditText()).setText(client.getCity());
        });

        childUpdates.put("/city:" , Tell.getEditText().getText());
        childUpdates.put("/fullname" , fullname.getEditText().getText());


        findViewById(R.id.returnAnim).setOnClickListener(v -> super.onBackPressed());
        findViewById(R.id.gonext3).setOnClickListener(v -> addDataClient(childUpdates));
    }

    private void addDataClient(Map<String, Object> childUpdates) {
        DatabaseReference databaseReference = FireBaseClient.getFireBaseClient().getDatabaseReference().child(Common.Client_DataBase_Table);
        gProgress.setMessage("المرجو الانتظار قليلا ⌛️");
        gProgress.show();
        databaseReference
                .updateChildren(childUpdates)
                .addOnFailureListener(Throwable::printStackTrace)
                .addOnSuccessListener(v-> {Snackbar.make(findViewById(android.R.id.content),"تم تحديث معلوماتك", Snackbar.LENGTH_LONG).show();
                gProgress.dismiss();});
    }

    @Override
    public void onBackPressed() { }
}
