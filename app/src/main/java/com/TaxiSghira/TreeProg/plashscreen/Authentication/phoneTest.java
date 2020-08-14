package com.TaxiSghira.TreeProg.plashscreen.Authentication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.TaxiSghira.TreeProg.plashscreen.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


public class phoneTest extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private EditText editText, N1, N2, N3, N4, N5, N6;
    private String phone;
    private CountryCodePicker countryCodePicker;
    private View root;
    private String Code;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NotNull PhoneAuthCredential phoneAuthCredential) {
            disableLayout();
            enableVerificationButton();
            Code = phoneAuthCredential.getSmsCode();
        }

        @Override
        public void onVerificationFailed(@NotNull FirebaseException e) {
            Toast.makeText(getActivity(), "نواجه مشكل في التواصل مع هاتفكم\uD83D\uDE14", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NotNull String s, @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.phone_test_fragment, container, false);

        root.findViewById(R.id.sendButton).setVisibility(View.GONE);
        root.findViewById(R.id.NumberNext).setVisibility(View.GONE);
        editText = root.findViewById(R.id.phoneText);
        countryCodePicker = root.findViewById(R.id.ccp);
        N1 = root.findViewById(R.id.N1);
        N2 = root.findViewById(R.id.N2);
        N3 = root.findViewById(R.id.N3);
        N4 = root.findViewById(R.id.N4);
        N5 = root.findViewById(R.id.N5);
        N6 = root.findViewById(R.id.N6);

        root.findViewById(R.id.makeCall).setOnClickListener(v -> { });
        root.findViewById(R.id.ChangePhoneNumber).setOnClickListener(v3 -> {
            enableLayout();
            root.findViewById(R.id.sendButton).setVisibility(View.VISIBLE);
            root.findViewById(R.id.NumberNext).setVisibility(View.GONE);
        });
        root.findViewById(R.id.sendButton).setOnClickListener(v -> sendVerificationCode());
        root.findViewById(R.id.resendCode).setOnClickListener(v -> sendVerificationCode());
        root.findViewById(R.id.NumberNext).setOnClickListener(v4 -> verifySignInCode());

        checkForSmsPermission();
        enableLayout();

        return root;
    }

    //**** Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (permissions[0].equalsIgnoreCase(Manifest.permission.SEND_SMS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                root.findViewById(R.id.sendButton).setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), getString(R.string.failure_permission), Toast.LENGTH_LONG).show();
                disableSmsButton();
            }
        }
    }

    private void checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            root.findViewById(R.id.sendButton).setVisibility(View.VISIBLE);
        } else {
            root.findViewById(R.id.sendButton).setVisibility(View.VISIBLE);
            root.findViewById(R.id.NumberNext).setVisibility(View.GONE);
        }
    }

    //**** UI Manipulation
    private boolean validateForm() {
        boolean valid = true;

        String email1 = editText.getText().toString();
        if (TextUtils.isEmpty(email1)) {
            editText.setError("املأ الفراغ من فضلك!!");
            valid = false;
        } else {
            editText.setError(null);
        }
        if (email1.length() != 9) {
            editText.setError("ضع رقم هاتف صحيح من فضلك!!");
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

    private void disableSmsButton() {
        Toast.makeText(getContext(), R.string.sms_disabled, Toast.LENGTH_LONG).show();
        root.findViewById(R.id.sendButton).setVisibility(View.GONE);
        root.findViewById(R.id.NumberNext).setVisibility(View.GONE);
    }

    private void disableLayout() {
        root.findViewById(R.id.linearLayout4).setVisibility(View.GONE);
        root.findViewById(R.id.ln00).setVisibility(View.VISIBLE);
    }

    private void enableLayout() {
        root.findViewById(R.id.linearLayout4).setVisibility(View.VISIBLE);
        root.findViewById(R.id.ln00).setVisibility(View.GONE);
    }

    private void enableVerificationButton() {
        root.findViewById(R.id.sendButton).setVisibility(View.GONE);
        root.findViewById(R.id.NumberNext).setVisibility(View.VISIBLE);
    }
//**********************************************************************
    private void sendVerificationCode() {
        if (!validateForm()) return;
        phone = "+" + countryCodePicker.getSelectedCountryCode() + editText.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                30,
                TimeUnit.SECONDS,
                requireActivity(),
                mCallbacks);
    }

    private void verifySignInCode() {
        if (!validateForm2()) return;
        String txt = "" + N1.getText().toString() + "" + N2.getText().toString() + "" + N3.getText().toString() + "" + N4.getText().toString() + "" + N5.getText().toString() + "" + N6.getText().toString();
        if (txt.equals(Code)) {
            Toast.makeText(getActivity(), "تم التحقح من رقمك بنجاح\uD83D\uDE04", Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putString("phone", phone);
            Navigation.findNavController(root).navigate(R.id.infoTest, bundle);
        } else {
            //will be Removed after
            Bundle bundle = new Bundle();
            bundle.putString("phone", phone);
            Navigation.findNavController(root).navigate(R.id.infoTest, bundle);
            Toast.makeText(getActivity(), "المرجو ادخال الارقام الاربعة الصحيحة!"+Code, Toast.LENGTH_SHORT).show();
        }
    }
}
