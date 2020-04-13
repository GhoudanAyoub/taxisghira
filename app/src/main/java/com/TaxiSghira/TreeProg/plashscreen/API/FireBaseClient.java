package com.TaxiSghira.TreeProg.plashscreen.API;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseClient {
    private static FireBaseClient fireBaseClient;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth ;
    private FirebaseFirestore firebaseFirestore;
    private GoogleSignInAccount UserLogEdInAccount;

    private FireBaseClient() { }

    public DatabaseReference getDatabaseReference() {
        if (databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    public FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    public FirebaseFirestore getFirebaseFirestore() {
        if (firebaseFirestore == null){
            firebaseFirestore = FirebaseFirestore.getInstance();
        }
        return firebaseFirestore;
    }

    public GoogleSignInAccount getUserLogEdInAccount() {
        return UserLogEdInAccount;
    }

    public void setUserLogEdInAccount(GoogleSignInAccount userLogEdInAccount) {
        UserLogEdInAccount = userLogEdInAccount;
    }

    public static FireBaseClient getFireBaseClient() {
        if (fireBaseClient == null){
            fireBaseClient = new FireBaseClient();
        }
        return fireBaseClient;
    }


}
