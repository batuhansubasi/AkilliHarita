package com.batuhansubasi.akilliharita;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class AracBilgileriEkle extends AppCompatActivity implements View.OnClickListener {

    //tanimlamalar
    private ListView list;
    private String secilenMarka;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter2;
    private EditText model, adSoyad, plaka;
    private Button kaydet;
    private TextView anaMenuyeDon;
    private String[] araba_markalari = {"Alfa Romeo","Anadolu","Aston Martin","Audi","Bentley","BMW","Bugatti","Buick","Cadillac","Caterham","Chery","Chevrolet","Chrysler","Citroën","Dacia" ,"Daewoo" ,"Daihatsu" ,"Dodge" ,"Ferrari" ,"Fiat" ,"Ford" ,"Geely","Honda","Hyundai","Ikco","Infiniti","Isuzu","Jaguar","Kia","Lada","Lamborghini","Lancia","Lexus","Lincoln","Lotus","Maserati","Mazda","McLaren","Mercedes - Benz","MG","Mini","Mitsubishi","Nissan","Opel","Peugeot","Pontiac","Porsche","Proton","Renault","Rolls-Royce","Rover","Seat","Skoda","Subaru","Suzuki","Tata","Tesla","Tofaş","Toyota","Volkswagen","Volvo" };
    private FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car_info);

        //bu tasarım olarak güzel duruyor, klavye açıldığı zaman layout kaymıyor
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //activity values
        plaka        = (EditText)    findViewById(R.id.plaka_t);
        model        = (EditText)    findViewById(R.id.model_t);
        adSoyad      = (EditText)    findViewById(R.id.adSoyad_t);
        kaydet       = (Button)      findViewById(R.id.kaydet);
        list         = (ListView)    findViewById(R.id.marka_t);
        anaMenuyeDon = (TextView)    findViewById(R.id.tvanaMenuyeDonus);

        //basılabilenler
        kaydet.setOnClickListener(this);
        anaMenuyeDon.setOnClickListener(this);

        //listbox 1.ye verilerin eklenmesi
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, araba_markalari);
        list.setAdapter(adapter);

        //secilen listbox'taki MARKANIN indexin alınması
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                secilenMarka = (String) (list.getItemAtPosition(position));
            }
        });

        //firestore
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View view) {
        //kayit ol butonuna basıldiysa, firebase işlemlerinin başlamasi
        if (view == kaydet){
            saveCarInformation();
        }
        else if(view == anaMenuyeDon) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void saveCarInformation(){
        String secilenModel            = model.getText().toString().trim();
        String secilenAdSoyad          = adSoyad.getText().toString().trim();
        String secilenPlaka            = plaka.getText().toString().trim();
        final String secilenKullanıcıAdıMail = getIntent().getStringExtra("EXTRA_SESSION_ID");

        if(TextUtils.isEmpty(secilenMarka)){
            Toast.makeText(getApplicationContext(), "Marka Bilgisi Bos!...", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(secilenModel)){
            Toast.makeText(getApplicationContext(), "Model Bilgisi Bos!...", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(secilenAdSoyad)){
            Toast.makeText(getApplicationContext(), "Ad Soyad Bilgisi Bos!...", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(secilenPlaka)){
            Toast.makeText(getApplicationContext(), "Plaka Bilgisi Bos!...", Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(secilenKullanıcıAdıMail)){
            Toast.makeText(getApplicationContext(), "Mail bilgisi gelmedi. Bir hata var!...", Toast.LENGTH_LONG).show();
            return;
        }

        //bütün kontrollerden geçtiyse...
        Map<String, Object> note = new HashMap<>();
        note.put("adSoyad", secilenAdSoyad);
        note.put("marka", secilenMarka);
        note.put("model", secilenModel);
        note.put("plaka"  , secilenPlaka);
        note.put("puan" , 0);

        db.collection("CarInfos").document(secilenKullanıcıAdıMail).set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Basarili!...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AracBilgileriEkle.this, SurucuActivity.class);
                intent.putExtra("EXTRA_SESSION_ID", secilenKullanıcıAdıMail);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Bir hata var. Veri yazilamadi!...", Toast.LENGTH_LONG).show();
                        return;
                    }
                });
    }
}

