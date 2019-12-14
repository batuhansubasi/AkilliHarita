package com.batuhansubasi.akilliharita;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    public EditText email, parola, yeniParola;
    public Button sifreDegistir;
    public RadioGroup yolcuSurucu;
    public RadioButton yolcu, surucu, seciliRadioButton;
    public ProgressDialog bilgiEkrani;
    public TextView anaMenuyeDonus;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        email = (EditText)findViewById(R.id.etemail);
        parola = (EditText)findViewById(R.id.etparola);
        yeniParola = (EditText)findViewById(R.id.etyeniParola);
        sifreDegistir = (Button)findViewById(R.id.bsifreDegistir);
        yolcuSurucu = (RadioGroup)findViewById(R.id.rgyolcuSurucu);
        yolcu = (RadioButton)findViewById(R.id.rbyolcu);
        surucu = (RadioButton)findViewById(R.id.rbsurucu);
        anaMenuyeDonus = (TextView)findViewById(R.id.tvanaMenuyeDonus);
        bilgiEkrani = new ProgressDialog(this);
        findViewById(R.id.bsifreDegistir).setOnClickListener(this);
        findViewById(R.id.tvanaMenuyeDonus).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bsifreDegistir:
                sifreDegistir();
                break;
            case R.id.tvanaMenuyeDonus:
                startActivity(new Intent(this, LoginActivity.class));
        }
    }
    public void sifreDegistir() {
        String emailSifreDegistir = email.getText().toString().trim();
        String parolaSifreDegistir = parola.getText().toString().trim();
        final String yeniParolaSifreDegistir = yeniParola.getText().toString().trim();
        boolean veriDogrulugu = true;
        if(TextUtils.isEmpty(emailSifreDegistir)) {
            Toast.makeText(getApplicationContext(), "Kullanıcı adı boş olamaz", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(TextUtils.isEmpty(parolaSifreDegistir)) {
            Toast.makeText(getApplicationContext(), "Parola boş olamaz", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(TextUtils.isEmpty(yeniParolaSifreDegistir)) {
            Toast.makeText(getApplicationContext(), "Yeni parola boş olamaz", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(parolaSifreDegistir.length() < 6) {
            Toast.makeText(getApplicationContext(), "Parola 6 haneden daha az olamaz", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(yeniParolaSifreDegistir.length() < 6) {
            Toast.makeText(getApplicationContext(), "Yeni parola 6 haneden daha az olamaz", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(veriDogrulugu == true) {
            seciliRadioButton = findViewById(yolcuSurucu.getCheckedRadioButtonId());
            if(seciliRadioButton.getText().equals("Yolcu")) {
                emailSifreDegistir = emailSifreDegistir + "@yolcu.com";
            }
            else {
                emailSifreDegistir = emailSifreDegistir + "@surucu.com";
            }
            bilgiEkrani.setMessage("Parola degiştiriliyor.");
            bilgiEkrani.show();
            mAuth = FirebaseAuth.getInstance();
            final AuthCredential Credential = EmailAuthProvider.getCredential(emailSifreDegistir, parolaSifreDegistir);
            mAuth.signInWithEmailAndPassword(emailSifreDegistir, parolaSifreDegistir)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                final FirebaseUser user = mAuth.getCurrentUser();
                                user.reauthenticate(Credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    user.updatePassword(yeniParolaSifreDegistir).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                bilgiEkrani.hide();
                                                                Toast.makeText(getApplicationContext(), "Parola değiştirme işlemi başarılı.", Toast.LENGTH_LONG).show();
                                                                startActivity(new Intent(ChangePassword.this, LoginActivity.class));
                                                            }
                                                            else {
                                                                bilgiEkrani.hide();
                                                                Toast.makeText(getApplicationContext(), "Parola değiştirme işlemi başarısız.", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            } else {
                                bilgiEkrani.hide();
                                Toast.makeText(getApplicationContext(), "Hesabınıza bağlanırken bir sıkıntı oluştu.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
