package com.TaxiSghira.TreeProg.plashscreen.Both;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.TaxiSghira.TreeProg.plashscreen.Client.Map;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.Operation.Op;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonalInfo extends AppCompatActivity {


    DatabaseReference databaseReference;
    EditText firstname, lastname, personalAdress;
    private ProgressDialog gProgress;
    Client client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        databaseReference = FirebaseDatabase.getInstance().getReference("Client");
        gProgress = new ProgressDialog(this);
        findViewById(R.id.returnAnim).setOnClickListener(v -> super.onBackPressed());
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        personalAdress = findViewById(R.id.personalAdress);
        findViewById(R.id.gonext3).setOnClickListener(v -> addDataClient());
        databaseReference.orderByChild("gmail").equalTo(PutPhone.gmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        client = dataSnapshot1.getValue(Client.class);
                    }
                    firstname.setText(client.getFullname());
                    personalAdress.setText(client.getTell());
                    lastname.setText(client.getGmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void addDataClient() {
        gProgress.setMessage("المرجو الانتظار قليلا ⌛️");
        gProgress.show();
        databaseReference.orderByChild("gmail").equalTo(PutPhone.gmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getRef().removeValue();
                    DatabaseReference newdata = databaseReference.push();
                    newdata.child("WHosme").setValue(Op.user+lastname.getText().toString());
                    newdata.child("Fullname").setValue(firstname.getText().toString());
                    newdata.child("tell").setValue(personalAdress.getText().toString());
                    newdata.child("gmail").setValue(lastname.getText().toString());
                    gProgress.dismiss();
                    Toast.makeText(getApplicationContext(), "تم التسجيل بنحاح\uD83E\uDD29", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), Map.class));
                }else {
                    DatabaseReference newdata = databaseReference.push();
                    newdata.child("WHosme").setValue(Op.user+lastname.getText().toString());
                    newdata.child("Fullname").setValue(firstname.getText().toString());
                    newdata.child("tell").setValue(personalAdress.getText().toString());
                    newdata.child("gmail").setValue(lastname.getText().toString());
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
    public void onBackPressed() {
    }
}
