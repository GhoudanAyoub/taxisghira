package com.TaxiSghira.TreeProg.plashscreen.Both;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Client.Map;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.ui.PersonalInfoModelView.PersonalInfoModelViewClass;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PersonalInfo extends AppCompatActivity {

    PersonalInfoModelViewClass personalInfoModelViewClass;
    TextInputLayout fullname, Adress, Tell;
    private ProgressDialog gProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        personalInfoModelViewClass = ViewModelProviders.of(this).get(PersonalInfoModelViewClass.class);
        personalInfoModelViewClass.getClientInfo();
        gProgress = new ProgressDialog(this);
        findViewById(R.id.returnAnim).setOnClickListener(v -> super.onBackPressed());
        fullname = findViewById(R.id.firstname);
        Adress = findViewById(R.id.lastname);
        Tell = findViewById(R.id.personalAdress);
        findViewById(R.id.gonext3).setOnClickListener(v -> addDataClient());

        personalInfoModelViewClass.getClientMutableLiveData().observe(this, client -> {
            Objects.requireNonNull(fullname.getEditText()).setText(client.getFullname());
            Objects.requireNonNull(Adress.getEditText()).setText(client.getGmail());
            Objects.requireNonNull(Tell.getEditText()).setText(client.getTell());
        });

    }

    private void addDataClient() {
        DatabaseReference databaseReference =
                FireBaseClient.getFireBaseClient().getDatabaseReference().child(Common.Client_DataBase_Table);
        gProgress.setMessage("المرجو الانتظار قليلا ⌛️");
        gProgress.show();
        databaseReference.orderByChild(Common.Gmail_String)
                .equalTo(Common.Current_Client_Gmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getRef().removeValue();
                    DatabaseReference newdata = databaseReference.push();
                    newdata.setValue(new Client(Objects.requireNonNull(fullname.getEditText()).getText().toString(), Objects.requireNonNull(Tell.getEditText()).getText().toString(),
                            Objects.requireNonNull(Adress.getEditText()).getText().toString(), Common.Current_Client_Id));
                    gProgress.dismiss();
                    Toast.makeText(getApplicationContext(), "تم التسجيل بنحاح\uD83E\uDD29", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Map.class));
                } else {
                    DatabaseReference newdata = databaseReference.push();
                    newdata.setValue(new Client(Objects.requireNonNull(fullname.getEditText()).getText().toString(), Objects.requireNonNull(Tell.getEditText()).getText().toString(),
                            Objects.requireNonNull(Adress.getEditText()).getText().toString(), Common.Current_Client_Id));
                    gProgress.dismiss();
                    Toast.makeText(getApplicationContext(), "تم التسجيل بنحاح\uD83E\uDD29", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Map.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "نواجه مشكل في التواصل\uD83D\uDE14", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() { }
}
