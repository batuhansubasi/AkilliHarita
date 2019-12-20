package com.batuhansubasi.akilliharita;

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
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class AracBilgileriGuncelle extends AppCompatActivity implements View.OnClickListener {

    //tanimlamalar
    private ListView list;
    private String secilenMarka;
    private ArrayAdapter<String> adapter;
    private EditText adSoyad, model, plaka;
    private Button guncelle;
    private String[] araba_markalari = {"Alfa Romeo","Anadolu","Aston Martin","Audi","Bentley","BMW","Bugatti","Buick","Cadillac","Caterham","Chery","Chevrolet","Chrysler","Citroën","Dacia" ,"Daewoo" ,"Daihatsu" ,"Dodge" ,"Ferrari" ,"Fiat" ,"Ford" ,"Geely","Honda","Hyundai","Ikco","Infiniti","Isuzu","Jaguar","Kia","Lada","Lamborghini","Lancia","Lexus","Lincoln","Lotus","Maserati","Mazda","McLaren","Mercedes - Benz","MG","Mini","Mitsubishi","Nissan","Opel","Peugeot","Pontiac","Porsche","Proton","Renault","Rolls-Royce","Rover","Seat","Skoda","Subaru","Suzuki","Tata","Tesla","Tofaş","Toyota","Volkswagen","Volvo" };
    private FirebaseFirestore db;
    private DocumentReference noteRef;
    private double puan;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_car_info);

        //bu tasarım olarak güzel duruyor, klavye açıldığı zaman layout kaymıyor
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //activity values
        adSoyad      = (EditText)    findViewById(R.id.adSoyad_t);
        model        = (EditText)    findViewById(R.id.model_t);
        plaka         = (EditText)    findViewById(R.id.plaka_t);
        guncelle     = (Button)      findViewById(R.id.guncelle);
        list         = (ListView)    findViewById(R.id.marka_t);

        //basılabilenler
        guncelle.setOnClickListener(this);

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

        //giris yapilan mail adresi
        String girisYapilanMail = getIntent().getStringExtra("EXTRA_SESSION_ID");

//email adresine göre arac bilgilerinin firestoredan çekilmesi...
        noteRef = db.collection("CarInfos").document(girisYapilanMail);
        noteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    int markaID = 0;

                    //markanın ID' sinin alınması
                    for (int i = 0; i < araba_markalari.length; i++){
                        if(araba_markalari[i].equals(documentSnapshot.getString("marka"))){
                            markaID = i;
                            break;
                        }
                    }

                    //belge bulunduysa
                    list.setSelection(markaID);
                    adSoyad.setText(documentSnapshot.getString("adSoyad"));
                    model.setText(documentSnapshot.getString("model"));
                    plaka.setText(documentSnapshot.getString("plaka"));
                    puan = documentSnapshot.getDouble("puan");

                } else {
                    Toast.makeText(getApplicationContext(), "Arac bilgileri gelmedi. Kayıt olurken girilmemis!...", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Bir hata var. Veriler gelmedi!...", Toast.LENGTH_LONG).show();
                return;
            }
        });

    }

    @Override
    public void onClick(View view) {
        //degistir butonuna basıldiysa, firebase işlemlerinin başlamasi
        if (view == guncelle){
            changeCarInformation();
        }
    }

    public void changeCarInformation(){
        String secilenAdSoyad          = adSoyad.getText().toString().trim();
        String secilenModel            = model.getText().toString().trim();
        String secilenPlaka            = plaka.getText().toString().trim();
        String secilenKullanıcıAdıMail = getIntent().getStringExtra("EXTRA_SESSION_ID");

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
        note.put("puan" , puan);

        db.collection("CarInfos").document(secilenKullanıcıAdıMail).set(note, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Basarili!...", Toast.LENGTH_LONG).show();
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

