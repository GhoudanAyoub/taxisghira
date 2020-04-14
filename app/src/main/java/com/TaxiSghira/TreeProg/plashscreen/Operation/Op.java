
package com.TaxiSghira.TreeProg.plashscreen.Operation;

import android.widget.Toast;

import androidx.annotation.NonNull;
import com.TaxiSghira.TreeProg.plashscreen.Module.Accept;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class Op {
    public Op() { }

    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static FirebaseUser user = auth.getCurrentUser();

    public static void AddFAvor(DatabaseReference databaseReference, String mAuth, String Name, String Chh_Num, String taxinum){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference newPost2 = databaseReference.push();
                newPost2.child("id").setValue(mAuth);
                newPost2.child("Ch_Name").setValue(Name);
                newPost2.child("Ch_num").setValue(Chh_Num);
                newPost2.child("Taxi_num").setValue(taxinum);
                Toast.makeText(getApplicationContext(),"تمت الاضافة بنجاح\uD83D\uDE04",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
