package com.TaxiSghira.TreeProg.plashscreen.Authentication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.Room.FireBaseClient;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;


public class phoneTest extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private EditText editText, N1, N2, N3, N4, N5, N6;
    private Button sendCodeButton;
    private Button phoneDone;
    private Button ResendCode;
    private TextView ChangePhoneNumber;
    private LinearLayout FirstLayout,SecondLayout;
    private String phone;
    private CountryCodePicker countryCodePicker;
    private View root;
    private String Code;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {
            enableSecondLayout();
            EnablePhoneDoneButton();
            Code = phoneAuthCredential.getSmsCode();
        }

        @Override
        public void onVerificationFailed(@NotNull FirebaseException e) {
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(getActivity(), getString(R.string.ProblemConnectionWithYourPhone), Toast.LENGTH_SHORT).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(getActivity(), getString(R.string.TooManyRequest), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCodeSent(@NotNull String s, @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mResendToken = forceResendingToken;
            Timber.e(s);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.phone_test_fragment, container, false);
        view(root);
        checkForSmsPermission();
        enableFirstLayout();
        
        ChangePhoneNumber.setOnClickListener(v3 -> {
            enableFirstLayout();
            sendCodeButton.setVisibility(View.VISIBLE);
            phoneDone.setVisibility(View.GONE);
        });
        sendCodeButton.setOnClickListener(v -> sendVerificationCode());
        ResendCode.setOnClickListener(v -> resendVerificationCode(phone,mResendToken));
        phoneDone.setOnClickListener(v4 -> verifySignInCode());
        
        return root;
    }
    
    //**** UI Manipulation
    public void view(View root){
        N1 = root.findViewById(R.id.N1);
        N2 = root.findViewById(R.id.N2);
        N3 = root.findViewById(R.id.N3);
        N4 = root.findViewById(R.id.N4);
        N5 = root.findViewById(R.id.N5);
        N6 = root.findViewById(R.id.N6);
        ChangePhoneNumber = root.findViewById(R.id.ChangePhoneNumber);
        sendCodeButton = root.findViewById(R.id.sendCodeButton);
        SecondLayout = root.findViewById(R.id.Secondlayout);
        Button makeCall = root.findViewById(R.id.makeCall);
        FirstLayout = root.findViewById(R.id.firstLayout);
        ResendCode = root.findViewById(R.id.resendCode);
        countryCodePicker = root.findViewById(R.id.ccp);
        phoneDone = root.findViewById(R.id.phonedone);
        editText = root.findViewById(R.id.phoneText);
        makeCall.setEnabled(false);
    }
    
    private boolean validateForm() {
        boolean valid = true;

        String email1 = editText.getText().toString();
        if (TextUtils.isEmpty(email1)) {
            editText.setError(getString(R.string.FillEveryPlace));
            valid = false;
        } else {
            editText.setError(null);
        }
        if (email1.length() != 9) {
            editText.setError(getString(R.string.putYourPhoneNumber));
            valid = false;
        } else {
            editText.setError(null);
        }
        return valid;
    }

    private boolean validateForm2() {
        boolean valid = true;

        String email1 = N1.getText().toString();
        if (TextUtils.isEmpty(email1)) {
            N1.setError(getString(R.string.FillEveryPlace));
            valid = false;
        } else {
            N1.setError(null);
        }
        String email2 = N2.getText().toString();
        if (TextUtils.isEmpty(email2)) {
            N2.setError(getString(R.string.FillEveryPlace));
            valid = false;
        } else {
            N2.setError(null);
        }
        String email3 = N3.getText().toString();
        if (TextUtils.isEmpty(email3)) {
            N3.setError(getString(R.string.FillEveryPlace));
            valid = false;
        } else {
            N3.setError(null);
        }
        String email4 = N4.getText().toString();
        if (TextUtils.isEmpty(email4)) {
            N4.setError(getString(R.string.FillEveryPlace));
            valid = false;
        } else {
            N4.setError(null);
        }
        String email5 = N5.getText().toString();
        if (TextUtils.isEmpty(email5)) {
            N5.setError(getString(R.string.FillEveryPlace));
            valid = false;
        } else {
            N5.setError(null);
        }
        String email6 = N6.getText().toString();
        if (TextUtils.isEmpty(email6)) {
            N6.setError(getString(R.string.FillEveryPlace));
            valid = false;
        } else {
            N6.setError(null);
        }
        return valid;
    }

    private void disableSmsButton() {
        Toast.makeText(getContext(), R.string.sms_disabled, Toast.LENGTH_LONG).show();
        sendCodeButton.setVisibility(View.GONE);
        phoneDone.setVisibility(View.GONE);
    }

    private void enableSecondLayout() {
        FirstLayout.setVisibility(View.GONE);
        SecondLayout.setVisibility(View.VISIBLE);
    }

    private void enableFirstLayout() {
        FirstLayout.setVisibility(View.VISIBLE);
        SecondLayout.setVisibility(View.GONE);
    }

    private void EnablePhoneDoneButton() {
        sendCodeButton.setVisibility(View.GONE);
        phoneDone.setVisibility(View.VISIBLE);
    }
    
    //******core
    private void sendVerificationCode() {
        if (!validateForm()) return;
        phone = "+" + countryCodePicker.getSelectedCountryCode() + editText.getText().toString();
        PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder(FireBaseClient.getFireBaseClient().getFirebaseAuth())
                .setPhoneNumber(phone)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(mCallbacks)
                .build());
        enableSecondLayout();
        EnablePhoneDoneButton();
    }
    
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.verifyPhoneNumber(
                PhoneAuthOptions.newBuilder(FireBaseClient.getFireBaseClient().getFirebaseAuth())
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(mCallbacks)
                .setForceResendingToken(token)
                .build());
    }

    private void verifySignInCode() {
        if (!validateForm2()) return;
        String txt = "" + N1.getText().toString() + "" + N2.getText().toString() + "" + N3.getText().toString() + "" + N4.getText().toString() + "" + N5.getText().toString() + "" + N6.getText().toString();
        if (txt.equals(Code)) {
            Toast.makeText(getActivity(), getString(R.string.VerificationPhoneSucced), Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString("phone", phone);
            Navigation.findNavController(root).navigate(R.id.infoTest, bundle);
        } else {
            // TODO: 2020-12-09 Delete THis block later att the end
            Bundle bundle = new Bundle();
            bundle.putString("phone", phone);
            Navigation.findNavController(root).navigate(R.id.infoTest, bundle);
            Toast.makeText(getActivity(),getString(R.string.PleaseEntreTheCorrectCode), Toast.LENGTH_SHORT).show();
        }
    }
    
    //**** Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String @NotNull [] permissions, @NotNull int @NotNull [] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (permissions[0].equalsIgnoreCase(Manifest.permission.SEND_SMS)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendCodeButton.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), getString(R.string.failure_permission), Toast.LENGTH_LONG).show();
                disableSmsButton();
            }
        }
    }

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            sendCodeButton.setVisibility(View.VISIBLE);
        } else {
            phoneDone.setVisibility(View.GONE);
        }
    }
}
