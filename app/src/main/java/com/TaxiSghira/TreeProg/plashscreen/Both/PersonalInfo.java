package com.TaxiSghira.TreeProg.plashscreen.Both;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.TaxiSghira.TreeProg.plashscreen.Client.Map;
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

    }

    public void addDataClient() {
        gProgress.setMessage("المرجو الانتظار قليلا ⌛️");
        gProgress.show();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference newdata = databaseReference.push();
                String key = databaseReference.push().getKey();
                newdata.child("ID").setValue(key);
                newdata.child("WHosme").setValue(Op.user+GmailValidation.gmail);
                newdata.child("firstName").setValue(firstname.getText().toString());
                newdata.child("secondName").setValue(lastname.getText().toString());
                newdata.child("adress").setValue(personalAdress.getText().toString());
                newdata.child("gmail").setValue(GmailValidation.gmail);
                newdata.child("tell").setValue(phoneNumber.tel);
                gProgress.dismiss();
                Toast.makeText(getApplicationContext(), "تم التسجيل بنحاح\uD83E\uDD29", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Map.class));
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
/*
    private void openImageFile() {
        Intent galinten = new Intent();
        galinten.setType("image/*");
        galinten.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galinten, "SELECT IMAGE"), GALLERI_PIK);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERI_PIK && resultCode == RESULT_OK) {
            imageUri = data.getData();
        }
    }
    public void addDataChiffeur(){
        gProgress.setMessage("المرجو الانتظار قليلا ⌛️");
        gProgress.show();

        try {
            StorageReference storageReference = storage.child("CIN_Image").child(imageUri.getLastPathSegment());
            storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
               final Task<Uri> download = taskSnapshot.getStorage().getDownloadUrl();
               final DatabaseReference newData2 = databaseReference2.push();
               key2 = databaseReference2.push().getKey();
               databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       newData2.child("ID").setValue(key2);
                       newData2.child("firstName").setValue(firstname.getText().toString());
                       newData2.child("secondName").setValue(lastname.getText().toString());
                       newData2.child("adress").setValue(personalAdress.getText().toString());
                       newData2.child("CIN").setValue(CinCHIFOR.getText().toString());
                       newData2.child("NUM").setValue(NumTAXI.getText().toString());
                       newData2.child("Counteur").setValue(p);
                       newData2.child("image").setValue(download.toString());
                       gProgress.dismiss();
                       Toast.makeText(getApplicationContext(), "تم التسجيل بنحاح\uD83E\uDD29", Toast.LENGTH_SHORT).show();
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) { Toast.makeText(getApplicationContext(),
                               "نواجه مشكل في التواصل\uD83D\uDE14", Toast.LENGTH_LONG).show(); }
               });
            });
        }catch (Exception e){

        }

    }
*/
}
