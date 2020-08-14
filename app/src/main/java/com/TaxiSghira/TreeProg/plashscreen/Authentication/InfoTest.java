package com.TaxiSghira.TreeProg.plashscreen.Authentication;

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

import com.TaxiSghira.TreeProg.plashscreen.API.FireBaseClient;
import com.TaxiSghira.TreeProg.plashscreen.Client.Map;
import com.TaxiSghira.TreeProg.plashscreen.Commun.Common;
import com.TaxiSghira.TreeProg.plashscreen.Module.Client;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding3.view.RxView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import kotlin.Unit;
import timber.log.Timber;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class InfoTest extends Fragment {

    private TextInputLayout fullname,  city, email;
    private CompositeDisposable disposable = new CompositeDisposable();
    private String phone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.info_test_fragment, container, false);

        fullname = root.findViewById(R.id.fullname);
        city = root.findViewById(R.id.city);
        email = root.findViewById(R.id.email);

        RxView.clicks(root.findViewById(R.id.nextInfo))
                .throttleFirst(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Unit>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(Unit unit) {
                        if (!validateForm()) return;
                        assert getArguments() != null;
                        phone = getArguments().getString("phone");
                        AddData(Objects.requireNonNull(fullname.getEditText()).getText().toString(), phone,
                                Objects.requireNonNull(city.getEditText()).getText().toString(),
                                Objects.requireNonNull(email.getEditText()).getText().toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);
                    }

                    @Override
                    public void onComplete() { }
                });

        return root;
    }

    void AddData(String fullname, String phone, String city, String email) {
        Client client = new Client(FireBaseClient.getFireBaseClient()
                .getFirebaseAuth().getCurrentUser().getUid(),fullname,phone,email,city.toLowerCase());
        FireBaseClient.getFireBaseClient()
                .getFirebaseDatabase()
                .getReference(Common.Client_DataBase_Table)
                .push()
                .setValue(client)
                .addOnFailureListener(Timber::e)
                .addOnSuccessListener(v->{
                    startActivity(new Intent(getApplicationContext(), Map.class));
                    Toast.makeText(getApplicationContext(),"أهلا بك",Toast.LENGTH_SHORT).show();});
    }
    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(Objects.requireNonNull(fullname.getEditText()).getText().toString())) {
            fullname.setError("املأ الفراغ من فضلك!!");
            valid = false;
        } else {
            fullname.setError(null);
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(city.getEditText()).getText().toString())) {
            city.setError("املأ الفراغ من فضلك!!");
            valid = false;
        } else {
            city.setError(null);
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(email.getEditText()).getText().toString())) {
            email.setError("املأ الفراغ من فضلك!!");
            valid = false;
        } else {
            email.setError(null);
        }
        return valid;
    }
}
