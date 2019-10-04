package com.TaxiSghira.TreeProg.plashscreen.Both;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.TaxiSghira.TreeProg.plashscreen.Operation.Op;
import com.TaxiSghira.TreeProg.plashscreen.R;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;
public class GmailValidation extends AppCompatActivity {

    MaterialEditText editText;
    Button b1;
    private ProgressDialog gProgress;
    static String gmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_validation);

        editText = findViewById(R.id.editText);
        b1 = findViewById(R.id.siiir);
        findViewById(R.id.returnAnim).setOnClickListener(v->super.onBackPressed());
        b1.setOnClickListener(v->{ createAccount(editText.getText().toString()); });

        gProgress = new ProgressDialog(this);
    }

    private void createAccount(String email) {

        gProgress.setMessage("لقد ارسلنالكم رسالة في بريدكم الاكتروني المرجو التأكد منها ⌛️");
        gProgress.show();
        if (!validateForm()) return;
        Op.auth.createUserWithEmailAndPassword(email, "1234567").addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = Op.auth.getCurrentUser();
                        user.sendEmailVerification().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                gmail = email;
                                gProgress.dismiss();
                                new Handler().postDelayed(()->{
                                    Toast.makeText(getApplicationContext(),"تم التحقق من بريدك الاكتروني بنجاح\uD83D\uDE04",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),PersonalInfo.class));
                                },6000);
                            }
                        });
                    } else {
                        Toast.makeText(GmailValidation.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }
    private boolean validateForm() {
        boolean valid = true;

        String email1 = editText.getText().toString();
        if (TextUtils.isEmpty(email1)) {
            editText.setError("أدخل بريد إلكتروني صحيح من فضلك!");
            valid = false;
        } else {
            editText.setError(null);
        }

        return valid;
    }

    @Override
    public void onBackPressed() { }

}
