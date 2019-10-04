
package com.TaxiSghira.TreeProg.plashscreen.Operation;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

    public static void Signout(){auth.signOut(); }

    public static void AddFAvor(DatabaseReference databaseReference, FirebaseAuth mAuth, String Name, String Chh_Num, int taxinum){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = mAuth.getUid();
                DatabaseReference newPost2 = databaseReference.push();
                newPost2.child("id").setValue(id);
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

    public static void AddUser(DatabaseReference databaseReference, FirebaseAuth mAuth, String first, String second, String adres, String tel){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = mAuth.getUid();
                DatabaseReference newPost2 = databaseReference.push();
                newPost2.child("ID").setValue(id);
                newPost2.child("firstName").setValue(first);
                newPost2.child("secondName").setValue(second);
                newPost2.child("adress").setValue(adres);
                newPost2.child("tell").setValue(tel);
                Toast.makeText(getApplicationContext(),"تمت الاضافة بنجاح\uD83D\uDE04",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void AddDemande(DatabaseReference databaseReference,String clientName,String arrive,double lnt,double along){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference NewOne = databaseReference.push();
                NewOne.child("ClientName").setValue(clientName);
                NewOne.child("Arrive").setValue(arrive);
                NewOne.child("Lnt").setValue(lnt);
                NewOne.child("Long").setValue(along);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getAccept(DatabaseReference databaseReference){
        List<Accept> acceptList = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    acceptList.clear();
                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                        Accept accept = dataSnapshot1.getValue(Accept.class);
                        acceptList.add(accept);
                    }
                    //Demande_Recycle.setAdapter(new Demande_Adpter(getApplicationContext(),acceptList));
                }else {
                    Toast.makeText(getApplicationContext(),"لايوجد اي طلب الان !!!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
