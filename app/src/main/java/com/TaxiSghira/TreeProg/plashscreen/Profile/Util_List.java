package com.TaxiSghira.TreeProg.plashscreen.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.TaxiSghira.TreeProg.plashscreen.Both.PersonalInfo;
import com.TaxiSghira.TreeProg.plashscreen.Both.PutPhone;
import com.TaxiSghira.TreeProg.plashscreen.Client.Map;
import com.TaxiSghira.TreeProg.plashscreen.Client.TopDriver;
import com.TaxiSghira.TreeProg.plashscreen.Operation.Op;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;

import de.hdodenhof.circleimageview.CircleImageView;

public class Util_List extends AppCompatActivity {

    TextView textViewName;
    CircleImageView circleImageViewClient;
    GoogleSignInApi googleSignInApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_util__list);
        circleImageViewClient = findViewById(R.id.circleImageViewClient);
        findViewById(R.id.imageView2back).setOnClickListener(v-> super.onBackPressed());
        findViewById(R.id.button3PN).setOnClickListener(v->startActivity(new Intent(getApplicationContext(), PersonalInfo.class)));
        findViewById(R.id.button6Stting).setOnClickListener(v->startActivity(new Intent(getApplicationContext(), ActivitySetting.class)));
        findViewById(R.id.buttonmap).setOnClickListener(v->startActivity(new Intent(getApplicationContext(), Map.class)));
        findViewById(R.id.button4TopDriver).setOnClickListener(v->startActivity(new Intent(getApplicationContext(), TopDriver.class)));
        findViewById(R.id.button8LogOut).setOnClickListener(v -> {
            Op.Signout();
            startActivity(new Intent(getApplicationContext(), PutPhone.class)); });
        findViewById(R.id.button7RuDriver).setOnClickListener(v->{
            try {
                getApplicationContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
            } catch (ActivityNotFoundException e) {
                getApplicationContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("" + getApplicationContext().getPackageName())));
            }});

        textViewName = findViewById(R.id.textViewName);
        try {
            textViewName.setText(PutPhone.name);
            Glide.with(getApplicationContext()).load(PutPhone.Image).centerCrop().into(circleImageViewClient);
        }catch(Exception e){ }

    }

    @Override
    public void onBackPressed() { }
}
