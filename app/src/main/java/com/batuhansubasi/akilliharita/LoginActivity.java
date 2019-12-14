package com.batuhansubasi.akilliharita;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button kayitButton, sifreDegistirButton, girisButton;
    private EditText loginName, loginPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private RadioGroup rg1;
    private RadioButton yolcusecim, surucusecim, rb;
    private String email;
    public final static String EXTRA_MESSAGE = "com.batuhansubasi.akilliharita.MESAJ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);

/*
        // Harita acilmasi icin
        Intent i = new Intent(getBaseContext(), YolcuActivity.class);
        startActivity(i);
*/      //startActivity(new Intent(this, YolcuActivity.class));

        //internet servisi açık mı değil mi kontrolü
        boolean connected = internet_kontrol();

        if (connected == false){
            progressDialog.setMessage("Uygulamayı kullanabilmek için lütfen internetinizi açınız!...");
            progressDialog.show();
        } else {

            //konum servisi açık mı değil mi kontrolü
            connected = isLocationEnabled(getApplicationContext());
            if (connected == false) {
                progressDialog.setMessage("Uygulamayı kullanabilmek için lütfen konum servisini açınız!...");
                progressDialog.show();
            }  else {

                    //yolcu ve surucu radiobuttonlarının kod icerisinde atanmasi
                    rg1 = (RadioGroup) findViewById(R.id.radioGroup);
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
                    sifreDegistirButton = (Button) findViewById(R.id.button3);
                    sifreDegistirButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openActivityChangePassword();
                        }
                    });

                    //ekranda girilen butondaki isim-sifre değerlerinin değişkenlere atilmasi
                    loginName = (EditText) findViewById(R.id.loginName);
                    loginPassword = (EditText) findViewById(R.id.loginPassword);

                    //giris butonunun degiskene atilmasi
                    girisButton = (Button) findViewById(R.id.btnLogin);
                    girisButton.setOnClickListener(this);
                }
            }
    }

    private boolean internet_kontrol() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else
            return false;
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
            email = kullaniciAdi;
            if (rb.getText().equals("Yolcu")) {
                kullaniciAdi = kullaniciAdi + "@yolcu.com";
            } else {
                kullaniciAdi = kullaniciAdi + "@surucu.com";
            }
            progressDialog.setMessage("Giriş Yapılıyor...");
            progressDialog.show();

            firebaseAuth.signInWithEmailAndPassword(kullaniciAdi, sifre).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Giriş Başarılı!...", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();

                        //giris basariliysa hangi sayfaya yönlendirileceği...
                        if (rb.getText().equals("Yolcu")) {
                            yolcuSayfasi();
                        } else {
                            surucuSayfasi();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Giriş Yapılamadı!...", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                    }
                }
            });
        }

    }

    private boolean kontrol(String kullaniciAdi, String sifre) {
        if(TextUtils.isEmpty(kullaniciAdi)){
            Toast.makeText(getApplicationContext(), "Kullanıcı Adı Boş!...", Toast.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(sifre)){
            Toast.makeText(getApplicationContext(), "Şifre Boş!...", Toast.LENGTH_LONG).show();
            return false;
        }

        if(sifre.length() < 6){
            Toast.makeText(getApplicationContext(), "Şifre 6 karakterden az olamaz!...", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void yolcuSayfasi(){
        Intent intent = new Intent(this, YolcuActivity.class);
        startActivity(intent);
    }

    public void surucuSayfasi(){
        Intent intent = new Intent(LoginActivity.this, SurucuActivity.class);
        intent.putExtra("EXTRA_SESSION_ID", email);
        startActivity(intent);
    }

    public static Boolean isLocationEnabled(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return  (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

}
