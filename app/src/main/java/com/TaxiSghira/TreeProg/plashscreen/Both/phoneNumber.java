package com.TaxiSghira.TreeProg.plashscreen.Both;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.TaxiSghira.TreeProg.plashscreen.R;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class phoneNumber extends AppCompatActivity {


    EditText editText;
    String codeSent;
    static String tel;
    CountryCodePicker countryCodePicker;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private ProgressDialog gProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        LottieAnimationView returnbutton = findViewById(R.id.returnAnim);

        gProgress = new ProgressDialog(this);
        editText =  findViewById(R.id.phoneText);
        findViewById(R.id.sendButton).setOnClickListener(v->sendVerificationCode());

        returnbutton.setOnClickListener(v->startActivity(new Intent(getApplicationContext(), Auth.class)));
        checkForSmsPermission();
        countryCodePicker = findViewById(R.id.ccp);
    }
    private boolean validateForm() {
        boolean valid = true;

        String email1 = editText.getText().toString();
        if (TextUtils.isEmpty(email1)) {
            editText.setError("املأ الفراغ من فضلك!!");
            valid = false;
        } else {
            editText.setError(null);
        }
        if (email1.length()!=9){
            editText.setError("ضع رقم هاتف صحيح من فضلك!!");
            valid = false;
        }else {
            editText.setError(null);
        }
        return valid;
    }

    //GetPermetion:
    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        } else {
            enableSmsButton();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (permissions[0].equalsIgnoreCase(Manifest.permission.SEND_SMS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableSmsButton();
            } else {
                Toast.makeText(this, getString(R.string.failure_permission), Toast.LENGTH_LONG).show();
                disableSmsButton();
            }
        }
    }
    private void disableSmsButton() {
        Toast.makeText(this, R.string.sms_disabled, Toast.LENGTH_LONG).show();
        Button smsButton =  findViewById(R.id.sendButton);
        smsButton.setVisibility(View.INVISIBLE);
    }
    void enableSmsButton() {
        Button smsButton = findViewById(R.id.sendButton);
        smsButton.setVisibility(View.VISIBLE);
    }
    //Firebase Verification  number
    private void sendVerificationCode(){


        gProgress.setMessage("المرجو الانتظار قليلا ⌛️");
        if (!validateForm()) return;
        gProgress.show();
        String phone = "+"+countryCodePicker.getSelectedCountryCode()+editText.getText().toString();
        tel = phone;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                30,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
        /*
        if ( Op.auth.getCurrentUser().getPhoneNumber().equalsIgnoreCase("+"+countryCodePicker.getSelectedCountryCode()+editText.getText().toString())){
            startActivity(new Intent(getApplicationContext(), Map.class));
        }else {
        }*/
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            gProgress.dismiss();
            startActivity(new Intent(getApplicationContext(), NumberVerification.class).putExtra("tel",tel));
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
    public void onBackPressed() {
        super.onBackPressed();
    }

}
