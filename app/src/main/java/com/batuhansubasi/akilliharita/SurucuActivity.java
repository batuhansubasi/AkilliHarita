package com.batuhansubasi.akilliharita;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SurucuActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST = 100;
    double enlem, boylam;
    private ProgressDialog progressDialog;
    private String email, surucuDurumu;
    private int haritaOdaklanmasiIcinKontrolDegiskeni = 0;
    private Button degistirButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surucu);

        degistirButton = (Button)findViewById(R.id.baracDegistir);
        degistirButton.setOnClickListener(this);

        email = getIntent().getStringExtra("EXTRA_SESSION_ID");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.harita);
        mapFragment.getMapAsync(this);
        izinKontrolleri();
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(enlem, boylam), 2));
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            // İzin islemleri.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            //Marker'lari ekler
            /*mMap.addMarker(new MarkerOptions().position(new LatLng(enlem, boylam))
                            .title("Burasi Konum1 ! "));*/
            // Zoom ekler.
            UiSettings uis = googleMap.getUiSettings();
            uis.setCompassEnabled(true);
            uis.setZoomControlsEnabled(true);
            //Konumumu aktiflestir.
            mMap.setMyLocationEnabled(true);
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(enlem, boylam), 5)); // Harita icerisinde zoom'u kontrol ediyor arttirdikca yakinlasir
    }
    //Konum servislerinin acik olup olmadiginin kontrolleri.
    public void izinKontrolleri() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            konumTakibiniBaslat();
        } else { Toast.makeText(getApplicationContext(), "Cihazın konum servislerine bağlanılamıyor.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }
    //Bu kisimda izinlerle ilgili.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            konumTakibiniBaslat();
        } else {
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }
    //Bu metodda surucunun 5 saniyede bir konumu hesaplanmakta ve firebase firecloud'a kaydedilmekte.
    private void konumGuncellemeTalebindeBulun() {
        LocationRequest request = new LocationRequest();
        request.setInterval(5000);//5 saniyede bir konumu tekrar hesaplar.
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> location = new HashMap<>();//Veri tabanina eklenen verinin yapisini olusturabilmek icin
                    //HashMap yapisi kullaniliyor.
                    location.put("enlem", locationResult.getLastLocation().getLatitude());//Yapiya enlem key'i ile enlem bilgisi eklenir.
                    location.put("boylam", locationResult.getLastLocation().getLongitude());//Yapiya boylam key'i ile boylam bilgisi eklenir.
                    location.put("surucu_Durumu", surucuDurumu);
                    db.collection("suruculer").document(email).set(location);//riders koleksiyonuna giriş yapan kullanicinin ismi ile bir dokuman eklenir.
                    enlem = locationResult.getLastLocation().getLatitude();//enlem degiskenine hesaplanan enlem degeri atanir.
                    boylam = locationResult.getLastLocation().getLongitude();//boylam degiskenine hesaplanan boylam degeri atanir.
                    if(haritaOdaklanmasiIcinKontrolDegiskeni == 0) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(enlem, boylam), 15));
                        haritaOdaklanmasiIcinKontrolDegiskeni = 1;
                    }
                }
            }, null);
        }
    }
    private void konumTakibiniBaslat() {
        Toast.makeText(this, "GPS izleme aktif edildi.", Toast.LENGTH_SHORT).show();
        konumGuncellemeTalebindeBulun();
    }
    public void surucununDurumunuDegistir(String durum) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference durumRef = db.collection("suruculer").document(email);
        durumRef.update("surucu_Durumu", durum);
    }
    @Override
    protected void onStart() {
        super.onStart();
        surucununDurumunuDegistir("cevrimici");
        surucuDurumu = "cevrimici";
    }
    @Override
    protected void onStop() {
        super.onStop();
        surucununDurumunuDegistir("cevrimdisi");
        surucuDurumu = "cevrimdisi";
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        surucununDurumunuDegistir("cevrimdisi");
        surucuDurumu = "cevrimdisi";
    }

    @Override
    public void onClick(View view) {
        if (view == degistirButton){ //Sürücünün araç bilgilerinin istenmesi

            //Login Activity' den gelen email bilgisini alıyorum...
            String secilenKullanıcıAdıMail = getIntent().getStringExtra("EXTRA_SESSION_ID");

            secilenKullanıcıAdıMail = secilenKullanıcıAdıMail + "@surucu.com";

            //AracBilgileriniDegistir Activity' sine email ile beraber yolluyorum.
            Intent intent = new Intent(SurucuActivity.this, AracBilgileriGuncelle.class);
            intent.putExtra("EXTRA_SESSION_ID", secilenKullanıcıAdıMail);
            startActivity(intent);
        }
    }
}