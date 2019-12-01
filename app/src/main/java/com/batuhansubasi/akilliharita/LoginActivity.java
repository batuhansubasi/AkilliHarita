package com.batuhansubasi.akilliharita;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button kayitButton, sifreDegistirButton, girisButton;
    private EditText loginName, loginPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private RadioGroup rg1;
    private RadioButton yolcusecim, surucusecim, rb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);

        //yolcu ve surucu radiobuttonlarının kod icerisinde atanmasi
        rg1        = (RadioGroup)  findViewById(R.id.radioGroup);
        yolcusecim = (RadioButton) findViewById(R.id.radioButton);
        surucusecim = (RadioButton) findViewById(R.id.radioButton2);

        //yetkilendirme tarafi
        firebaseAuth = FirebaseAuth.getInstance();

        //kayit olma ekrani
        kayitButton = (Button) findViewById(R.id.button2);
        kayitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityRegister();
            }
        });

        //sifre degistirme ekrani
        sifreDegistirButton = (Button)findViewById(R.id.button3);
        sifreDegistirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityChangePassword();
            }
        });

        //ekranda girilen butondaki isim-sifre değerlerinin değişkenlere atilmasi
        loginName = (EditText)findViewById(R.id.loginName);
        loginPassword = (EditText)findViewById(R.id.loginPassword);

        //giris butonunun degiskene atilmasi
        girisButton = (Button)findViewById(R.id.btnLogin);
        girisButton.setOnClickListener(this);

    }

    //kayit olma activitysinin açılması
    public void openActivityRegister(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //sifre degistirme activitysinin açılması
    public void openActivityChangePassword(){
        Intent intent = new Intent(this, ChangePassword.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view == girisButton){
            //giris butonuna basıldıysa...
            userLogin();
        }
    }

    private void userLogin() {
        String kullaniciAdi = loginName.getText().toString().trim();
        String sifre = loginPassword.getText().toString().trim();

        //genel kontrollerin yapilmasi
        boolean basarili = kontrol(kullaniciAdi, sifre);

        if (basarili != false) {
            //yolcu ve surucu seçimine göre authentication ayrımının yapilmasi
            int radiobuttonid = rg1.getCheckedRadioButtonId();
            rb = findViewById(radiobuttonid);
            if (rb.getText().equals("Yolcu")) {
                kullaniciAdi = kullaniciAdi + "@yolcu.com";
            } else {
                kullaniciAdi = kullaniciAdi + "@surucu.com";
            }

            progressDialog.setMessage("Giris Yapiliyor...");
            progressDialog.show();

            //kullanici adi ve sifreyle giris denenmesi
            firebaseAuth.signInWithEmailAndPassword(kullaniciAdi, sifre).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Giris Basarili!...", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();

                        //giris basariliysa hangi sayfaya yönlendirileceği...
                        if (rb.getText().equals("Yolcu")) {
                            yolcuSayfasi();
                        } else {
                            surucuSayfasi();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Giris Yapilamadi!...", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }
            });
        }

    }

    private boolean kontrol(String kullaniciAdi, String sifre) {
        if(TextUtils.isEmpty(kullaniciAdi)){
            Toast.makeText(getApplicationContext(), "Kullanici Adi Bos!...", Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(sifre)){
            Toast.makeText(getApplicationContext(), "Sifre Bos!...", Toast.LENGTH_LONG).show();
            return false;
        }

        if(sifre.length() < 6){
            Toast.makeText(getApplicationContext(), "Sifre 6 karakterden az olamaz!...", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void yolcuSayfasi(){
        Intent intent = new Intent(this, YolcuActivity.class);
        startActivity(intent);
    }

    public void surucuSayfasi(){
        Intent intent = new Intent(this, SurucuActivity.class);
        startActivity(intent);
    }

}
