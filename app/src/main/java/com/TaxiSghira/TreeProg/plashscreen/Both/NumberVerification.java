package com.TaxiSghira.TreeProg.plashscreen.Both;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Client.Map;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class NumberVerification extends AppCompatActivity {

    EditText N1,N2,N3,N4,N5,N6;
    String txt,codeSent,destinationAddress;
    String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_verification);

        destinationAddress = getIntent().getStringExtra("tel");
        N1= findViewById(R.id.N1);
        N2= findViewById(R.id.N2);
        N3= findViewById(R.id.N3);
        N4= findViewById(R.id.N4);
        N5= findViewById(R.id.N5);
        N6= findViewById(R.id.N6);

        findViewById(R.id.resendCode).setOnClickListener(v->sendVerificationCode());
        findViewById(R.id.makeCall).setOnClickListener(v->{});
        findViewById(R.id.returnAnim).setOnClickListener(v2->startActivity(new Intent(getApplicationContext(),phoneNumber.class)));
        findViewById(R.id.NumberNext).setOnClickListener(v4->verifySignInCode());
        findViewById(R.id.ChangePhoneNumber).setOnClickListener(v3->startActivity(new Intent(getApplicationContext(),phoneNumber.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    private boolean validateForm() {
        boolean valid = true;

        String email1 = N1.getText().toString();
        if (TextUtils.isEmpty(email1)) {
            N1.setError("املأ الفراغ من فضلك!!");
            valid = false;
        } else {
            N1.setError(null);
        }
        String email2 = N2.getText().toString();
        if (TextUtils.isEmpty(email2)) {
            N2.setError("املأ الفراغ من فضلك!!");
            valid = false;
        } else {
            N2.setError(null);
        }
        String email3 = N3.getText().toString();
        if (TextUtils.isEmpty(email3)) {
            N3.setError("املأ الفراغ من فضلك!!");
            valid = false;
        } else {
            N3.setError(null);
        }
        String email4 = N4.getText().toString();
        if (TextUtils.isEmpty(email4)) {
            N4.setError("املأ الفراغ من فضلك!!");
            valid = false;
        } else {
            N4.setError(null);
        }
        String email5 = N5.getText().toString();
        if (TextUtils.isEmpty(email5)) {
            N5.setError("املأ الفراغ من فضلك!!");
            valid = false;
        } else {
            N5.setError(null);
        }
        String email6 = N6.getText().toString();
        if (TextUtils.isEmpty(email6)) {
            N6.setError("املأ الفراغ من فضلك!!");
            valid = false;
        } else {
            N6.setError(null);
        }
        return valid;
    }

    //VerifyCode:::
    private void verifySignInCode(){
        if (!validateForm())return;
        txt = ""+N1.getText().toString()+""+N2.getText().toString()+""+N3.getText().toString()+""+N4.getText().toString()+""+N5.getText().toString()+""+N6.getText().toString();
        try{
            startActivity(new Intent(getApplicationContext(),Map.class));
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent,txt);
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FireBaseClient.getFireBaseClient().getFirebaseAuth().signInWithCredential(credential).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "تم التحقح من رقمك بنجاح\uD83D\uDE04", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), Map.class));
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(),"المرجو ادخال الارقام الاربعة الصحيحة!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //resend Code :::
    private void sendVerificationCode(){
        N1.setText("");N2.setText("");N3.setText("");N4.setText("");N5.setText("");N6.setText("");
        phone = "+"+phoneNumber.tel;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                30,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), "نواجه مشكل في التواصل مع هاتفكم\uD83D\uDE14", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }
    };


    @Override
    public void onBackPressed() { }
}
