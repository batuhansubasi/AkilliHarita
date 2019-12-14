package com.batuhansubasi.akilliharita;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class YolcuActivity extends FragmentActivity implements OnMapReadyCallback/*, View.OnClickListener*/ {
    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST = 100;
    private double enlem, boylam;
    private ArrayList<Surucu> suruculer = new ArrayList<Surucu>();
    private Surucu surucuAl;
    private int haritaOdaklanmasiIcinKontrolDegiskeni = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yolcu);
        izinKontrolleri();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            UiSettings uis = googleMap.getUiSettings();
            uis.setCompassEnabled(true);
            uis.setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
        }
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
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
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
        konumGuncellemeTalebindeBulun();
    }

    public void konumuBul(View view) {
      suruculeriGoster();
    }

    public void suruculeriGoster(){
        mMap.clear();
        suruculer.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("suruculer")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                surucuAl = new Surucu(document.getDouble("enlem"), document.getDouble("boylam"), document.getString("surucu_Durumu"));
                                if(surucuAl.surucununDurumu == "cevrimici") {
                                    suruculer.add(surucuAl);
                                    mMap.addMarker(new MarkerOptions().position(new LatLng(surucuAl.enlem, surucuAl.boylam)));
                                }
                            }
                        }
                    }
                });
    }
}
