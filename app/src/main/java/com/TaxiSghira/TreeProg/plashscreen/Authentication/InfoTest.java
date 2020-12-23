package com.TaxiSghira.TreeProg.plashscreen.Authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.TaxiSghira.TreeProg.plashscreen.Room.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.ui.Map;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class InfoTest extends Fragment {

    private TextInputLayout FullName, city, email;
    private String phone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.info_test_fragment, container, false);

        FullName = root.findViewById(R.id.fullname);
        city = root.findViewById(R.id.city);
        email = root.findViewById(R.id.email);

        RxView.clicks(root.findViewById(R.id.nextInfo))
                .throttleFirst(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> {
                    if (!validateForm()) return;
                    assert getArguments() != null;
                    phone = getArguments().getString("phone");
                    Client client = new Client(Objects.requireNonNull(FireBaseClient.getFireBaseClient()
                            .getFirebaseAuth().getCurrentUser()).getUid(),
                            Objects.requireNonNull(FullName.getEditText()).getText().toString(),
                            phone
                            , Objects.requireNonNull(email.getEditText()).getText().toString()
                            , Objects.requireNonNull(city.getEditText()).getText().toString().toLowerCase());
                    AddData(client);
                }, Throwable::printStackTrace);
        return root;
    }

    void AddData(Client client) {
        try {
            FireBaseClient.getFireBaseClient()
                    .getFirebaseDatabase()
                    .getReference(Common.Client_DataBase_Table)
                    .push()
                    .setValue(client)
                    .addOnFailureListener(Timber::e);
            startActivity(new Intent(requireActivity(), Map.class));
            Toast.makeText(getApplicationContext(), getString(R.string.welcome), Toast.LENGTH_SHORT).show();
        } catch (Throwable throwable) {
            Timber.e(throwable);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(Objects.requireNonNull(FullName.getEditText()).getText().toString())) {
            FullName.setError(getString(R.string.FillEveryPlace));
            valid = false;
        } else {
            FullName.setError(null);
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(city.getEditText()).getText().toString())) {
            city.setError(getString(R.string.FillEveryPlace));
            valid = false;
        } else {
            city.setError(null);
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(email.getEditText()).getText().toString())) {
            email.setError(getString(R.string.FillEveryPlace));
            valid = false;
        } else {
            email.setError(null);
        }
        return valid;
    }
}
