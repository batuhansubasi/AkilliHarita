package com.batuhansubasi.akilliharita;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText kayitKullanıcıAdı, kayitSifre1, kayitSifre2;
    private Button kayitOlButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private RadioGroup rg1;
    private RadioButton yolcusecim, surucusecim, rb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //yetkilendirme tarafi
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        //yolcu ve surucu radiobuttonlarının kod icerisinde atanmasi
        rg1        = (RadioGroup)  findViewById(R.id.radioGroup);
        yolcusecim = (RadioButton) findViewById(R.id.radioButton);
        surucusecim = (RadioButton) findViewById(R.id.radioButton2);

        //kullanici adi, sifre gibi bilgilerin degiskenlerce tanimlanmasi
        kayitKullanıcıAdı = (EditText) findViewById(R.id.registerName);
        kayitSifre1       = (EditText) findViewById(R.id.registerPassword);
        kayitSifre2       = (EditText) findViewById(R.id.registerPassword2);
        kayitOlButton     = (Button)   findViewById(R.id.buttonregister);

        kayitOlButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        //kayit ol butonuna basıldiysa, firebase işlemlerinin başlamasi
        if (view == kayitOlButton){
            registerUser();
        }
    }

    private void registerUser() {
        String kullaniciAdi = kayitKullanıcıAdı.getText().toString().trim();
        String sifre        = kayitSifre1.getText().toString().trim();
        String gecici       = kayitSifre2.getText().toString().trim();

        //girilen ifadelerin düzgünlük kontrolü
        if(TextUtils.isEmpty(kullaniciAdi)){
            Toast.makeText(getApplicationContext(), "Kullanıcı Adı Boş!...", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(sifre)){
            Toast.makeText(getApplicationContext(), "Sifre Bos!...", Toast.LENGTH_LONG).show();
            return;
        }
        if(!sifre.equalsIgnoreCase(gecici)){
            Toast.makeText(getApplicationContext(), "İki sifre ayni olmalidir!...", Toast.LENGTH_LONG).show();
            return;
        }
        if(sifre.length() < 6){
            Toast.makeText(getApplicationContext(), "Sifre 6 karakterden az olamaz!...", Toast.LENGTH_LONG).show();
            return;
        }

        //yolcu ve surucu seçimine göre authentication ayrımının yapilmasi
        int radiobuttonid = rg1.getCheckedRadioButtonId();
        rb = findViewById(radiobuttonid);
        if(rb.getText().equals("Yolcu")){
            kullaniciAdi = kullaniciAdi + "@yolcu.com";
        } else {
            kullaniciAdi = kullaniciAdi + "@surucu.com";
        }

        progressDialog.setMessage("Kullanici Kaydediliyor...");
        progressDialog.show();

        //kullanici adi ve sifreyle kullanıcının firebase kaydedilmesi
        firebaseAuth.createUserWithEmailAndPassword(kullaniciAdi,sifre).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Kaydedildi", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                } else {
                    Toast.makeText(getApplicationContext(), "Kayit Yapilamadi!...", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }
            }
        });


    }



}
